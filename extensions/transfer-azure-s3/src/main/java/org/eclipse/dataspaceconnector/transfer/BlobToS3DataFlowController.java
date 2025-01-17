package org.eclipse.dataspaceconnector.transfer;

import org.eclipse.dataspaceconnector.spi.asset.DataAddressResolver;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.security.Vault;
import org.eclipse.dataspaceconnector.spi.transfer.flow.DataFlowController;
import org.eclipse.dataspaceconnector.spi.transfer.flow.DataFlowInitiateResult;
import org.eclipse.dataspaceconnector.spi.transfer.response.ResponseStatus;
import org.eclipse.dataspaceconnector.spi.types.TypeManager;
import org.eclipse.dataspaceconnector.spi.types.domain.transfer.DataRequest;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.lang.String.format;

public class BlobToS3DataFlowController implements DataFlowController {
    private static final List<String> ALLOWED_TYPES = Arrays.asList("AmazonS3", "AzureStorage");
    private final Vault vault;
    private final Monitor monitor;
    private final TypeManager typeManager;
    private final DataAddressResolver dataAddressResolver;

    public BlobToS3DataFlowController(Vault vault, Monitor monitor, TypeManager typeManager, DataAddressResolver dataAddressResolver) {

        this.vault = vault;
        this.monitor = monitor;
        this.typeManager = typeManager;
        this.dataAddressResolver = dataAddressResolver;
    }

    @Override
    public boolean canHandle(DataRequest dataRequest) {
        var source = dataAddressResolver.resolveForAsset(dataRequest.getAssetId());
        String sourceType = source.getType();

        String destinationType = dataRequest.getDestinationType();

        return verifyType(sourceType) && verifyType(destinationType);
    }

    @Override
    public @NotNull DataFlowInitiateResult initiateFlow(DataRequest dataRequest) {
        var source = dataAddressResolver.resolveForAsset(dataRequest.getAssetId());
        var sourceType = source.getType();

        String destinationType = dataRequest.getDestinationType();

        var destSecretName = dataRequest.getDataDestination().getKeyName();
        if (destSecretName == null) {
            monitor.severe(format("No credentials found for %s, will not copy!", destinationType));
            return DataFlowInitiateResult.failure(ResponseStatus.ERROR_RETRY, "Did not find credentials for data destination.");
        }
        var secret = vault.resolveSecret(destSecretName);

        monitor.info(format("Copying data from %s to %s", sourceType, destinationType));

        var reader = getReader(sourceType);
        var writer = getWriter(destinationType);
        CompletableFuture.supplyAsync(() -> reader.read(source))
                .thenAccept(byteArrayInputStream -> writer.write(dataRequest.getDataDestination(), dataRequest.getAssetId(), byteArrayInputStream, secret))
                .whenComplete((unused, throwable) -> {
                    if (throwable != null) {
                        monitor.severe("Error during copy process: " + throwable.getMessage());
                        //todo: move process to error
                    } else {
                        monitor.info("Transfer complete");
                    }
                });


        return DataFlowInitiateResult.success("");
    }

    private @NotNull DataWriter getWriter(String destinationType) {
        switch (destinationType) {
            case "AmazonS3":
                return new S3BucketWriter(monitor, typeManager);
            case "AzureStorage":
                return new BlobStoreWriter(monitor, typeManager);
            default:
                throw new IllegalArgumentException("Unknown source type " + destinationType);
        }
    }

    private @NotNull DataReader getReader(String sourceType) {
        switch (sourceType) {
            case "AmazonS3":
                return new S3BucketReader();
            case "AzureStorage":
                return new BlobStoreReader(vault);
            default:
                throw new IllegalArgumentException("Unknown source type " + sourceType);
        }
    }

    private boolean verifyType(String type) {
        return ALLOWED_TYPES.contains(type);
    }
}


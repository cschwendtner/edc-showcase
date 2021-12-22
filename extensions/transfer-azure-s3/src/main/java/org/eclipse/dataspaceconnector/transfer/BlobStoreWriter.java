package org.eclipse.dataspaceconnector.transfer;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobClientBuilder;
import org.eclipse.dataspaceconnector.provision.azure.AzureSasToken;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.types.TypeManager;
import org.eclipse.dataspaceconnector.spi.types.domain.transfer.DataAddress;

import java.io.InputStream;
import java.util.Objects;

class BlobStoreWriter implements DataWriter {


    private final Monitor monitor;
    private final TypeManager typeManager;

    public BlobStoreWriter(Monitor monitor, TypeManager typeManager) {
        this.monitor = monitor;
        this.typeManager = typeManager;
    }

    @Override
    public void write(DataAddress destination, String name, InputStream data, String secretToken) {
        var accountName = destination.getProperty("account");
        var container = destination.getProperty("container");

        AzureSasToken sasToken = null;
        try {
            sasToken = typeManager.readValue(secretToken, AzureSasToken.class);
        } catch (Exception e) {
            monitor.severe("Cannot interpret temporary secret as valid AzureSasToken!");
            return;
        }


        Objects.requireNonNull(accountName, "accountName");
        if (secretToken == null) {
            throw new IllegalArgumentException("BlobStoreWriter secretToken cannot be null!");
        } else {
            var endpoint = "https://" + accountName + ".blob.core.windows.net";

            var sas = sasToken.getSas();
            if (sas.startsWith("?")) {
                sas = sas.substring(1);
            }

            BlobClient blobClient = new BlobClientBuilder()
                    .endpoint(endpoint)
                    .sasToken(sas)
                    .containerName(container)
                    .blobName(name)
                    .buildClient();

            blobClient.upload(BinaryData.fromStream(data), true);

            blobClient = new BlobClientBuilder()
                    .endpoint(endpoint)
                    .sasToken(sas)
                    .containerName(container)
                    .blobName(name + ".complete")
                    .buildClient();

            blobClient.upload(BinaryData.fromBytes(new byte[0]), true);
        }
    }
}

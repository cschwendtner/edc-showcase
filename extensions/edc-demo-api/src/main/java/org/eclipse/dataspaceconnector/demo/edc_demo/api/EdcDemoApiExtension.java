package org.eclipse.dataspaceconnector.demo.edc_demo.api;

import org.eclipse.dataspaceconnector.catalog.spi.QueryEngine;
import org.eclipse.dataspaceconnector.dataloading.AssetLoader;
import org.eclipse.dataspaceconnector.spi.contract.offer.store.ContractDefinitionStore;
import org.eclipse.dataspaceconnector.spi.message.RemoteMessageDispatcherRegistry;
import org.eclipse.dataspaceconnector.spi.policy.PolicyRegistry;
import org.eclipse.dataspaceconnector.spi.protocol.web.WebService;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;
import org.eclipse.dataspaceconnector.spi.transfer.TransferProcessManager;
import org.eclipse.dataspaceconnector.spi.transfer.store.TransferProcessStore;
import org.jetbrains.annotations.Contract;

import java.util.Set;


public class EdcDemoApiExtension implements ServiceExtension {

    @Override
    public Set<String> requires() {
        return Set.of(
                "dataspaceconnector:transferprocessstore",
                "dataspaceconnector:dispatcher",
                ContractDefinitionStore.FEATURE,
                QueryEngine.FEATURE,
                AssetLoader.FEATURE);
    }

    @Override
    public void initialize(ServiceExtensionContext context) {
        var webService = context.getService(WebService.class);
        var monitor = context.getMonitor();

        // get all required services
        var transferProcessManager = context.getService(TransferProcessManager.class);
        var processStore = context.getService(TransferProcessStore.class);
        var catalogQueryEngine = context.getService(QueryEngine.class);
        var dispatcherRegistry = context.getService(RemoteMessageDispatcherRegistry.class);
        var contractDefinitionStore = context.getService(ContractDefinitionStore.class);
        var connectorId = context.getConnectorId();
        var assetSink = context.getService(AssetLoader.class);
//        var policyRegistry = context.getService(PolicyRegistry.class);

        var controller = new EdcDemoApiController(context.getConnectorId(), monitor, transferProcessManager, processStore, catalogQueryEngine, dispatcherRegistry, contractDefinitionStore, connectorId, assetSink); //, policyRegistry);
        webService.registerController(controller);

        monitor.info("Initialized EdcDemo API extension");
    }

}

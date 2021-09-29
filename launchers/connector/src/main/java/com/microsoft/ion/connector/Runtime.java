/*
 *  Copyright (c) 2020, 2021 Microsoft Corporation
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Microsoft Corporation - initial API and implementation
 *
 */

package com.microsoft.ion.connector;

import org.eclipse.dataspaceconnector.monitor.MonitorProvider;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;
import org.eclipse.dataspaceconnector.spi.types.TypeManager;
import org.eclipse.dataspaceconnector.system.DefaultServiceExtensionContext;

import java.util.List;
import java.util.ListIterator;

import static java.lang.String.format;
import static org.eclipse.dataspaceconnector.system.ExtensionLoader.bootServiceExtensions;
import static org.eclipse.dataspaceconnector.system.ExtensionLoader.loadMonitor;
import static org.eclipse.dataspaceconnector.system.ExtensionLoader.loadVault;

public class Runtime {

    public static void main(String[] args) {
        TypeManager typeManager = new TypeManager();
        var monitor = loadMonitor();
        MonitorProvider.setInstance(monitor);
        DefaultServiceExtensionContext context = new DefaultServiceExtensionContext(typeManager, monitor);
        context.initialize();

        var name = context.getConnectorId();
        try {
            loadVault(context);
            List<ServiceExtension> serviceExtensions = context.loadServiceExtensions();
            java.lang.Runtime.getRuntime().addShutdownHook(new Thread(() -> shutdown(serviceExtensions, monitor)));
            bootServiceExtensions(serviceExtensions, context);
        } catch (Exception e) {
            monitor.severe("Error booting runtime", e);
            System.exit(-1);  // stop the process
        }
        monitor.info(format("%s ready", name));

    }

    private static void shutdown(List<ServiceExtension> serviceExtensions, Monitor monitor) {
        ListIterator<ServiceExtension> iter = serviceExtensions.listIterator(serviceExtensions.size());
        while (iter.hasPrevious()) {
            iter.previous().shutdown();
        }
        monitor.info("Connector shutdown complete");
    }

}

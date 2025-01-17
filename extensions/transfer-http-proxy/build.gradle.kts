/*
 *  Copyright (c) 2021 Microsoft Corporation
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

plugins {
    `java-library`
}

val rsApi: String by project
val nimbusVersion: String by project
val edcversion: String by project
val group = "org.eclipse.dataspaceconnector"

dependencies {
    api("${group}:spi:${edcversion}")

    implementation("jakarta.ws.rs:jakarta.ws.rs-api:${rsApi}")
    implementation("com.nimbusds:nimbus-jose-jwt:${nimbusVersion}")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.9.3")

}
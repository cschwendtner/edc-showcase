/*
 * Copyright (c) Microsoft Corporation.
 * All rights reserved.
 */

plugins {
    `java-library`
    application
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

val edcversion: String by project
val group = "org.eclipse.dataspaceconnector"

dependencies {
    // dependencies from this project
    implementation(project(":extensions:public-rest-api"))
    implementation(project(":extensions:dataseeding:catalog"))
    implementation(project(":extensions:dataseeding:hub"))
    implementation(project(":extensions:transfer-azure-s3"))
    implementation(project(":extensions:identity-hub-verifier"))
    implementation(project(":extensions:transfer-http-proxy"))

    // EDC core dependencies
    implementation("${group}:core:${edcversion}")
    implementation("${group}:schema:${edcversion}")
    implementation("${group}:transfer-process-store-memory:${edcversion}")
    implementation("${group}:contract-definition-store-cosmos:${edcversion}")
//    implementation("${group}:contract-negotiation-store-cosmos:${edcversion}")
    implementation("${group}:contractnegotiation-store-memory:${edcversion}")
    // implementation("${group}:observability-api:${edcversion}")
    implementation("${group}:control-api:${edcversion}")

    // ids
    implementation("${group}:ids-policy-mock:${edcversion}")
    implementation("${group}:ids:${edcversion}")

    // simple in-memory and filesystem implementations
    implementation("${group}:policy-registry-memory:${edcversion}")
    implementation("${group}:identity-hub-memory:${edcversion}")
    implementation("${group}:did-document-store-memory:${edcversion}")
    implementation("${group}:filesystem-configuration:${edcversion}")
    implementation("${group}:assetindex-cosmos:${edcversion}")
    implementation("${group}:contract-definition-store-cosmos:${edcversion}")

    //cloud stuff
    implementation("${group}:azure-vault:${edcversion}")
    implementation("${group}:s3-provision:${edcversion}")
    implementation("${group}:blob-provision:${edcversion}")

    // distributed identity stuff
    implementation("${group}:identity-did-web:${edcversion}")
    implementation("${group}:identity-did-spi:${edcversion}")
    implementation("${group}:identity-did-core:${edcversion}")
    implementation("${group}:identity-did-service:${edcversion}")

    // embed an FCC into the runtime
    implementation("${group}:catalog-spi:${edcversion}")
    implementation("${group}:catalog-cache:${edcversion}")
    implementation("${group}:catalog-node-directory-memory:${edcversion}")
    implementation("${group}:catalog-cache-store-memory:${edcversion}")

}

application {
    @Suppress("DEPRECATION")
    mainClassName = "com.microsoft.edc.showcase.connector.Runtime"
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    exclude("**/pom.properties", "**/pom.xm")
    mergeServiceFiles()
    archiveFileName.set("connector.jar")
}


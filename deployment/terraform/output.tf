output "primary_client_id" {
  value = azuread_application.demo-app-id.application_id
}

output "primary_id_certfile" {
  value = abspath("${path.root}/cert.pfx")
}

output "provider-url" {
  value = "${azurerm_container_group.provider-connector.dns_name_label}.${var.location}.azurecontainer.io"
}
#
#output "consumer-url" {
#  value = "${azurerm_container_group.consumer-connector.dns_name_label}.${var.location}azureconainer.io"
#}
#
#output "rev-svc-url" {
#  value = "${azurerm_container_group.registration-service.dns_name_label}.${var.location}azureconainer.io"
#}

output "vault-name" {
  value = azurerm_key_vault.main-vault.name
}

output "static-web" {
  value = azurerm_storage_account.main-blobstore.primary_web_endpoint
}

//output "URLs" {
//  value = {
//    provider    = "https://${module.provider-cluster-bmw.public-ip.fqdn}"
//    consumer-fr = azurerm_container_group.consumer-fr.fqdn
//  }
//}

//output "namespaces" {
//  value = {
//    connector = module.provider-bmw-deployment.connector-cluster-namespace
//  }
//}

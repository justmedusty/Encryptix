package com.test.security

import io.ktor.http.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import java.security.KeyStore
import java.io.File
import java.io.FileInputStream
import java.security.cert.CertificateFactory

fun ContentType.Application.module(){
    val keyStore  = KeyStore.getInstance(KeyStore.getDefaultType())
    keyStore.load(null,null)

    val keyDirectoryPath = "src/keys"
    val directory = File(keyDirectoryPath)

    directory.listFiles{_, name -> name.endsWith(".asc") }?.forEach {
         file ->
            val certFile = FileInputStream(file)
            val certificate = CertificateFactory.getInstance("asc").generateCertificate(certFile)
            keyStore.setCertificateEntry(file.nameWithoutExtension, certificate)
        }


}
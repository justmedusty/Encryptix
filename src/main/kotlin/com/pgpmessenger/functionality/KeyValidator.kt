package com.pgpmessenger.functionality



fun isValidOpenPGPPublicKey(publicKey: String): Boolean {
    val header = "-----BEGIN PGP PUBLIC KEY BLOCK-----"
    val footer = "-----END PGP PUBLIC KEY BLOCK-----"

    return publicKey.trim().startsWith(header) && publicKey.trim().endsWith(footer)
}
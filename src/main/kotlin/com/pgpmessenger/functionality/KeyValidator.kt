package com.pgpmessenger.functionality


/**
 * Is valid open p g p public key
 *
 * @param publicKey
 * @return boolean true if is valid, false if not valid
 */
fun isValidOpenPGPPublicKey(publicKey: String): Boolean {
    val header = "-----BEGIN PGP PUBLIC KEY BLOCK-----"
    val footer = "-----END PGP PUBLIC KEY BLOCK-----"
    return publicKey.trim().startsWith(header) && publicKey.trim().endsWith(footer)
}
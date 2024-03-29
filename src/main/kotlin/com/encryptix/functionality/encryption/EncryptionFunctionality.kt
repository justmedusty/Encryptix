package com.encryptix.functionality.encryption

import org.bouncycastle.openpgp.PGPPublicKeyRing
import org.bouncycastle.util.io.Streams
import org.pgpainless.PGPainless
import org.pgpainless.algorithm.SymmetricKeyAlgorithm
import org.pgpainless.encryption_signing.EncryptionOptions
import org.pgpainless.encryption_signing.EncryptionStream
import org.pgpainless.encryption_signing.ProducerOptions
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.*

/**
 * Encrypt message
 *
 * @param publicKey
 * @param message
 * @return encrypted message
 */
fun encryptMessage(publicKey: String, message: String): ByteArray {
    val outputStream = ByteArrayOutputStream()

    // Parse the publicKey String to a PGPPublicKeyRing
    val publicKeyObj: PGPPublicKeyRing = PGPainless.readKeyRing().publicKeyRing(publicKey)!!
    val plaintextInputStream = ByteArrayInputStream(message.toByteArray())

    val encryptionStream: EncryptionStream = PGPainless.encryptAndOrSign().onOutputStream(outputStream).withOptions(
            ProducerOptions.encrypt(
                EncryptionOptions().addRecipient(publicKeyObj)
                    .overrideEncryptionAlgorithm(SymmetricKeyAlgorithm.AES_192),

                ).setAsciiArmor(true), // Ascii armor or not
        )

    Streams.pipeAll(plaintextInputStream, encryptionStream)
    encryptionStream.close()

    // Information about the encryption (algorithms, detached signatures etc.)

    // Information about the encryption (algorithms, detached signatures etc.)
    val encryptedMessage = Base64.getEncoder().encodeToString(outputStream.toByteArray())

    return encryptedMessage.toByteArray()
}


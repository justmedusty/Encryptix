package com.encryptix.database

enum class CheckConstraints(val MAX_SIZE: Int) {
    MESSAGE_BYTES(26214400),
    PUBLIC_KEY_LENGTH(21845)// We have to provide the value while instantiating
}
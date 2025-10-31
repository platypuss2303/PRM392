package com.example.whatsapp.domain.models

data class Message(
    val senderPhoneNumber: String = "",
    val message: String = "",
    val timestamp: Long = 0L
)

package com.example.cashwire.models

import java.io.Serializable

data class User(
    val id: String = "1", // Default ID
    var name: String = "",
    var email: String = "",
    var profileImage: String? = null, // Store path or URL to image
    var phoneNumber: String? = null,
    var currency: String = "LKR",
    val createdAt: Long = System.currentTimeMillis(),
    var updatedAt: Long = System.currentTimeMillis()
) : Serializable
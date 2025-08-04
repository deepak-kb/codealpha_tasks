package com.example.collegealertapplication.data

data class User(
    val uid: String = "",
    val email: String = "",
    val fullName: String = "",
    val mobileNumber: String = "",
    val dateOfBirth: String = "",
    val collegeAffiliation: String = "",
    val createdAt: Long = System.currentTimeMillis()
)


package com.example.pilltracker

data class MyPills(
    val id: Int,
    val userName: String,
    val nameOfMedicine: String,
    val dose: String,
    val timeToTakeMed: String,
    val additionalNotes: String,
    val remainingMedicine: String
)
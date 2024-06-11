package uk.ac.aber.dcs.cs39440.majorproject.components

data class LinkRequest(
    val requestID: String,
    val clientID: String,
    val trainerID: String,
    val senderID: String
)

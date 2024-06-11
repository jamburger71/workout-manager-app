package uk.ac.aber.dcs.cs39440.majorproject.components

import com.google.firebase.Timestamp

data class ChatMessage(
    var sender: String,
    var message: String,
    var sendTime: Timestamp
)
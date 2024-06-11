package uk.ac.aber.dcs.cs39440.majorproject.components

import com.google.firebase.Timestamp

data class Workout(
    var workoutName: String,
    var workoutDescription: String,
    var startDate: Timestamp, // DATE OF WORKOUT START
    var repeats: Boolean, // 0 if infinite, 1 if once etc.
    var exercises: MutableMap<String, Exercise> = mutableMapOf(), // LIST OF DAYS AND THEIR ASSOCIATED EXERCISE LIST
    var completedDates: ArrayList<Timestamp> = ArrayList()
)
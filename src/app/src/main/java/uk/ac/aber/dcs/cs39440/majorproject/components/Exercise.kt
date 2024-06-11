package uk.ac.aber.dcs.cs39440.majorproject.components

data class Exercise(
    var exerciseName: String,
    var exerciseType: ExerciseType,
    var exerciseDuration: Int,
    var exerciseWeight: Int,
    var exerciseDistance: Int
)
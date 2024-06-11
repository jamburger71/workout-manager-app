package uk.ac.aber.dcs.cs39440.majorproject.firebase

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.getField
import uk.ac.aber.dcs.cs39440.majorproject.TAG
import uk.ac.aber.dcs.cs39440.majorproject.components.ChatDetails
import uk.ac.aber.dcs.cs39440.majorproject.components.ChatMessage
import uk.ac.aber.dcs.cs39440.majorproject.components.Exercise
import uk.ac.aber.dcs.cs39440.majorproject.components.ExerciseType
import uk.ac.aber.dcs.cs39440.majorproject.components.LinkRequest
import uk.ac.aber.dcs.cs39440.majorproject.components.TaskStatus
import uk.ac.aber.dcs.cs39440.majorproject.components.ThemeType
import uk.ac.aber.dcs.cs39440.majorproject.components.UserViewModel
import uk.ac.aber.dcs.cs39440.majorproject.components.Workout
import java.util.Date

const val FIREBASE_TAG = "Firestore Manager"

const val USERS = "users"
const val CLIENT_NAME = "clientName"
const val CLIENT_SETUP = "clientSetupComplete"

const val WORKOUTS = "workouts"
const val WORKOUT_NAME = "workoutName"
const val WORKOUT_DESC = "workoutDescription"
const val START_DATE = "startDate"
const val REPEATS = "repeatsAmount"

const val EXERCISES = "exercises"
const val EXERCISE_NAME = "exerciseName"
const val EXERCISE_TYPE = "exerciseType"
const val EXERCISE_DURATION = "exerciseDuration"
const val EXERCISE_WEIGHT = "exerciseWeight"
const val EXERCISE_DISTANCE = "exerciseDistance"

const val PERSONAL_TRAINERS = "personalTrainers"
const val TRAINER_NAME = "trainerName"
const val TRAINER_SETUP = "trainerSetupComplete"

const val LINK_REQUESTS = "linkRequests"
const val CLIENT_ID = "clientID"
const val TRAINER_ID = "trainerID"
const val SENDER_ID = "senderID"

const val CHATS = "chats"
const val CHAT_MESSAGES = "chatMessages"
const val CHAT_SENDER = "chatSender"
const val CHAT_MESSAGE = "chatMessage"
const val CHAT_TIMESTAMP = "chatTimestamp"

const val LINKS = "links"

const val THEME = "theme"

fun addWorkout(
    workoutName: String,
    workoutDescription: String,
    startDate: Timestamp,
    repeats: Boolean,
    exercises: MutableList<Exercise>,
    clientID: String? = null
) {
    val db = Firebase.firestore
    val user = FirebaseAuth.getInstance().currentUser

    val workoutMap = hashMapOf(
        WORKOUT_NAME to workoutName,
        WORKOUT_DESC to workoutDescription,
        START_DATE to startDate,
        REPEATS to repeats
    )
    if (user != null) {
        db.collection(USERS)
            .document(clientID ?: user.uid)
            .collection(WORKOUTS)
            .add(workoutMap)
            .addOnSuccessListener { ref ->
                Log.d("TAG", "Workout added with ID: ${ref.id}")
                exercises.forEach { exercise ->
                    val exerciseMap = hashMapOf(
                        EXERCISE_NAME to exercise.exerciseName,
                        EXERCISE_TYPE to exercise.exerciseType,
                        EXERCISE_DURATION to exercise.exerciseDuration,
                        EXERCISE_WEIGHT to exercise.exerciseWeight,
                        EXERCISE_DISTANCE to exercise.exerciseDistance
                    )
                    db.collection(USERS)
                        .document(clientID ?: user.uid)
                        .collection(WORKOUTS)
                        .document(ref.id)
                        .collection(EXERCISES)
                        .add(exerciseMap)
                        .addOnSuccessListener { ref ->
                            Log.d(FIREBASE_TAG, "Exercise added with ID: ${ref.id}")
                        }
                        .addOnFailureListener { e ->
                            Log.w(FIREBASE_TAG, "Failed to add exercise", e)
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.w("TAG", "Failed to add workout", e)
            }

    }

}

fun editWorkout(
    workout: Pair<String, Workout>,
    unsavedExercises: MutableList<Exercise>,
    savedExercises: MutableMap<String, Exercise>,
    deletedExercise: MutableList<String>,
    clientID: String? = null
) {
    val db = Firebase.firestore
    val user = FirebaseAuth.getInstance().currentUser
    if (user != null) {
        Log.d("TAG","Editing workout with ID: ${workout.first} and name: ${workout.second.workoutName}")
        db.collection(USERS)
            .document(clientID ?: user.uid)
            .collection(WORKOUTS)
            .document(workout.first)
            .update(
                WORKOUT_NAME, workout.second.workoutName,
                WORKOUT_DESC, workout.second.workoutDescription,
                START_DATE, workout.second.startDate,
                REPEATS, workout.second.repeats
            )
            .addOnSuccessListener {
                Log.d("TAG", "Workout successfully modified")
                savedExercises.forEach { (exerciseID, exercise) ->
                    db.collection(USERS)
                        .document(clientID ?: user.uid)
                        .collection(WORKOUTS)
                        .document(workout.first)
                        .collection(EXERCISES)
                        .document(exerciseID)
                        .update(
                            EXERCISE_NAME, exercise.exerciseName,
                            EXERCISE_TYPE, exercise.exerciseType,
                            EXERCISE_DURATION, exercise.exerciseDuration,
                            EXERCISE_WEIGHT, exercise.exerciseWeight,
                            EXERCISE_DISTANCE, exercise.exerciseDistance
                        )
                }
                unsavedExercises.forEach { exercise ->
                    val data: Map<String, Any> = hashMapOf(
                        EXERCISE_NAME to exercise.exerciseName,
                        EXERCISE_TYPE to exercise.exerciseType,
                        EXERCISE_DURATION to exercise.exerciseDuration,
                        EXERCISE_WEIGHT to exercise.exerciseWeight,
                        EXERCISE_DISTANCE to exercise.exerciseDistance
                    )
                    db.collection(USERS)
                        .document(clientID ?: user.uid)
                        .collection(WORKOUTS)
                        .document(workout.first)
                        .collection(EXERCISES)
                        .add(data)
                }
                deletedExercise.forEach { id ->
                    db.collection(USERS)
                        .document(clientID ?: user.uid)
                        .collection(WORKOUTS)
                        .document(workout.first)
                        .collection(EXERCISES)
                        .document(id)
                        .delete()
                }
            }
            .addOnFailureListener { e ->
                Log.w("TAG", "Failed to edit workout", e)
            }
    }
}

fun deleteWorkout(workoutID: String) {
    val db = Firebase.firestore
    val user = FirebaseAuth.getInstance().currentUser

    if (user != null) {
        db.collection(USERS)
            .document(user.uid)
            .collection(WORKOUTS)
            .document(workoutID)
            .delete()
            .addOnSuccessListener {
                Log.d("TAG", "Workout deleted with id: $workoutID")
            }
            .addOnFailureListener { e ->
                Log.w(FIREBASE_TAG, "Failed to delete workout with id: $workoutID", e)
            }
    }
}

fun getAllWorkouts(
    workouts: SnapshotStateMap<String, Workout>,
    userID: String? = null
) {
    val db = Firebase.firestore
    val user = FirebaseAuth.getInstance().currentUser

    if (user != null) {
        db.collection(USERS)
            .document(userID ?: user.uid)
            .collection(WORKOUTS)
            .get()
            .addOnSuccessListener { loadedWorkouts ->
                if (!loadedWorkouts.isEmpty) {
                    loadedWorkouts.forEach { workout ->
                        var workoutName = workout.getString(WORKOUT_NAME)
                        var workoutDescription = workout.getString(WORKOUT_DESC)
                        var startDate = workout.getField<Timestamp>(START_DATE)
                        var repeats = workout.getField<Boolean>(REPEATS)
                        if (workoutName == null) {
                            Log.w("TAG", "Workout ${workout.id}: No name found")
                            workoutName = "No Name"
                        }
                        if (workoutDescription == null) {
                            Log.w("TAG", "Workout ${workout.id}: No description found")
                            workoutDescription = "No Description"
                        }
                        if (startDate == null) {
                            Log.w("TAG", "Workout ${workout.id}: No time signature found")
                            startDate = Timestamp(Date(0))
                        }
                        if (repeats == null) {
                            Log.w("TAG", "Workout ${workout.id}: No repeating data found")
                            repeats = false
                        }
                        val tempWorkout = Workout(
                            workoutName = workoutName,
                            workoutDescription = workoutDescription,
                            startDate = startDate,
                            repeats = repeats
                        )
                        getExercisesForWorkout(workoutID = workout.id, exercises = tempWorkout.exercises, userID = userID ?: user.uid)
                        Log.d("TAG", "Workout loaded from database with ID ${workout.id}")
                        workouts[workout.id] = tempWorkout
                    }
                    Log.d("TAG", "Workouts loaded from database")
                } else {
                    Log.w("TAG", "No workouts were found")
                }
            }
            .addOnFailureListener { e ->
                Log.w("TAG", "Failed to get workouts", e)
            }
    }
}

private fun getExercisesForWorkout(
    workoutID: String,
    userID: String,
    exercises: MutableMap<String, Exercise>
) {
    val db = Firebase.firestore

    db.collection(USERS)
        .document(userID)
        .collection(WORKOUTS)
        .document(workoutID)
        .collection(EXERCISES)
        .get()
        .addOnSuccessListener { collection ->
            if (!collection.isEmpty) {
                collection.forEach { exercise ->
                    val exerciseName = exercise.getString(EXERCISE_NAME)
                    val exerciseDistance = exercise.getField<Int>(EXERCISE_DISTANCE)
                    val exerciseWeight = exercise.getField<Int>(EXERCISE_WEIGHT)
                    val exerciseDuration = exercise.getField<Int>(EXERCISE_DURATION)
                    val exerciseType = exercise.getField<ExerciseType>(EXERCISE_TYPE)
                    if (exerciseName != null && exerciseDistance != null && exerciseDuration != null && exerciseType != null && exerciseWeight != null) {
                        val tempExercise = Exercise(
                            exerciseName = exerciseName,
                            exerciseType = exerciseType,
                            exerciseDistance = exerciseDistance,
                            exerciseDuration = exerciseDuration,
                            exerciseWeight = exerciseWeight
                        )
                        exercises[exercise.id] = tempExercise
                    }
                }
            }
        }
}

fun getUserTheme(theme: MutableState<ThemeType>) {
    val db = Firebase.firestore
    val user = FirebaseAuth.getInstance().currentUser

    if (user != null) {
        db.collection(USERS)
            .document(user.uid)
            .get()
            .addOnSuccessListener { document ->
                val localTheme = document.getField<ThemeType>(THEME)
                if (localTheme != null) {
                    theme.value = localTheme
                } else {
                    theme.value = ThemeType.System
                    setUserTheme(ThemeType.System)
                }
                Log.d(FIREBASE_TAG, "Theme loaded as ${theme.value}")
            }
            .addOnFailureListener { e ->
                Log.w(FIREBASE_TAG, "Failed to get theme", e)
                theme.value = ThemeType.System
            }
    }

}

fun setUserTheme(theme: ThemeType) {
    val db = Firebase.firestore
    val user = FirebaseAuth.getInstance().currentUser

    if (user != null) {
        db.collection(USERS)
            .document(user.uid)
            .update(THEME,theme)
            .addOnSuccessListener {
                Log.d(FIREBASE_TAG,"Theme saved successfully as $theme")
            }
            .addOnFailureListener { e ->
                Log.w(FIREBASE_TAG, "Failed to get theme", e)
            }
    }
}

fun getAllWorkoutsFromStoreForPT(
    workouts: SnapshotStateMap<String, SnapshotStateMap<String, Workout>>
) {
    val db = Firebase.firestore
    val user = FirebaseAuth.getInstance().currentUser

    if (user != null) {
        db.collection(LINKS)
            .whereEqualTo(TRAINER_ID, user.uid)
            .get()
            .addOnSuccessListener { ids ->
                ids.forEach { document ->
                    Log.i(TAG, document.id)
                    val clientID = document.getString(CLIENT_ID)
                    if (clientID != null) {
                        workouts[clientID] = mutableStateMapOf()
                        getAllWorkouts(workouts = workouts[clientID]!!, clientID)
                    }
                }
            }
    }
}

fun getTrainers(
    listOfTrainers: MutableList<Pair<String, String?>>
) {
    val db = Firebase.firestore
    val user = FirebaseAuth.getInstance().currentUser

    if (user != null) {
        db.collection(PERSONAL_TRAINERS)
            .whereEqualTo(TRAINER_SETUP, true)
            .get()
            .addOnSuccessListener { list ->
                list.forEach { trainerSnapshot ->
                    val trainer = Pair(
                        trainerSnapshot.id, trainerSnapshot.getField<String>(TRAINER_NAME)
                    )
                    if (trainer.second != null) {
                        listOfTrainers.add(trainer)
                    }
                }
            }
    }
}

fun getClients(
    listOfClients: MutableList<Pair<String, String?>>
) {
    val db = Firebase.firestore
    val user = FirebaseAuth.getInstance().currentUser

    if (user != null) {
        db.collection(USERS)
            .whereEqualTo(CLIENT_SETUP, true)
            .get()
            .addOnSuccessListener { list ->
                list.forEach { snapshot ->
                    val client = Pair(snapshot.id, snapshot.getField<String>(CLIENT_NAME))
                    if (client.second != null) {
                        listOfClients.add(client)
                    }
                }
            }
    }
}

fun getLinkRequest(
    isClient: Boolean,
    otherPersonID: String,
    linkExists: MutableState<LinkRequest?>
) {
    val db = Firebase.firestore
    val user = FirebaseAuth.getInstance().currentUser

    if (user != null) {
        val clientID = if (isClient) user.uid else otherPersonID
        val trainerID = if (isClient) otherPersonID else user.uid

        db.collection(LINK_REQUESTS)
            .whereEqualTo(CLIENT_ID, clientID)
            .whereEqualTo(TRAINER_ID, trainerID)
            .get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.isEmpty) {
                    snapshot.forEach { document ->
                        val clientIDLink = document.getString(CLIENT_ID)
                        val trainerIDLink = document.getString(TRAINER_ID)
                        val senderID = document.getString(SENDER_ID)
                        if (clientIDLink != null && trainerIDLink != null && senderID != null) {
                            val tempLinkRequest = LinkRequest(
                                clientID = clientIDLink,
                                trainerID = trainerIDLink,
                                senderID = senderID,
                                requestID = document.id
                            )
                            linkExists.value = tempLinkRequest
                        }
                    }
                } else {
                    linkExists.value = null
                }
            }
    }
}

fun getLinkRequests(
    isFromClient: Boolean,
    listOfLinks: SnapshotStateList<LinkRequest>
) {
    val db = Firebase.firestore
    val user = FirebaseAuth.getInstance().currentUser

    if (user != null) {
        val reference = if (isFromClient) CLIENT_ID else TRAINER_ID
        db.collection(LINK_REQUESTS)
            .whereEqualTo(reference, user.uid)
            .whereNotEqualTo(SENDER_ID, user.uid)
            .get()
            .addOnSuccessListener { snapshot ->
                Log.i(TAG, "Found link request snapshot")
                if (!snapshot.isEmpty) {
                    snapshot.forEach { document ->
                        Log.i(TAG, "found link request")
                        val clientID = document.getString(CLIENT_ID)
                        val trainerID = document.getString(TRAINER_ID)
                        val senderID = document.getString(SENDER_ID)
                        if (clientID != null && trainerID != null && senderID != null) {
                            val tempLinkRequest = LinkRequest(
                                clientID = clientID,
                                senderID = senderID,
                                trainerID = trainerID,
                                requestID = document.id
                            )
                            listOfLinks.add(tempLinkRequest)
                        }
                    }
                }
            }
    }
}

fun createLinkRequest(
    isFromClient: Boolean,
    otherPersonID: String,
    linkRequest: MutableState<LinkRequest?>
) {
    val db = Firebase.firestore
    val user = FirebaseAuth.getInstance().currentUser

    if (user != null ) {
        val data: HashMap<String, Any> = hashMapOf()
        if (isFromClient) {
            data[CLIENT_ID] = user.uid
            data[TRAINER_ID] = otherPersonID
        } else {
            data[CLIENT_ID] = otherPersonID
            data[TRAINER_ID] = user.uid
        }
        data[SENDER_ID] = user.uid

        db.collection(LINK_REQUESTS)
            .add(data)
            .addOnSuccessListener { document ->
                linkRequest.value = LinkRequest(
                    clientID = data[CLIENT_ID].toString(),
                    trainerID = data[TRAINER_ID].toString(),
                    senderID = data[SENDER_ID].toString(),
                    requestID = document.id
                )
            }
    }
}

fun deleteLinkRequest(
    requestID: String
) {
    val db = Firebase.firestore
    val user = FirebaseAuth.getInstance().currentUser

    if (user != null) {

        db.collection(LINK_REQUESTS)
            .document(requestID)
            .delete()
            .addOnSuccessListener {
                Log.i(TAG, "Successfully deleted link request")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "failed to delete link request with error: ",e)
            }
    }
}

fun createLink(
    linkRequest: LinkRequest
) {
    val db = Firebase.firestore
    val user = FirebaseAuth.getInstance().currentUser

    if (user != null) {
        val clientID = linkRequest.clientID
        val trainerID = linkRequest.trainerID
        val data = hashMapOf(
            CLIENT_ID to clientID,
            TRAINER_ID to trainerID
        )
        db.collection(LINKS)
            .add(data)
    }
}
fun getLinks(
    links: MutableList<Pair<String,String>>,
    isFromClient: Boolean
) {
    val db = Firebase.firestore
    val user = FirebaseAuth.getInstance().currentUser

    if (user != null) {
        val reference = if (!isFromClient) TRAINER_ID else CLIENT_ID
        db.collection(LINKS)
            .whereEqualTo(reference, user.uid)
            .get()
            .addOnSuccessListener { docList ->
                if (!docList.isEmpty) {
                    docList.forEach { document ->
                        val clientID = document.getString(CLIENT_ID)
                        val trainerID = document.getString(TRAINER_ID)
                        if (clientID != null && trainerID != null) {
                            links.add(Pair(clientID, trainerID))
                        }
                    }
                }
            }
    }
}

fun getLinkExists(
    isFromClient: Boolean,
    otherPersonID: String,
    isExists: MutableState<TaskStatus>
) {
    val db = Firebase.firestore
    val user = FirebaseAuth.getInstance().currentUser

    if (user != null) {
        val reference = if (isFromClient) CLIENT_ID else TRAINER_ID
        val otherReference = if (!isFromClient) CLIENT_ID else TRAINER_ID
        db.collection(LINKS)
            .whereEqualTo(reference, user.uid)
            .whereEqualTo(otherReference, otherPersonID)
            .get()
            .addOnSuccessListener { docList ->
                if (!docList.isEmpty) {
                    isExists.value = TaskStatus.IsTrue
                } else {
                    isExists.value = TaskStatus.IsFalse
                }
            }
    }
}

fun deleteLink(
    isFromClient: Boolean,
    otherPersonID: String
) {
    val db = Firebase.firestore
    val user = FirebaseAuth.getInstance().currentUser

    if (user != null) {
        val clientID = if (isFromClient) user.uid else otherPersonID
        val trainerID = if (isFromClient) otherPersonID else user.uid

        db.collection(LINKS)
            .whereEqualTo(CLIENT_ID, clientID)
            .whereEqualTo(TRAINER_ID, trainerID)
            .get()
            .addOnSuccessListener { docList ->
                docList.documents.forEach { document ->
                    db.collection(LINKS)
                        .document(document.id)
                        .delete()
                }
            }
    }
}

fun setTrainerName(
    name: String,
    isSaved: MutableState<TaskStatus>
) {
    val db = Firebase.firestore
    val user = FirebaseAuth.getInstance().currentUser

    if (user != null && name != "") {
        val data = hashMapOf(
            TRAINER_NAME to name,
            TRAINER_SETUP to true
        )
        db.collection(PERSONAL_TRAINERS)
            .document(user.uid)
            .set(data)
            .addOnSuccessListener {
                isSaved.value = TaskStatus.Succeeded
            }
            .addOnFailureListener { e ->
                isSaved.value = TaskStatus.Failed
                Log.e(TAG, "Failed to save client name: Firebase Error", e)
            }
    }
}

fun getTrainerName(
    name: MutableState<String>,
    senderID: String
) {
    val db = Firebase.firestore
    val user = FirebaseAuth.getInstance().currentUser

    if (user != null) {
        db.collection(PERSONAL_TRAINERS)
            .document(senderID)
            .get()
            .addOnSuccessListener {
                val trainerName = it.getString(TRAINER_NAME)
                if (trainerName != null) name.value = trainerName
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to get trainer name", e)
            }
    }
}

fun getTrainerSetup(
    value: MutableState<Boolean>,
    name: MutableState<String>
) {
    val db = Firebase.firestore
    val user = FirebaseAuth.getInstance().currentUser

    if (user != null) {
        db.collection(PERSONAL_TRAINERS)
            .document(user.uid)
            .get()
            .addOnSuccessListener { document ->
                val isSetup = document.getField<Boolean>(TRAINER_SETUP)
                value.value = isSetup != null && isSetup
                if (value.value) {
                    name.value = document.getField<String>(TRAINER_NAME).toString()
                }
            }
    }
}

fun setClientName(
    name: String,
    isSaved: MutableState<TaskStatus>
) {
    val db = Firebase.firestore
    val user = FirebaseAuth.getInstance().currentUser

    if (user != null && name != "") {
        val data = hashMapOf(
            CLIENT_NAME to name,
            CLIENT_SETUP to true
        )
        db.collection(USERS)
            .document(user.uid)
            .set(data)
            .addOnSuccessListener {
                isSaved.value = TaskStatus.Succeeded
            }
            .addOnFailureListener { e ->
                isSaved.value = TaskStatus.Failed
                Log.e(TAG, "Failed to save client name: Firebase Error", e)
            }
    }
}

fun getClientName(
    name: MutableState<String>,
    senderID: String
) {
    val db = Firebase.firestore
    val user = FirebaseAuth.getInstance().currentUser

    if (user != null) {
        db.collection(USERS)
            .document(senderID)
            .get()
            .addOnSuccessListener {
                name.value = it.getField<String>(CLIENT_NAME)!!
            }
    }
}

fun getClientSetup(
    value: MutableState<Boolean>,
    name: MutableState<String>
) {
    val db = Firebase.firestore
    val user = FirebaseAuth.getInstance().currentUser

    if (user != null) {
        db.collection(USERS)
            .document(user.uid)
            .get()
            .addOnSuccessListener { document ->
                val isSetup = document.getField<Boolean>(CLIENT_SETUP)
                value.value = isSetup != null && isSetup
                if (value.value) {
                    name.value = document.getField<String>(CLIENT_NAME).toString()
                }
            }
    }
}

fun getChats(
    isFromClient: Boolean,
    listOfChats: SnapshotStateList<ChatDetails>
) {
    val db = Firebase.firestore
    val user = FirebaseAuth.getInstance().currentUser

    if (user != null) {
        val reference = if (isFromClient) CLIENT_ID else TRAINER_ID

        db.collection(CHATS)
            .whereEqualTo(reference, user.uid)
            .get()
            .addOnSuccessListener { snapshot ->
                listOfChats.clear()
                snapshot.forEach { chat ->
                    val clientID = chat.getString(CLIENT_ID)
                    val trainerID = chat.getString(TRAINER_ID)
                    if (clientID != null && trainerID != null) {
                        listOfChats.add(ChatDetails(
                            chatID = chat.id,
                            clientID = clientID,
                            trainerID = trainerID
                        ))
                    }
                }
            }
    }
}

fun getChat(
    chatID: String,
    chatDetails: MutableState<ChatDetails>
) {
    val db = Firebase.firestore
    val user = FirebaseAuth.getInstance().currentUser

    if (user != null) {
        db.collection(CHATS)
            .document(chatID)
            .get()
            .addOnSuccessListener { document ->
                val clientID = document.getString(CLIENT_ID)
                val trainerID = document.getString(TRAINER_ID)
                if (!clientID.isNullOrBlank() && !trainerID.isNullOrBlank()) {
                    val newChatDetails = ChatDetails(
                        chatID = chatID,
                        trainerID = trainerID,
                        clientID = clientID
                    )
                    chatDetails.value = newChatDetails
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to get chat",e)
            }
    }
}

fun listenToChat(
    chatID: String,
    chatList: SnapshotStateList<Pair<String, ChatMessage>>
) {
    val db = Firebase.firestore
    val user = FirebaseAuth.getInstance().currentUser

    if (user != null) {
        db.collection(CHATS)
            .document(chatID)
            .collection(CHAT_MESSAGES)
            .addSnapshotListener { snapshot, e ->
                if (e != null) run {
                    Log.e(TAG, "Failed to listen to chat", e)
                    return@addSnapshotListener
                } else {
                    chatList.clear()
                    snapshot?.forEach { chat ->
                        val sender = chat.getString(CHAT_SENDER)
                        val message = chat.getString(CHAT_MESSAGE)
                        val timestamp = chat.getField<Timestamp>(CHAT_TIMESTAMP)
                        if (sender != null && message != null && timestamp != null) {
                            chatList.add(
                                Pair(
                                    chat.id,
                                    ChatMessage(
                                        sender = sender,
                                        message = message,
                                        sendTime = timestamp
                                    )
                                )
                            )
                        }
                    }
                }
            }
    }
}

fun sendChat(
    chatID: String,
    message: String
) {
    val db = Firebase.firestore
    val user = FirebaseAuth.getInstance().currentUser

    if (user != null) {
        val data = hashMapOf(
            CHAT_SENDER to user.uid,
            CHAT_MESSAGE to message,
            CHAT_TIMESTAMP to Timestamp(Date())
        )
        db.collection(CHATS)
            .document(chatID)
            .collection(CHAT_MESSAGES)
            .add(data)
    }
}

fun createChat(
    isFromClient: Boolean,
    otherPersonID: String,
    currentChat: MutableState<String>
) {
    val db = Firebase.firestore
    val user = FirebaseAuth.getInstance().currentUser

    if (user != null) {
        val clientID = if (isFromClient) user.uid else otherPersonID
        val trainerID = if (isFromClient) otherPersonID else user.uid

        val data = hashMapOf(
            CLIENT_ID to clientID,
            TRAINER_ID to trainerID
        )
        db.collection(CHATS)
            .add(data)
            .addOnSuccessListener {
                currentChat.value = it.id
            }
    }
}
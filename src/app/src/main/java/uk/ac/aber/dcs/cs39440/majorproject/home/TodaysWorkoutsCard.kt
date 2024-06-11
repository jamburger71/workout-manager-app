package uk.ac.aber.dcs.cs39440.majorproject.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import uk.ac.aber.dcs.cs39440.majorproject.R
import uk.ac.aber.dcs.cs39440.majorproject.components.ExerciseType
import uk.ac.aber.dcs.cs39440.majorproject.components.UserViewModel
import uk.ac.aber.dcs.cs39440.majorproject.components.Workout
import uk.ac.aber.dcs.cs39440.majorproject.firebase.getAllWorkouts
import uk.ac.aber.dcs.cs39440.majorproject.firebase.getAllWorkoutsFromStoreForPT
import uk.ac.aber.dcs.cs39440.majorproject.firebase.getClientName
import uk.ac.aber.dcs.cs39440.majorproject.components.Screen
import uk.ac.aber.dcs.cs39440.majorproject.ui.theme.typography

@Composable
fun TodaysWorkoutsCard(
    navController: NavHostController,
    userViewModel: UserViewModel,
    isClient: Boolean
) {
    val workouts: SnapshotStateMap<String, SnapshotStateMap<String, Workout>> = remember { mutableStateMapOf() }
    val user = FirebaseAuth.getInstance().currentUser

    LaunchedEffect(key1 = userViewModel.userMode.value) {
        if (user != null) {
            if (isClient) {
                workouts[user.uid] = mutableStateMapOf()
                workouts[user.uid]?.let { getAllWorkouts(it) }
            } else {
                getAllWorkoutsFromStoreForPT(workouts)
            }
        }
    }

    Card(
        modifier = Modifier
            .padding(all = 10.dp)
            .fillMaxWidth()
    ) {
        var noWorkouts = true
        Text(
            modifier = Modifier
                .padding(10.dp),
            text = if (workouts.size == 1) stringResource(id = R.string.homepage_todaysWorkout_one) else stringResource(id = R.string.homepage_todaysWorkout_many),
            style = typography.titleLarge
        )
        if (workouts.isNotEmpty()) {
            workouts.forEach { users ->
                if (users.value.isNotEmpty()) {
                    val username = remember { mutableStateOf("") }
                    if (!isClient) getClientName(username, users.key)
                    noWorkouts = false
                    users.value.forEach { workout ->
                        WorkoutCard(
                            workout = workout,
                            userViewModel = userViewModel,
                            users = users,
                            isClient = isClient,
                            username = username,
                            navController = navController
                        )
                    }
                }
            }
        }
        if (noWorkouts) {
            Text(
                modifier = Modifier
                    .padding(horizontal = 10.dp),
                text = stringResource(id = R.string.homepage_todaysWorkout_noWorkoutSet),
                style = typography.titleLarge
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Button(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(bottom = 5.dp),
                onClick = {
                    navController.navigate(Screen.CreateWorkout.route)
                }
            ) {
                Text(
                    text = stringResource(id = R.string.createWorkout_title)
                )
            }
        }
    }
}

@Composable
fun WorkoutCard(
    workout: Map.Entry<String, Workout>,
    userViewModel: UserViewModel,
    users: Map.Entry<String, SnapshotStateMap<String, Workout>>,
    isClient: Boolean,
    username: MutableState<String>,
    navController: NavHostController
) {
    val expanded = remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .clickable {
                    expanded.value = !expanded.value
                }
        ) {
            Text(
                text = workout.value.workoutName,
                style = typography.titleMedium
            )
            AnimatedVisibility(visible = expanded.value) {
                Column {
                    Text(
                        text = workout.value.workoutDescription,
                        style = typography.bodyMedium
                    )
                    Text(
                        text = stringResource(id = R.string.homepage_todaysWorkout_exercises),
                        style = typography.titleSmall
                    )
                    Column(
                        modifier = Modifier
                            .padding(10.dp)
                    ) {
                        HorizontalDivider()
                        workout.value.exercises.forEach { exercise ->
                            Column(
                                modifier = Modifier
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = exercise.value.exerciseName,
                                    style = typography.bodyLarge
                                )
                                if (
                                    exercise.value.exerciseType == ExerciseType.Weight ||
                                    exercise.value.exerciseType == ExerciseType.TimedWeight ||
                                    exercise.value.exerciseType == ExerciseType.DistanceWeight ||
                                    exercise.value.exerciseType == ExerciseType.TimedDistanceWeight
                                ) {
                                    val text = StringBuilder()
                                    text.append(stringResource(id = R.string.weight_prefix))
                                    text.append(" ")
                                    text.append(exercise.value.exerciseWeight)
                                    text.append(stringResource(id = R.string.weight_suffix))
                                    Text(text = text.toString())
                                }
                                if (
                                    exercise.value.exerciseType == ExerciseType.Timed ||
                                    exercise.value.exerciseType == ExerciseType.TimedWeight ||
                                    exercise.value.exerciseType == ExerciseType.TimedDistance ||
                                    exercise.value.exerciseType == ExerciseType.TimedDistanceWeight
                                ) {
                                    val timeHour = exercise.value.exerciseDuration / 3600
                                    val timeMinute = exercise.value.exerciseDuration / 60 - (timeHour * 60)
                                    val timeSecond = exercise.value.exerciseDuration % 3600
                                    val text = StringBuilder()
                                    text.append(stringResource(id = R.string.time_prefix))
                                    if (timeHour > 0) {
                                        text.append(timeHour)
                                        text.append(stringResource(id = R.string.time_suffix_h))
                                        text.append(" ")
                                    }
                                    if (timeMinute > 0) {
                                        text.append(timeMinute)
                                        text.append(stringResource(id = R.string.time_suffix_m))
                                        text.append(" ")
                                    }
                                    if (timeSecond > 0) {
                                        text.append(timeSecond)
                                        text.append(stringResource(id = R.string.time_suffix_s))
                                        text.append(" ")
                                    }
                                    Text(text = text.toString())
                                }
                                if (
                                    exercise.value.exerciseType == ExerciseType.Distance ||
                                    exercise.value.exerciseType == ExerciseType.DistanceWeight ||
                                    exercise.value.exerciseType == ExerciseType.TimedDistance ||
                                    exercise.value.exerciseType == ExerciseType.TimedDistanceWeight
                                ) {
                                    val distanceKM = exercise.value.exerciseDistance / 1000
                                    val distanceM = exercise.value.exerciseDistance % 1000
                                    val text = StringBuilder()
                                    text.append(stringResource(id = R.string.distance_prefix))
                                    text.append(" ")
                                    if (distanceKM > 0) {
                                        text.append(distanceKM)
                                        text.append(stringResource(id = R.string.distance_suffix_km))
                                        text.append(" ")
                                    }
                                    if (distanceM > 0) {
                                        text.append(distanceM)
                                        text.append(stringResource(id = R.string.distance_suffix_metre))
                                    }
                                    Text(text = text.toString())
                                }
                            }
                            HorizontalDivider()
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Button(
                            onClick = {
                                userViewModel.currentWorkout.value = Pair(workout.key, workout.value)
                                userViewModel.currentClient.value = users.key
                                navController.navigate(Screen.EditWorkout.route)
                            }
                        ) {
                            Text(text = stringResource(id = R.string.button_edit))
                        }
                    }
                }
            }
            if (!isClient) {
                Text(
                    text = "${stringResource(id = R.string.homepage_todaysWorkout_workoutForPrefix)} ${username.value}",
                    style = typography.bodyMedium
                )
            }
        }
    }
}
package uk.ac.aber.dcs.cs39440.majorproject.createWorkout

import android.icu.text.DateFormat
import android.icu.text.SimpleDateFormat
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.google.firebase.Timestamp
import uk.ac.aber.dcs.cs39440.majorproject.R
import uk.ac.aber.dcs.cs39440.majorproject.TAG
import uk.ac.aber.dcs.cs39440.majorproject.calendar.PickDatePopup
import uk.ac.aber.dcs.cs39440.majorproject.calendar.PickTimePopup
import uk.ac.aber.dcs.cs39440.majorproject.calendar.TIME_FORMAT
import uk.ac.aber.dcs.cs39440.majorproject.calendar.updateStartDate
import uk.ac.aber.dcs.cs39440.majorproject.edit.EditExercisePopupUnsaved
import uk.ac.aber.dcs.cs39440.majorproject.components.Exercise
import uk.ac.aber.dcs.cs39440.majorproject.components.ExerciseType
import uk.ac.aber.dcs.cs39440.majorproject.components.UserMode
import uk.ac.aber.dcs.cs39440.majorproject.components.UserViewModel
import uk.ac.aber.dcs.cs39440.majorproject.firebase.addWorkout
import uk.ac.aber.dcs.cs39440.majorproject.firebase.getClientName
import uk.ac.aber.dcs.cs39440.majorproject.firebase.getLinks
import uk.ac.aber.dcs.cs39440.majorproject.components.Screen
import uk.ac.aber.dcs.cs39440.majorproject.navigation.TopLevelScaffold
import uk.ac.aber.dcs.cs39440.majorproject.ui.theme.typography
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateWorkoutScreen(
    navController: NavHostController,
    userViewModel: UserViewModel
) {
    TopLevelScaffold(
        navController = navController,
        userViewModel = userViewModel
    ) { innerPadding ->
        userViewModel.currentScreen.value = Screen.CreateWorkout
        Surface(
            modifier = Modifier
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                val workoutName = remember { mutableStateOf("") }
                val workoutDescription = remember { mutableStateOf("") }
                val startDate = remember { mutableStateOf(Timestamp(Date())) }
                val datePickerState = rememberDatePickerState(initialSelectedDateMillis = startDate.value.seconds * 1000)
                val initialDate = startDate.value.seconds * 1000
                val datePickerOpen = remember { mutableStateOf(false) }
                val timeHour = remember { mutableIntStateOf(((startDate.value.seconds % 86400) / 3600).toInt()) }
                val timeMinute = remember { mutableIntStateOf(0) }
                val timePickerOpen = remember { mutableStateOf(false) }
                val repeats = remember { mutableStateOf(false) }
                val exercisesUnsaved: MutableList<Exercise> = remember { mutableStateListOf() }
                val exerciseCreatorOpen = remember { mutableStateOf(false) }

                val formatter = SimpleDateFormat(TIME_FORMAT, Locale.getDefault())
                Box(
                    modifier = Modifier
                        .padding(all = 20.dp)
                        .fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // WORKOUT NAME //
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 5.dp),
                                text = stringResource(id = R.string.calendar_workoutView_name),
                                textAlign = TextAlign.End,
                                style = typography.titleMedium
                            )
                            TextField(
                                modifier = Modifier
                                    .weight(1.5f),
                                value = workoutName.value,
                                onValueChange = { value ->
                                    workoutName.value = value
                                }
                            )
                        }
                        HorizontalDivider()

                        // WORKOUT DESCRIPTION //
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 5.dp),
                                text = stringResource(id = R.string.calendar_workoutView_description),
                                textAlign = TextAlign.End,
                                style = typography.titleMedium
                            )
                            TextField(
                                modifier = Modifier
                                    .weight(1.5f),
                                minLines = 4,
                                value = workoutDescription.value,
                                onValueChange = { value ->
                                    workoutDescription.value = value
                                }
                            )
                        }
                        HorizontalDivider()

                        // DATE //
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(end = 5.dp),
                                        text = stringResource(id = R.string.calendar_workoutView_startDate),
                                        textAlign = TextAlign.End,
                                        style = typography.titleMedium
                                    )
                                    Text(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(horizontal = 10.dp),
                                        text = DateFormat.getDateInstance(1)
                                            .format(datePickerState.selectedDateMillis),

                                        style = typography.bodyMedium
                                    )
                                }
                                Button(
                                    modifier = Modifier
                                        .align(Alignment.CenterHorizontally),
                                    onClick = {
                                        datePickerOpen.value = true
                                    }
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.calendar_workoutView_button_changeDate)
                                    )
                                }
                            }

                            if (datePickerOpen.value) {
                                PickDatePopup(
                                    datePickerState = datePickerState,
                                    originalState = initialDate,
                                    open = datePickerOpen
                                )
                            }
                        }
                        HorizontalDivider()

                        // TIME //
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(end = 5.dp),
                                        text = stringResource(id = R.string.calendar_workoutView_startTime),
                                        textAlign = TextAlign.End,
                                        style = typography.titleMedium
                                    )
                                    Text(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(horizontal = 10.dp),
                                        text = formatter.format((((timeHour.intValue - 1) * 3600) + (timeMinute.intValue * 60)) * 1000),
                                        style = typography.bodyMedium
                                    )
                                }
                                Button(
                                    modifier = Modifier
                                        .align(Alignment.CenterHorizontally),
                                    onClick = {
                                        timePickerOpen.value = true
                                    }
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.calendar_workoutView_button_changeTime)
                                    )
                                }
                            }
                            if (timePickerOpen.value) {
                                PickTimePopup(
                                    timePickerHour = timeHour,
                                    timePickerMinute = timeMinute,
                                    open = timePickerOpen
                                )
                            }
                        }
                        HorizontalDivider()

                        // REPEATS //
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                modifier = Modifier
                                    .weight(1f),
                                text = stringResource(id = R.string.calendar_workoutView_repeats),
                                textAlign = TextAlign.End,
                                style = typography.titleMedium
                            )
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                            ) {
                                Switch(
                                    modifier = Modifier
                                        .align(Alignment.Center),
                                    checked = repeats.value,
                                    onCheckedChange = {
                                        repeats.value = !repeats.value
                                    }
                                )
                            }
                        }
                        HorizontalDivider()

                        // SHOW EXERCISES & EXERCISE BUTTON //
                        if (exercisesUnsaved.isNotEmpty()) {
                            Text(text = stringResource(id = R.string.createWorkout_exercises))
                            exercisesUnsaved.forEachIndexed { index, exercise ->

                                val exerciseExtended = remember { mutableStateOf(false) }

                                Card(
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .fillMaxWidth()
                                        .clickable {
                                            exerciseExtended.value = !exerciseExtended.value
                                        }
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .padding(10.dp)
                                            .fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(text = exercise.exerciseName)
                                        AnimatedVisibility(
                                            visible = exerciseExtended.value
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                            ) {
                                                if (
                                                    exercise.exerciseType == ExerciseType.Weight
                                                    || exercise.exerciseType == ExerciseType.TimedWeight
                                                    || exercise.exerciseType == ExerciseType.TimedDistanceWeight
                                                    || exercise.exerciseType == ExerciseType.DistanceWeight
                                                ) {
                                                    Text(
                                                        text = "${stringResource(id = R.string.weight_prefix)}${exercise.exerciseWeight}${
                                                            stringResource(
                                                                id = R.string.createWorkout_createExercise_weightSuffixKG
                                                            )
                                                        }"
                                                    )
                                                }
                                                if (
                                                    exercise.exerciseType == ExerciseType.Distance
                                                    || exercise.exerciseType == ExerciseType.DistanceWeight
                                                    || exercise.exerciseType == ExerciseType.TimedDistanceWeight
                                                    || exercise.exerciseType == ExerciseType.TimedDistance
                                                ) {
                                                    val distanceKM =
                                                        exercise.exerciseDistance / 1000
                                                    val distanceM = exercise.exerciseDistance % 1000
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
                                                if (
                                                    exercise.exerciseType == ExerciseType.Timed
                                                    || exercise.exerciseType == ExerciseType.TimedWeight
                                                    || exercise.exerciseType == ExerciseType.TimedDistanceWeight
                                                    || exercise.exerciseType == ExerciseType.TimedDistance
                                                ) {
                                                    val exTimeHour =
                                                        exercise.exerciseDuration / 3600
                                                    val exTimeMinute =
                                                        exercise.exerciseDuration / 60 - (exTimeHour * 60)
                                                    val exTimeSecond =
                                                        exercise.exerciseDuration % 3600
                                                    val text = StringBuilder()
                                                    text.append(stringResource(id = R.string.time_prefix))
                                                    if (exTimeHour > 0) {
                                                        text.append(exTimeHour)
                                                        text.append(stringResource(id = R.string.time_suffix_h))
                                                        text.append(" ")
                                                    }
                                                    if (exTimeMinute > 0) {
                                                        text.append(exTimeMinute)
                                                        text.append(stringResource(id = R.string.time_suffix_m))
                                                        text.append(" ")
                                                    }
                                                    if (exTimeSecond > 0) {
                                                        text.append(exTimeSecond)
                                                        text.append(stringResource(id = R.string.time_suffix_s))
                                                        text.append(" ")
                                                    }
                                                    Text(text = text.toString())
                                                }
                                                val editVisible = remember { mutableStateOf(false) }
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceEvenly
                                                ) {
                                                    Button(
                                                        onClick = {
                                                            editVisible.value = true
                                                        }
                                                    ) {
                                                        Text(text = stringResource(id = R.string.button_edit))
                                                    }
                                                    Button(
                                                        onClick = {
                                                            exercisesUnsaved.removeAt(index)
                                                        }
                                                    ) {
                                                        Text(text = stringResource(id = R.string.button_delete))
                                                    }
                                                }
                                                AnimatedVisibility(visible = editVisible.value) {
                                                    EditExercisePopupUnsaved(
                                                        exerciseIndex = index,
                                                        exerciseList = exercisesUnsaved,
                                                        visible = editVisible
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            Text(text = stringResource(id = R.string.createWorkout_noExercises))
                        }
                        Button(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally),
                            onClick = {
                                exerciseCreatorOpen.value = true
                            }
                        ) {
                            Text(
                                text = stringResource(id = R.string.createWorkout_addNewExercise)
                            )
                        }
                        if (exerciseCreatorOpen.value) {
                            CreateExercisePopup(
                                exerciseList = exercisesUnsaved,
                                visible = exerciseCreatorOpen
                            )
                        }

                        // FINAL BUTTONS //
                        if (exercisesUnsaved.isEmpty()) {
                            Text(
                                text = stringResource(id = R.string.createWorkout_cannotCreateWorkoutWithoutExercise)
                            )
                        }
                        if (workoutName.value == "") {
                            Text(
                                text = stringResource(id = R.string.createWorkout_cannotCreateWorkoutWithoutName)
                            )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            if (userViewModel.userMode.value == UserMode.Client) {
                                Button(
                                    enabled =  workoutName.value != ""
                                            && workoutDescription.value != ""
                                            && exercisesUnsaved.isNotEmpty(),
                                    onClick = {
                                        updateStartDate(
                                            hour = timeHour.intValue,
                                            minute = timeMinute.intValue,
                                            date = datePickerState.selectedDateMillis,
                                            startDate = startDate
                                        )
                                        addWorkout(
                                            workoutName = workoutName.value,
                                            workoutDescription = workoutDescription.value,
                                            startDate = startDate.value,
                                            repeats = repeats.value,
                                            exercises = exercisesUnsaved
                                        )
                                        navController.navigateUp()
                                    }
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.button_save)
                                    )
                                }
                            } else {
                                val assignVisible = remember { mutableStateOf(false) }
                                val listOfClients = remember { mutableStateListOf<Pair<String, String>>() }
                                LaunchedEffect(key1 = null) {
                                    getLinks(listOfClients, isFromClient = userViewModel.userMode.value == UserMode.Client)
                                }
                                AnimatedVisibility(visible = assignVisible.value) {
                                    Dialog(
                                        onDismissRequest = { assignVisible.value = false }
                                    ) {
                                        Card {
                                            Column(
                                                modifier = Modifier
                                                    .padding(10.dp)
                                            ) {
                                                Text(
                                                    text = stringResource(id = R.string.createWorkout_assignToClient_title),
                                                    style = typography.headlineLarge
                                                )
                                                if (listOfClients.isNotEmpty()) {
                                                    listOfClients.forEach { client ->
                                                        val clientName = remember { mutableStateOf("") }
                                                        LaunchedEffect(key1 = null) {
                                                            getClientName(clientName, client.first)
                                                        }
                                                        Log.i(TAG, "client name is ${clientName.value}")
                                                        HorizontalDivider()
                                                        Column(
                                                            modifier = Modifier
                                                                .verticalScroll(rememberScrollState())
                                                                .clickable {
                                                                    updateStartDate(
                                                                        hour = timeHour.intValue,
                                                                        minute = timeMinute.intValue,
                                                                        date = datePickerState.selectedDateMillis,
                                                                        startDate = startDate
                                                                    )
                                                                    addWorkout(
                                                                        workoutName = workoutName.value,
                                                                        workoutDescription = workoutDescription.value,
                                                                        startDate = startDate.value,
                                                                        repeats = repeats.value,
                                                                        exercises = exercisesUnsaved,
                                                                        clientID = client.first
                                                                    )
                                                                    assignVisible.value = false
                                                                    navController.navigateUp()
                                                                }
                                                        ) {
                                                            Text(
                                                                text = clientName.value,
                                                                style = typography.headlineSmall
                                                            )
                                                        }
                                                    }
                                                    HorizontalDivider()
                                                } else {
                                                    Text(
                                                        text = stringResource(id = R.string.createWorkout_assignToClient_noClients)
                                                    )
                                                }
                                                Button(
                                                    onClick = {
                                                        assignVisible.value = false
                                                    }
                                                ) {
                                                    Text(
                                                        text = stringResource(id = R.string.button_cancel)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                                Button(
                                    enabled =  workoutName.value != ""
                                            && workoutDescription.value != ""
                                            && exercisesUnsaved.isNotEmpty(),
                                    onClick = {
                                        assignVisible.value = true
                                    }
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.button_saveToTrainee)
                                    )
                                }
                            }
                            Button(
                                onClick = {
                                    navController.navigateUp()
                                }
                            ) {
                                Text(
                                    text = stringResource(id = R.string.button_cancel)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
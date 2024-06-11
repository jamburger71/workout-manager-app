package uk.ac.aber.dcs.cs39440.majorproject.calendar

import android.icu.text.DateFormat
import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.google.firebase.Timestamp
import uk.ac.aber.dcs.cs39440.majorproject.R
import uk.ac.aber.dcs.cs39440.majorproject.components.Workout
import uk.ac.aber.dcs.cs39440.majorproject.firebase.editWorkout
import uk.ac.aber.dcs.cs39440.majorproject.ui.theme.typography
import java.util.Date
import java.util.Locale

const val TIME_FORMAT = "kk:mm"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutViewPopup(
    workout: Pair<String, Workout>,
    workoutList: SnapshotStateMap<String, Workout>,
    visible: MutableState<Boolean>
) {
    val workoutName = remember { mutableStateOf(workout.second.workoutName) }
    val workoutDescription = remember { mutableStateOf(workout.second.workoutDescription) }
    val startDate = remember { mutableStateOf(workout.second.startDate) }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = startDate.value.seconds*1000)
    val initialDate = startDate.value.seconds*1000
    val datePickerOpen = remember { mutableStateOf(false) }
    val timeHour = remember{ mutableIntStateOf(((startDate.value.seconds % 86400)/3600).toInt()) }
    val timeMinute = remember{ mutableIntStateOf(0) }
    val timePickerOpen = remember{ mutableStateOf(false) }
    val repeats = remember { mutableStateOf(workout.second.repeats) }

    val formatter = SimpleDateFormat(TIME_FORMAT, Locale.getDefault())

    Dialog(
        onDismissRequest = { visible.value = false }
    ) {
        Card{
            Box(
                modifier = Modifier
                    .padding(all = 20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        modifier = Modifier
                            .padding(bottom = 20.dp),
                        text = stringResource(id = R.string.calendar_workoutView_title),
                        textAlign = TextAlign.Center,
                        style = typography.titleLarge
                    )
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
                                    text = DateFormat.getDateInstance(1).format(datePickerState.selectedDateMillis),

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
                                    text = formatter.format((((timeHour.intValue-1)*3600) + (timeMinute.intValue*60)) * 1000),
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

                    // FINAL BUTTONS //
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                updateStartDate(
                                    hour = timeHour.intValue,
                                    minute = timeMinute.intValue,
                                    date = datePickerState.selectedDateMillis,
                                    startDate = startDate
                                )
                                workout.second.workoutName = workoutName.value
                                workout.second.workoutDescription = workoutDescription.value
                                workout.second.startDate = startDate.value
                                editWorkout(
                                    workout,
                                    savedExercises = workout.second.exercises,
                                    unsavedExercises = mutableListOf(),
                                    deletedExercise = mutableListOf()
                                )
                                workoutList[workout.first]?.workoutName = ""
                                workoutList[workout.first]?.workoutName = workoutName.value
                                workoutList[workout.first] = workout.second
                                visible.value = false
                            }
                        ) {
                            Text(
                                text = stringResource(id = R.string.button_save)
                            )
                        }
                        Button(
                            onClick = {
                                visible.value = false
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

fun updateStartDate(
    hour: Int,
    minute: Int,
    date: Long?,
    startDate: MutableState<Timestamp>
) {
    if (date != null) {
        val firebaseDate = Date((((hour * 3600*1000) + (minute * 60*1000)) + date))
        startDate.value = Timestamp(firebaseDate)
    }
}
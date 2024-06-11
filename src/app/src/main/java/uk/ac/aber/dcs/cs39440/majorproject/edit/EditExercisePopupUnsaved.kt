package uk.ac.aber.dcs.cs39440.majorproject.edit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import uk.ac.aber.dcs.cs39440.majorproject.R
import uk.ac.aber.dcs.cs39440.majorproject.components.Exercise
import uk.ac.aber.dcs.cs39440.majorproject.components.ExerciseType
import uk.ac.aber.dcs.cs39440.majorproject.ui.theme.typography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditExercisePopupUnsaved(
    exerciseIndex: Int,
    visible: MutableState<Boolean>,
    exerciseList: MutableList<Exercise>
) {
    val exercise = exerciseList[exerciseIndex]
    val exerciseName = remember { mutableStateOf(exercise.exerciseName) }
    val exerciseType = remember { mutableStateOf(exercise.exerciseType) }
    val exerciseDurationS =
        remember { mutableStateOf((exercise.exerciseDuration % 3600).toString()) }
    val exerciseDurationM =
        remember { mutableStateOf((exercise.exerciseDuration / 60 - (exercise.exerciseDuration / 3600) * 60).toString()) }
    val exerciseDurationH =
        remember { mutableStateOf((exercise.exerciseDuration / 3600).toString()) }
    val exerciseWeight = remember { mutableStateOf(exercise.exerciseWeight.toString()) }
    val exerciseDistance = remember { mutableStateOf(exercise.exerciseDistance.toString()) }
    val exerciseDistanceType = remember { mutableStateOf("m") }

    val distanceShown =
        remember { mutableStateOf(exerciseType.value == ExerciseType.Distance || exerciseType.value == ExerciseType.DistanceWeight || exerciseType.value == ExerciseType.TimedDistance || exerciseType.value == ExerciseType.TimedDistanceWeight) }
    val weightShown =
        remember { mutableStateOf(exerciseType.value == ExerciseType.Weight || exerciseType.value == ExerciseType.DistanceWeight || exerciseType.value == ExerciseType.TimedWeight || exerciseType.value == ExerciseType.TimedDistanceWeight) }
    val timeShown =
        remember { mutableStateOf(exerciseType.value == ExerciseType.Timed || exerciseType.value == ExerciseType.TimedWeight || exerciseType.value == ExerciseType.TimedDistance || exerciseType.value == ExerciseType.TimedDistanceWeight) }

    exerciseType.value =
        if (distanceShown.value) {
            if (weightShown.value) {
                if (timeShown.value) {
                    ExerciseType.TimedDistanceWeight
                } else {
                    ExerciseType.DistanceWeight
                }
            } else {
                if (timeShown.value) {
                    ExerciseType.TimedDistance
                } else {
                    ExerciseType.Distance
                }
            }
        } else {
            if (weightShown.value) {
                if (timeShown.value) {
                    ExerciseType.TimedWeight
                } else {
                    ExerciseType.Weight
                }
            } else {
                if (timeShown.value) {
                    ExerciseType.Timed
                } else {
                    ExerciseType.None
                }
            }
        }
    Dialog(
        onDismissRequest = {
            visible.value = false
        }
    ) {
        Card {
            Column(
                modifier = Modifier
                    .padding(10.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.createWorkout_createExercise_title),
                    style = typography.titleLarge
                )
                HorizontalDivider()

                // NAME //
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        modifier = Modifier
                            .weight(1f),
                        text = stringResource(id = R.string.createWorkout_createExercise_name)
                    )
                    TextField(
                        modifier = Modifier
                            .weight(1f),
                        value = exerciseName.value,
                        onValueChange = {
                            exerciseName.value = it
                        }
                    )
                }
                HorizontalDivider()

                // TIME //
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        modifier = Modifier
                            .weight(1f),
                        text = stringResource(id = R.string.createWorkout_createExercise_time)
                    )
                    Switch(
                        modifier = Modifier
                            .weight(1f),
                        checked = timeShown.value,
                        onCheckedChange = {
                            timeShown.value = !timeShown.value
                        }
                    )
                }
                AnimatedVisibility(
                    visible = timeShown.value
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                        ) {
                            Text(
                                modifier = Modifier
                                    .weight(1f),
                                text = stringResource(id = R.string.hours)
                            )
                            Text(
                                modifier = Modifier
                                    .weight(1f),
                                text = stringResource(id = R.string.minutes)
                            )
                            Text(
                                modifier = Modifier
                                    .weight(1f),
                                text = stringResource(id = R.string.seconds)
                            )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            TextField(
                                modifier = Modifier
                                    .weight(1f),
                                value = exerciseDurationH.value,
                                onValueChange = { value ->
                                    exerciseDurationH.value = value.replace(Regex("[^0-9]"), "")
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                            TextField(
                                modifier = Modifier
                                    .weight(1f),
                                value = exerciseDurationM.value,
                                onValueChange = { value ->
                                    exerciseDurationM.value = value.replace(Regex("[^0-9]"), "")
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                            TextField(
                                modifier = Modifier
                                    .weight(1f),
                                value = exerciseDurationS.value,
                                onValueChange = { value ->
                                    exerciseDurationS.value = value.replace(Regex("[^0-9]"), "")
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                        }
                    }
                }

                // DISTANCE //
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        modifier = Modifier
                            .weight(1f),
                        text = stringResource(id = R.string.createWorkout_createExercise_distance)
                    )
                    Switch(
                        modifier = Modifier
                            .weight(1f),
                        checked = distanceShown.value,
                        onCheckedChange = {
                            distanceShown.value = !distanceShown.value
                        }
                    )
                }
                AnimatedVisibility(
                    visible = distanceShown.value
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        val dropDownOpen = remember { mutableStateOf(false) }
                        TextField(
                            modifier = Modifier
                                .weight(1f),
                            value = exerciseDistance.value,
                            onValueChange = { value ->
                                exerciseDistance.value = value.replace(Regex("[^0-9]"), "")
                            },
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                        )
                        ExposedDropdownMenuBox(
                            modifier = Modifier
                                .weight(1f),
                            expanded = dropDownOpen.value,
                            onExpandedChange = {
                                dropDownOpen.value = !dropDownOpen.value
                            }
                        ) {
                            TextField(
                                value = exerciseDistanceType.value,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        expanded = dropDownOpen.value
                                    )
                                },
                                modifier = Modifier
                                    .menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = dropDownOpen.value,
                                onDismissRequest = { dropDownOpen.value = false }
                            ) {
                                val textMetres = stringResource(id = R.string.distance_metres)
                                DropdownMenuItem(
                                    text = { Text(text = textMetres) },
                                    onClick = {
                                        exerciseDistanceType.value = textMetres
                                        dropDownOpen.value = false
                                    }
                                )
                                val textKilometres =
                                    stringResource(id = R.string.distance_kilometres)
                                DropdownMenuItem(
                                    text = { Text(text = textKilometres) },
                                    onClick = {
                                        exerciseDistanceType.value = textKilometres
                                        dropDownOpen.value = false
                                    }
                                )
                            }
                        }
                    }

                }

                // WEIGHT //
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        modifier = Modifier
                            .weight(1f),
                        text = stringResource(id = R.string.createWorkout_createExercise_weight)
                    )
                    Switch(
                        modifier = Modifier
                            .weight(1f),
                        checked = weightShown.value,
                        onCheckedChange = {
                            weightShown.value = !weightShown.value
                        }
                    )
                }
                AnimatedVisibility(
                    visible = weightShown.value
                ) {
                    TextField(
                        value = exerciseWeight.value,
                        onValueChange = { value ->
                            exerciseWeight.value = value.replace(Regex("[^0-9]"), "")
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                    )
                }

                // FINAL BUTTONS //
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            visible.value = false
                        }
                    ) {
                        Text(text = stringResource(id = R.string.button_cancel))
                    }
                    Button(
                        enabled = (exerciseName.value != "" && exerciseType.value != ExerciseType.None),
                        onClick = {
                            val duration: Int =
                            (if (exerciseDurationH.value != "") exerciseDurationH.value.toInt() else 0) * 3600 +
                                    (if (exerciseDurationM.value != "") exerciseDurationM.value.toInt() else 0) * 60 +
                                    (if (exerciseDurationS.value != "") exerciseDurationS.value.toInt() else 0)
                            exerciseList[exerciseIndex] = Exercise(
                                exerciseName = exerciseName.value,
                                exerciseType = exerciseType.value,
                                exerciseDuration = duration,
                                exerciseDistance = if (exerciseDistance.value != "") if (exerciseDistanceType.value == "m") exerciseDistance.value.toInt() * 1000 else exerciseDistance.value.toInt() else 0,
                                exerciseWeight = if (exerciseWeight.value != "") exerciseWeight.value.toInt() else 0
                            )
                            exerciseList[exerciseIndex] = exercise
                            visible.value = false
                        }
                    ) {
                        Text(text = stringResource(id = R.string.button_confirm))
                    }
                }
            }
        }
    }
}
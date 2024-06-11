package uk.ac.aber.dcs.cs39440.majorproject.calendar

import android.icu.text.SimpleDateFormat
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uk.ac.aber.dcs.cs39440.majorproject.R
import uk.ac.aber.dcs.cs39440.majorproject.components.Workout
import uk.ac.aber.dcs.cs39440.majorproject.ui.theme.typography
import java.util.Locale

@Composable
fun WorkoutCard(
    workout: Pair<String, Workout>,
    workouts: SnapshotStateMap<String, Workout>
) {
    val extended = remember { mutableStateOf(false) }
    val popupVisible = remember { mutableStateOf(false) }
    val deletePopupVisible = remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .shadow(5.dp)
            .clickable {
                extended.value = !extended.value
            },
        colors = CardColors(
            containerColor = MaterialTheme.colorScheme.inversePrimary,
            contentColor = contentColorFor(backgroundColor = MaterialTheme.colorScheme.inversePrimary),
            disabledContainerColor = MaterialTheme.colorScheme.error,
            disabledContentColor = contentColorFor(backgroundColor = MaterialTheme.colorScheme.error)
        )
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
        ) {
            Text(
                text = workout.second.workoutName,
                style = typography.titleLarge
            )
            AnimatedVisibility(visible = extended.value) {
                Column {
                    Text(
                        text = workout.second.workoutDescription,
                        style = typography.bodyMedium
                    )
                    Text(
                        text = "${stringResource(id = R.string.calendar_workout_startTimePrefix)} ${SimpleDateFormat(
                            TIME_FORMAT, Locale.getDefault()).format(workout.second.startDate.seconds*1000)}"
                    )
                    Text(
                        text = if (workout.second.exercises.isNotEmpty()) {
                            stringResource(id = R.string.calendar_workout_exercises_title)
                        } else {
                            stringResource(id = R.string.calendar_workout_exercises_noExercises)
                        }
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = { popupVisible.value = true }
                        ) {
                            Text(text = stringResource(id = R.string.calendar_workout_edit))
                        }
                        Button(
                            onClick = { deletePopupVisible.value = true }
                        ) {
                            Text(text = stringResource(id = R.string.calendar_workout_delete))
                        }
                    }
                }
            }
        }
    }
    if (popupVisible.value) {
        WorkoutViewPopup(workout = workout, workoutList = workouts, visible = popupVisible)
    }
    if (deletePopupVisible.value) {
        DeleteWorkoutPopup(workout = workout, workoutList = workouts, visible = deletePopupVisible)
    }
}
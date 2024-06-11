package uk.ac.aber.dcs.cs39440.majorproject.calendar

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.res.stringResource
import uk.ac.aber.dcs.cs39440.majorproject.R
import uk.ac.aber.dcs.cs39440.majorproject.components.Workout
import uk.ac.aber.dcs.cs39440.majorproject.firebase.deleteWorkout
import uk.ac.aber.dcs.cs39440.majorproject.ui.theme.typography

@Composable
fun DeleteWorkoutPopup(
    workout: Pair<String, Workout>,
    workoutList: SnapshotStateMap<String, Workout>,
    visible: MutableState<Boolean>
) {
    AlertDialog(
        onDismissRequest = { visible.value = false },
        confirmButton = {
            Button(
                onClick = {
                    deleteWorkout(workout.first)
                    workoutList.remove(workout.first)
                    visible.value = false
                }
            ) {
                Text(text = stringResource(id = R.string.button_confirm))
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    visible.value = false
                }
            ) {
                Text(text = stringResource(id = R.string.button_cancel))
            }
        },
        title = {
            Text(
                text = "${stringResource(id = R.string.calendar_workout_delete_areYouSure_title)} ${workout.second.workoutName}?",
                style = typography.headlineMedium
            )
        },
        text = {
            Text(
                text = stringResource(id = R.string.calendar_workout_delete_areYouSure_content),
                style = typography.titleMedium
            )
        }
    )
}
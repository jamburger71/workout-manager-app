package uk.ac.aber.dcs.cs39440.majorproject.calendar

import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.Timestamp
import uk.ac.aber.dcs.cs39440.majorproject.R
import uk.ac.aber.dcs.cs39440.majorproject.components.UserViewModel
import uk.ac.aber.dcs.cs39440.majorproject.components.Workout
import uk.ac.aber.dcs.cs39440.majorproject.firebase.getAllWorkouts
import uk.ac.aber.dcs.cs39440.majorproject.components.Screen
import uk.ac.aber.dcs.cs39440.majorproject.navigation.TopLevelScaffold
import uk.ac.aber.dcs.cs39440.majorproject.ui.theme.typography
import java.util.Date
import java.util.Locale

const val SECONDS_TO_DAYS = 86400
const val CALENDAR_FORMAT = "eeee, MMMM dd"

@Composable
fun CalendarScreen(
    navController: NavHostController,
    userViewModel: UserViewModel
) {
    TopLevelScaffold(
        navController = navController,
        userViewModel = userViewModel
    ) { innerPadding ->
        userViewModel.currentScreen.value = Screen.Calendar
        val today = (Timestamp.now().seconds / SECONDS_TO_DAYS)
        val workouts = remember { mutableStateMapOf<String, Workout>() }
        LaunchedEffect(
            key1 = userViewModel,
            key2 = workouts.entries
        ) {
            getAllWorkouts(workouts)
        }
        val formatter = SimpleDateFormat(CALENDAR_FORMAT, Locale.getDefault())

        val workoutMatrix: MutableMap<Int, ArrayList<Pair<String, Workout>>> = mutableMapOf()
        for (range in 0..13) {
            workoutMatrix[range] = ArrayList()
        }
        workouts.forEach { workout ->
            val workoutPair: Pair<String, Workout> = Pair(workout.key, workout.value)
            var dayOfWorkout = workout.value.startDate.seconds / SECONDS_TO_DAYS
            val daysUntilWorkout = dayOfWorkout - today
            if (workout.value.repeats) {
                while (dayOfWorkout < 0) {
                    dayOfWorkout += 7
                }
                workoutMatrix[daysUntilWorkout.toInt()]?.add(workoutPair)
                workoutMatrix[daysUntilWorkout.toInt() + 7]?.add(workoutPair)
            } else {
                if (daysUntilWorkout in 0 .. 13) {
                    workoutMatrix[daysUntilWorkout.toInt()]?.add(workoutPair)
                }
            }
        }
        Surface(
            modifier = Modifier
                .padding(innerPadding)
        ) {
            val scrollState = rememberScrollState()
            Column {
                Column(
                    modifier = Modifier
                        .verticalScroll(state = scrollState)
                        .weight(10f)
                ) {
                    workoutMatrix.forEach { (index, day) ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                        ) {
                            val dateOfDay = Date(((index * SECONDS_TO_DAYS) + (today * SECONDS_TO_DAYS))*1000)
                            Text(
                                text = formatter.format(dateOfDay),
                                style = typography.headlineMedium
                            )
                            if (day.isNotEmpty()) {
                                day.forEach { workout ->
                                    WorkoutCard(workout, workouts)
                                }
                            } else {
                                Text(text = stringResource(id = R.string.calendar_dayContent_noWorkoutsToday))
                            }
                        }
                    }
                }
                Button(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                    onClick = { navController.navigate(Screen.CreateWorkout.route)}
                ) {
                    Text(text = stringResource(id = R.string.calendar_addNewWorkout))
                }
            }
        }
    }
}
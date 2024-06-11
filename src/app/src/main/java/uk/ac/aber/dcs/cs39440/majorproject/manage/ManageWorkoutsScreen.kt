package uk.ac.aber.dcs.cs39440.majorproject.manage

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import uk.ac.aber.dcs.cs39440.majorproject.components.UserViewModel
import uk.ac.aber.dcs.cs39440.majorproject.components.Workout
import uk.ac.aber.dcs.cs39440.majorproject.firebase.getAllWorkouts
import uk.ac.aber.dcs.cs39440.majorproject.components.Screen
import uk.ac.aber.dcs.cs39440.majorproject.navigation.TopLevelScaffold
import uk.ac.aber.dcs.cs39440.majorproject.ui.theme.typography

@Composable
fun ManageWorkoutsScreen(
    navController: NavHostController,
    userViewModel: UserViewModel
) {
    userViewModel.currentScreen.value = Screen.ManageWorkouts

    val workouts: SnapshotStateMap<String, Workout> = remember { mutableStateMapOf() }
    LaunchedEffect(key1 = userViewModel) {
        getAllWorkouts(workouts)
    }

    TopLevelScaffold(
        navController = navController,
        userViewModel = userViewModel
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
            ) {
                if (workouts.isNotEmpty()) {
                    workouts.entries.forEach { workout ->
                        Card {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = workout.value.workoutName,
                                    style = typography.titleLarge
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
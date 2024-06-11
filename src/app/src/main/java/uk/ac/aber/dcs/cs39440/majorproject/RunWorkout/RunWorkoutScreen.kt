package uk.ac.aber.dcs.cs39440.majorproject.RunWorkout

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import uk.ac.aber.dcs.cs39440.majorproject.components.UserViewModel
import uk.ac.aber.dcs.cs39440.majorproject.components.Screen
import uk.ac.aber.dcs.cs39440.majorproject.navigation.TopLevelScaffold

@Composable
fun RunWorkoutScreen(
    navController: NavHostController,
    userViewModel: UserViewModel
) {
    TopLevelScaffold(
        navController = navController,
        userViewModel = userViewModel
    ) {    paddingValues ->
        userViewModel.currentScreen.value = Screen.RunWorkout

        Surface(
            modifier = Modifier
                .padding(paddingValues = paddingValues)
        ) {

        }
    }
}
package uk.ac.aber.dcs.cs39440.majorproject.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import uk.ac.aber.dcs.cs39440.majorproject.components.UserMode
import uk.ac.aber.dcs.cs39440.majorproject.components.UserViewModel
import uk.ac.aber.dcs.cs39440.majorproject.firebase.getClientSetup
import uk.ac.aber.dcs.cs39440.majorproject.firebase.getTrainerSetup
import uk.ac.aber.dcs.cs39440.majorproject.components.Screen
import uk.ac.aber.dcs.cs39440.majorproject.navigation.TopLevelScaffold

@Composable
fun HomeScreen(
    navController: NavHostController,
    userViewModel: UserViewModel
) {
    userViewModel.currentScreen.value = Screen.Home
    TopLevelScaffold(
        navController = navController,
        userViewModel = userViewModel
    ) { innerPadding ->

        val setup = remember { mutableStateOf(true) }
        val name = remember { mutableStateOf("") }
        var isClient = userViewModel.userMode.value == UserMode.Client

        LaunchedEffect(key1 = userViewModel.userMode.value) {
            isClient = userViewModel.userMode.value == UserMode.Client
            if (isClient) {
                getClientSetup(setup, name)
            }
            else {
                getTrainerSetup(setup, name)
            }
        }

        Surface(
            modifier = Modifier
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                WelcomeCard(setup = setup, name = name, isClient = isClient)
                SetupCard(setup = setup,name = name, isClient = isClient)
                LinkRequestsCard(isClient = isClient)
                TodaysWorkoutsCard(navController, userViewModel, isClient)
            }
        }
    }
}
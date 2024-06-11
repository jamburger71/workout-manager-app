package uk.ac.aber.dcs.cs39440.majorproject.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import uk.ac.aber.dcs.cs39440.majorproject.R
import uk.ac.aber.dcs.cs39440.majorproject.components.NavIcon
import uk.ac.aber.dcs.cs39440.majorproject.components.Screen
import uk.ac.aber.dcs.cs39440.majorproject.components.UserViewModel

@Composable
fun TopLevelScaffold(
    navController: NavHostController,
    userViewModel: UserViewModel,
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
    showBottomBar: Boolean = true,
    pageContent: @Composable (innerPadding: PaddingValues) -> Unit = {},
) {
    val context = LocalContext.current
    val icons = mapOf(
        Screen.Home to NavIcon(
            filledIcon = Icons.Filled.Home,
            outlineIcon = Icons.Outlined.Home,
            label = stringResource(id = R.string.homepage_navigationButton_text),
            titleText = stringResource(id = R.string.homepage_title)
        ),
        Screen.Chat to NavIcon(
            filledIcon = Icons.Filled.Email,
            outlineIcon = Icons.Outlined.Email,
            label = stringResource(id = R.string.chat_navigationButton_text),
            titleText = stringResource(id = R.string.chat_title)
        ),
        Screen.Calendar to NavIcon(
            filledIcon = Icons.Filled.DateRange,
            outlineIcon = Icons.Outlined.DateRange,
            label = stringResource(id = R.string.calendar_navigationButton_text),
            titleText = stringResource(id = R.string.calendar_title)
        ),
        Screen.RunWorkout to NavIcon(
            titleText = stringResource(id = R.string.workout_title)
        ),
        Screen.Settings to NavIcon(
            titleText = stringResource(id = R.string.settings_title)
        ),
        Screen.CreateWorkout to NavIcon(
            titleText = stringResource(id = R.string.createWorkout_title)
        ),
        Screen.ConnectWithOther to NavIcon(
            titleText = stringResource(id = R.string.connectWithOther_title)
        ),
        Screen.ManageWorkouts to NavIcon(
            titleText = stringResource(id = R.string.manageWorkouts_title)
        ),
        Screen.ManageClients to NavIcon(
            titleText = stringResource(id = R.string.manageClients_title)
        ),
        Screen.EditWorkout to NavIcon(
            titleText = stringResource(id = R.string.editWorkout_title)
        )
    )
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    NavDrawer(
        drawerState = drawerState,
        context = context,
        navController = navController,
        userViewModel = userViewModel
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize(),
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            topBar = {
                TopTitleBar(navController, icons, drawerState)
            },
            bottomBar = {
                if (showBottomBar) BottomNavBar(navController, icons)
            },
            content = { innerPadding ->
                pageContent(innerPadding)
            }
        )
    }
}


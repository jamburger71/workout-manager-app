package uk.ac.aber.dcs.cs39440.majorproject.components

/**
 * Wraps as objects, singletons for each screen used in
 * navigation. Each has a unique route.
 * @param route To pass through the route string
 * @author Jay Kirkham
 */
sealed class Screen(val route: String) {
    data object Home: Screen("home")
    data object Settings: Screen("settings")
    data object Calendar: Screen("exercises")
    data object Chat: Screen("chat")
    data object RunWorkout: Screen("runWorkout")
    data object ChooseUserType: Screen("chooseUserType")
    data object CreateWorkout: Screen("createWorkout")
    data object ConnectWithOther: Screen("connectWithOther")
    data object ManageWorkouts: Screen("manageWorkouts")
    data object ManageClients: Screen("manageClients")
    data object EditWorkout: Screen("editWorkout")
}

/**
 * List of top-level screens provided as a convenience.
 */
val topLevelScreens = listOf(
    Screen.Home,
    Screen.Calendar,
    Screen.Chat
)
val allScreens = topLevelScreens.plus (
    listOf(
        Screen.Settings,
        Screen.RunWorkout,
        Screen.ChooseUserType,
        Screen.CreateWorkout,
        Screen.ConnectWithOther,
        Screen.ManageWorkouts,
        Screen.ManageClients,
        Screen.EditWorkout
    )
)
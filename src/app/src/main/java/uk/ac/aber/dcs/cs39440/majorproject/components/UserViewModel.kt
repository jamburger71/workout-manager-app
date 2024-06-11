package uk.ac.aber.dcs.cs39440.majorproject.components

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class UserViewModel: ViewModel(

) {
    val userMode = mutableStateOf(UserMode.None)

    val currentScreen: MutableState<Screen> = mutableStateOf(Screen.ChooseUserType)

    val darkTheme = mutableStateOf(ThemeType.Light)

    val currentWorkout: MutableState<Pair<String, Workout>?> = mutableStateOf(null)

    val currentClient: MutableState<String> = mutableStateOf("")
}
package uk.ac.aber.dcs.cs39440.majorproject

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import uk.ac.aber.dcs.cs39440.majorproject.calendar.CalendarScreen
import uk.ac.aber.dcs.cs39440.majorproject.chat.ChatScreen
import uk.ac.aber.dcs.cs39440.majorproject.chooseUserType.ChooseUserTypeScreen
import uk.ac.aber.dcs.cs39440.majorproject.components.UserViewModel
import uk.ac.aber.dcs.cs39440.majorproject.connectWithOther.ConnectWithOther
import uk.ac.aber.dcs.cs39440.majorproject.createWorkout.CreateWorkoutScreen
import uk.ac.aber.dcs.cs39440.majorproject.edit.EditWorkoutScreen
import uk.ac.aber.dcs.cs39440.majorproject.home.HomeScreen
import uk.ac.aber.dcs.cs39440.majorproject.manage.ManageWorkoutsScreen
import uk.ac.aber.dcs.cs39440.majorproject.components.Screen
import uk.ac.aber.dcs.cs39440.majorproject.settings.SettingsScreen
import uk.ac.aber.dcs.cs39440.majorproject.ui.theme.MajorProjectTheme

const val TAG = "Main Activity"
class MainActivity : AppCompatActivity() {

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract(),
    ) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userViewModel = UserViewModel()
        val firebaseListener = FirebaseAuth.AuthStateListener {
            setContent {
                MajorProjectTheme(
                    userViewModel = userViewModel
                ) {
                    if (Firebase.auth.currentUser != null) {
                        BuildNavigationGraph(userViewModel)
                    } else {
                        createSignInIntent()
                    }
                }
            }
        }
        val firebaseInstance = FirebaseAuth.getInstance()
        firebaseInstance.addAuthStateListener(firebaseListener)
    }

    private fun createSignInIntent() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.PhoneBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build(),
            //AuthUI.IdpConfig.FacebookBuilder().build(), FACEBOOK NOT HAPPY WITH APP CONFIG
        )
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setLogo(R.mipmap.ic_launcher)
            .setTheme(R.style.Theme_MajorProject)
            .build()

        signInLauncher.launch(signInIntent)
    }
}

@Composable
private fun BuildNavigationGraph(
    userViewModel: UserViewModel
) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState()}

    NavHost(
        navController = navController,
        startDestination = Screen.ChooseUserType.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(navController, userViewModel)
        }
        composable(Screen.Calendar.route) {
            CalendarScreen(navController, userViewModel)
        }
        composable(Screen.Chat.route) {
            ChatScreen(navController, userViewModel)
        }
        composable(Screen.Settings.route) {
            SettingsScreen(navController, userViewModel)
        }
        composable(Screen.ChooseUserType.route) {
            ChooseUserTypeScreen(navController, userViewModel)
        }
        composable(Screen.RunWorkout.route) {
            //WorkoutScreen(navController, userViewModel)
        }
        composable(Screen.CreateWorkout.route) {
            CreateWorkoutScreen(navController, userViewModel)
        }
        composable(Screen.ConnectWithOther.route) {
            ConnectWithOther(navController, userViewModel, snackbarHostState)
        }
        composable(Screen.ManageWorkouts.route) {
            ManageWorkoutsScreen(navController, userViewModel)
        }
        composable(Screen.EditWorkout.route) {
            EditWorkoutScreen(navController, userViewModel)
        }
    }
}
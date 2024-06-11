package uk.ac.aber.dcs.cs39440.majorproject.navigation

import android.content.Context
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.firebase.ui.auth.AuthUI
import kotlinx.coroutines.launch
import uk.ac.aber.dcs.cs39440.majorproject.R
import uk.ac.aber.dcs.cs39440.majorproject.components.Screen
import uk.ac.aber.dcs.cs39440.majorproject.components.UserMode
import uk.ac.aber.dcs.cs39440.majorproject.components.UserViewModel

@Composable
fun NavDrawer(
    drawerState: DrawerState,
    context: Context,
    navController: NavHostController,
    userViewModel: UserViewModel,
    content: @Composable () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {

                Text(text = stringResource(id = R.string.navigation_drawerTitle))
                HorizontalDivider()

                val otherType = if (userViewModel.userMode.value == UserMode.Client) UserMode.PT else UserMode.Client
                NavigationDrawerItem(
                    label = { Text(text = stringResource(id =
                    if (userViewModel.userMode.value == UserMode.Client)
                        R.string.chooseUserType_changeUserType_personalTrainer
                    else R.string.chooseUserType_changeUserType_client ))
                    },
                    selected = false,
                    onClick = {
                        userViewModel.userMode.value = otherType
                        coroutineScope.launch { drawerState.close() }
                    }
                )
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text(text = stringResource(id = if (userViewModel.userMode.value == UserMode.Client) R.string.navigation_drawer_linkWithPersonalTrainer else R.string.navigation_drawer_linkWithClient)) },
                    selected = false,
                    onClick = {
                        navController.navigate(Screen.ConnectWithOther.route)
                        coroutineScope.launch { drawerState.close() }
                    }
                )
                if (userViewModel.userMode.value != UserMode.Client) {
                    NavigationDrawerItem(
                        label = { Text(text = stringResource(id = R.string.navigation_drawer_manageClients)) },
                        selected = false,
                        onClick = {
                            navController.navigate(Screen.ManageClients.route)
                            coroutineScope.launch { drawerState.close() }
                        }
                    )
                }
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text(text = stringResource(id = R.string.navigation_drawer_settings ))
                    },
                    selected = false,
                    onClick = {
                        navController.navigate(Screen.Settings.route) {
                            restoreState = false
                        }
                        coroutineScope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    label = { Text(text = stringResource(id = R.string.navigation_drawer_signOut)) },
                    selected = false,
                    onClick = { AuthUI.getInstance().signOut(context) }
                )
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text(text = stringResource(id = R.string.navigation_drawer_manageWorkouts)) },
                    selected = false,
                    onClick = { navController.navigate(Screen.ManageWorkouts.route) }
                )
            }
        },
        content = content
    )
}
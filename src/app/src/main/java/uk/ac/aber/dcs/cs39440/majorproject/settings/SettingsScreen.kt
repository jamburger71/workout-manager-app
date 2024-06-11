package uk.ac.aber.dcs.cs39440.majorproject.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import uk.ac.aber.dcs.cs39440.majorproject.R
import uk.ac.aber.dcs.cs39440.majorproject.components.ThemeType
import uk.ac.aber.dcs.cs39440.majorproject.components.UserMode
import uk.ac.aber.dcs.cs39440.majorproject.components.UserViewModel
import uk.ac.aber.dcs.cs39440.majorproject.firebase.setUserTheme
import uk.ac.aber.dcs.cs39440.majorproject.components.Screen
import uk.ac.aber.dcs.cs39440.majorproject.navigation.TopLevelScaffold
import uk.ac.aber.dcs.cs39440.majorproject.ui.theme.typography

@Composable
fun SettingsScreen(
    navController: NavHostController,
    userViewModel: UserViewModel
) {
    TopLevelScaffold(
        navController = navController,
        userViewModel = userViewModel
    ) { paddingValues ->

        userViewModel.currentScreen.value = Screen.Settings
        val usernamePopupVisible = remember { mutableStateOf(false) }

        Surface(
            modifier = Modifier
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .verticalScroll(rememberScrollState())
            ) {

                // THEME //
                Text(
                    text = stringResource(id = R.string.settings_theme_title),
                    style = typography.titleLarge
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Text(
                        modifier = Modifier
                            .weight(1f),
                        text = stringResource(id = R.string.settings_theme_system)
                    )
                    Switch(
                        modifier = Modifier
                            .weight(1f),
                        checked = (userViewModel.darkTheme.value == ThemeType.System),
                        onCheckedChange = {
                            if (userViewModel.darkTheme.value == ThemeType.System) {
                                userViewModel.darkTheme.value = ThemeType.Light
                            } else {
                                userViewModel.darkTheme.value = ThemeType.System
                            }
                            setUserTheme(userViewModel.darkTheme.value)
                        }
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Text(
                        modifier = Modifier
                            .weight(1f),
                        text = stringResource(id = R.string.settings_theme_switch)
                    )
                    Switch(
                        modifier = Modifier
                            .weight(1f),
                        checked = (userViewModel.darkTheme.value == ThemeType.Dark),
                        onCheckedChange = {
                            if (userViewModel.darkTheme.value == ThemeType.Dark) {
                                userViewModel.darkTheme.value = ThemeType.Light
                            } else {
                                userViewModel.darkTheme.value = ThemeType.Dark
                            }
                            setUserTheme(userViewModel.darkTheme.value)
                        },
                        enabled = (userViewModel.darkTheme.value != ThemeType.System)
                    )
                }
                HorizontalDivider()
                Button(
                    onClick = { usernamePopupVisible.value = true }
                ) {
                    Text(
                        text = stringResource(id = R.string.settings_changeUserName_button)
                    )
                    AnimatedVisibility(visible = usernamePopupVisible.value) {
                        ChangeUserNamePopup(
                            isClient = userViewModel.userMode.equals(UserMode.Client),
                            isVisible = usernamePopupVisible
                        )
                    }
                }
            }
        }
    }
}
package uk.ac.aber.dcs.cs39440.majorproject.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uk.ac.aber.dcs.cs39440.majorproject.R
import uk.ac.aber.dcs.cs39440.majorproject.settings.ChangeUserNamePopup
import uk.ac.aber.dcs.cs39440.majorproject.ui.theme.typography

@Composable
fun SetupCard(
    setup: MutableState<Boolean>,
    isClient: Boolean,
    name: MutableState<String>
    ) {
    val namePopupVisible = remember { mutableStateOf(false) }

    if (!setup.value) {
        Card(
            modifier = Modifier
                .padding(all = 10.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(all = 10.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.homepage_completeSetup_title),
                    style = typography.headlineSmall
                )
                Text(
                    text = stringResource(id = R.string.homepage_completeSetup_tasksRemaining),
                    style = typography.titleLarge
                )
                Row(
                    modifier = Modifier
                        .clickable {
                            namePopupVisible.value = true
                        }
                        .padding(vertical = 10.dp)
                ) {
                    Checkbox(
                        checked = setup.value,
                        onCheckedChange = null,
                        enabled = false
                    )
                    Text(
                        text = stringResource(id = R.string.homepage_completeSetup_task_setName)
                    )
                }
            }
        }
        if (namePopupVisible.value) {
            ChangeUserNamePopup(
                isSetup = setup,
                isClient = isClient,
                isVisible = namePopupVisible,
                localUsername = name
            )
        }
    }
}
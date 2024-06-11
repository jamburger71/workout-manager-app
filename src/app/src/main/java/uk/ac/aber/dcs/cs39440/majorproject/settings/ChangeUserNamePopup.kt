package uk.ac.aber.dcs.cs39440.majorproject.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import uk.ac.aber.dcs.cs39440.majorproject.R
import uk.ac.aber.dcs.cs39440.majorproject.components.TaskStatus
import uk.ac.aber.dcs.cs39440.majorproject.firebase.setClientName
import uk.ac.aber.dcs.cs39440.majorproject.firebase.setTrainerName
import uk.ac.aber.dcs.cs39440.majorproject.ui.theme.typography

@Composable
fun ChangeUserNamePopup(
    isSetup: MutableState<Boolean> = mutableStateOf(false),
    isClient: Boolean,
    isVisible: MutableState<Boolean>,
    localUsername: MutableState<String> = mutableStateOf("")
) {
    Dialog(
        onDismissRequest = { isVisible.value = false }
    ) {
        val tempName = remember { mutableStateOf("") }
        val isSaved: MutableState<TaskStatus> = remember { mutableStateOf(TaskStatus.Pending) }
        val nameNotLongEnough = remember { mutableStateOf(false) }
        Card {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Text(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 10.dp),
                    text = stringResource(id = R.string.settings_changeUserName_title)
                )
                TextField(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 10.dp),
                    value = tempName.value,
                    onValueChange = {
                        tempName.value = it
                    }
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly

                ) {
                    Button(
                        onClick = {
                            if (tempName.value.length > 2) {
                                isSaved.value = TaskStatus.Pending
                                nameNotLongEnough.value = false
                                if (isClient) {
                                    setClientName(name = tempName.value, isSaved = isSaved)
                                } else {
                                    setTrainerName(name = tempName.value, isSaved = isSaved)
                                }
                                isSetup.value = true
                            } else {
                                nameNotLongEnough.value = true
                            }
                        }
                    ) {
                        Text(
                            text = stringResource(id = R.string.button_save)
                        )
                    }
                    Button(
                        onClick = {
                            isVisible.value = false
                        }
                    ) {
                        Text(
                            text = stringResource(id = R.string.button_cancel)
                        )
                    }
                }
                if (isSaved.value == TaskStatus.Succeeded) {
                    localUsername.value = tempName.value
                    isVisible.value = false
                } else if (nameNotLongEnough.value) {
                    Text(
                        text = stringResource(id = R.string.settings_changeUserName_notLongEnough),
                        style = typography.bodyMedium.plus(TextStyle(color = Color.Red))
                    )
                } else if (isSaved.value == TaskStatus.Failed) {
                    Text(
                        text = stringResource(id = R.string.settings_changeUserName_error),
                        style = typography.bodyMedium.plus(TextStyle(color = Color.Red))
                    )
                }
            }
        }
    }
}
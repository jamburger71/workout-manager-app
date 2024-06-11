package uk.ac.aber.dcs.cs39440.majorproject.chat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import uk.ac.aber.dcs.cs39440.majorproject.R
import uk.ac.aber.dcs.cs39440.majorproject.firebase.createChat
import uk.ac.aber.dcs.cs39440.majorproject.firebase.getClientName
import uk.ac.aber.dcs.cs39440.majorproject.firebase.getLinks
import uk.ac.aber.dcs.cs39440.majorproject.firebase.getTrainerName
import uk.ac.aber.dcs.cs39440.majorproject.components.Screen
import uk.ac.aber.dcs.cs39440.majorproject.ui.theme.typography

@Composable
fun CreateNewChat(
    visible: MutableState<Boolean>,
    isClient: Boolean,
    currentChat: MutableState<String>,
    navController: NavHostController
) {
    Dialog(
        onDismissRequest = {
            visible.value = false
        }
    ) {
        val links: MutableList<Pair<String,String>> = remember { mutableStateListOf() }
        LaunchedEffect(key1 = null) {
            getLinks(links, isClient)
        }
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 100.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                links.forEach { (client, trainer) ->
                    val personName = remember { mutableStateOf("") }
                    LaunchedEffect(key1 = null) {
                        if (isClient) getTrainerName(personName, trainer) else getClientName(personName, client)
                    }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                            .clickable {
                                createChat(
                                    isFromClient = isClient,
                                    otherPersonID = if (isClient) trainer else client,
                                    currentChat = currentChat
                                )
                                visible.value = false
                            }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                        ) {
                            Text(
                                text = personName.value,
                                style = typography.headlineLarge
                            )
                        }
                    }
                }
                if (links.isEmpty()) {
                    Text(
                        text = stringResource(id = if (isClient) R.string.chat_createNewChat_noLinksToChat_client else R.string.chat_createNewChat_noLinksToChat_trainer)
                    )
                    Button(
                        onClick = {
                            navController.navigate(Screen.ConnectWithOther.route)
                            visible.value = false
                        }
                    ) {
                        Text(
                            text = stringResource(id = if (isClient) R.string.navigation_drawer_linkWithPersonalTrainer else R.string.navigation_drawer_linkWithClient)
                        )
                    }
                }
            }

        }
    }
}
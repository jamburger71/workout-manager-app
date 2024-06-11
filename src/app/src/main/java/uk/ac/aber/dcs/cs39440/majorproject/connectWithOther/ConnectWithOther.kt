package uk.ac.aber.dcs.cs39440.majorproject.connectWithOther

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import uk.ac.aber.dcs.cs39440.majorproject.R
import uk.ac.aber.dcs.cs39440.majorproject.components.LinkRequest
import uk.ac.aber.dcs.cs39440.majorproject.components.TaskStatus
import uk.ac.aber.dcs.cs39440.majorproject.components.UserMode
import uk.ac.aber.dcs.cs39440.majorproject.components.UserViewModel
import uk.ac.aber.dcs.cs39440.majorproject.firebase.createLinkRequest
import uk.ac.aber.dcs.cs39440.majorproject.firebase.deleteLink
import uk.ac.aber.dcs.cs39440.majorproject.firebase.getLinkExists
import uk.ac.aber.dcs.cs39440.majorproject.firebase.getLinkRequest
import uk.ac.aber.dcs.cs39440.majorproject.firebase.getTrainers
import uk.ac.aber.dcs.cs39440.majorproject.firebase.deleteLinkRequest
import uk.ac.aber.dcs.cs39440.majorproject.firebase.getClients
import uk.ac.aber.dcs.cs39440.majorproject.components.Screen
import uk.ac.aber.dcs.cs39440.majorproject.navigation.TopLevelScaffold
import uk.ac.aber.dcs.cs39440.majorproject.ui.theme.typography

@Composable
fun ConnectWithOther(
    navController: NavHostController,
    userViewModel: UserViewModel,
    snackbarHostState: SnackbarHostState
) {
    TopLevelScaffold(
        navController = navController,
        userViewModel = userViewModel,
        snackbarHostState = snackbarHostState
    ) { paddingValues ->
        userViewModel.currentScreen.value = Screen.ConnectWithOther
        val isClient = userViewModel.userMode.value == UserMode.Client

        Surface(
            modifier = Modifier
                .padding(paddingValues)
        ) {
            val filter = remember { mutableStateOf("") }
            val listOfOthers: MutableList<Pair<String, String?>> = remember { mutableStateListOf() }
            val scope = rememberCoroutineScope()

            LaunchedEffect(key1 = null) {
                if (isClient) getTrainers(listOfOthers) else getClients(listOfOthers)
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier
                            .padding(end = 10.dp),
                        text = stringResource(id = R.string.connectWithOther_filterTag),
                        style = typography.titleMedium
                    )
                    TextField(
                        value = filter.value,
                        onValueChange = { value ->
                            filter.value = value
                        }
                    )
                }
                HorizontalDivider(
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                )
                if (listOfOthers.isNotEmpty()) {
                    listOfOthers.forEach { other ->
                        if (other.second != null && other.second!!.lowercase().contains(filter.value.lowercase())) {
                            val linkRequest: MutableState<LinkRequest?> = remember { mutableStateOf(null) }
                            val isLinked = remember { mutableStateOf(TaskStatus.Pending) }
                            val messageLinkSent = stringResource(id = R.string.connectWithOther_snackbar_linkSent)
                            val messageLinkRequestRemoved = stringResource(id = R.string.connectWithOther_snackbar_linkDeleted)
                            val undo = stringResource(id = R.string.button_undo)

                            LaunchedEffect(key1 = null) {
                                getLinkRequest(
                                    otherPersonID = other.first,
                                    isClient = isClient,
                                    linkExists = linkRequest
                                )
                                getLinkExists(
                                    otherPersonID = other.first,
                                    isFromClient = true,
                                    isExists = isLinked
                                )
                            }

                            Card(
                                modifier = Modifier
                                    .clickable {
                                        if (linkRequest.value == null && isLinked.value == TaskStatus.IsFalse) {
                                            scope.launch {
                                                createLinkRequest(
                                                    isFromClient = true,
                                                    otherPersonID = other.first,
                                                    linkRequest = linkRequest
                                                )

                                                val result = snackbarHostState.showSnackbar(
                                                    message = messageLinkSent,
                                                    actionLabel = undo,
                                                    withDismissAction = true
                                                )
                                                when (result) {
                                                    SnackbarResult.ActionPerformed -> {
                                                        deleteLink(
                                                            isFromClient = true,
                                                            otherPersonID = other.first
                                                        )
                                                        linkRequest.value = null
                                                    }

                                                    SnackbarResult.Dismissed -> {}
                                                }
                                            }
                                        } else if (isLinked.value == TaskStatus.IsFalse) {
                                            scope.launch {
                                                linkRequest.value?.let { deleteLinkRequest(requestID = it.requestID) }
                                                linkRequest.value = null
                                                snackbarHostState.showSnackbar(
                                                    message = messageLinkRequestRemoved,
                                                    withDismissAction = true
                                                )
                                            }
                                        }
                                    }
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(10.dp)
                                ) {
                                    Text(
                                        text = other.second!!,
                                        style = typography.headlineSmall
                                    )
                                    if (linkRequest.value != null) {
                                        Text(
                                            text = stringResource(id = if (isClient) R.string.connectWithOther_linkAlreadySentTrainer else R.string.connectWithOther_linkAlreadySentClient)
                                        )
                                    }
                                    if (isLinked.value == TaskStatus.IsTrue) {
                                        Text(
                                            text = stringResource(id = if (isClient) R.string.connectWithOther_alreadyLinkedTrainer else R.string.connectWithOther_alreadyLinkedClient)
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Text(
                        text = stringResource(id = if (isClient) R.string.connectWithOther_noTrainersFound else R.string.connectWithOther_noClientsFound),
                        style = typography.titleLarge
                    )
                }
            }
        }
    }
}
package uk.ac.aber.dcs.cs39440.majorproject.home

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uk.ac.aber.dcs.cs39440.majorproject.R
import uk.ac.aber.dcs.cs39440.majorproject.TAG
import uk.ac.aber.dcs.cs39440.majorproject.components.LinkRequest
import uk.ac.aber.dcs.cs39440.majorproject.firebase.createLink
import uk.ac.aber.dcs.cs39440.majorproject.firebase.deleteLinkRequest
import uk.ac.aber.dcs.cs39440.majorproject.firebase.getClientName
import uk.ac.aber.dcs.cs39440.majorproject.firebase.getLinkRequests
import uk.ac.aber.dcs.cs39440.majorproject.firebase.getTrainerName
import uk.ac.aber.dcs.cs39440.majorproject.ui.theme.typography

@Composable
fun LinkRequestsCard(
    isClient: Boolean
) {
    val existingLinks = remember { mutableStateListOf<LinkRequest>() }
    LaunchedEffect(key1 = null) {
        getLinkRequests(
            isFromClient = isClient,
            listOfLinks = existingLinks
        )
    }
    if (existingLinks.isNotEmpty()) {
        Log.w(TAG, "found link requests")
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .shadow(5.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.homepage_linkRequest_title),
                    style = typography.titleLarge
                )
                existingLinks.forEach { link ->
                    val senderName = remember { mutableStateOf("") }
                    LaunchedEffect(key1 = null) {
                        if (isClient) {
                            getTrainerName(senderName, link.trainerID)
                        } else {
                            getClientName(senderName, link.clientID)
                        }
                    }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                            .shadow(5.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                        ) {
                            Text(
                                modifier = Modifier
                                    .padding(vertical = 10.dp),
                                text = senderName.value,
                                style = typography.titleSmall
                            )
                            Row {
                                Button(
                                    onClick = {
                                        createLink(link)
                                        deleteLinkRequest(
                                            requestID = link.requestID
                                        )
                                        existingLinks.remove(link)
                                    }
                                ) {
                                    Text(text = stringResource(id = R.string.button_confirm))
                                }
                                Button(
                                    onClick = {
                                        deleteLinkRequest(
                                            requestID = link.requestID
                                        )
                                        existingLinks.remove(link)
                                    }
                                ) {
                                    Text(text = stringResource(id = R.string.button_reject))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
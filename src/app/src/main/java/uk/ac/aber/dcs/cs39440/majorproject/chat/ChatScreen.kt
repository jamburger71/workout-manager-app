package uk.ac.aber.dcs.cs39440.majorproject.chat

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import uk.ac.aber.dcs.cs39440.majorproject.R
import uk.ac.aber.dcs.cs39440.majorproject.TAG
import uk.ac.aber.dcs.cs39440.majorproject.components.ChatDetails
import uk.ac.aber.dcs.cs39440.majorproject.components.ChatMessage
import uk.ac.aber.dcs.cs39440.majorproject.components.UserMode
import uk.ac.aber.dcs.cs39440.majorproject.components.UserViewModel
import uk.ac.aber.dcs.cs39440.majorproject.firebase.getChat
import uk.ac.aber.dcs.cs39440.majorproject.firebase.getChats
import uk.ac.aber.dcs.cs39440.majorproject.firebase.getClientName
import uk.ac.aber.dcs.cs39440.majorproject.firebase.getTrainerName
import uk.ac.aber.dcs.cs39440.majorproject.firebase.listenToChat
import uk.ac.aber.dcs.cs39440.majorproject.firebase.sendChat
import uk.ac.aber.dcs.cs39440.majorproject.components.Screen
import uk.ac.aber.dcs.cs39440.majorproject.navigation.TopLevelScaffold
import uk.ac.aber.dcs.cs39440.majorproject.ui.theme.typography

@Composable
fun ChatScreen(
    navController: NavHostController,
    userViewModel: UserViewModel
) {

    val user = FirebaseAuth.getInstance().currentUser
    TopLevelScaffold(
        navController = navController,
        userViewModel = userViewModel
    ) { paddingValues ->
        userViewModel.currentScreen.value = Screen.Chat
        var isClient = userViewModel.userMode.value == UserMode.Client
        Surface(
            modifier = Modifier
                .padding(paddingValues)
        ) {
            val chats = remember { mutableStateListOf<ChatDetails>() }
            val selectedChat: MutableState<String> = remember { mutableStateOf("") }
            if (selectedChat.value == "") {
                val createChatVisible = remember { mutableStateOf(false) }
                LaunchedEffect(key1 = userViewModel.userMode.value) {
                    isClient = userViewModel.userMode.value == UserMode.Client
                    getChats(isFromClient = isClient, listOfChats = chats)
                }
                Column {
                    if (chats.isNotEmpty()) {
                        chats.forEach { chat ->
                            val otherPersonName = remember { mutableStateOf("") }
                            LaunchedEffect(key1 = userViewModel.userMode.value) {
                                if (isClient) getTrainerName(otherPersonName, chat.trainerID) else getClientName(otherPersonName, chat.clientID)
                            }
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp)
                                    .clickable {
                                        selectedChat.value = chat.chatID
                                    }
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp)
                                ) {
                                    Text(
                                        text = otherPersonName.value,
                                        style = typography.headlineLarge
                                    )
                                }
                            }
                        }
                    } else {
                        Text(text = stringResource(id = R.string.chat_noChatsFound))
                    }
                    Button(
                        onClick = {
                            createChatVisible.value = true
                        }
                    ) {
                        Text(text = stringResource(id = R.string.chat_createNewChat))
                    }
                }
                if (createChatVisible.value) {
                    CreateNewChat(
                        visible = createChatVisible,
                        currentChat = selectedChat,
                        isClient = isClient,
                        navController = navController
                    )
                }
            } else {
                val chatDetails = remember { mutableStateOf(ChatDetails()) }
                val chatMessages = remember { mutableStateListOf<Pair<String, ChatMessage>>() }
                val otherPersonName = remember { mutableStateOf("") }
                LaunchedEffect(key1 = null) {
                    getChat(
                        selectedChat.value,
                        chatDetails
                    )
                    listenToChat(
                        chatID = selectedChat.value,
                        chatList = chatMessages
                    )
                }
                LaunchedEffect(key1 = chatDetails.value) {
                    Log.i(TAG, "Running to get names with clientID ${chatDetails.value.clientID}")
                    if (chatDetails.value.chatID != "") {
                        if (!isClient) getClientName(otherPersonName, chatDetails.value.clientID)
                        else getTrainerName(otherPersonName, chatDetails.value.trainerID)
                    }
                }
                chatMessages.sortBy { (_, chat) -> chat.sendTime }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(70.dp)
                            .shadow(5.dp)
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(10.dp),
                            text = otherPersonName.value,
                            style = typography.headlineMedium
                        )
                        Button(
                            modifier = Modifier
                                .size(50.dp)
                                .align(Alignment.CenterEnd)
                                .padding(end = 5.dp),
                            contentPadding = PaddingValues(10.dp),
                            onClick = {
                                selectedChat.value = ""
                            }
                        ) {
                            Icon(
                                modifier = Modifier
                                    .size(40.dp),
                                imageVector = Icons.Rounded.Close,
                                contentDescription = stringResource(id = R.string.backArrow)
                            )
                        }
                    }
                    Column(
                        modifier = Modifier
                            .weight(10f)
                            .verticalScroll(rememberScrollState())
                    ) {
                        if (user != null) {
                            if (chatMessages.isNotEmpty()) {
                                chatMessages.forEach { (_, chatMessage) ->
                                    val timestamp = convertTimeStampToString(chatMessage.sendTime)
                                    Card(
                                        modifier = Modifier
                                            .align(if (chatMessage.sender == user.uid) Alignment.End else Alignment.Start)
                                            .widthIn(10.dp, 200.dp)
                                            .padding(10.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(10.dp)
                                        ) {
                                            Text(
                                                text = chatMessage.message,
                                                style = typography.titleLarge
                                            )
                                            Text(
                                                modifier = Modifier
                                                    .align(Alignment.End),
                                                text = "${stringResource(id = R.string.chat_sentAt_prefix)} $timestamp"
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        val currentMessage = remember { mutableStateOf("") }
                        TextField(
                            value = currentMessage.value,
                            onValueChange = { value ->
                                currentMessage.value = value
                            }
                        )
                        Button(
                            modifier = Modifier
                                .size(50.dp),
                            contentPadding = PaddingValues(10.dp),
                            onClick = {
                                sendChat(chatDetails.value.chatID, currentMessage.value)
                                currentMessage.value = ""
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.Send,
                                contentDescription = stringResource(id = R.string.chat_sendMessage_description)
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun convertTimeStampToString(
    timestamp: Timestamp
): String {
    val timeSeconds = timestamp.seconds % 86400
    val timeSecondsWithoutMinutes = timeSeconds % 60
    val timeHours = timeSeconds / 3600
    val timeMinutes = timeSeconds / 60 - timeHours * 60

    val secondsString = if (timeSecondsWithoutMinutes < 10) "0$timeSecondsWithoutMinutes" else timeSecondsWithoutMinutes.toString()
    val minutesString = if (timeMinutes < 10) "0$timeMinutes" else timeMinutes.toString()
    val hoursString = if (timeHours < 10) "0$timeHours" else timeHours.toString()

    return "$hoursString:$minutesString:$secondsString"
}
package uk.ac.aber.dcs.cs39440.majorproject.chooseUserType

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import uk.ac.aber.dcs.cs39440.majorproject.R
import uk.ac.aber.dcs.cs39440.majorproject.components.UserMode
import uk.ac.aber.dcs.cs39440.majorproject.components.UserViewModel
import uk.ac.aber.dcs.cs39440.majorproject.components.Screen
import uk.ac.aber.dcs.cs39440.majorproject.ui.theme.typography

@Composable
fun ChooseUserTypeScreen(
    navController: NavHostController,
    userViewModel: UserViewModel
) {
    userViewModel.currentScreen.value = Screen.ChooseUserType
    val expandedCard = remember{ mutableStateOf(UserMode.None) }
    val bulletChar = "\u2022"
    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier
                    .padding(bottom = 20.dp, top = 100.dp),
                text = stringResource(id = R.string.chooseUserType_title),
                style = typography.headlineLarge
            )
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .clickable {
                        if (expandedCard.value == UserMode.PT) {
                            expandedCard.value = UserMode.None
                        } else {
                            expandedCard.value = UserMode.PT
                        }
                    }
            ) {
                Text(
                    modifier = Modifier
                        .padding(10.dp),
                    text = stringResource(id = R.string.personalTrainer),
                    style = typography.headlineSmall
                )
                AnimatedVisibility(visible = (expandedCard.value == UserMode.PT)) {
                    val listOfMessagesPT = listOf(
                        stringResource(id = R.string.chooseUserType_changeUserType_personalTrainer_content_1),
                        stringResource(id = R.string.chooseUserType_changeUserType_personalTrainer_content_2),
                        stringResource(id = R.string.chooseUserType_changeUserType_personalTrainer_content_3)
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                                .wrapContentHeight(),
                            text = buildAnnotatedString {
                                append(stringResource(id = R.string.chooseUserType_changeUserType_content_title))
                                listOfMessagesPT.forEach {
                                    withStyle(style = ParagraphStyle(
                                        textIndent = TextIndent(restLine = 21.sp)
                                    )) {
                                        append("\t")
                                        append(bulletChar)
                                        append("\t\t")
                                        append(it)
                                    }
                                }
                            }
                        )
                        Button(
                            modifier = Modifier
                                .padding(end = 20.dp)
                                .align(Alignment.CenterHorizontally),
                            onClick = {
                                userViewModel.userMode.value = UserMode.PT
                                navController.navigate(Screen.Home.route)
                            }
                        ) {
                            Text(
                                text = stringResource(id = R.string.chooseUserType_changeUserType_personalTrainer)
                            )
                        }
                    }
                }
            }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .clickable {
                        if (expandedCard.value == UserMode.Client) {
                            expandedCard.value = UserMode.None
                        } else {
                            expandedCard.value = UserMode.Client
                        }
                    }
            ) {
                Text(
                    modifier = Modifier
                        .padding(10.dp),
                    text = stringResource(id = R.string.client),
                    style = typography.headlineSmall
                )
                AnimatedVisibility(visible = (expandedCard.value == UserMode.Client)) {
                    val listOfMessagesClient = listOf(
                        stringResource(id = R.string.chooseUserType_changeUserType_client_content_1),
                        stringResource(id = R.string.chooseUserType_changeUserType_client_content_2),
                        stringResource(id = R.string.chooseUserType_changeUserType_client_content_3)
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                                .wrapContentHeight(),
                            text = buildAnnotatedString {
                                append(stringResource(id = R.string.chooseUserType_changeUserType_content_title))
                                listOfMessagesClient.forEach {
                                    withStyle(style = ParagraphStyle(
                                        textIndent = TextIndent(restLine = 21.sp)
                                    )) {
                                        append("\t")
                                        append(bulletChar)
                                        append("\t\t")
                                        append(it)
                                    }
                                }
                            }
                        )
                        Button(
                            modifier = Modifier
                                .padding(end = 20.dp)
                                .align(Alignment.CenterHorizontally),
                            onClick = {
                                userViewModel.userMode.value = UserMode.Client
                                navController.navigate(Screen.Home.route)
                            }
                        ) {
                            Text(
                                text = stringResource(id = R.string.chooseUserType_changeUserType_client)
                            )
                        }
                    }
                }
            }
        }
    }
}
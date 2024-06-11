package uk.ac.aber.dcs.cs39440.majorproject.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uk.ac.aber.dcs.cs39440.majorproject.R
import uk.ac.aber.dcs.cs39440.majorproject.ui.theme.typography
import java.text.DateFormat
import java.util.Calendar

@Composable
fun WelcomeCard(
    setup: MutableState<Boolean>,
    name: MutableState<String>,
    isClient: Boolean
) {
    val calendar = Calendar.getInstance().time
    val currentDay = DateFormat.getDateInstance().format(calendar)

    Card(
        modifier = Modifier
            .padding(all = 10.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
        ) {
            Text(
                text = if (setup.value) {
                    "${stringResource(id = R.string.homepage_welcome_prefixSetupComplete)} ${name.value}"
                } else stringResource(id = R.string.homepage_welcome_prefix),
                style = typography.titleLarge
            )
            Text(
                text = "${stringResource(id = R.string.homepage_day_prefix)} $currentDay",
                style = typography.titleMedium
            )
            Text(
                text = if (!isClient) stringResource(id = R.string.homepage_welcome_welcomeTrainer) else stringResource(id = R.string.homepage_welcome_welcomeClient),
                style = typography.titleSmall
            )
        }
    }
}
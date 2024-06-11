package uk.ac.aber.dcs.cs39440.majorproject.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import uk.ac.aber.dcs.cs39440.majorproject.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickDatePopup(
    datePickerState: DatePickerState,
    originalState: Long,
    open: MutableState<Boolean>
) {
    Dialog(
        onDismissRequest = {
            open.value = false
        }
    ) {
        Card {
            Column(
                modifier = Modifier
                    .padding(all = 10.dp)
            ) {
                DatePicker(
                    state = datePickerState
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Button(
                        onClick = {
                            open.value = false
                        }
                    ) {
                        Text(
                            text = stringResource(id = R.string.button_confirm)
                        )
                    }
                    Button(
                        onClick = {
                            datePickerState.selectedDateMillis = originalState
                            open.value = false
                        }
                    ) {
                        Text(
                            text = stringResource(id = R.string.button_cancel)
                        )
                    }
                }
            }
        }
    }
}
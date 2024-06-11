package uk.ac.aber.dcs.cs39440.majorproject.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class NavIcon(
    val filledIcon: ImageVector = Icons.Filled.Clear,
    val outlineIcon: ImageVector = Icons.Outlined.Clear,
    val label: String = "",
    val titleText: String
)
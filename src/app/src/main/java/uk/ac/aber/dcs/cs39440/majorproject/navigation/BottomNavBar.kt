package uk.ac.aber.dcs.cs39440.majorproject.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import uk.ac.aber.dcs.cs39440.majorproject.components.NavIcon
import uk.ac.aber.dcs.cs39440.majorproject.components.Screen
import uk.ac.aber.dcs.cs39440.majorproject.components.topLevelScreens
import uk.ac.aber.dcs.cs39440.majorproject.ui.theme.typography

@Composable
fun BottomNavBar(
    navController: NavController,
    icons: Map<Screen, NavIcon>
) {
    Surface(
        color = NavigationBarDefaults.containerColor,
        contentColor = MaterialTheme.colorScheme.contentColorFor(NavigationBarDefaults.containerColor),
        tonalElevation = NavigationBarDefaults.Elevation,
        modifier = Modifier
            .shadow(5.dp)
            .height(60.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            topLevelScreens.forEach { screen ->
                val isSelected =
                    currentDestination?.hierarchy?.any { it.route == screen.route } == true
                val labelText = icons[screen]!!.label
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .selectable(
                            selected = isSelected,
                            role = Role.Tab,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                        .weight(1f)
                        .background(color = if (isSelected) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.inverseOnSurface
                        }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = (if (isSelected)
                                icons[screen]!!.filledIcon
                            else
                                icons[screen]!!.outlineIcon),
                            contentDescription = labelText
                        )
                        Text(
                            text = labelText,
                            style = typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}
package uk.ac.aber.dcs.cs39440.majorproject.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.coroutines.launch
import uk.ac.aber.dcs.cs39440.majorproject.R
import uk.ac.aber.dcs.cs39440.majorproject.components.NavIcon
import uk.ac.aber.dcs.cs39440.majorproject.components.Screen
import uk.ac.aber.dcs.cs39440.majorproject.components.allScreens
import uk.ac.aber.dcs.cs39440.majorproject.components.topLevelScreens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopTitleBar(
    navController: NavController,
    icons: Map<Screen, NavIcon>,
    drawerState: DrawerState
) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    var isTopLevel = false
    val scope = rememberCoroutineScope()
    var text = ""

    topLevelScreens.forEach{ screen ->
        if (currentDestination?.hierarchy?.any { it.route == screen.route } == true) {
            isTopLevel = true
        }
    }
    allScreens.forEach { screen ->
        if (currentDestination?.hierarchy?.any { it.route == screen.route } == true) {
            text = icons[screen]!!.titleText
        }
    }

    TopAppBar(
        modifier = Modifier
            .shadow(elevation = 5.dp),
        title = {
            Text(
                modifier = Modifier
                    .padding(horizontal = 10.dp),
                text = text
            )
        },
        navigationIcon = {
            if (isTopLevel) {
                Button(
                    modifier = Modifier
                        .size(50.dp),
                    contentPadding = PaddingValues(10.dp),
                    onClick = {
                        scope.launch {
                            drawerState.apply {
                                if (isClosed) open() else close()
                            }
                        }
                    }
                ) {
                    Icon(
                        modifier = Modifier
                            .size(40.dp),
                        imageVector = Icons.Rounded.Menu,
                        contentDescription = stringResource(id = R.string.navigation_menuDescription)
                    )
                }
            } else {
                Button(
                    modifier = Modifier
                        .size(50.dp),
                    contentPadding = PaddingValues(10.dp),
                    onClick = {
                        navController.navigateUp()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBack,
                        contentDescription = stringResource(id = R.string.navigation_backDescription)
                    )
                }
            }
        }
    )
}
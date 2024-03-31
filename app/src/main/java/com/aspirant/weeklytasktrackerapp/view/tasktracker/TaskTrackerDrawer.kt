package com.aspirant.weeklytasktrackerapp.view.tasktracker

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemColors
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.aspirant.weeklytasktrackerapp.model.auth.AuthService
import com.aspirant.weeklytasktrackerapp.view.tasktracker.todayview.TodayScreen
import com.aspirant.weeklytasktrackerapp.view.tasktracker.todayview.TodayViewModel
import com.aspirant.weeklytasktrackerapp.view.tasktracker.weeklyview.WeeklyView
import com.aspirant.weeklytasktrackerapp.view.tasktracker.weeklyview.WeeklyViewViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskTrackerDrawer(viewModel: TaskTrackerDrawerViewModel) {
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val selectedIndex by rememberUpdatedState(newValue = viewModel.selectedIndex)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("Task Tracker", modifier = Modifier.padding(16.dp))
                HorizontalDivider()
                viewModel.getTitles().mapIndexed { index, title ->
                    if (title == "Logout") {
                        NavigationDrawerItem(
                            label = {
                                Text(title)
                            },
                            selected = false,
                            onClick = {
                                viewModel.getAuthService().logout()
                                viewModel.getOnNavigateToLogin()()
                            },
                            colors = NavigationDrawerItemDefaults.colors(
                                unselectedTextColor = Color.Red,
                                unselectedContainerColor = Color(0x00AA0000)
                            ),
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    } else {
                        NavigationDrawerItem(
                            label = { Text(title) },
                            selected = selectedIndex == index,
                            onClick = {
                                viewModel.updateSelectedIndex(index)
                                scope.launch { drawerState.close() }
                            }
                        )
                    }
                }
            }
        },
        gesturesEnabled = true,
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(viewModel.getAppBarTitle()) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Localized description")
                        }
                    }
                )
            }) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                when (selectedIndex) {
                    0 -> TodayScreen(
                        viewModel = TodayViewModel(
                            authService = viewModel.getAuthService(),
                            onNavigateToLogin = viewModel.getOnNavigateToLogin(),
                            updateDrawerIndex = { viewModel.updateSelectedIndex(it) }
                        )
                    )

                    1 -> WeeklyView(
                        viewModel = WeeklyViewViewModel(
                            authService = viewModel.getAuthService(),
                            onNavigateToLogin = viewModel.getOnNavigateToLogin()
                        )
                    )
                }
            }
        }
    }
}

class TaskTrackerDrawerViewModel(
    private val authService: AuthService,
    private val onNavigateToLogin: () -> Unit
) : ViewModel() {

    var selectedIndex by mutableIntStateOf(0)
        private set

    fun getTitles(): List<String> {
        return listOf(
            "Today's Tasks",
            "Weekly View",
            "Task Editor",
            "Logout"
        )
    }

    fun getAppBarTitle(): String {
        val titles = getTitles()
        return when (selectedIndex) {
            in 0..titles.size -> titles[selectedIndex]
            else -> "UNKNOWN VIEW"
        }
    }

    fun updateSelectedIndex(index: Int) {
        selectedIndex = index
    }

    fun getAuthService() = authService
    fun getOnNavigateToLogin() = onNavigateToLogin
}
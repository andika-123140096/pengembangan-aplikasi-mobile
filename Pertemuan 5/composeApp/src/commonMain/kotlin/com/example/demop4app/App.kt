package com.example.demop4app

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.demop4app.ui.*
import com.example.demop4app.viewmodel.TodoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    val navController = rememberNavController()
    val viewModel: TodoViewModel = viewModel { TodoViewModel() }

    val darkGreen = Color(0xFF1B5E20)
    val greenColorScheme = lightColorScheme(
        primary = darkGreen,
        onPrimary = Color.White,
        primaryContainer = Color(0xFFC8E6C9),
        onPrimaryContainer = darkGreen,
        secondary = Color(0xFF388E3C),
        surface = Color.White,
        onSurface = Color.Black
    )

    MaterialTheme(colorScheme = greenColorScheme) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        val showBottomBar = bottomNavItems.any { it.route == currentDestination?.route }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { 
                        Text(
                            text = "Notes App", 
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        ) 
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = darkGreen
                    )
                )
            },
            bottomBar = {
                if (showBottomBar) {
                    NavigationBar(
                        containerColor = Color.White
                    ) {
                        bottomNavItems.forEach { screen ->
                            NavigationBarItem(
                                icon = {
                                    screen.icon?.let {
                                        Icon(
                                            it, 
                                            contentDescription = screen.title,
                                            tint = if (currentDestination?.hierarchy?.any { it.route == screen.route } == true) darkGreen else Color.Gray
                                        )
                                    }
                                },
                                label = { Text(screen.title) },
                                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                                onClick = {
                                    navController.navigate(screen.route) {
                                        val startDest = navController.graph.findStartDestination()
                                        startDest.route?.let {
                                            popUpTo(it) {
                                                saveState = true
                                            }
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Surface(
                modifier = Modifier.padding(innerPadding),
                color = Color.White
            ) {
                NavHost(
                    navController = navController,
                    startDestination = Screen.Notes.route
                ) {
                    composable(Screen.Notes.route) {
                        TodoScreen(
                            viewModel = viewModel,
                            onNoteClick = { noteId ->
                                navController.navigate(Screen.NoteDetail.createRoute(noteId))
                            },
                            onAddNoteClick = {
                                navController.navigate(Screen.AddNote.route)
                            }
                        )
                    }
                    composable(Screen.Favorites.route) {
                        FavoritesScreen(
                            viewModel = viewModel,
                            onNoteClick = { noteId ->
                                navController.navigate(Screen.NoteDetail.createRoute(noteId))
                            }
                        )
                    }
                    composable(Screen.Profile.route) {
                        ProfileScreen()
                    }
                    composable(
                        route = Screen.NoteDetail.route,
                        arguments = listOf(navArgument("noteId") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val noteId = backStackEntry.arguments?.getInt("noteId") ?: 0
                        NoteDetailScreen(
                            noteId = noteId,
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }
                    composable(Screen.AddNote.route) {
                        AddNoteScreen(
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}

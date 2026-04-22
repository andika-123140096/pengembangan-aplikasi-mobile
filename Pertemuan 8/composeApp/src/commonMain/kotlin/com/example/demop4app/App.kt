package com.example.demop4app

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.demop4app.data.model.ThemeMode
import com.example.demop4app.ui.*
import com.example.demop4app.viewmodel.NotesViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }

    val viewModel: NotesViewModel = koinInject()

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val darkGreen = Color(0xFF1B5E20)
    val lightScheme = lightColorScheme(
        primary = darkGreen,
        onPrimary = Color.White,
        primaryContainer = Color(0xFFC8E6C9),
        onPrimaryContainer = darkGreen,
        secondary = Color(0xFF388E3C),
        surface = Color.White,
        onSurface = Color.Black
    )
    val darkScheme = darkColorScheme(
        primary = Color(0xFF66BB6A),
        onPrimary = Color.Black,
        primaryContainer = Color(0xFF1B5E20),
        onPrimaryContainer = Color.White,
        secondary = Color(0xFF81C784),
        surface = Color(0xFF121212),
        onSurface = Color.White
    )

    val useDarkTheme = when (uiState.themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    LaunchedEffect(uiState.errorMessage) {
        val message = uiState.errorMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(message)
        viewModel.clearError()
    }

    MaterialTheme(colorScheme = if (useDarkTheme) darkScheme else lightScheme) {
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
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            bottomBar = {
                if (showBottomBar) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface
                    ) {
                        bottomNavItems.forEach { screen ->
                            NavigationBarItem(
                                icon = {
                                    screen.icon?.let {
                                        Icon(
                                            it,
                                            contentDescription = screen.title,
                                            tint = if (currentDestination?.hierarchy?.any { it.route == screen.route } == true) {
                                                MaterialTheme.colorScheme.primary
                                            } else {
                                                Color.Gray
                                            }
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
                color = MaterialTheme.colorScheme.surface
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
                    composable(Screen.Settings.route) {
                        SettingsScreen(
                            themeMode = uiState.themeMode,
                            sortOrder = uiState.sortOrder,
                            onThemeChange = viewModel::updateThemeMode,
                            onSortOrderChange = viewModel::updateSortOrder
                        )
                    }
                    composable(Screen.Profile.route) {
                        ProfileScreen()
                    }
                    composable(
                        route = Screen.NoteDetail.route,
                        arguments = listOf(navArgument("noteId") { type = NavType.LongType })
                    ) { backStackEntry ->
                        val noteId = backStackEntry.arguments?.getLong("noteId") ?: 0L
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
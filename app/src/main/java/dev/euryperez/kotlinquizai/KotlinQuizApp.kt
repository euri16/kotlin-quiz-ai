package dev.euryperez.kotlinquizai

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.euryperez.kotlinquizai.features.difficultyLevel.DifficultyLevelScreen
import dev.euryperez.kotlinquizai.features.quizGame.QuizGameScreen
import dev.euryperez.kotlinquizai.utils.AppNavigation
import dev.euryperez.kotlinquizai.utils.compositionLocals.LocalNavController
import dev.euryperez.kotlinquizai.utils.compositionLocals.LocalSnackBarHostState

@Composable
fun KotlinQuizApp(modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(LocalSnackBarHostState.current) },
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text(text = stringResource(R.string.toolbar_title)) },
                navigationIcon = { BackButton() }
            )
        }
    ) {
        AppNavHost(modifier = Modifier.padding(it))
    }
}

@Composable
private fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = LocalNavController.current
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = AppNavigation.DifficultyLevelDestination.navRoute.route
    ) {
        composable(AppNavigation.DifficultyLevelDestination.navRoute.route) { DifficultyLevelScreen() }

        composable(AppNavigation.QuizGameDestination.navRoute.route) { QuizGameScreen() }
    }
}

@Composable
private fun BackButton(
    modifier: Modifier = Modifier,
    navController: NavController = LocalNavController.current
) {
    var isVisible by remember { mutableStateOf(navController.previousBackStackEntry != null) }

    if (isVisible) {
        IconButton(
            modifier = modifier,
            onClick = { navController.navigateUp() }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back"
            )
        }
    }

    DisposableEffect(navController) {
        val listener = NavController.OnDestinationChangedListener { controller, _, _ ->
            isVisible = controller.previousBackStackEntry != null
        }

        navController.addOnDestinationChangedListener(listener)

        onDispose { navController.removeOnDestinationChangedListener(listener) }
    }
}

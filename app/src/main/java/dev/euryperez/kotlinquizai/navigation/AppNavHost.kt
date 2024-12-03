package dev.euryperez.kotlinquizai.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dev.euryperez.kotlinquizai.features.difficultyLevel.DifficultyLevelScreen
import dev.euryperez.kotlinquizai.features.quizGame.QuizGameScreen
import dev.euryperez.kotlinquizai.utils.compositionLocals.LocalNavController

@Composable
internal fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = LocalNavController.current
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = DifficultyLevelRoute
    ) {
        composable<DifficultyLevelRoute> { DifficultyLevelScreen() }

        composable<QuizGameRoute> { QuizGameScreen() }
    }
}

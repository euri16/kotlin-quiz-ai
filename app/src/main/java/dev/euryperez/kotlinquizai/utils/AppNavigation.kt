package dev.euryperez.kotlinquizai.utils

import dev.euryperez.kotlinquizai.models.DifficultyLevel

sealed interface AppNavigation {
    val route: String

    data object DifficultyLevelDestination : AppNavigation {
        override val route: String = "difficultyScreen"
    }

    data object QuizGameDestination : AppNavigation {
        const val DIFFICULTY_LEVEL_ARG = "difficultyLevel"

        override val route = "quizGameScreen/{$DIFFICULTY_LEVEL_ARG}"

        fun getRouteWithArgs(difficultyLevelType: DifficultyLevel) =
            "quizGameScreen/${difficultyLevelType.name}"
    }
}

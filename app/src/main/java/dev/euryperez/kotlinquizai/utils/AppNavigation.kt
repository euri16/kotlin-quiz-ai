package dev.euryperez.kotlinquizai.utils

import dev.euryperez.kotlinquizai.models.DifficultyLevel

sealed interface AppNavigation {
    val navRoute: NavRoute

    data object DifficultyLevelDestination : AppNavigation {
        override val navRoute: NavRoute = NavRoute("difficultyScreen")
    }

    data object QuizGameDestination : AppNavigation {
        const val DIFFICULTY_LEVEL_ARG = "difficultyLevel"

        override val navRoute = NavRoute("quizGameScreen/{$DIFFICULTY_LEVEL_ARG}")

        fun getNavRoute(difficultyLevelType: DifficultyLevel) =
            NavRoute("quizGameScreen/${difficultyLevelType.name}")
    }
}

@JvmInline
value class NavRoute(val route: String)
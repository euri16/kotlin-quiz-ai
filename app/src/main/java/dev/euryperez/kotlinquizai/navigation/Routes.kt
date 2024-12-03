package dev.euryperez.kotlinquizai.navigation

import dev.euryperez.kotlinquizai.models.DifficultyLevel
import kotlinx.serialization.Serializable

sealed interface KotlinQuizAppRoute

@Serializable
data object DifficultyLevelRoute : KotlinQuizAppRoute

@Serializable
data class QuizGameRoute(val difficultyLevel: DifficultyLevel) : KotlinQuizAppRoute

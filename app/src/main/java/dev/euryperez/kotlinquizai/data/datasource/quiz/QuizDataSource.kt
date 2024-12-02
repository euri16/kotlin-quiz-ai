package dev.euryperez.kotlinquizai.data.datasource.quiz

import androidx.annotation.IntRange
import dev.euryperez.kotlinquizai.data.common.NetworkResponse
import dev.euryperez.kotlinquizai.models.DifficultyLevel
import dev.euryperez.kotlinquizai.models.Quiz

interface QuizDataSource {
    suspend fun getQuizResponse(
        difficultyLevel: DifficultyLevel,
        @IntRange(from = 1, to = 10) numberOfQuestions: Int
    ): NetworkResponse<Quiz>
}

package dev.euryperez.kotlinquizai.data

import dev.euryperez.kotlinquizai.data.common.NetworkResponse
import dev.euryperez.kotlinquizai.data.datasource.quiz.QuizDataSource
import dev.euryperez.kotlinquizai.models.DifficultyLevel
import dev.euryperez.kotlinquizai.models.Quiz
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuizRepository @Inject constructor(private val quizDataSource: QuizDataSource) {

    private val _quizSharedFlow = MutableSharedFlow<NetworkResponse<Quiz>>()
    val quizSharedFlow = _quizSharedFlow.asSharedFlow()

    suspend fun generateQuiz(difficultyLevel: DifficultyLevel, numberOfQuestions: Int) {
        quizDataSource.getQuizResponse(difficultyLevel, numberOfQuestions = numberOfQuestions)
            .also { _quizSharedFlow.emit(it) }
    }
}

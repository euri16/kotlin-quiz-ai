package dev.euryperez.kotlinquizai.data

import android.util.Log
import androidx.annotation.IntRange
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.ai.client.generativeai.type.SerializationException
import com.google.ai.client.generativeai.type.content
import dev.euryperez.kotlinquizai.data.common.NetworkResponse
import dev.euryperez.kotlinquizai.data.common.getSamplePrompt
import dev.euryperez.kotlinquizai.data.dto.QuestionDTO
import dev.euryperez.kotlinquizai.data.dto.toModel
import dev.euryperez.kotlinquizai.models.DifficultyLevel
import dev.euryperez.kotlinquizai.models.Quiz
import dev.euryperez.kotlinquizai.utils.DispatcherProvider
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuizRepository @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
    private val model: GenerativeModel,
) {
    private val _quizSharedFlow = MutableSharedFlow<NetworkResponse<Quiz>>()
    val quizSharedFlow = _quizSharedFlow.asSharedFlow()

    @OptIn(ExperimentalSerializationApi::class)
    private val json = Json {
        allowTrailingComma = true
    }

    suspend fun generateQuiz(difficultyLevel: DifficultyLevel) = coroutineScope {
        launch {
            getQuizResponse(difficultyLevel, numberOfQuestions = 4)
                .also { _quizSharedFlow.emit(it) }
        }

        launch {
            getQuizResponse(difficultyLevel, numberOfQuestions = 6, useModelTraining = true)
                .let { _quizSharedFlow.emit(it) }
        }
    }

    private suspend fun getQuizResponse(
        difficultyLevel: DifficultyLevel,
        @IntRange(from = 1, to = 10) numberOfQuestions: Int,
        useModelTraining: Boolean = false
    ): NetworkResponse<Quiz> {
        return try {
            withContext(dispatcherProvider.default) {
                getGeminiResponse(difficultyLevel, numberOfQuestions, useModelTraining)
                    .takeIf { it.isSuccess } // 1
                    ?.getOrNull() // 2
                    ?.text // 3
                    ?.replace("```json", "") // 4
                    ?.replace("```", "") // 5
                    ?.also { Log.d("QuizRepository", it) } // 6
                    ?.let { json.decodeFromString<List<QuestionDTO>>(it) } // 7
                    ?.map(QuestionDTO::toModel) // 8
                    ?.let(::Quiz) // 9
                    ?.let { NetworkResponse.Success(it) } // 10
                    ?: NetworkResponse.Error(throwable = Throwable("response is null")) // 11
            }
        } catch (ex: SerializationException) {
            NetworkResponse.Error(ex)
        } catch (ex: IllegalArgumentException) {
            NetworkResponse.Error(ex)
        }
    }

    /**
     * Generates a list of random single-choice quiz questions at a specified difficulty level.
     *
     * @param difficultyLevel the difficulty level of the questions to generate
     * @param numberOfQuestions the number of questions to generate
     * @param useModelTraining whether to include a sample prompt for model training
     */
    private suspend fun getGeminiResponse(
        difficultyLevel: DifficultyLevel,
        @IntRange(from = 1, to = 10) numberOfQuestions: Int,
        useModelTraining: Boolean = false
    ): Result<GenerateContentResponse> {
        return withContext(dispatcherProvider.io) {
            runCatching {
                model.generateContent(
                    content {
                        // 1
                        buildPrompt(difficultyLevel, numberOfQuestions).also { text(it) }

                        if (useModelTraining) {
                            // 2
                            text(difficultyLevel.getSamplePrompt())
                            text("Give me $numberOfQuestions more random questions.")
                        } else {
                            // 3
                            text("output: ")
                        }
                    }
                )
            }
        }
    }

    private fun buildPrompt(difficulty: DifficultyLevel, numberOfQuestions: Int) = """
        Generate a list of $numberOfQuestions random single-choice quiz questions, 
        at a ${difficulty.displayValue} difficulty level, to evaluate knowledge of Kotlin. 
        The questions should cover concepts referencing the Kotlin documentation: 
        https://kotlinlang.org/docs/home.html. Each question should have 3 answer options, 
        with only one correct answer. The output should be in valid JSON format as follows:
        [
          {
            "id": "String",
            "question": "String",
            "options": [
              { "id": "1", "text": "String" },
              { "id": "2", "text": "String" },
              { "id": "3", "text": "String" }
            ],
            "correct_answer": "1",
            "explanation": "String"
          }
        ]
    """.trimIndent()
}

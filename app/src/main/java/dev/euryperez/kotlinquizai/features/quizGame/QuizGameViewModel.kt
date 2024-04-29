package dev.euryperez.kotlinquizai.features.quizGame

import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.euryperez.kotlinquizai.R
import dev.euryperez.kotlinquizai.data.QuizRepository
import dev.euryperez.kotlinquizai.data.common.NetworkResponse
import dev.euryperez.kotlinquizai.models.Answer
import dev.euryperez.kotlinquizai.models.DifficultyLevel
import dev.euryperez.kotlinquizai.models.Question
import dev.euryperez.kotlinquizai.utils.AppNavigation
import dev.euryperez.kotlinquizai.utils.DispatcherProvider
import dev.euryperez.kotlinquizai.utils.DispatcherProviderImpl
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizGameViewModel @Inject constructor(
    private val quizRepository: QuizRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _viewStateFlow = MutableStateFlow(ViewState())
    val viewStateFlow = _viewStateFlow.asStateFlow()

    private val questions = mutableListOf<Question>()
    private val answersMap = mutableMapOf<String, Answer>()
    private var selectedQuestionIndex = 0

    init {
        savedStateHandle.get<String>(AppNavigation.QuizGameDestination.DIFFICULTY_LEVEL_ARG)
            ?.let(DifficultyLevel::valueOf)
            ?.also { difficultyLevel ->
                viewModelScope.launch {
                    quizRepository.generateQuiz(difficultyLevel = difficultyLevel)
                }
            }
            ?: _viewStateFlow.update { it.copy(viewEffect = ViewEffect.ErrorGettingDifficultyLevel) }

        quizRepository.quizSharedFlow
            .onEach { response ->
                when (response) {
                    is NetworkResponse.Success -> {
                        questions.addAll(response.data.questions)

                        if (_viewStateFlow.value.isLoading) {
                            _viewStateFlow.update { viewState ->
                                viewState.copy(
                                    isLoading = false,
                                    gameState = GameState.InProgress(selectedQuestion = questions.first())
                                )
                            }
                        }
                    }

                    is NetworkResponse.Error -> {
                        Log.e("QuizGameViewModel", "Error: $response")
                        _viewStateFlow.update { it.copy(viewEffect = ViewEffect.ShowSnackBar(R.string.general_error)) }
                    }
                }
            }.launchIn(viewModelScope)
    }

    fun processEvent(event: ViewEvent) {
        when (event) {
            ViewEvent.NextQuestion -> showNextQuestionOrFinish()

            is ViewEvent.SelectAnswer -> {
                _viewStateFlow.update {
                    it.copy(gameState = it.gameState.copy(selectedAnswerId = event.answer.id))
                }

                (viewStateFlow.value.gameState as GameState.InProgress)
                    .selectedQuestion
                    .also { answersMap[it.id] = event.answer }

                viewModelScope.launch {
                    delay(5000)
                    showNextQuestionOrFinish()
                }
            }

            ViewEvent.ConsumeEffect -> _viewStateFlow.update { it.copy(viewEffect = null) }
        }
    }

    private fun showNextQuestionOrFinish() {
        selectedQuestionIndex++

        val isGameFinished = questions.size <= selectedQuestionIndex
        if (isGameFinished) {
            viewModelScope.launch {
                questions.map { it.copy(options = listOfNotNull(answersMap[it.id])) }
                    .also { allResponses ->
                        val totalCorrectAnswers = allResponses.count { question ->
                            question.options.firstOrNull()?.isCorrectAnswer == true
                        }

                        val scorePercentage =
                            ((totalCorrectAnswers / allResponses.size.toFloat()) * 100).toInt()

                        _viewStateFlow.update {
                            it.copy(
                                gameState = GameState.Finished(
                                    allResponses,
                                    totalCorrectAnswers,
                                    scorePercentage
                                )
                            )
                        }
                    }
            }
        } else {
            _viewStateFlow.update {
                it.copy(gameState = GameState.InProgress(selectedQuestion = questions[selectedQuestionIndex]))
            }
        }
    }

    data class ViewState(
        val isLoading: Boolean = true,
        val gameState: GameState = GameState.InProgress(),
        val viewEffect: ViewEffect? = null
    )

    sealed interface ViewEffect {
        data class ShowSnackBar(@StringRes val stringRes: Int) : ViewEffect
        data object ErrorGettingDifficultyLevel : ViewEffect
    }

    sealed interface ViewEvent {
        data object NextQuestion : ViewEvent
        data class SelectAnswer(val answer: Answer) : ViewEvent
        data object ConsumeEffect : ViewEvent
    }

    sealed interface GameState {
        data class InProgress(
            val selectedQuestion: Question = Question.empty,
            val selectedAnswerId: String? = null
        ) : GameState

        data class Finished(
            val responses: List<Question> = emptyList(),
            val totalCorrectAnswers: Int = 0,
            val scorePercentage: Int = 0
        ) : GameState

        fun copy(selectedQuestion: Question? = null, selectedAnswerId: String? = null): GameState {
            return when (this) {
                is InProgress -> copy(
                    selectedQuestion = selectedQuestion ?: this.selectedQuestion,
                    selectedAnswerId = selectedAnswerId ?: this.selectedAnswerId
                )

                is Finished -> throw IllegalAccessException("Finished state cannot be modified")
            }
        }
    }
}

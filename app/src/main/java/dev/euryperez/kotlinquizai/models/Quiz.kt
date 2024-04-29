package dev.euryperez.kotlinquizai.models

data class Quiz(val questions: List<Question>)

data class Question(
    val id: String,
    val question: String,
    val options: List<Answer>,
    val correctAnswerId: String,
    val explanation: String
) {
    companion object {
        val empty
            get() = Question("", "", emptyList(), "", "")
    }
}

data class Answer(val id: String, val text: String, val isCorrectAnswer: Boolean = false)
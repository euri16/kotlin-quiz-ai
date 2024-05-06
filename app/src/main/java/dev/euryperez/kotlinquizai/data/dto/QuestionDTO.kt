package dev.euryperez.kotlinquizai.data.dto

import dev.euryperez.kotlinquizai.models.Answer
import dev.euryperez.kotlinquizai.models.Question
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QuestionDTO(
    val id: String,
    val question: String,
    val options: List<AnswerDTO>,
    @SerialName("correct_answer")
    val correctAnswerId: String,
    val explanation: String
)

fun QuestionDTO.toModel() = Question(
    id,
    question,
    options.map { Answer(it.id, it.text, it.id == correctAnswerId) },
    correctAnswerId,
    explanation
)

package com.andika.interactivequizapp.model

import kotlinx.serialization.Serializable


@Serializable
data class QuizResponse(
    val quizTitle: String = "AI Quiz",
    val questions: List<QuizQuestion> = emptyList()
)


@Serializable
data class QuizQuestion(
    val question: String = "",
    val options: List<String> = emptyList(),
    val correctAnswer: String = "",
    val rationale: String = ""
)

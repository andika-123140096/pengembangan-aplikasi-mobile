package com.example.demop4app.data.model

data class Todo(
    val id: Int,
    val text: String,
    val isDone: Boolean = false,
    val isFavorite: Boolean = false
)

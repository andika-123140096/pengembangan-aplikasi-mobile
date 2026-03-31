package com.example.demop4app.data.model

data class TodoUiState(
    val todos: List<Todo> = emptyList(),
    val inputText: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

package com.andika.interactivequizapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andika.interactivequizapp.model.QuizQuestion
import com.andika.interactivequizapp.model.QuizResponse
import com.andika.interactivequizapp.service.GeminiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


sealed class QuizUiState {
    data object Initial : QuizUiState()
    data class Loading(val message: String) : QuizUiState()
    data class Success(val quiz: QuizResponse) : QuizUiState()
    data class Error(val message: String) : QuizUiState()
}


data class SelectedFile(
    val name: String,
    val content: ByteArray,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as SelectedFile
        if (name != other.name) return false
        if (!content.contentEquals(other.content)) return false
        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + content.contentHashCode()
        return result
    }
}


class QuizViewModel(private val geminiService: GeminiService = GeminiService()) : ViewModel() {

    private val _uiState = MutableStateFlow<QuizUiState>(QuizUiState.Initial)
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    private val _selectedFiles = MutableStateFlow<List<SelectedFile>>(emptyList())
    val selectedFiles: StateFlow<List<SelectedFile>> = _selectedFiles.asStateFlow()

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    private val _difficulty = MutableStateFlow("Medium")
    val difficulty: StateFlow<String> = _difficulty.asStateFlow()

    private val _questionCount = MutableStateFlow(5)
    val questionCount: StateFlow<Int> = _questionCount.asStateFlow()

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

    private val _score = MutableStateFlow(0)
    val score: StateFlow<Int> = _score.asStateFlow()

    private val _selectedAnswer = MutableStateFlow<String?>(null)
    val selectedAnswer: StateFlow<String?> = _selectedAnswer.asStateFlow()

    private val _isAnswerCorrect = MutableStateFlow<Boolean?>(null)
    val isAnswerCorrect: StateFlow<Boolean?> = _isAnswerCorrect.asStateFlow()

    
    fun updateInputText(text: String) {
        _inputText.value = text
    }

    
    fun addFile(name: String, content: ByteArray) {
        _selectedFiles.value += SelectedFile(name, content)
    }

    
    fun removeFile(file: SelectedFile) {
        _selectedFiles.value -= file
    }

    
    fun setDifficulty(difficulty: String) {
        _difficulty.value = difficulty
    }

    
    fun setQuestionCount(count: Int) {
        _questionCount.value = count
    }

    
    fun startQuiz() {
        viewModelScope.launch {
            _uiState.value = QuizUiState.Loading("Initializing quiz...")
            try {
                val inputText = _inputText.value
                val total = _questionCount.value
                val files = _selectedFiles.value
                val difficulty = _difficulty.value
                
                _uiState.value = QuizUiState.Loading("Generating quiz title...")
                val title = geminiService.generateTitle(inputText, files)
                
                val questions = mutableListOf<QuizQuestion>()
                for (i in 1..total) {
                    var success = false
                    while (!success) {
                        try {
                            _uiState.value = QuizUiState.Loading("Generating question $i of $total...")
                            val question = geminiService.generateQuestion(
                                prompt = inputText,
                                files = files,
                                difficulty = difficulty,
                                index = i,
                                total = total,
                                excludedQuestions = questions.map { it.question }
                            )
                            questions.add(question)
                            success = true
                        } catch (e: Exception) {
                            kotlinx.coroutines.delay(1000L)
                        }
                    }
                }
                
                _uiState.value = QuizUiState.Success(QuizResponse(title, questions))
                _currentQuestionIndex.value = 0
                _score.value = 0
            } catch (e: Exception) {
                _uiState.value = QuizUiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    
    fun submitAnswer(answer: String, correctAnswer: String) {
        if (_selectedAnswer.value != null) return 

        _selectedAnswer.value = answer
        val isCorrect = answer.trim().lowercase() == correctAnswer.trim().lowercase()
        _isAnswerCorrect.value = isCorrect
        
        if (isCorrect) {
            _score.value += 1
        }
    }

    
    fun nextQuestion() {
        _selectedAnswer.value = null
        _isAnswerCorrect.value = null
        _currentQuestionIndex.value += 1
    }

    
    fun resetQuiz() {
        _uiState.value = QuizUiState.Initial
        _currentQuestionIndex.value = 0
        _score.value = 0
        _selectedAnswer.value = null
        _isAnswerCorrect.value = null
    }
}

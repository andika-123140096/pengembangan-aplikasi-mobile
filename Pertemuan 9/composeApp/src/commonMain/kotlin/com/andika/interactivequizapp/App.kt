package com.andika.interactivequizapp

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.andika.interactivequizapp.ui.LightGreenPrimary
import com.andika.interactivequizapp.ui.QuizScreen
import com.andika.interactivequizapp.viewmodel.QuizViewModel
import androidx.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    val quizViewModel: QuizViewModel = viewModel { QuizViewModel() }
    
    val colorScheme = lightColorScheme(
        primary = LightGreenPrimary,
        onPrimary = androidx.compose.ui.graphics.Color.White,
        primaryContainer = androidx.compose.ui.graphics.Color(0xFFC8E6C9),
        onPrimaryContainer = androidx.compose.ui.graphics.Color(0xFF1B5E20)
    )

    MaterialTheme(colorScheme = colorScheme) {
        QuizScreen(quizViewModel)
    }
}

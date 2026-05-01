package com.andika.interactivequizapp.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andika.interactivequizapp.model.QuizResponse
import com.andika.interactivequizapp.viewmodel.QuizUiState
import com.andika.interactivequizapp.viewmodel.QuizViewModel
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType
import kotlinx.coroutines.launch

val LightGreenPrimary = Color(0xFF81C784)
val LightGreenBackground = Color(0xFFFFFFFF)
val CorrectGreen = Color(0xFF4CAF50)
val IncorrectRed = Color(0xFFE57373)

@Composable
fun QuizScreen(viewModel: QuizViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = LightGreenBackground
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val state = uiState) {
                is QuizUiState.Initial -> InitialState(
                    viewModel = viewModel,
                    onStart = { viewModel.startQuiz() }
                )
                is QuizUiState.Loading -> LoadingState(state.message)
                is QuizUiState.Success -> QuizContent(state.quiz, viewModel)
                is QuizUiState.Error -> {
                    InitialState(
                        viewModel = viewModel,
                        onStart = { viewModel.startQuiz() }
                    )
                    LaunchedEffect(state) {
                        snackbarHostState.showSnackbar(
                            message = state.message,
                            duration = SnackbarDuration.Short
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InitialState(viewModel: QuizViewModel, onStart: () -> Unit) {
    val text by viewModel.inputText.collectAsState()
    val selectedFiles by viewModel.selectedFiles.collectAsState()
    val difficulty by viewModel.difficulty.collectAsState()
    val questionCount by viewModel.questionCount.collectAsState()
    val scope = rememberCoroutineScope()

    val launcher = rememberFilePickerLauncher(
        type = PickerType.File(extensions = listOf("pdf", "txt")),
        mode = PickerMode.Multiple(),
        title = "Select Documents"
    ) { files ->
        files?.forEach { file ->
            scope.launch {
                val bytes = file.readBytes()
                viewModel.addFile(file.name, bytes)
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            "Interactive Quiz App",
            style = MaterialTheme.typography.headlineMedium,
            color = LightGreenPrimary,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Difficulty:",
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Easy", "Medium", "Hard").forEach { level ->
                SelectionCard(
                    text = level,
                    isSelected = difficulty == level,
                    onClick = { viewModel.setDifficulty(level) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "Number of Questions:",
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(5, 10, 15).forEach { count ->
                SelectionCard(
                    text = count.toString(),
                    isSelected = questionCount == count,
                    onClick = { viewModel.setQuestionCount(count) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = text,
            onValueChange = { viewModel.updateInputText(it) },
            label = { Text("Paste context text here (optional)...") },
            modifier = Modifier.fillMaxWidth().height(150.dp),
            shape = RoundedCornerShape(12.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            "Selected Documents:",
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        if (selectedFiles.isEmpty()) {
            Text("No files selected", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        } else {
            selectedFiles.forEach { file ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White)
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Description, contentDescription = null, tint = LightGreenPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(file.name, modifier = Modifier.weight(1f), maxLines = 1)
                    IconButton(onClick = { viewModel.removeFile(file) }) {
                        Icon(Icons.Default.Close, contentDescription = "Remove", tint = IncorrectRed)
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = { launcher.launch() },
            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = LightGreenPrimary),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(48.dp).border(1.dp, LightGreenPrimary, RoundedCornerShape(12.dp))
        ) {
            Text("Add Files (PDF/TXT)")
        }

        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = { if (text.isNotBlank() || selectedFiles.isNotEmpty()) onStart() },
            colors = ButtonDefaults.buttonColors(containerColor = LightGreenPrimary),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            val uiStateByViewModel by viewModel.uiState.collectAsState()
            if (uiStateByViewModel is QuizUiState.Error) {
                Icon(Icons.Default.Refresh, null)
                Spacer(Modifier.width(8.dp))
                Text("Retry Generate Quiz", fontSize = 18.sp)
            } else {
                Text("Generate Quiz with AI", fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun SelectionCard(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) LightGreenPrimary else Color.White)
            .border(1.dp, if (isSelected) Color.Transparent else Color.LightGray, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.White else Color.Black,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 14.sp
        )
    }
}

@Composable
fun LoadingState(message: String) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        ShimmerItem()
        Spacer(modifier = Modifier.height(16.dp))
        Text(message, color = LightGreenPrimary, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun ShimmerItem() {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.6f),
        Color.LightGray.copy(alpha = 0.2f),
        Color.LightGray.copy(alpha = 0.6f),
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = androidx.compose.ui.geometry.Offset.Zero,
        end = androidx.compose.ui.geometry.Offset(x = translateAnim, y = translateAnim)
    )

    Column {
        repeat(3) {
            Box(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth()
                    .height(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(brush)
            )
        }
    }
}

@Composable
fun QuizContent(quiz: QuizResponse, viewModel: QuizViewModel) {
    val currentIndex by viewModel.currentQuestionIndex.collectAsState()
    val score by viewModel.score.collectAsState()
    val selectedAnswer by viewModel.selectedAnswer.collectAsState()
    val isCorrect by viewModel.isAnswerCorrect.collectAsState()
    val haptic = LocalHapticFeedback.current

    if (currentIndex >= quiz.questions.size) {
        QuizResult(score, quiz.questions.size, onRestart = { viewModel.resetQuiz() })
        return
    }

    val currentQuestion = quiz.questions[currentIndex]

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.resetQuiz() }) {
                Icon(Icons.Default.Close, contentDescription = "Back to Home", tint = Color.Gray)
            }
            Text(
                "Back to Home",
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray
            )
        }

        LinearProgressIndicator(
            progress = { (currentIndex + 1).toFloat() / quiz.questions.size },
            modifier = Modifier.fillMaxWidth().height(6.dp),
            color = LightGreenPrimary,
            trackColor = Color(0xFFF5F5F5)
        )

        AnimatedContent(
            targetState = currentIndex,
            transitionSpec = {
                fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
            },
            label = "quiz_content"
        ) { targetIndex ->
            val question = quiz.questions[targetIndex]
            Column(modifier = Modifier.padding(24.dp).verticalScroll(rememberScrollState())) {
                Text(
                    "Question ${targetIndex + 1} of ${quiz.questions.size}",
                    style = MaterialTheme.typography.labelLarge,
                    color = LightGreenPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    question.question,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(24.dp))

                question.options.forEach { option ->
                    val isSelected = selectedAnswer == option
                    val backgroundColor = when {
                        isSelected && isCorrect == true -> CorrectGreen
                        isSelected && isCorrect == false -> IncorrectRed
                        else -> Color.White
                    }
                    val borderColor = if (isSelected) Color.Transparent else LightGreenPrimary

                    Box(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(backgroundColor)
                            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                            .clickable(enabled = selectedAnswer == null) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.submitAnswer(option, currentQuestion.correctAnswer)
                            }
                            .padding(16.dp)
                    ) {
                        Text(
                            option,
                            color = if (isSelected) Color.White else Color.Black,
                            fontSize = 16.sp
                        )
                    }
                }

                AnimatedVisibility(visible = selectedAnswer != null && isCorrect != null) {
                    Column(modifier = Modifier.padding(top = 16.dp)) {
                        val resultText = if (isCorrect == true) "Correct!" else "Incorrect"
                        val resultColor = if (isCorrect == true) CorrectGreen else IncorrectRed
                        
                        Text(
                            resultText,
                            color = resultColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            question.rationale,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { viewModel.nextQuestion() },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = LightGreenPrimary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(if (currentIndex < quiz.questions.size - 1) "Next Question" else "See Results")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuizResult(score: Int, total: Int, onRestart: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Quiz Completed!", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Your Score: $score / $total",
            style = MaterialTheme.typography.displaySmall,
            color = LightGreenPrimary,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onRestart,
            colors = ButtonDefaults.buttonColors(containerColor = LightGreenPrimary),
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Refresh, null)
            Spacer(Modifier.width(8.dp))
            Text("Try Again")
        }
    }
}

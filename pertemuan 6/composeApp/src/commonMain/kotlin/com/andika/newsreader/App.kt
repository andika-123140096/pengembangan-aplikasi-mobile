package com.andika.newsreader

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.background
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.andika.newsreader.data.HackerNewsRepository
import com.andika.newsreader.model.NewsArticle
import com.andika.newsreader.ui.NewsDetailScreen
import com.andika.newsreader.ui.NewsListScreen
import kotlinx.coroutines.launch

@Composable
@Preview
fun App() {
    val repository = remember { HackerNewsRepository() }
    val scope = rememberCoroutineScope()

    var uiState by remember { mutableStateOf<NewsUiState>(NewsUiState.Loading) }
    var isRefreshing by remember { mutableStateOf(false) }
    var selectedArticleId by remember { mutableStateOf<Long?>(null) }

    fun loadStories(userRefresh: Boolean) {
        val previousSuccess = uiState as? NewsUiState.Success

        if (userRefresh && previousSuccess != null) {
            isRefreshing = true
        } else {
            uiState = NewsUiState.Loading
        }

        scope.launch {
            runCatching { repository.fetchTopStories(limit = 20) }
                .onSuccess { stories ->
                    uiState = if (stories.isEmpty()) {
                        NewsUiState.Error("No stories found from Hacker News")
                    } else {
                        NewsUiState.Success(stories)
                    }
                }
                .onFailure { throwable ->
                    if (previousSuccess != null && userRefresh) {
                        uiState = previousSuccess.copy(
                            refreshError = "Refresh gagal. Coba tarik lagi."
                        )
                    } else {
                        uiState = NewsUiState.Error(
                            message = "Gagal memuat berita: ${throwable.message ?: "Unknown error"}"
                        )
                    }
                }

            isRefreshing = false
        }
    }

    LaunchedEffect(Unit) {
        loadStories(userRefresh = false)
    }

    MaterialTheme {
        Column(
            modifier = Modifier
                .background(Color(0xFFF4F7F2))
                .fillMaxSize(),
        ) {
            AppHeader()

            when (val state = uiState) {
                NewsUiState.Loading -> LoadingState()

                is NewsUiState.Error -> ErrorState(
                    message = state.message,
                    onRetry = { loadStories(userRefresh = false) },
                )

                is NewsUiState.Success -> {
                    val selected = state.articles.firstOrNull { it.id == selectedArticleId }

                    if (selected != null) {
                        NewsDetailScreen(
                            article = selected,
                            onBackClick = { selectedArticleId = null },
                            modifier = Modifier.fillMaxSize(),
                        )
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxSize(),
                        ) {
                            if (state.refreshError != null) {
                                Text(
                                    text = state.refreshError,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                )
                            }

                            NewsListScreen(
                                articles = state.articles,
                                isRefreshing = isRefreshing,
                                onRefresh = { loadStories(userRefresh = true) },
                                onArticleClick = { article -> selectedArticleId = article.id },
                                modifier = Modifier.fillMaxSize(),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AppHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1B5E20)),
    ) {
        Text(
            text = "News Reader",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            modifier = Modifier
                .statusBarsPadding()
                .padding(horizontal = 14.dp, vertical = 12.dp),
        )
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            CircularProgressIndicator()
            Text(
                text = "Loading top stories...",
                color = Color.Black,
            )
        }
    }
}

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black,
            )
            Button(onClick = onRetry) {
                Text("Try Again")
            }
        }
    }
}

private sealed interface NewsUiState {
    data object Loading : NewsUiState
    data class Success(
        val articles: List<NewsArticle>,
        val refreshError: String? = null,
    ) : NewsUiState

    data class Error(val message: String) : NewsUiState
}
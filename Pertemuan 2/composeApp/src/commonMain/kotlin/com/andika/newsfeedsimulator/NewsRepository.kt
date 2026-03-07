package com.andika.newsfeedsimulator

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

val NEWS_CATEGORIES = listOf("Olahraga", "Teknologi", "Politik", "Kesehatan")

object NewsRepository {
    private var articleCounter = 0

    val latestNews: Flow<NewsArticle> = flow {
        while (true) {
            delay(2000)
            val newArticle = NewsArticle(
                id = ++articleCounter,
                title = "Berita Penting #${articleCounter}",
                category = NEWS_CATEGORIES.random()
            )
            emit(newArticle)
        }
    }
        .catch { e ->
            emit(NewsArticle(0, "Error: ${e.message}", "Error", "Terjadi kesalahan"))
        }
}
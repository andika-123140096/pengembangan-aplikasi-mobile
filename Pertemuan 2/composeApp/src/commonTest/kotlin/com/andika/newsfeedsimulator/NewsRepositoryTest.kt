package com.andika.newsfeedsimulator

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class NewsRepositoryTest {

    @Test
    fun testLatestNewsEmitsArticle() = runTest {
        val repository = NewsRepository

        val article = withTimeout(3000) {
            repository.latestNews.first()
        }

        assertNotNull(article)
        assertTrue(article.title.contains("Berita Penting"))
    }

    @Test
    fun testArticleHasValidCategory() = runTest {
        val repository = NewsRepository

        val article = withTimeout(3000) {
            repository.latestNews.first()
        }

        assertTrue(NEWS_CATEGORIES.contains(article.category))
    }

    @Test
    fun testMultipleArticlesEmitted() = runTest {
        val repository = NewsRepository

        val articles = withTimeout(6000) {
            repository.latestNews.take(2).toList()
        }

        assertEquals(articles.size, 2, "Should emit 2 articles")
        assertTrue(articles[0].id < articles[1].id, "IDs should increment")
    }
}
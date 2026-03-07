package com.andika.newsfeedsimulator

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NewsViewModel(coroutineScope: CoroutineScope) {

    private val _allNews = MutableStateFlow<List<NewsArticle>>(emptyList())
    private val _categoryFilter = MutableStateFlow("Semua")
    val categoryFilter: StateFlow<String> = _categoryFilter.asStateFlow()

    private val _readArticlesCount = MutableStateFlow(0)
    val readArticlesCount: StateFlow<Int> = _readArticlesCount.asStateFlow()

    private val _readArticleIds = MutableStateFlow<Set<Int>>(emptySet())

    init {
        coroutineScope.launch {
            NewsRepository.latestNews.collect { newArticle ->
                _allNews.value += newArticle
            }
        }
    }

    val filteredNews: StateFlow<List<DisplayArticle>> =
        combine(_allNews, _categoryFilter) { articles, category ->
            articles
                .filter {
                    category == "Semua" || it.category.equals(category, ignoreCase = true)
                }
                .map {
                    DisplayArticle(
                        id = it.id,
                        displayTitle = it.title,
                        category = it.category
                    )
                }
                .sortedByDescending { it.id }
        }.stateIn(coroutineScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setCategoryFilter(category: String) {
        _categoryFilter.value = category
    }

    fun markAsRead(articleId: Int) {
        if (!_readArticleIds.value.contains(articleId)) {
            _readArticleIds.value += articleId
            _readArticlesCount.value = _readArticleIds.value.size
        }
    }

    suspend fun fetchArticleDetail(articleId: Int): NewsArticle? {
        return try {
            delay(500)
            _allNews.value.find { it.id == articleId }
        } catch (e: Exception) {
            null
        }
    }
}
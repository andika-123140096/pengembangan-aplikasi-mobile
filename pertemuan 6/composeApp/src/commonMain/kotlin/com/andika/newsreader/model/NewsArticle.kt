package com.andika.newsreader.model

data class NewsArticle(
    val id: Long,
    val title: String,
    val description: String,
    val imageUrl: String?,
    val articleUrl: String?,
    val author: String,
)

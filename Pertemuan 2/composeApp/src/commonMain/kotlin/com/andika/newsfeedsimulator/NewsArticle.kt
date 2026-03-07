package com.andika.newsfeedsimulator

data class NewsArticle(
    val id: Int,
    val title: String,
    val category: String,
    val content: String = "Ini adalah konten berita lengkap untuk artikel $id."
)
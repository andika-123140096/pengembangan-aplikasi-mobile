package com.andika.newsreader

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
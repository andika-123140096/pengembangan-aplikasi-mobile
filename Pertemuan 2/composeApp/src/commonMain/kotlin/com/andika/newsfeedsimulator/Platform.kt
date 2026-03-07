package com.andika.newsfeedsimulator

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
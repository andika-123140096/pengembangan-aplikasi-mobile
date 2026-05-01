package com.andika.interactivequizapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
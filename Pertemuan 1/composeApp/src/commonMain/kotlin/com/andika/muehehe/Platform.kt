package com.andika.muehehe

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
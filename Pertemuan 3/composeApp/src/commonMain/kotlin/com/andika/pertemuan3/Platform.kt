package com.andika.pertemuan3

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
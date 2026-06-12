package io.ak1.rangvikalpsample

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
package icu.hku.vekumin

import kotlinx.serialization.Serializable

@Serializable
data class Secret(val platform: String, val keys: Map<String, String>)

interface Postable {
    fun post(title: String, content: String)
}

class ThreadPoster(val secret: Secret) : Postable {

    override fun post(title: String, content: String) {
        println("Posting thread with title: $title and content: $content")
    }
}

class XPoster(val secret: Secret) : Postable {
    override fun post(title: String, content: String) {
        println("Posting X with title: $title and content: $content")
    }
}

fun createPostable(secret: Secret): Postable {
    return when (secret.platform) {
        "thread" -> ThreadPoster(secret)
        "x" -> XPoster(secret)
        else -> throw IllegalArgumentException("Unknown platform: ${secret.platform}")
    }
}
package icu.hku.vekumin.post

enum class Platform {
    THREAD, X, FAKE
}

data class Secret(val platform: Platform, val keys: Map<String, String>)

interface Postable {
    fun post(title: String, content: String)
}

class ThreadPoster(val secret: Secret) : Postable {

    override fun post(title: String, content: String) {
        // TODO: Implement posting to Thread
    }
}

class XPoster(val secret: Secret) : Postable {
    override fun post(title: String, content: String) {
        // TODO: Implement posting to X
    }
}

class FakePoster(val secret: Secret) : Postable {
    override fun post(title: String, content: String) {
        println("Posting fake with title: $title and content: $content")
        println("Secret keys: ${secret.keys}")
    }
}

fun createPostable(secret: Secret): Postable {
    return when (secret.platform) {
        Platform.X -> XPoster(secret)
        Platform.FAKE -> FakePoster(secret)
        Platform.THREAD -> ThreadPoster(secret)
    }
}
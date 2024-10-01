package icu.hku.vekumin

interface Postable {
    fun post(title: String, content: String)
}

class ThreadPoster : Postable {
    override fun post(title: String, content: String) {
        println("Posting thread with title: $title and content: $content")
    }
}
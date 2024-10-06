package icu.hku.vekumin.post.postables

import icu.hku.vekumin.post.Postable
import icu.hku.vekumin.post.data.Secret

class FakePoster(val secret: Secret) : Postable {
    override fun post(title: String, content: String) {
        println("Posting fake with title: $title and content: $content")
        println("Secret keys: ${secret.keys}")
    }
}
package icu.hku.vekumin.post.postables

import icu.hku.vekumin.post.Postable
import icu.hku.vekumin.post.Secret

class XPoster(val secret: Secret) : Postable {
    override fun post(title: String, content: String) {
        // TODO: Implement posting to X
    }
}
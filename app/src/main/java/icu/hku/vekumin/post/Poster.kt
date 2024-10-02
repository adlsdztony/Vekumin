package icu.hku.vekumin.post

import icu.hku.vekumin.post.postables.FakePoster
import icu.hku.vekumin.post.postables.ThreadPoster
import icu.hku.vekumin.post.postables.XPoster


fun createPostable(secret: Secret): Postable {
    return when (secret.platform) {
        Platform.X -> XPoster(secret)
        Platform.FAKE -> FakePoster(secret)
        Platform.THREAD -> ThreadPoster(secret)
    }
}
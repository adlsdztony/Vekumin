package icu.hku.vekumin.post

import android.net.Uri
import icu.hku.vekumin.post.data.Secret
import icu.hku.vekumin.post.postables.FakePoster
import icu.hku.vekumin.post.postables.ThreadPoster
import icu.hku.vekumin.post.postables.XPoster

enum class Platform {
    THREAD, X, FAKE;

    val authUrl: Uri
        get() = when (this) {
            THREAD -> Uri.parse("https://vekumin.hku.icu/api/thread/oauth")
            X -> Uri.parse("https://vekumin.hku.icu/api/twitter/oauth")
            FAKE -> Uri.parse("https://vekumin.hku.icu/api/fake/oauth")
        }

    val createPoster: (Secret) -> Postable
        get() = when (this) {
            THREAD -> { secret -> ThreadPoster(secret) }
            X -> { secret -> XPoster(secret) }
            FAKE -> { secret -> FakePoster(secret) }
        }
}

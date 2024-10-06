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
            THREAD -> Uri.parse("https://thread.icu.hku.icu/auth")
            X -> Uri.parse("https://x.icu.hku.icu/auth")
            FAKE -> Uri.parse("https://fake.icu.hku.icu/auth")
        }

    val createPoster: (Secret) -> Postable
        get() = when (this) {
            THREAD -> { secret -> ThreadPoster(secret) }
            X -> { secret -> XPoster(secret) }
            FAKE -> { secret -> FakePoster(secret) }
        }
}

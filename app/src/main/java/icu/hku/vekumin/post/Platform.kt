package icu.hku.vekumin.post

import android.net.Uri
import icu.hku.vekumin.post.data.Secret
import icu.hku.vekumin.post.postables.FakePoster
import icu.hku.vekumin.post.postables.BlueSkyPoster
import icu.hku.vekumin.post.postables.XPoster

enum class Platform {
    X, BLUESKY;
//    , FAKE;

    val authUrl: Uri
        get() = when (this) {
            BLUESKY -> Uri.parse("https://vekumin.hku.icu/api/bluesky/oauth")
            X -> Uri.parse("https://vekumin.hku.icu/api/twitter/oauth")
//            FAKE -> Uri.parse("https://vekumin.hku.icu/api/fake/oauth")
        }

    val postUrl: Uri
        get() = when (this) {
            BLUESKY -> Uri.parse("https://vekumin.hku.icu/api/bluesky/post")
            X -> Uri.parse("https://vekumin.hku.icu/api/twitter/post")
//            FAKE -> Uri.parse("https://vekumin.hku.icu/api/fake/post")
        }

    val createPoster: (Secret) -> Postable
        get() = when (this) {
            BLUESKY -> { secret -> BlueSkyPoster(secret) }
            X -> { secret -> XPoster(secret) }
//            FAKE -> { secret -> FakePoster(secret) }
        }
}

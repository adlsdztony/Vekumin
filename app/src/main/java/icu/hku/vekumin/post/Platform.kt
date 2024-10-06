package icu.hku.vekumin.post

import android.net.Uri

enum class Platform {
    THREAD, X, FAKE;

    val authUrl: Uri
        get() = when (this) {
            Platform.THREAD -> Uri.parse("https://thread.icu.hku.icu/auth")
            Platform.X -> Uri.parse("https://x.icu.hku.icu/auth")
            Platform.FAKE -> Uri.parse("https://fake.icu.hku.icu/auth")
        }
}
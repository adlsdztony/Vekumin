package icu.hku.vekumin.post.postables

import icu.hku.vekumin.post.Postable
import icu.hku.vekumin.post.data.Secret
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URLEncoder

class XPoster(val secret: Secret) : Postable {
    override fun post(title: String, content: String) {
        // encode message
        val message = URLEncoder.encode("$title\n$content", "utf-8")
        val data = "message=$message&access_token=${secret.keys["access_token"]}&access_token_secret=${secret.keys["access_token_secret"]}"

        val url = "https://vekumin.hku.icu/api/twitter/post?$data"

        // okhttp
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .get()
            .build()
        val response = client.newCall(request).execute().code()

        if (response == 200) {
            println("Post successful")
            // TODO: Implement post success handling
        } else {
            println("Post failed")
            // TODO: Implement post failure handling
        }
    }
}


//// Test
//fun main()
//{
//    val secret = Secret(
//        Platform.X,
//        mapOf("access_token" to "fake", "access_token_secret" to "fake")
//    )
//    val poster = XPoster(secret)
//    poster.post("title", "content")
//}
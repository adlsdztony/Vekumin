package icu.hku.vekumin.post.postables

import icu.hku.vekumin.post.Postable
import icu.hku.vekumin.post.data.Secret
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URLEncoder

class BlueSkyPoster(val secret: Secret) : Postable {

    override fun post(title: String, content: String) {
        // encode message
        val message = URLEncoder.encode("$title\n$content", "utf-8")
        val data = "message=$message&username=${secret.keys["username"]}&password=${secret.keys["password"]}"

        val url = secret.platform.postUrl.toString() + "?" + data

        // okhttp
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .get()
            .build()
        val response = client.newCall(request).execute().code()

        if (response == 200) {
            println("Post successful")
        } else {
            println("Post failed")
        }
    }
}
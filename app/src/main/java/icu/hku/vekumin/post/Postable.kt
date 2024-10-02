package icu.hku.vekumin.post

interface Postable {
    fun post(title: String, content: String)
}
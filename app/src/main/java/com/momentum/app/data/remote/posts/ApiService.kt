package com.momentum.app.data.remote.posts

import com.momentum.app.data.model.posts.Post

import retrofit2.http.GET

interface ApiService {

    // Endpoint de la guía 14
    @GET("posts")
    suspend fun getPosts(): List<Post>
}

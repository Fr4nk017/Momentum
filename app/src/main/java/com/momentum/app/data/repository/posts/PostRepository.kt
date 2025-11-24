package com.momentum.app.data.repository.posts

import com.momentum.app.data.model.posts.Post
import com.momentum.app.data.remote.posts.ApiService
import com.momentum.app.data.remote.posts.RetrofitInstance

// Lo hacemos open y con dependencia inyectable para poder testear
open class PostRepository(
    private val api: ApiService = RetrofitInstance.api
) {

    open suspend fun getPosts(): List<Post> {
        return api.getPosts()
    }
}

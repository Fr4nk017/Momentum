package com.momentum.app.data.repository.posts

import com.momentum.app.data.model.posts.Post
import com.momentum.app.data.remote.posts.RetrofitInstance

class PostRepository {

    suspend fun getPosts(): List<Post> {
        return RetrofitInstance.api.getPosts()
    }
}

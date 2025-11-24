package com.momentum.app.viewmodel.posts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.momentum.app.data.model.posts.Post
import com.momentum.app.data.repository.posts.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PostViewModel(
    private val repository: PostRepository = PostRepository()
) : ViewModel() {

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun fetchPosts() {
        viewModelScope.launch {
            try {
                _error.value = null
                _posts.value = repository.getPosts()
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Error al obtener posts"
            }
        }
    }
}

package com.momentum.app.tests

import com.momentum.app.data.model.posts.Post
import com.momentum.app.data.remote.posts.ApiService
import com.momentum.app.data.repository.posts.PostRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PostRepositoryTest {

    @Test
    fun `getPosts devuelve lista desde la api`() = runTest {
        // Mock de la API remota
        val apiMock = mockk<ApiService>()

        val fakePosts = listOf(
            Post(
                userId = 1,
                id = 10,
                title = "Titulo desde API fake",
                body = "Contenido desde API fake"
            )
        )

        // Cuando se llame apiMock.getPosts() devolverá fakePosts
        coEvery { apiMock.getPosts() } returns fakePosts

        // Repo usando el mock en vez de Retrofit real
        val repository = PostRepository(apiMock)

        // Act
        val result = repository.getPosts()

        // Assert
        assertEquals(1, result.size)
        assertEquals("Titulo desde API fake", result[0].title)
        assertEquals("Contenido desde API fake", result[0].body)
    }
}

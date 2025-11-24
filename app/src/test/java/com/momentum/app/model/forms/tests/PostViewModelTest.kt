package com.momentum.app.tests

import com.momentum.app.data.model.posts.Post
import com.momentum.app.data.repository.posts.PostRepository
import com.momentum.app.viewmodel.posts.PostViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PostViewModelTest {

    // Dispatcher especial para tests (reemplaza al Main)
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeEach
    fun setup() {
        // Sobrescribimos Dispatchers.Main para que no use el Looper real de Android
        Dispatchers.setMain(testDispatcher)
    }

    @AfterEach
    fun tearDown() {
        // Dejamos Main como estaba para no romper otros tests
        Dispatchers.resetMain()
    }

    @Test
    fun `fetchPosts actualiza lista correctamente`() = runTest {
        // Repo falso para el test
        val fakeRepo = object : PostRepository() {
            override suspend fun getPosts(): List<Post> {
                return listOf(
                    Post(
                        userId = 1,
                        id = 99,
                        title = "Post de prueba",
                        body = "Contenido de prueba"
                    )
                )
            }
        }

        val vm = PostViewModel(repository = fakeRepo)

        // Act
        vm.fetchPosts()

        // Assert
        val resultado = vm.posts.value
        assertEquals(1, resultado.size)
        assertEquals("Post de prueba", resultado[0].title)
        assertEquals("Contenido de prueba", resultado[0].body)
    }
}


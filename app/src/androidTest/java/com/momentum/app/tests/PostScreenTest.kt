package com.momentum.app.tests

import androidx.activity.ComponentActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.momentum.app.ui.screens.posts.PostScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PostScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun muestra_titulo_de_lista_de_posts() {
        composeRule.setContent {
            MaterialTheme {
                PostScreen()
            }
        }

        composeRule
            .onNodeWithText("Listado de Posts (API REST)")
            .assertIsDisplayed()
    }
}

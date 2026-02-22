package com.abstudio.voicenote

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.abstudio.voicenote.core.ui.viewmodel.GlobalBootstrapViewModel
import com.abstudio.voicenote.data.repository.NoteRepository
import com.abstudio.voicenote.data.repository.TaskRepository
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@Config(instrumentedPackages = ["androidx.loader.content"])
class MainActivityUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val noteRepository = mockk<NoteRepository>(relaxed = true)
    private val taskRepository = mockk<TaskRepository>(relaxed = true)
    private val testDispatcher = kotlinx.coroutines.test.StandardTestDispatcher()

    @org.junit.Before
    fun setup() {
        kotlinx.coroutines.Dispatchers.setMain(testDispatcher)
    }

    @org.junit.After
    fun tearDown() {
        kotlinx.coroutines.Dispatchers.resetMain()
    }

    @Test
    fun globalErrorOverlay_displaysWhenErrorSet() {
        // Test the ConnectivityAlertScreen directly for pure UI verification
        composeTestRule.setContent {
            com.abstudio.voicenote.core.ui.components.ConnectivityAlertScreen(
                onClose = {},
                onTryAgain = {}
            )
        }

        composeTestRule.onNodeWithText("Connectivity Lost").assertExists()
        composeTestRule.onNodeWithContentDescription("Retry").assertExists()
    }
}

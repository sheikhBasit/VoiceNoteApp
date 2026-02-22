package com.abstudio.voicenote

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.abstudio.voicenote.core.ui.components.RecordingFAB
import com.abstudio.voicenote.core.ui.theme.VoiceNoteTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.system.measureTimeMillis
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.size
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(instrumentedPackages = ["androidx.loader.content"])
class MemoryAndPerformanceTests {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun recordingFAB_renders() {
        composeTestRule.setContent {
            VoiceNoteTheme {
                RecordingFAB(onClick = {})
            }
        }
        composeTestRule.onNodeWithContentDescription("Start Recording", substring = true).assertExists()
    }

    @Test
    fun bottomNavigation_renders() {
        composeTestRule.setContent {
            VoiceNoteTheme {
                com.abstudio.voicenote.core.ui.components.VoiceNoteBottomNavigation(
                    currentRoute = "dashboard",
                    onNavigate = {}
                )
            }
        }
        composeTestRule.onNodeWithText("Home").assertExists()
    }

    @Test
    fun heroTaskCard_renders() {
        val task = com.abstudio.voicenote.core.ui.components.HeroTask(
            title = "Test Meeting",
            time = "10:00 AM",
            location = "Office",
            priority = "HIGH",
            attendeeAvatars = emptyList()
        )

        composeTestRule.setContent {
            VoiceNoteTheme {
                com.abstudio.voicenote.core.ui.components.HeroTaskCard(
                    task = task,
                    onJoinClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Test Meeting").assertExists()
    }
}

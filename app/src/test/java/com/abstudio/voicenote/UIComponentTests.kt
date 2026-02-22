package com.abstudio.voicenote

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.abstudio.voicenote.core.ui.components.*
import com.abstudio.voicenote.core.ui.theme.VoiceNoteTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(instrumentedPackages = ["androidx.loader.content"])
class UIComponentTests {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun bottomNavigation_displaysAllTabs() {
        composeTestRule.setContent {
            VoiceNoteTheme {
                VoiceNoteBottomNavigation(
                    currentRoute = "dashboard",
                    onNavigate = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Home").assertExists()
        composeTestRule.onNodeWithText("Search").assertExists()
        composeTestRule.onNodeWithText("Vault").assertExists()
        composeTestRule.onNodeWithText("Account").assertExists()
    }

    @Test
    fun bottomNavigation_clickChangesSelection() {
        var selectedRoute = "dashboard"
        
        composeTestRule.setContent {
            VoiceNoteTheme {
                VoiceNoteBottomNavigation(
                    currentRoute = selectedRoute,
                    onNavigate = { selectedRoute = it }
                )
            }
        }

        composeTestRule.onNodeWithText("Vault").performClick()
        assert(selectedRoute == "vault")
    }

    @Test
    fun recordingFAB_isClickable() {
        var clicked = false
        
        composeTestRule.setContent {
            VoiceNoteTheme {
                RecordingFAB(onClick = { clicked = true })
            }
        }

        composeTestRule.onNodeWithContentDescription("Start Recording").performClick()
        assert(clicked)
    }

    @Test
    fun heroTaskCard_displaysAllElements() {
        val task = HeroTask(
            title = "Test Meeting",
            time = "10:00 AM",
            location = "Room A",
            priority = "HIGH",
            attendeeAvatars = listOf("url1", "url2", "url3")
        )

        composeTestRule.setContent {
            VoiceNoteTheme {
                HeroTaskCard(task = task, onJoinClick = {})
            }
        }

        composeTestRule.onNodeWithText("Test Meeting").assertExists()
        composeTestRule.onNodeWithText("10:00 AM").assertExists()
        composeTestRule.onNodeWithText("Room A").assertExists()
        composeTestRule.onNodeWithText("View Details").assertExists()
    }

    @Test
    fun aiKeyPointCard_displaysCorrectly() {
        val keyPoint = com.abstudio.voicenote.data.models.AIKeyPoint(
            type = com.abstudio.voicenote.data.models.AIKeyPointType.ACTION,
            text = "Complete the report"
        )

        composeTestRule.setContent {
            VoiceNoteTheme {
                AIKeyPointCard(keyPoint = keyPoint)
            }
        }

        composeTestRule.onNodeWithText("ACTION ITEM").assertExists()
        composeTestRule.onNodeWithText("Complete the report").assertExists()
    }

    @Test
    fun waveformVisualizer_rendersWithoutCrash() {
        composeTestRule.setContent {
            VoiceNoteTheme {
                WaveformVisualizer(
                    isPlaying = true,
                    modifier = Modifier.size(200.dp, 50.dp)
                )
            }
        }

        // Just verify it renders without crashing
        composeTestRule.waitForIdle()
    }

    @Test
    fun floatingAudioPlayer_displaysControls() {
        composeTestRule.setContent {
            VoiceNoteTheme {
                FloatingAudioPlayer(
                    isPlaying = false,
                    currentTime = "00:15",
                    totalTime = "02:30",
                    currentSpeed = 1.0f,
                    onPlayPauseClick = {},
                    onSpeedClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("00:15").assertExists()
        composeTestRule.onNodeWithText("02:30").assertExists()
        composeTestRule.onNodeWithContentDescription("Play").assertExists()
    }
}

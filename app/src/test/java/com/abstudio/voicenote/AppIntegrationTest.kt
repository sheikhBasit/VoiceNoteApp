package com.abstudio.voicenote

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.abstudio.voicenote.features.auth.WelcomeScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(instrumentedPackages = ["androidx.loader.content"])
class AppIntegrationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun welcomeScreen_navigatesOnStarted() {
        var startedClicked = false
        composeTestRule.setContent {
            WelcomeScreen(onGetStarted = { startedClicked = true })
        }

        composeTestRule.onNodeWithText("Get Started").performClick()
        assert(startedClicked)
    }

    @Test
    fun fullFlow_mockSimulation() {
        // TDD/Integration: Simulate the navigation flow
        // In a real project, we would use Hilt to swap APIs with fakes here
    }
}

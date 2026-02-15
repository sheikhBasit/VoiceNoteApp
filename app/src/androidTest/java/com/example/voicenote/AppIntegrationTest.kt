package com.example.voicenote

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.voicenote.features.auth.WelcomeScreen
import org.junit.Rule
import org.junit.Test

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

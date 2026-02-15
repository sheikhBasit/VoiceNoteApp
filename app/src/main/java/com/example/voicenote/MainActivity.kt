package com.example.voicenote

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.voicenote.features.auth.*
import com.example.voicenote.ui.theme.VoiceNoteTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VoiceNoteTheme {
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp() {
    val navController = rememberNavController()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        NavHost(navController = navController, startDestination = "welcome") {
            composable("welcome") {
                WelcomeScreen(onGetStarted = { navController.navigate("sign_in") })
            }
            composable("sign_in") {
                SignInScreen(
                    onSignIn = { email -> navController.navigate("role_selection") },
                    onBack = { navController.popBackStack() }
                )
            }
            composable("role_selection") {
                RoleSelectionScreen(onRoleSelected = { role -> navController.navigate("workspace_setup") })
            }
            composable("workspace_setup") {
                WorkspaceSetupScreen(onComplete = { name -> navController.navigate("permissions_guide") })
            }
            composable("permissions_guide") {
                PermissionsGuideScreen(onGrantAll = { navController.navigate("dashboard") })
            }
            composable("dashboard") {
                // Placeholder for now
                Box(modifier = Modifier.fillMaxSize()) {
                    Text("Dashboard Placeholder", color = androidx.compose.ui.graphics.Color.White)
                }
            }
        }
    }
}
package com.abstudio.voicenote

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.abstudio.voicenote.features.auth.*
import com.abstudio.voicenote.features.dashboard.DashboardScreen
import com.abstudio.voicenote.features.vault.NoteDetailScreen
import com.abstudio.voicenote.features.vault.VaultScreen
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.Intent
import androidx.hilt.navigation.compose.hiltViewModel
import com.abstudio.voicenote.features.recording.FloatingRecordButton
import com.abstudio.voicenote.features.recording.VoiceRecorderService
import com.abstudio.voicenote.core.ui.theme.VoiceNoteTheme
import com.abstudio.voicenote.features.search.SearchScreen
import com.abstudio.voicenote.features.settings.*
import com.abstudio.voicenote.features.notifications.*
import com.abstudio.voicenote.features.admin.*
import com.abstudio.voicenote.features.vault.AIChatScreen
import com.abstudio.voicenote.features.vault.NoteInsightsScreen

import dagger.hilt.android.AndroidEntryPoint

import androidx.fragment.app.FragmentActivity
import com.abstudio.voicenote.core.util.BiometricHelper
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    @Inject
    lateinit var biometricHelper: BiometricHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VoiceNoteTheme {
                MainApp(biometricHelper = biometricHelper)
            }
        }
    }
}

@Composable
fun MainApp(biometricHelper: BiometricHelper?) {
    val navController = rememberNavController()
    val audioPlayerViewModel: com.abstudio.voicenote.features.player.AudioPlayerViewModel = androidx.hilt.navigation.compose.hiltViewModel()
    val authViewModel: com.abstudio.voicenote.features.auth.AuthViewModel = androidx.hilt.navigation.compose.hiltViewModel()
    val bootstrapViewModel: com.abstudio.voicenote.core.ui.viewmodel.GlobalBootstrapViewModel = androidx.hilt.navigation.compose.hiltViewModel()
    val audioState by audioPlayerViewModel.uiState.collectAsState()

    val context = LocalContext.current
    var isRecording by remember { mutableStateOf(false) }
    val currentRoute = navController.currentBackStackEntryFlow.collectAsState(initial = null).value?.destination?.route

    // Handle Deep Link
    LaunchedEffect(Unit) {
        val intent = (context as? MainActivity)?.intent
        if (intent?.action == Intent.ACTION_VIEW) {
            val data = intent.data
            if (data != null && data.scheme == "voicenote" && data.host == "callback") {
                val code = data.getQueryParameter("code")
                if (code != null) {
                    authViewModel.exchangeCodeForToken(code)
                }
            }
        }
    }

    // React to Auth State for automatic navigation
    val authState by authViewModel.state.collectAsState()
    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            // If we're on login or welcome, move to dashboard
            if (currentRoute in listOf("welcome", "sign_in", "workspace_setup")) {
                navController.navigate("dashboard") {
                    popUpTo(0) { inclusive = true }
                }
            }
        }
    }
    // Global Error Collection
    val globalError by bootstrapViewModel.globalError.collectAsState()
    
    // Snackbar for global notifications
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            if (currentRoute in listOf("dashboard", "search", "vault", "account", "task_center")) {
                com.abstudio.voicenote.core.ui.components.VoiceNoteBottomNavigation(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            if (currentRoute in listOf("dashboard", "vault", "task_center")) {
                Box(
                    modifier = Modifier.padding(bottom = if (audioState.currentUrl != null) 90.dp else 16.dp) // Lift FAB if player visible
                ) {
                    com.abstudio.voicenote.core.ui.components.RecordingFAB(
                        onClick = {
                            if (isRecording) {
                                val intent = Intent(context, VoiceRecorderService::class.java).apply {
                                    action = "STOP_RECORDING"
                                }
                                context.startService(intent)
                                isRecording = false
                            } else {
                                val startIntent = Intent(context, VoiceRecorderService::class.java).apply {
                                    action = "START_RECORDING"
                                }
                                ContextCompat.startForegroundService(context, startIntent)
                                isRecording = true
                            }
                        }
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(innerPadding)
        ) {
            NavHost(
                navController = navController, 
                startDestination = "welcome",
                enterTransition = { androidx.compose.animation.slideInHorizontally(initialOffsetX = { 1000 }) },
                exitTransition = { androidx.compose.animation.slideOutHorizontally(targetOffsetX = { -1000 }) },
                popEnterTransition = { androidx.compose.animation.slideInHorizontally(initialOffsetX = { -1000 }) },
                popExitTransition = { androidx.compose.animation.slideOutHorizontally(targetOffsetX = { 1000 }) }
            ) {
                composable("welcome") {
                    val authState by authViewModel.state.collectAsState()
                    
                    LaunchedEffect(authState) {
                        if (authState is AuthState.Success) {
                            navController.navigate("dashboard") {
                                popUpTo("welcome") { inclusive = true }
                            }
                        }
                    }
                    
                    LoginScreen(
                        authState = authState,
                        onLogin = { email, password -> 
                            authViewModel.authenticate(email, password)
                        },
                        onGoogleSignIn = {
                            authViewModel.signInWithGoogle(context)
                        }
                    )
                }
                composable("sign_in") {
                    SignInScreen(
                        onSignInSuccess = { navController.navigate("role_selection") },
                        onBack = { navController.popBackStack() }
                    )
                }
                composable("role_selection") {
                    RoleSelectionScreen(onRoleSelected = { role ->
                        authViewModel.updateRole(role)
                        navController.navigate("workspace_setup")
                    })
                }
                composable("workspace_setup") {
                    WorkspaceSetupScreen(
                        onBack = { navController.popBackStack() },
                        onJoinWorkspace = { code -> navController.navigate("permissions_guide") },
                        onCreateWorkspace = { navController.navigate("permissions_guide") }
                    )
                }
                composable("permissions_guide") {
                    PermissionsGuideScreen(onGrantAll = { navController.navigate("dashboard") })
                }
                composable("dashboard") {
                    DashboardScreen(
                        onNavigateToNote = { noteId -> navController.navigate("note_detail/$noteId") },
                        onNavigateToVault = { navController.navigate("vault") },
                        onNavigateToTaskCenter = { navController.navigate("task_center") },
                        onNavigateToNotifications = { navController.navigate("notifications") },
                        onNavigateToAura = { navController.navigate("ai_chat") }
                    )
                }
                composable("search") {
                    SearchScreen(
                        onNavigateToNote = { noteId -> navController.navigate("note_detail/$noteId") }
                    )
                }
                composable("account") {
                    val authViewModel: com.abstudio.voicenote.features.auth.AuthViewModel = hiltViewModel()
                    UserProfileStatsScreen(
                        onBack = { navController.popBackStack() },
                        onSettingsClick = { navController.navigate("ai_customization") },
                        onSignOut = { 
                            authViewModel.logout()
                            navController.navigate("welcome") {
                                popUpTo(0) // Clear backstack
                            }
                        },
                        onHelpSupportClick = { navController.navigate("help_support") },
                        onBillingClick = { navController.navigate("billing") },
                        onEnterpriseAdminClick = { navController.navigate("enterprise_admin") }
                    )
                }
                composable("notifications") {
                    NotificationCenterScreen(
                        onBack = { navController.popBackStack() },
                        onViewInsight = { noteId -> navController.navigate("note_insights/$noteId") }
                    )
                }
                composable("ai_customization") {
                    AICustomizationScreen(
                        onBack = { navController.popBackStack() }
                    )
                }
                composable("enterprise_admin") {
                    EnterpriseAdminControlScreen(
                        onBack = { navController.popBackStack() },
                        onManagePlan = { navController.navigate("billing") }
                    )
                }
                composable("folder_access/{folderName}") { backStackEntry ->
                    val folderName = backStackEntry.arguments?.getString("folderName") ?: "Folder"
                    val context = LocalContext.current
                    FolderAccessControlScreen(
                        folderName = folderName,
                        onBack = { navController.popBackStack() },
                        onAddMember = { 
                            android.widget.Toast.makeText(context, "Adding member logic triggered for $folderName", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    )
                }
                composable("help_support") {
                    val context = LocalContext.current
                    HelpSupportScreen(
                        onBack = { navController.popBackStack() },
                        onChatSupport = { 
                            val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse("https://wa.me/1234567890"))
                            context.startActivity(intent)
                        },
                        onEmailSupport = { 
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = android.net.Uri.parse("mailto:support@voicenote.ai")
                                putExtra(Intent.EXTRA_SUBJECT, "VoiceNote Support Request")
                            }
                            context.startActivity(intent)
                        }
                    )
                }
                composable("billing") {
                    val context = LocalContext.current
                    WalletBillingScreen(
                        onBack = { navController.popBackStack() },
                        onManagePayments = { 
                            val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse("https://billing.voicenote.ai/manage"))
                            context.startActivity(intent)
                        }
                    )
                }
                composable("task_center") {
                     com.abstudio.voicenote.features.tasks.TaskCenterScreen(
                         onBack = { navController.popBackStack() }
                     )
                }
                composable("note_insights/{noteId}") { backStackEntry ->
                    val noteId = backStackEntry.arguments?.getString("noteId") ?: ""
                    NoteInsightsScreen(
                        viewModel = androidx.hilt.navigation.compose.hiltViewModel(),
                        noteId = noteId,
                        onBack = { navController.popBackStack() }
                    )
                }
                composable("ai_assistant") {
                    AIChatScreen()
                }
                composable("note_detail/{noteId}") { backStackEntry ->
                    val noteId = backStackEntry.arguments?.getString("noteId")
                    NoteDetailScreen(
                        noteId = noteId,
                        onBack = { navController.popBackStack() }
                    )
                }
                composable("vault") {
                    VaultScreen(
                        onNoteClick = { noteId -> navController.navigate("note_detail/$noteId") },
                        onFolderClick = { folderName -> navController.navigate("folder_access/$folderName") },
                        biometricHelper = biometricHelper
                    )
                }
            }
            
            // Audio Player Overlay
            if (audioState.currentUrl != null) {
                com.abstudio.voicenote.core.ui.components.FloatingAudioPlayer(
                    isPlaying = audioState.isPlaying,
                    currentTime = formatTime(audioState.currentPosition),
                    totalTime = formatTime(audioState.duration),
                    currentSpeed = audioState.playbackSpeed,
                    onPlayPauseClick = { audioPlayerViewModel.togglePlayPause() },
                    onSpeedClick = { audioPlayerViewModel.toggleSpeed() },
                )
            }

            // Global Error Overlay
            if (globalError != null) {
                com.abstudio.voicenote.core.ui.components.ConnectivityAlertScreen(
                    onClose = { bootstrapViewModel.setError(null) },
                    onTryAgain = { 
                        bootstrapViewModel.setError(null)
                        bootstrapViewModel.bootstrap()
                    }
                )
            }
        }
    }
}


fun formatTime(millis: Long): String {
    val seconds = (millis / 1000) % 60
    val minutes = (millis / (1000 * 60)) % 60
    return String.format("%02d:%02d", minutes, seconds)
}

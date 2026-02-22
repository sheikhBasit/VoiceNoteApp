package com.abstudio.voicenote.features.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abstudio.voicenote.core.ui.theme.PrimaryBlue

@Composable
fun EnterpriseSignInScreen(
    onClose: () -> Unit,
    onSignIn: (String, String) -> Unit,
    onSsoLogin: (String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isSignInMode by remember { mutableStateOf(true) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, "Close", tint = Color.Gray)
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier.size(24.dp).background(PrimaryBlue, RoundedCornerShape(6.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Mic, "Icon", tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                    Text("VoiceNote", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Spacer(modifier = Modifier.size(48.dp))
            }

            // Hero
            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(contentAlignment = Alignment.Center) {
                    Box(modifier = Modifier.size(72.dp).background(PrimaryBlue.copy(alpha = 0.1f), RoundedCornerShape(24.dp)).border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(24.dp)))
                    Icon(Icons.Default.GraphicEq, "Hero", tint = PrimaryBlue, modifier = Modifier.size(40.dp))
                }
                Text("Enterprise Portal", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(top = 24.dp))
                Text(
                    "Harness AI-driven insights for your team's productivity.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Auth Toggle
            Surface(
                color = Color.Black.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Row(modifier = Modifier.padding(4.dp)) {
                    AuthToggleButton("Sign In", isSignInMode, modifier = Modifier.weight(1f)) { isSignInMode = true }
                    AuthToggleButton("Sign Up", !isSignInMode, modifier = Modifier.weight(1f)) { isSignInMode = false }
                }
            }

            // Form
            Column(modifier = Modifier.padding(vertical = 24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Column {
                    Text("WORK EMAIL", style = MaterialTheme.typography.labelSmall, color = Color.Gray, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(bottom = 6.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("name@company.com", color = Color.Gray.copy(alpha = 0.5f)) },
                        leadingIcon = { Icon(Icons.Default.Mail, "Email", tint = Color.Gray) },
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                            focusedBorderColor = PrimaryBlue
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("PASSWORD", style = MaterialTheme.typography.labelSmall, color = Color.Gray, fontWeight = FontWeight.ExtraBold)
                        Text("Forgot?", color = PrimaryBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                        placeholder = { Text("••••••••", color = Color.Gray.copy(alpha = 0.5f)) },
                        leadingIcon = { Icon(Icons.Default.Lock, "Pass", tint = Color.Gray) },
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                            focusedBorderColor = PrimaryBlue
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Button(
                    onClick = { onSignIn(email, password) },
                    modifier = Modifier.fillMaxWidth().height(56.dp).padding(top = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text(if (isSignInMode) "Sign In" else "Sign Up", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.ArrowForward, "Go", modifier = Modifier.size(18.dp))
                }
            }

            // Divider
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 16.dp)) {
                Box(modifier = Modifier.weight(1f).height(1.dp).background(Color.White.copy(alpha = 0.1f)))
                Text("OR CONTINUE WITH", modifier = Modifier.padding(horizontal = 16.dp), style = MaterialTheme.typography.labelSmall, color = Color.Gray, fontWeight = FontWeight.ExtraBold)
                Box(modifier = Modifier.weight(1f).height(1.dp).background(Color.White.copy(alpha = 0.1f)))
            }

            // SSO
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SSOButton("Google", onSsoLogin)
                SSOButton("Azure AD", onSsoLogin)
            }

            Spacer(modifier = Modifier.weight(1f))

            // Footer
            Column(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.VerifiedUser, "Security", tint = Color.Gray, modifier = Modifier.size(12.dp))
                    Text("SECURE ENTERPRISE ENCRYPTION ACTIVE", style = MaterialTheme.typography.labelSmall, color = Color.Gray, fontWeight = FontWeight.ExtraBold, fontSize = 9.sp)
                }
                Text(
                    "By signing in, you agree to our Terms and Privacy Policy.",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun AuthToggleButton(label: String, isSelected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        color = if (isSelected) Color.White.copy(alpha = 0.1f) else Color.Transparent,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(label, color = if (isSelected) Color.White else Color.Gray, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
    }
}

@Composable
fun SSOButton(provider: String, onClick: (String) -> Unit) {
    OutlinedButton(
        onClick = { onClick(provider) },
        modifier = Modifier.fillMaxWidth().height(56.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Text(provider, fontSize = 20.sp) // Adjusted font size
        Spacer(modifier = Modifier.width(12.dp))
        Text("Sign in with $provider", color = Color.White, fontWeight = FontWeight.Bold)
    }
}

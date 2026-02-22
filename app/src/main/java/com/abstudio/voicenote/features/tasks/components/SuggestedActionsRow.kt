package com.abstudio.voicenote.features.tasks.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.abstudio.voicenote.core.analytics.AnalyticsTracker
import com.abstudio.voicenote.core.ui.theme.PrimaryBlue
import com.abstudio.voicenote.data.models.SuggestedActions

@Composable
fun SuggestedActionsRow(
    suggestedActions: SuggestedActions,
    onAiPromptClick: ((String) -> Unit)? = null
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Google Search chip
        suggestedActions.googleSearch?.get("query")?.let { query ->
            ActionChip(
                label = "Search",
                icon = Icons.Outlined.Search,
                color = Color(0xFF4285F4),
                onClick = {
                    AnalyticsTracker.trackTaskActionClicked("google_search")
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://www.google.com/search?q=${Uri.encode(query)}")
                    )
                    context.startActivity(intent)
                }
            )
        }

        // Email chip
        suggestedActions.email?.let { emailData ->
            val to = emailData["to"] ?: ""
            val subject = emailData["subject"] ?: ""
            val body = emailData["body"] ?: ""
            if (to.isNotEmpty() || subject.isNotEmpty()) {
                ActionChip(
                    label = "Email",
                    icon = Icons.Outlined.Email,
                    color = Color(0xFFEA4335),
                    onClick = {
                        AnalyticsTracker.trackTaskActionClicked("email")
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:${Uri.encode(to)}")
                            putExtra(Intent.EXTRA_SUBJECT, subject)
                            putExtra(Intent.EXTRA_TEXT, body)
                        }
                        context.startActivity(Intent.createChooser(intent, "Send email"))
                    }
                )
            }
        }

        // WhatsApp chip
        suggestedActions.whatsapp?.let { waData ->
            val phone = waData["phone"] ?: ""
            val message = waData["message"] ?: ""
            if (phone.isNotEmpty() || message.isNotEmpty()) {
                ActionChip(
                    label = "WhatsApp",
                    icon = null,
                    color = Color(0xFF25D366),
                    onClick = {
                        AnalyticsTracker.trackTaskActionClicked("whatsapp")
                        val url = if (phone.isNotEmpty()) {
                            "https://wa.me/${phone.replace("+", "")}?text=${Uri.encode(message)}"
                        } else {
                            "https://wa.me/?text=${Uri.encode(message)}"
                        }
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                    },
                    text = "WA"
                )
            }
        }

        // Maps chip
        suggestedActions.map?.let { mapData ->
            val query = mapData["query"] ?: mapData["address"] ?: ""
            if (query.isNotEmpty()) {
                ActionChip(
                    label = "Maps",
                    icon = Icons.Outlined.Map,
                    color = Color(0xFF34A853),
                    onClick = {
                        AnalyticsTracker.trackTaskActionClicked("maps")
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("geo:0,0?q=${Uri.encode(query)}")
                        )
                        context.startActivity(intent)
                    }
                )
            }
        }

        // AI Prompt chip
        suggestedActions.aiPrompt?.get("prompt")?.let { prompt ->
            ActionChip(
                label = "AI",
                icon = Icons.Outlined.SmartToy,
                color = PrimaryBlue,
                onClick = {
                    AnalyticsTracker.trackTaskActionClicked("ai_prompt")
                    onAiPromptClick?.invoke(prompt)
                }
            )
        }
    }
}

@Composable
private fun ActionChip(
    label: String,
    icon: ImageVector?,
    color: Color,
    onClick: () -> Unit,
    text: String? = null
) {
    AssistChip(
        onClick = onClick,
        label = { Text(label, color = color) },
        leadingIcon = {
            if (icon != null) {
                Icon(icon, contentDescription = label, tint = color, modifier = Modifier.size(16.dp))
            } else if (text != null) {
                Text(text, color = color, style = MaterialTheme.typography.labelSmall)
            }
        },
        shape = RoundedCornerShape(20.dp),
        colors = AssistChipDefaults.assistChipColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        border = AssistChipDefaults.assistChipBorder(
            enabled = true,
            borderColor = color.copy(alpha = 0.3f)
        )
    )
}

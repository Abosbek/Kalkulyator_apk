package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AngleUnit
import com.example.model.CalculatorMode
import com.example.ui.theme.AccentRed
import com.example.ui.theme.LocalCalculatorColors

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun DisplaySection(
    expression: String,
    liveResult: String,
    mainResult: String,
    errorMessage: String?,
    mode: CalculatorMode,
    angleUnit: AngleUnit,
    isDarkTheme: Boolean,
    isOledBlack: Boolean,
    historyCount: Int,
    onModeChange: (CalculatorMode) -> Unit,
    onAngleToggle: () -> Unit,
    onThemeToggle: () -> Unit,
    onHistoryClick: () -> Unit,
    onBackspace: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalCalculatorColors.current
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Automatically scroll to end of expression when it changes
    LaunchedEffect(expression) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    // Dynamic result font size based on text length
    val resultFontSize = when {
        mainResult.length > 15 -> 30.sp
        mainResult.length > 12 -> 36.sp
        mainResult.length > 9 -> 44.sp
        mainResult.length > 6 -> 52.sp
        else -> 60.sp
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                var totalDrag = 0f
                detectHorizontalDragGestures(
                    onDragStart = { totalDrag = 0f },
                    onHorizontalDrag = { _, dragAmount ->
                        totalDrag += dragAmount
                        if (totalDrag < -60f || totalDrag > 60f) {
                            onBackspace()
                            totalDrag = 0f
                        }
                    }
                )
            }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // --- Top Bar: Mode Switcher & Tools ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Mode Segmented Switcher
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(colors.surfaceCard)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ModeTab(
                    title = "Oddiy",
                    selected = mode == CalculatorMode.STANDARD,
                    onClick = { onModeChange(CalculatorMode.STANDARD) }
                )
                ModeTab(
                    title = "Ilmiy",
                    selected = mode == CalculatorMode.SCIENTIFIC,
                    onClick = { onModeChange(CalculatorMode.SCIENTIFIC) }
                )
                ModeTab(
                    title = "Konverter",
                    selected = mode == CalculatorMode.CONVERTER,
                    onClick = { onModeChange(CalculatorMode.CONVERTER) }
                )
            }

            // Quick Actions: Angle unit (in scientific), History button, Theme toggle
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (mode == CalculatorMode.SCIENTIFIC) {
                    // DEG/RAD Toggle Badge
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(colors.surfaceElevated)
                            .clickable(onClick = onAngleToggle)
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("angle_unit_toggle")
                    ) {
                        Text(
                            text = angleUnit.label,
                            color = colors.accentPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                // History button with count badge
                IconButton(
                    onClick = onHistoryClick,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(colors.surfaceCard)
                        .testTag("history_button")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "Tarix",
                            tint = colors.textPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        if (historyCount > 0) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .align(Alignment.TopEnd)
                                    .clip(CircleShape)
                                    .background(colors.accentPrimary)
                            )
                        }
                    }
                }

                // Theme Toggle Button
                IconButton(
                    onClick = onThemeToggle,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(colors.surfaceCard)
                        .testTag("theme_toggle_button")
                ) {
                    Icon(
                        imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = "Mavzuni o'zgartirish",
                        tint = colors.textPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Only show calculator expression display for STANDARD and SCIENTIFIC modes
        if (mode != CalculatorMode.CONVERTER) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.End
            ) {
                // Expression Row with horizontal scroll
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(scrollState),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (expression.isEmpty()) "0" else expression,
                        fontSize = 28.sp,
                        color = if (expression.isEmpty()) colors.textSecondary.copy(alpha = 0.5f) else colors.textSecondary,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.End,
                        maxLines = 1,
                        modifier = Modifier.testTag("expression_text")
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Error or Live Preview result
                if (errorMessage != null) {
                    Text(
                        text = errorMessage,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = AccentRed,
                        textAlign = TextAlign.End,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("error_message_text")
                    )
                } else if (liveResult.isNotEmpty()) {
                    Text(
                        text = liveResult,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = colors.accentPrimary.copy(alpha = 0.85f),
                        textAlign = TextAlign.End,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("live_result_text")
                    )
                } else {
                    Spacer(modifier = Modifier.height(24.dp))
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Main Result Display (Click to copy)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .combinedClickable(
                            onClick = {},
                            onLongClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Natija", mainResult)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Natija nusxalandi: $mainResult", Toast.LENGTH_SHORT).show()
                            }
                        ),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = mainResult,
                        fontSize = resultFontSize,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                        textAlign = TextAlign.End,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.testTag("main_result_text")
                    )
                }
            }
        }
    }
}

@Composable
fun ModeTab(
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val colors = LocalCalculatorColors.current
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) colors.accentPrimary else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .testTag("tab_${title.lowercase()}")
    ) {
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = if (selected) Color.White else colors.textSecondary
        )
    }
}

package com.roxy.flare.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.roxy.flare.FlareAnimationType
import com.roxy.flare.FlareDuration
import com.roxy.flare.FlarePosition
import com.roxy.flare.FlareQueueMode
import com.roxy.flare.FlareType
import com.roxy.flare.android.Flare
import com.roxy.flare.compose.FlareHost
import com.roxy.flare.compose.rememberFlareHostState
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF121212) // dark premium background
                ) {
                    DemoScreen(activity = this)
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DemoScreen(activity: MainActivity) {
    val flareHostState = rememberFlareHostState()
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // Setup Compose FlareHost wrapper over entire screen
    FlareHost(state = flareHostState) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFF1E1E1E), Color(0xFF121212))
                            )
                        )
                        .padding(horizontal = 20.dp, vertical = 24.dp)
                ) {
                    Text(
                        text = "🔥 Flare Alerts",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Beautiful, expressive alerts for Android — by Roxy",
                        color = Color.LightGray,
                        fontSize = 14.sp
                    )
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // SECTION 1: COMPOSE API DEMOS
                SectionTitle(text = "Jetpack Compose API")
                
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DemoButton(
                        text = "Success (Bottom)",
                        color = Color(0xFF2E7D32)
                    ) {
                        scope.launch {
                            flareHostState.show {
                                type = FlareType.SUCCESS
                                message = "Data saved successfully!"
                                duration = FlareDuration.SHORT
                                position = FlarePosition.BOTTOM
                                showProgressBar = true
                            }
                        }
                    }

                    DemoButton(
                        text = "Error (Top, Slide)",
                        color = Color(0xFFD32F2F)
                    ) {
                        scope.launch {
                            flareHostState.show {
                                type = FlareType.ERROR
                                message = "Connection lost. Please try again."
                                duration = FlareDuration.LONG
                                position = FlarePosition.TOP
                                animationType = FlareAnimationType.SLIDE
                                action("Retry") {
                                    // Handle retry
                                }
                            }
                        }
                    }

                    DemoButton(
                        text = "Warning (Center, Bounce)",
                        color = Color(0xFFED6C02)
                    ) {
                        scope.launch {
                            flareHostState.show {
                                type = FlareType.WARNING
                                message = "Low disk space alert!"
                                duration = FlareDuration.SHORT
                                position = FlarePosition.CENTER
                                animationType = FlareAnimationType.BOUNCE
                            }
                        }
                    }

                    DemoButton(
                        text = "Loading (Indefinite)",
                        color = Color(0xFF1976D2)
                    ) {
                        scope.launch {
                            flareHostState.show {
                                type = FlareType.LOADING
                                message = "Syncing with cloud server..."
                                duration = FlareDuration.INDEFINITE
                                action("Cancel") {
                                    // Handle cancel
                                }
                            }
                        }
                    }

                    DemoButton(
                        text = "Custom Tint & Color",
                        color = Color(0xFF7B1FA2)
                    ) {
                        scope.launch {
                            flareHostState.show {
                                type = FlareType.CUSTOM(0xFF7B1FA2)
                                message = "Custom purple styling applied."
                                duration = FlareDuration.SHORT
                                showProgressBar = true
                            }
                        }
                    }
                }

                // SECTION 2: VIEW SYSTEM API DEMOS
                SectionTitle(text = "Android View System (XML) API")
                
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DemoButton(
                        text = "Success (Bottom, Bounce)",
                        color = Color(0xFF2E7D32)
                    ) {
                        Flare.with(activity)
                            .type(FlareType.SUCCESS)
                            .message("Files updated successfully!")
                            .position(FlarePosition.BOTTOM)
                            .duration(FlareDuration.SHORT)
                            .animation(FlareAnimationType.BOUNCE)
                            .showProgressBar(true)
                            .show()
                    }

                    DemoButton(
                        text = "Error (Top, Slide)",
                        color = Color(0xFFD32F2F)
                    ) {
                        Flare.with(activity)
                            .type(FlareType.ERROR)
                            .message("Failed to fetch API details.")
                            .position(FlarePosition.TOP)
                            .duration(FlareDuration.LONG)
                            .animation(FlareAnimationType.SLIDE)
                            .action("Undo") {
                                // Undo action
                            }
                            .show()
                    }

                    DemoButton(
                        text = "Warning (Center, Fade)",
                        color = Color(0xFFED6C02)
                    ) {
                        Flare.with(activity)
                            .type(FlareType.WARNING)
                            .message("Unsaved edits will be lost!")
                            .position(FlarePosition.CENTER)
                            .duration(FlareDuration.SHORT)
                            .animation(FlareAnimationType.FADE)
                            .show()
                    }

                    DemoButton(
                        text = "Loading Spinner",
                        color = Color(0xFF1976D2)
                    ) {
                        Flare.with(activity)
                            .type(FlareType.LOADING)
                            .message("Processing payment secure gateway...")
                            .duration(FlareDuration.INDEFINITE)
                            .action("Dismiss") {}
                            .show()
                    }
                }

                // SECTION 3: UTILITY CONTROLS
                SectionTitle(text = "Queue Management")
                
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DemoButton(
                        text = "Trigger Stacking Queue (5x)",
                        color = Color(0xFF37474F)
                    ) {
                        // Rapid fire 5 notifications
                        repeat(5) { i ->
                            Flare.with(activity)
                                .type(FlareType.INFO)
                                .message("Queue item #${i + 1} processing")
                                .duration(FlareDuration.CUSTOM(1200))
                                .show()
                        }
                    }

                    DemoButton(
                        text = "Clear All Queue / Dismiss",
                        color = Color(0xFF4E342E)
                    ) {
                        // Clear Core/Compose/View queue
                        flareHostState.clearQueue()
                        Flare.clearQueue()
                    }
                    
                    DemoButton(
                        text = "Toggle Replace Mode",
                        color = Color(0xFF455A64)
                    ) {
                        // Toggle queue strategy
                        val currentMode = com.roxy.flare.FlareConfig.get().queueMode
                        val newMode = if (currentMode == FlareQueueMode.ENQUEUE) {
                            FlareQueueMode.REPLACE
                        } else {
                            FlareQueueMode.ENQUEUE
                        }
                        com.roxy.flare.FlareConfig.configure {
                            queueMode = newMode
                        }
                        
                        // Show confirmation
                        Flare.with(activity)
                            .type(FlareType.INFO)
                            .message("Queue Mode changed to: ${newMode.name}")
                            .duration(FlareDuration.SHORT)
                            .show()
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun SectionTitle(text: String) {
    Text(
        text = text,
        color = Color.Gray,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

@Composable
fun DemoButton(
    text: String,
    color: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = color.copy(alpha = 0.25f)),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color),
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

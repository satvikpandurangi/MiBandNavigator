package com.satvik.mibandnavigator

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin

// Core color theme
val SlateBackground = Color(0xFF0F172A)
val SlateCard = Color(0xFF1E293B)
val CyanAccent = Color(0xFF22D3EE)
val TextActive = Color(0xFFF8FAFC)
val TextGray = Color(0xFF94A3B8)
val SuccessGreen = Color(0xFF10B981)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val colorScheme = darkColorScheme(
                background = SlateBackground,
                surface = SlateBackground,
                primary = CyanAccent,
                onBackground = TextActive,
                surfaceVariant = SlateCard
            )

            MaterialTheme(colorScheme = colorScheme) {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    MainAppController()
                }
            }
        }
    }
}

@Composable
fun MainAppController() {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = { AppNavigationBar(selectedTab) { selectedTab = it } }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when (selectedTab) {
                0 -> NavigateScreen()
                1 -> SetupScreen()
                2 -> SettingsScreen()
            }
        }
    }
}

// ==========================================
// DYNAMIC MI BAND ICON (Canvas + Vector)
// ==========================================
@Composable
fun MiBandIcon(modifier: Modifier = Modifier, tint: Color = Color.White, showTick: Boolean = false) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // IMPORTANT FIX: Base all widths on 'h' (height) instead of 'w'.
            // This prevents the icon from stretching into a "square" when placed
            // inside a square modifier, keeping the sleek Mi Band shape.
            val sw = h * 0.015f // Dynamic stroke width

            val cx = w / 2f
            val cy = h / 2f

            // Sleek Body dimensions
            val bodyH = h * 0.65f
            val bodyW = bodyH * 0.45f
            val bodyL = cx - bodyW / 2f
            val bodyR = cx + bodyW / 2f
            val bodyT = cy - bodyH / 2f
            val bodyB = cy + bodyH / 2f

            // Strap dimensions (slightly narrower than the main body)
            val strapW = bodyW * 0.85f
            val strapL = cx - strapW / 2f
            val strapR = cx + strapW / 2f
            val topY = h * 0.05f
            val bottomY = h * 0.95f

            // ==========================================
            // 1. OUTER STRAP & SILHOUETTE
            // ==========================================
            val outerPath = androidx.compose.ui.graphics.Path().apply {
                // Top strap edge
                moveTo(strapL, topY + strapW * 0.1f)
                quadraticTo(cx, topY - strapW * 0.1f, strapR, topY + strapW * 0.1f)

                // Right strap smoothly curving down to the tracker body
                cubicTo(
                    strapR, bodyT - bodyW * 0.3f,
                    bodyR, bodyT - bodyW * 0.1f,
                    bodyR, bodyT + bodyW / 2f
                )

                // Right body edge
                lineTo(bodyR, bodyB - bodyW / 2f)

                // Right body smoothly curving down to the bottom strap
                cubicTo(
                    bodyR, bodyB + bodyW * 0.1f,
                    strapR, bodyB + bodyW * 0.3f,
                    strapR, bottomY - strapW * 0.1f
                )

                // Bottom strap edge
                quadraticTo(cx, bottomY + strapW * 0.1f, strapL, bottomY - strapW * 0.1f)

                // Left bottom strap curving up to the body
                cubicTo(
                    strapL, bodyB + bodyW * 0.3f,
                    bodyL, bodyB + bodyW * 0.1f,
                    bodyL, bodyB - bodyW / 2f
                )

                // Left body edge
                lineTo(bodyL, bodyT + bodyW / 2f)

                // Left body curving up to the top strap
                cubicTo(
                    bodyL, bodyT - bodyW * 0.1f,
                    strapL, bodyT - bodyW * 0.3f,
                    strapL, topY + strapW * 0.1f
                )
                close()
            }

            // Draw Outer Glow + Solid Line
            drawPath(path = outerPath, color = tint.copy(alpha = 0.3f), style = Stroke(width = sw * 2.5f))
            drawPath(path = outerPath, color = tint, style = Stroke(width = sw))

            // ==========================================
            // 2. INNER SCREEN PILL
            // ==========================================
            val innerH = bodyH * 0.82f
            val innerW = bodyW * 0.82f
            val innerOffset = Offset(cx - innerW / 2f, cy - innerH / 2f)
            val innerSize = Size(innerW, innerH)
            val innerRadius = CornerRadius(innerW / 2f, innerW / 2f)

            // Draw Inner Glow + Solid Line
            drawRoundRect(
                color = tint.copy(alpha = 0.3f),
                topLeft = innerOffset,
                size = innerSize,
                cornerRadius = innerRadius,
                style = Stroke(width = sw * 2.5f)
            )
            drawRoundRect(
                color = tint,
                topLeft = innerOffset,
                size = innerSize,
                cornerRadius = innerRadius,
                style = Stroke(width = sw)
            )

            // ==========================================
            // 3. SHARP GEOMETRIC CHECKMARK
            // ==========================================
            if (showTick) {
                val tickPath = androidx.compose.ui.graphics.Path().apply {
                    moveTo(cx - innerW * 0.25f, cy + innerH * 0.05f)
                    lineTo(cx - innerW * 0.05f, cy + innerH * 0.22f)
                    lineTo(cx + innerW * 0.32f, cy - innerH * 0.15f)
                }

                val tickColor = SuccessGreen

                // Draw Tick Glow + Solid Line
                drawPath(
                    path = tickPath,
                    color = tickColor.copy(alpha = 0.4f),
                    style = Stroke(width = sw * 3f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
                drawPath(
                    path = tickPath,
                    color = tickColor,
                    style = Stroke(width = sw * 1.5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
            }
        }
    }
}

// ==========================================
// SCREEN 1: NAVIGATE
// ==========================================
@Composable
fun NavigateScreen() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var hasPermission by remember { mutableStateOf(false) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                val enabledListeners = Settings.Secure.getString(context.contentResolver, "enabled_notification_listeners")
                hasPermission = enabledListeners?.contains(context.packageName) == true
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HeaderSection(isListening = hasPermission)
        Spacer(modifier = Modifier.height(24.dp))

        ReadyCard(
            hasPermission = hasPermission,
            onEnableClick = { context.startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")) }
        )

        Spacer(modifier = Modifier.height(16.dp))
        ZeppStrip()
        Spacer(modifier = Modifier.height(32.dp))
        TestNavigationSection()
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun ReadyCard(hasPermission: Boolean, onEnableClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = SlateCard)) {
        Column(modifier = Modifier.padding(vertical = 32.dp, horizontal = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {

            // DYNAMIC ICON INJECTION
            if (hasPermission) {
                MiBandIcon(modifier = Modifier.size(80.dp), tint = CyanAccent, showTick = true)
            } else {
                MiBandIcon(modifier = Modifier.size(80.dp), tint = Color.DarkGray, showTick = false)
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(if (hasPermission) "Service Running" else "Permission Required", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = TextActive)
            Spacer(modifier = Modifier.height(12.dp))
            Text(if (hasPermission) "Your phone is actively listening to Google Maps and forwarding alerts to your Mi Band." else "Please grant Notification Access so this app can read Maps data.", textAlign = TextAlign.Center, fontSize = 13.sp, color = TextGray)

            if (!hasPermission) {
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = onEnableClick, colors = ButtonDefaults.buttonColors(containerColor = CyanAccent)) {
                    Text("Grant Notification Access", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ==========================================
// SCREEN 2: SETUP
// ==========================================
@Composable
fun SetupScreen() {
    Column(
        modifier = Modifier.fillMaxSize().background(SlateBackground).padding(20.dp).verticalScroll(rememberScrollState())
    ) {
        Text("Setup Guide", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = TextActive, modifier = Modifier.padding(top = 24.dp, bottom = 8.dp))
        Text("Configure MiBand Navigator to work with your band via the Zepp Life app.", color = TextGray, fontSize = 14.sp, modifier = Modifier.padding(bottom = 24.dp))

        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = SlateCard)) {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Map, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(32.dp))
                    Text("Maps", fontSize = 10.sp, color = TextGray, modifier = Modifier.padding(top = 4.dp))
                }
                Icon(Icons.Default.ArrowForward, contentDescription = null, tint = TextGray, modifier = Modifier.size(16.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(32.dp))
                    Text("MiBand Navigator App", fontSize = 10.sp, color = TextGray, modifier = Modifier.padding(top = 4.dp))
                }
                Icon(Icons.Default.ArrowForward, contentDescription = null, tint = TextGray, modifier = Modifier.size(16.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // PURE ICON
                    MiBandIcon(modifier = Modifier.size(32.dp), tint = CyanAccent, showTick = false)
                    Text("Band", fontSize = 10.sp, color = TextGray, modifier = Modifier.padding(top = 4.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text("COMPATIBLE DEVICES", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextGray, letterSpacing = 1.sp, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        CompatibleDevicesSection()

        Spacer(modifier = Modifier.height(32.dp))

        Text("INSTRUCTIONS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextGray, letterSpacing = 1.sp, modifier = Modifier.padding(bottom = 16.dp))

        SetupStepCard(step = "1", icon = Icons.Default.Settings, title = "Grant Permissions", desc = "Tap 'Grant Notification Access' on the main screen and allow MiBand Navigator.")
        SetupStepCard(step = "2", icon = Icons.Default.BluetoothConnected, title = "Open Zepp / Zepp Life", desc = "Open your official fitness app and go to Profile > Your Smart Band.")
        SetupStepCard(step = "3", icon = Icons.Default.NotificationsActive, title = "Enable App Alerts", desc = "Tap on 'App Alerts' and turn the main switch ON.")
        SetupStepCard(step = "4", icon = Icons.Default.Checklist, title = "Select This App", desc = "Tap 'Manage apps' and check the box next to MiBand Navigator.")
        SetupStepCard(step = "5", icon = Icons.Default.DriveEta, title = "Start Driving!", desc = "Start navigation in Google Maps and keep this app in the background.")

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
fun CompatibleDevicesSection() {
    val bands = listOf("Mi Band 4", "Mi Band 5", "Mi Band 6", "Mi Band 7", "Amazfit Band")

    LazyRow(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(bands.size) { index ->
            val bandName = bands[index]
            Card(modifier = Modifier.width(130.dp), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = SlateCard)) {
                Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    // PURE ICON
                    MiBandIcon(modifier = Modifier.size(48.dp), tint = TextGray, showTick = false)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(bandName, fontWeight = FontWeight.Bold, color = TextActive, textAlign = TextAlign.Center)
                    Text("Zepp Life", fontSize = 11.sp, color = CyanAccent, modifier = Modifier.padding(top = 4.dp))
                }
            }
        }
    }
}

// ==========================================
// SCREEN 3: SETTINGS
// ==========================================
@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val sharedPrefs = context.getSharedPreferences("NavSettings", Context.MODE_PRIVATE)

    var vibrationEnabled by remember { mutableStateOf(sharedPrefs.getBoolean("vibrate_turn", true)) }
    var compactEnabled by remember { mutableStateOf(sharedPrefs.getBoolean("compact_mode", false)) }
    var debugEnabled by remember { mutableStateOf(sharedPrefs.getBoolean("debug_mode", false)) }

    fun saveBool(key: String, value: Boolean) {
        sharedPrefs.edit().putBoolean(key, value).apply()
    }

    Column(modifier = Modifier.fillMaxSize().padding(20.dp).verticalScroll(rememberScrollState())) {
        Text("Settings", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = TextActive, modifier = Modifier.padding(top = 24.dp, bottom = 24.dp))

        Text("NAVIGATION", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextGray, letterSpacing = 1.sp, modifier = Modifier.padding(bottom = 8.dp))
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = SlateCard)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Vibration on turn change", fontWeight = FontWeight.Bold, color = TextActive)
                        Text("Force vibration when arrow changes", fontSize = 12.sp, color = TextGray)
                    }
                    Switch(checked = vibrationEnabled, onCheckedChange = { vibrationEnabled = it; saveBool("vibrate_turn", it) })
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.White.copy(alpha = 0.05f))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Compact Mode", fontWeight = FontWeight.Bold, color = TextActive)
                        Text("Use small emojis instead of dot-matrix", fontSize = 12.sp, color = TextGray)
                    }
                    Switch(checked = compactEnabled, onCheckedChange = { compactEnabled = it; saveBool("compact_mode", it) })
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("DEVELOPER", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextGray, letterSpacing = 1.sp, modifier = Modifier.padding(bottom = 8.dp))
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = SlateCard)) {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.BugReport, contentDescription = null, tint = TextGray, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Debug mode", fontWeight = FontWeight.Bold, color = TextActive)
                        Text("Print full payload to Logcat", fontSize = 12.sp, color = TextGray)
                    }
                }
                Switch(checked = debugEnabled, onCheckedChange = { debugEnabled = it; saveBool("debug_mode", it) })
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text("NOTIFICATION PREVIEW", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextGray, letterSpacing = 1.sp, modifier = Modifier.padding(bottom = 12.dp))
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = SlateCard)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("MI BAND DISPLAY (Simulated)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CyanAccent.copy(alpha = 0.6f), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(16.dp))
                WatchDisplaySimulator(compactMode = compactEnabled)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Forwarded via Zepp Life → Mi Band", fontSize = 11.sp, color = TextGray, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        AboutNavigatorSection()
        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
fun WatchDisplaySimulator(compactMode: Boolean) {
    Box(modifier = Modifier.fillMaxWidth().background(Color.Black, shape = RoundedCornerShape(16.dp)).padding(20.dp), contentAlignment = Alignment.Center) {
        if (compactMode) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("⬆️ 150m", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Test Road", color = TextGray, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("10 min • 4.5 km", color = TextGray, fontSize = 12.sp)
            }
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("150 m", color = Color.White, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text("    •    \n   •••   \n    •    \n    •    ", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, lineHeight = 22.sp, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(12.dp))
                Text("Test Road", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text("10 min • 4.5 km", color = TextGray, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun AboutNavigatorSection() {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        // PURE ICON
        Surface(modifier = Modifier.size(64.dp), shape = CircleShape, color = SlateCard, border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))) {
            Box(contentAlignment = Alignment.Center) {
                MiBandIcon(modifier = Modifier.size(36.dp), tint = CyanAccent, showTick = false)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("MiBand Navigator", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = TextActive)
        Text("Version 1.0.0", fontSize = 12.sp, color = TextGray, modifier = Modifier.padding(top = 2.dp))

        Spacer(modifier = Modifier.height(24.dp))
        Text("A systems-integration project built to bridge Google Maps navigation directly to legacy Xiaomi and Amazfit hardware through standard notification protocols.", textAlign = TextAlign.Center, fontSize = 12.sp, color = TextGray, lineHeight = 18.sp)
    }
}

// ==========================================
// STATIC/REUSABLE UI COMPONENTS
// ==========================================
@Composable
fun SetupStepCard(step: String, icon: ImageVector, title: String, desc: String) {
    Card(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp), colors = CardDefaults.cardColors(containerColor = SlateCard)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(modifier = Modifier.size(48.dp), shape = CircleShape, color = CyanAccent.copy(alpha = 0.1f)) {
                Box(contentAlignment = Alignment.Center) { Icon(icon, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(24.dp)) }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, color = TextActive, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(2.dp))
                Text(desc, color = TextGray, fontSize = 13.sp, lineHeight = 18.sp)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Surface(modifier = Modifier.size(32.dp), shape = CircleShape, color = CyanAccent) {
                Box(contentAlignment = Alignment.Center) { Text(step, color = Color.Black, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp) }
            }
        }
    }
}

@Composable
fun ZeppStrip() {
    Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), color = Color.Black.copy(alpha = 0.15f)) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Link, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text("Zepp forwards notifications → Mi Band vibrates", fontSize = 12.sp, color = TextGray, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun TestNavigationSection() {
    val context = LocalContext.current
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("TEST NAVIGATION", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextGray, letterSpacing = 1.sp, modifier = Modifier.padding(bottom = 16.dp))
        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                TestNavItem("Left", Icons.Default.KeyboardArrowLeft, NavDirection.LEFT, context, 1f)
                TestNavItem("Right", Icons.Default.KeyboardArrowRight, NavDirection.RIGHT, context, 1f)
                TestNavItem("Straight", Icons.Default.ArrowUpward, NavDirection.STRAIGHT, context, 1f)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                TestNavItem("Roundabout", Icons.Default.Refresh, NavDirection.ROUNDABOUT, context, 1f)
                TestNavItem("U-Turn", Icons.Default.Undo, NavDirection.UTURN, context, 1f)
                TestNavItem("Slight L", Icons.Default.TurnSlightLeft, NavDirection.SLIGHT_LEFT, context, 1f)
            }
        }
    }
}

@Composable
fun RowScope.TestNavItem(title: String, icon: ImageVector, direction: NavDirection, context: Context, weight: Float) {
    Surface(
        modifier = Modifier.weight(weight).aspectRatio(1.2f).clickable {
            val intent = Intent("TRIGGER_TEST_NAV")
            intent.putExtra("test_dir", direction.name)
            context.sendBroadcast(intent)
        },
        color = SlateCard, shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, Color.White.copy(alpha = 0.03f))
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = CyanAccent)
            Spacer(modifier = Modifier.height(10.dp))
            Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextActive)
        }
    }
}

@Composable
fun HeaderSection(isListening: Boolean) {
    Row(modifier = Modifier.fillMaxWidth().padding(top = 24.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Column {
            Text("MiBand", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = TextActive)
            Text("NAVIGATOR", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CyanAccent, letterSpacing = 2.sp)
        }
        Surface(color = if(isListening) Color(0xFF10B981).copy(alpha = 0.2f) else Color.Black.copy(alpha = 0.2f), shape = CircleShape) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
                Surface(modifier = Modifier.size(8.dp), shape = CircleShape, color = if(isListening) Color(0xFF10B981) else Color.Gray){}
                Spacer(modifier = Modifier.width(8.dp))
                Text(if(isListening) "Active" else "Idle", fontSize = 12.sp, color = if(isListening) Color(0xFF10B981) else Color.Gray, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun AppNavigationBar(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    NavigationBar(containerColor = SlateBackground, tonalElevation = 0.dp) {
        NavigationBarItem(
            selected = selectedTab == 0, onClick = { onTabSelected(0) },
            label = { Text("Navigate", fontSize = 10.sp, fontWeight = if (selectedTab == 0) FontWeight.SemiBold else FontWeight.Normal) },
            icon = { Icon(Icons.Default.Navigation, contentDescription = null, modifier = Modifier.size(18.dp)) },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = CyanAccent, selectedTextColor = CyanAccent, indicatorColor = Color.Transparent, unselectedIconColor = TextGray, unselectedTextColor = TextGray)
        )
        NavigationBarItem(
            selected = selectedTab == 1, onClick = { onTabSelected(1) },
            label = { Text("Setup", fontSize = 10.sp, fontWeight = if (selectedTab == 1) FontWeight.SemiBold else FontWeight.Normal) },
            icon = { Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(18.dp)) },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = CyanAccent, selectedTextColor = CyanAccent, indicatorColor = Color.Transparent, unselectedIconColor = TextGray, unselectedTextColor = TextGray)
        )
        NavigationBarItem(
            selected = selectedTab == 2, onClick = { onTabSelected(2) },
            label = { Text("Settings", fontSize = 10.sp, fontWeight = if (selectedTab == 2) FontWeight.SemiBold else FontWeight.Normal) },
            icon = { Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(18.dp)) },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = CyanAccent, selectedTextColor = CyanAccent, indicatorColor = Color.Transparent, unselectedIconColor = TextGray, unselectedTextColor = TextGray)
        )
    }
}
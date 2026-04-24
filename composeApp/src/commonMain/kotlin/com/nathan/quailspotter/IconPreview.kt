package com.nathan.quailspotter

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

// Colors
val ForestGreen = Color(0xFF2E7D32)
val LightBrown = Color(0xFFD7CCC8)

@Composable
fun CoturnixQuailShape(modifier: Modifier = Modifier, color: Color = LightBrown) {
    Canvas(modifier = modifier.aspectRatio(1f)) {
        val w = size.width
        val h = size.height

        val path = Path().apply {
            // Start at the tail tip (left)
            moveTo(w * 0.05f, h * 0.75f)
            
            // Tail top edge
            lineTo(w * 0.25f, h * 0.62f)
            
            // Back curve up to the neck
            quadraticTo(w * 0.45f, h * 0.35f, w * 0.75f, h * 0.25f)
            
            // Head top and back of head
            quadraticTo(w * 0.85f, h * 0.18f, w * 0.92f, h * 0.22f)
            
            // Beak
            lineTo(w * 0.98f, h * 0.28f)
            lineTo(w * 0.92f, h * 0.32f)
            
            // Throat and Chest bulge
            quadraticTo(w * 0.88f, h * 0.45f, w * 0.92f, h * 0.62f)
            
            // Belly curve
            quadraticTo(w * 0.85f, h * 0.82f, w * 0.55f, h * 0.85f)
            
            // Under tail
            quadraticTo(w * 0.25f, h * 0.85f, w * 0.05f, h * 0.75f)
            close()
        }
        drawPath(path, color)

        // Eye
        drawCircle(
            color = ForestGreen,
            radius = w * 0.018f,
            center = Offset(w * 0.86f, h * 0.28f)
        )
        
        // Legs
        val legPath = Path().apply {
            // Front leg
            moveTo(w * 0.55f, h * 0.83f)
            lineTo(w * 0.52f, h * 0.95f)
            lineTo(w * 0.45f, h * 0.97f) // Toes
            
            // Back leg
            moveTo(w * 0.65f, h * 0.81f)
            lineTo(w * 0.62f, h * 0.92f)
            lineTo(w * 0.55f, h * 0.94f)
        }
        drawPath(legPath, color, style = Stroke(width = w * 0.02f))
        
        // Simple speckle pattern (a few teardrops)
        val patternColor = ForestGreen.copy(alpha = 0.3f)
        for (i in 0..2) {
            drawCircle(
                color = patternColor,
                radius = w * 0.02f,
                center = Offset(w * (0.75f + i * 0.05f), h * (0.45f + i * 0.08f))
            )
            drawCircle(
                color = patternColor,
                radius = w * 0.015f,
                center = Offset(w * (0.8f + i * 0.04f), h * (0.55f + i * 0.06f))
            )
        }
    }
}

@Preview
@Composable
fun Option1_CoturnixSpotter() {
    // The "Spotter" - Quail in Viewfinder
    Box(
        modifier = Modifier.size(128.dp).background(ForestGreen),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(20.dp)) {
            val thickness = 3.dp.toPx()
            val len = 15.dp.toPx()
            
            // Corners
            drawRect(LightBrown, Offset(0f, 0f), Size(len, thickness))
            drawRect(LightBrown, Offset(0f, 0f), Size(thickness, len))
            drawRect(LightBrown, Offset(size.width - len, 0f), Size(len, thickness))
            drawRect(LightBrown, Offset(size.width - thickness, 0f), Size(thickness, len))
            drawRect(LightBrown, Offset(0f, size.height - thickness), Size(len, thickness))
            drawRect(LightBrown, Offset(0f, size.height - len), Size(thickness, len))
            drawRect(LightBrown, Offset(size.width - len, size.height - thickness), Size(len, thickness))
            drawRect(LightBrown, Offset(size.width - thickness, size.height - len), Size(thickness, len))
        }
        CoturnixQuailShape(modifier = Modifier.fillMaxSize(0.55f))
    }
}

@Preview
@Composable
fun Option2_MinimalistCircle() {
    // Modern minimalist circle
    Box(
        modifier = Modifier.size(128.dp).background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier.fillMaxSize(0.9f).clip(CircleShape).background(ForestGreen),
            contentAlignment = Alignment.Center
        ) {
            CoturnixQuailShape(modifier = Modifier.fillMaxSize(0.6f))
        }
    }
}

@Preview
@Composable
fun Option3_NatureBadge() {
    // Badge style with decorative ring
    Box(
        modifier = Modifier.size(128.dp).background(ForestGreen),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = LightBrown.copy(alpha = 0.2f),
                radius = size.width * 0.42f,
                style = Stroke(width = 2.dp.toPx())
            )
        }
        CoturnixQuailShape(modifier = Modifier.fillMaxSize(0.6f))
    }
}

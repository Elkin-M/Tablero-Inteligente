package com.example.myapplication.ui.auth

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import com.example.myapplication.ui.theme.EcoColors
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onFinished: () -> Unit
) {
    val scale = remember { Animatable(0.7f) }

    LaunchedEffect(Unit) {
        scale.animateTo(1f, animationSpec = tween(1000, easing = FastOutSlowInEasing))
        delay(2000)
        onFinished()
    }

    SplashScreenContent(scale.value)
}

@Composable
fun SplashScreenContent(scaleValue: Float) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(EcoColors.DarkGreen),
        contentAlignment = Alignment.Center
    ) {
        // Imagen de fondo que ocupa toda la pantalla sin bordes
        Image(
            painter = painterResource(id = R.drawable.splash_overlay),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Capa semi-transparente para asegurar legibilidad del texto y logo
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
        )

        // Contenido animado sobre la imagen
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size((100 * scaleValue).dp)
                    .background(EcoColors.DarkGreen, shape = CircleShape), // <-- Usando DarkGreen
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.overlay),
                    contentDescription = "Logo",
                    modifier = Modifier.size(64.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "EcoLibertad IA",
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "Juntos cuidamos nuestro planeta",
                color = Color.White.copy(alpha = 0.9f),
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(64.dp))

            CircularProgressIndicator(
                color = EcoColors.AccentGreen,
                modifier = Modifier.size(44.dp),
                strokeWidth = 4.dp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    MyApplicationTheme {
        SplashScreenContent(scaleValue = 1f)
    }
}
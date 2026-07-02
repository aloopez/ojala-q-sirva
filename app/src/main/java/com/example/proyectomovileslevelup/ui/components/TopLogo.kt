package com.example.proyectomovileslevelup.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectomovileslevelup.ui.theme.Black
import com.example.proyectomovileslevelup.ui.theme.White

@Composable
fun TopLogo() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Black)
            .statusBarsPadding()
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "LEVELUP",
            color = White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 8.sp
        )
    }
}

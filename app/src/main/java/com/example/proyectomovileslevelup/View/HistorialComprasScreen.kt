package com.example.proyectomovileslevelup.View

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.proyectomovileslevelup.Data.Compra
import com.example.proyectomovileslevelup.Data.PurchaseRepository
import com.example.proyectomovileslevelup.ui.components.TopLogo
import com.example.proyectomovileslevelup.ui.theme.*
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialComprasScreen(navController: NavController) {
    val compras by PurchaseRepository.purchases.collectAsState()
    val totalGastado = compras.sumOf { it.precio }

    Scaffold(
        topBar = {
            Column {
                TopLogo()
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Regresar",
                        tint = White
                    )
                }
            }
        },
        containerColor = Black
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Black)
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = "HISTORIAL DE COMPRAS",
                color = White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
            
            Spacer(modifier = Modifier.height(24.dp))

            // Tarjeta de resumen (Estética premium)
            Card(
                colors = CardDefaults.cardColors(containerColor = AstonSurface),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(0.5.dp, Color.DarkGray)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "TOTAL GASTADO EN TECNOLOGÍA",
                        color = Silver,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "$${String.format(Locale.US, "%.2f", totalGastado)}",
                        color = White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                "TUS ADQUISICIONES",
                color = Silver,
                fontSize = 14.sp,
                letterSpacing = 2.sp,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            if (compras.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No tienes compras registradas", color = Silver)
                }
            } else {
                // Lista de compras
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(compras) { compra ->
                        CompraItemCard(compra)
                    }
                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }
            }
        }
    }
}

@Composable
fun CompraItemCard(compra: Compra) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkGrey),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = compra.producto,
                    color = White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "FECHA: ${compra.fecha}",
                    color = Silver,
                    fontSize = 12.sp,
                    letterSpacing = 1.sp
                )
            }
            Text(
                text = "$${String.format(Locale.US, "%.2f", compra.precio)}",
                color = White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

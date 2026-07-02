package com.example.proyectomovileslevelup.View

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proyectomovileslevelup.ViewModel.ProductDetailViewModel
import com.example.proyectomovileslevelup.Navigation.AppRoutes
import com.example.proyectomovileslevelup.ui.components.TopLogo
import com.example.proyectomovileslevelup.ui.theme.Black
import com.example.proyectomovileslevelup.ui.theme.DarkGrey
import com.example.proyectomovileslevelup.ui.theme.Silver
import com.example.proyectomovileslevelup.ui.theme.White
import kotlinx.coroutines.launch
import androidx.compose.runtime.*
import coil.compose.AsyncImage
import java.util.Locale

@Composable
fun ProductDetailPage(
    productId: String,
    navController: NavController,
    viewModel: ProductDetailViewModel = viewModel()
) {
    val product by viewModel.product.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(productId) {
        println("DEBUG: Cargando producto con ID: $productId")
        viewModel.loadProduct(productId)
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = DarkGrey,
                    contentColor = White,
                    actionColor = White
                )
            }
        },
        topBar = {
            Column {
                TopLogo()
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = White)
                }
            }
        },
        containerColor = Black
    ) { paddingValues ->
        product?.let { item ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                // Placeholder Image
                // Cambia el Box por AsyncImage
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = "Imagen de ${item.name}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop // Esto ajusta la imagen para que se vea bien
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Text(
                    text = item.category.uppercase(),
                    color = Silver,
                    fontSize = 12.sp,
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.Light
                )
                
                Text(
                    text = item.name,
                    color = White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                
                Text(
                    text = item.brand.uppercase(),
                    color = White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "DESCRIPCIÓN",
                    color = Silver,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = item.description,
                    color = White,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.Light
                )
                
                Spacer(modifier = Modifier.height(40.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "PRECIO", color = Silver, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = "$${String.format(Locale.US, "%.2f", item.price)}",
                            color = White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Button(
                        onClick = { 
                            viewModel.addToCart(item)
                            scope.launch {
                                val result = snackbarHostState.showSnackbar(
                                    message = "Se añadió ${item.name} al carrito",
                                    actionLabel = "VER CARRITO",
                                    duration = SnackbarDuration.Short
                                )
                                if (result == SnackbarResult.ActionPerformed) {
                                    navController.navigate(AppRoutes.Cart)
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = White, contentColor = Black),
                        shape = MaterialTheme.shapes.extraSmall,
                        modifier = Modifier.height(56.dp)
                    ) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "AÑADIR AL CARRITO", fontWeight = FontWeight.Bold)
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = White)
        }
    }
}

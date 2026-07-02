package com.example.proyectomovileslevelup.View

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.proyectomovileslevelup.Navigation.AppRoutes
import com.example.proyectomovileslevelup.ui.components.LevelUpBottomBar
import com.example.proyectomovileslevelup.ui.components.TopLogo
import com.example.proyectomovileslevelup.ui.theme.Black
import com.example.proyectomovileslevelup.ui.theme.White
import com.example.proyectomovileslevelup.ui.theme.Silver
import com.example.proyectomovileslevelup.ui.theme.DarkGrey
import com.example.proyectomovileslevelup.Data.ProductRepository

import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.proyectomovileslevelup.Data.Auth.FireBaseAutenRepositorio
import com.example.proyectomovileslevelup.ViewModel.AutenViewModel
import com.example.proyectomovileslevelup.ViewModel.CatalogViewModel
import com.example.proyectomovileslevelup.ViewModel.CatalogViewModelFactory
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.Brush
import java.util.Locale

@Composable
fun HomePage(navController: NavController) {

    val autenViewModel: AutenViewModel = viewModel(
        factory = AutenViewModel.Factory(FireBaseAutenRepositorio()),
    )

    Scaffold(
        topBar = { TopLogo() },
        bottomBar = { LevelUpBottomBar(navController, autenViewModel) },
        containerColor = Black
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Black)
        ) {
            item {
                HeroSection(navController)
            }
            item {
                FeaturedCategories(navController)
            }
            item {
                LatestArrivals(navController)
            }
        }
    }
}

@Composable
fun HeroSection(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp),
        contentAlignment = Alignment.BottomStart
    ) {
        // Imagen de Banner de Fondo
        AsyncImage(
            model = "https://i.ibb.co/8nR8tKCb/hero-bg.jpg",
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Capa de degradado para asegurar que el texto sea legible (Estética Aston Martin)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Black.copy(alpha = 0.8f)),
                        startY = 500f
                    )
                )
        )

        Column(
            modifier = Modifier
                .padding(24.dp)
        ) {
            Text(
                text = "LEVELUP PC'S",
                color = White,
                fontSize = 12.sp,
                letterSpacing = 4.sp,
                fontWeight = FontWeight.Light
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "EL PODER EN\nTUS MANOS",
                color = White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 38.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { 
                    navController.navigate(AppRoutes.Catalog()) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        // No restauramos estado aquí para que "Explorar" siempre limpie filtros
                        restoreState = false
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = White, contentColor = Black),
                shape = MaterialTheme.shapes.extraSmall // Estilo cuadrado elegante
            ) {
                Text(text = "EXPLORAR", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun FeaturedCategories(navController: NavController) {
    Column(modifier = Modifier.padding(24.dp)) {
        Text(
            text = "CATEGORÍAS DESTACADAS",
            color = Silver,
            fontSize = 14.sp,
            letterSpacing = 2.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        // Aquí irían tarjetas minimalistas
        CategoryItem("PROCESADORES") {
            navController.navigate(AppRoutes.Catalog("Procesadores", System.currentTimeMillis())) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
            }
        }
        CategoryItem("TARJETAS GRÁFICAS") {
            navController.navigate(AppRoutes.Catalog("Tarjetas Gráficas", System.currentTimeMillis())) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
            }
        }
        CategoryItem("MEMORIA RAM") {
            navController.navigate(AppRoutes.Catalog("Memoria RAM", System.currentTimeMillis())) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
            }
        }
    }
}

@Composable
fun CategoryItem(name: String, onClick: () -> Unit) {
    Column(modifier = Modifier.clickable { onClick() }) {
        HorizontalDivider(color = Color.DarkGray, thickness = 0.5.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = name, color = White, fontSize = 18.sp, fontWeight = FontWeight.Light)
            Text(text = "→", color = White, fontSize = 20.sp)
        }
    }
}

@Composable
fun LatestArrivals(navController: NavController) {
    // Usamos el CatalogViewModel para obtener la lista desde Firebase
    val viewModel: CatalogViewModel = viewModel(
        factory = CatalogViewModelFactory(ProductRepository())
    )
    val products by viewModel.products.collectAsState()
    val featuredProducts = products.take(2)

    Column(modifier = Modifier.padding(24.dp)) {
        Text(
            text = "LO ÚLTIMO EN TIENDA",
            color = Silver,
            fontSize = 14.sp,
            letterSpacing = 2.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            featuredProducts.forEach { product ->
                ProductCard(product.name, "$${String.format(Locale.US, "%.2f", product.price)}", product.imageUrl, Modifier.weight(1f)) {
                    navController.navigate(AppRoutes.ProductDetail(product.id))
                }
            }
        }
    }
}

@Composable
fun ProductCard(name: String, price: String, imageUrl: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Column(modifier = modifier.clickable { onClick() }) {
        AsyncImage(
            model = imageUrl,
            contentDescription = "Imagen de $name",
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .background(DarkGrey),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = name, color = White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        Text(text = price, color = Silver, fontSize = 12.sp)
    }
}

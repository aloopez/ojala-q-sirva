package com.example.proyectomovileslevelup.View

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proyectomovileslevelup.Data.Auth.FireBaseAutenRepositorio
import com.example.proyectomovileslevelup.Navigation.AppRoutes
import com.example.proyectomovileslevelup.Data.Product
import com.example.proyectomovileslevelup.Data.ProductRepository
import com.example.proyectomovileslevelup.ViewModel.AutenViewModel
import com.example.proyectomovileslevelup.ViewModel.CatalogViewModel
import com.example.proyectomovileslevelup.ViewModel.CatalogViewModelFactory
import com.example.proyectomovileslevelup.ui.components.LevelUpBottomBar
import com.example.proyectomovileslevelup.ui.components.TopLogo
import com.example.proyectomovileslevelup.ui.theme.Black
import com.example.proyectomovileslevelup.ui.theme.DarkGrey
import com.example.proyectomovileslevelup.ui.theme.Silver
import com.example.proyectomovileslevelup.ui.theme.White
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.draw.scale
import coil.compose.AsyncImage
import java.util.Locale

@Composable
fun CatalogPage(
    navController: NavController,
    viewModel: CatalogViewModel = viewModel(
        factory = CatalogViewModelFactory(ProductRepository()),
    ),
    initialCategory: String = "Todas",
    navId: Long = 0
) {
    val products by viewModel.filteredProducts.collectAsState(initial = emptyList())
    val categories by viewModel.categories.collectAsState()
    val brands by viewModel.brands.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedBrand by viewModel.selectedBrand.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val priceRange by viewModel.priceRange.collectAsState()

    val autenViewModel: AutenViewModel = viewModel(
        factory = AutenViewModel.Factory(FireBaseAutenRepositorio())
    )

    // Solo aplicamos la categoría si el navId cambia (nueva navegación desde Home)
    LaunchedEffect(navId) {
        if (navId != 0L) {
            viewModel.selectCategory(initialCategory)
        }
    }
    
    var showFilters by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopLogo() },
        bottomBar = { LevelUpBottomBar(navController, autenViewModel) },
        containerColor = Black
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Black)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(end = 16.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    SearchBar(searchQuery) { viewModel.updateSearchQuery(it) }
                }
                IconButton(onClick = { showFilters = !showFilters }) {
                    Icon(
                        Icons.Default.FilterList,
                        contentDescription = "Filtros",
                        tint = if (showFilters) White else Silver
                    )
                }
            }
            
            if (showFilters) {
                FilterSection(
                    categories = categories,
                    selectedCategory = selectedCategory,
                    onCategorySelected = { viewModel.selectCategory(it) },
                    brands = brands,
                    selectedBrand = selectedBrand,
                    onBrandSelected = { viewModel.selectBrand(it) },
                    priceRange = priceRange,
                    onPriceRangeChange = { viewModel.updatePriceRange(it) }
                )
            } else {
                // Si los filtros están ocultos, al menos mostrar las categorías principales
                CategoryFilter(
                    categories = categories,
                    selectedCategory = selectedCategory,
                    onCategorySelected = { viewModel.selectCategory(it) }
                )
            }

            if (products.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No se encontraron productos", color = Silver)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(products) { product ->
                        CatalogProductCard(product) {
                            navController.navigate(AppRoutes.ProductDetail(product.id))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FilterSection(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    brands: List<String>,
    selectedBrand: String,
    onBrandSelected: (String) -> Unit,
    priceRange: ClosedFloatingPointRange<Float>,
    onPriceRangeChange: (ClosedFloatingPointRange<Float>) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(DarkGrey, RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Text("CATEGORÍA", color = Silver, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        CategoryFilterRow(categories, selectedCategory, onCategorySelected)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text("MARCA", color = Silver, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        CategoryFilterRow(brands, selectedBrand, onBrandSelected)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text("PRECIO", color = Silver, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text(
                "$${priceRange.start.toInt()} - $${priceRange.endInclusive.toInt()}",
                color = White,
                fontSize = 10.sp
            )
        }
        RangeSlider(
            value = priceRange,
            onValueChange = onPriceRangeChange,
            valueRange = 0f..2000f,
            colors = SliderDefaults.colors(
                thumbColor = White,
                activeTrackColor = White,
                inactiveTrackColor = Color.Gray
            )
        )
    }
}

@Composable
fun CategoryFilterRow(items: List<String>, selectedItem: String, onItemSelected: (String) -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(items) { item ->
            val isSelected = item == selectedItem
            Surface(
                onClick = { onItemSelected(item) },
                color = if (isSelected) White else Color.Transparent,
                border = if (isSelected) null else BorderStroke(0.5.dp, Color.Gray),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = item.uppercase(),
                    color = if (isSelected) Black else Silver,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        placeholder = { Text("Buscar componentes...", color = Silver) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Silver) },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = DarkGrey,
            unfocusedContainerColor = DarkGrey,
            focusedTextColor = White,
            unfocusedTextColor = White,
            cursorColor = White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        shape = RoundedCornerShape(8.dp),
        singleLine = true
    )
}

@Composable
fun CategoryFilter(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(bottom = 8.dp)
    ) {
        items(categories) { category ->
            val isSelected = category == selectedCategory
            Surface(
                onClick = { onCategorySelected(category) },
                color = if (isSelected) White else DarkGrey,
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.height(36.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = category.uppercase(),
                        color = if (isSelected) Black else Silver,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}

@Composable
fun CatalogProductCard(product: Product, onClick: () -> Unit) {
    // 1. Detectamos si el usuario está presionando la tarjeta
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // 2. Animamos la escala (se encoge levemente al presionar)
    val scale by animateFloatAsState(if (isPressed) 0.95f else 1f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale) // Aplicamos la animación
            .clickable(
                interactionSource = interactionSource,
                indication = null // Quitamos el efecto ripple por defecto para uno más elegante
            ) { onClick() }
    ) {
        AsyncImage(
            model = product.imageUrl,
            contentDescription = "Imagen de ${product.name}",
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .background(DarkGrey),
            contentScale = androidx.compose.ui.layout.ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = product.category.uppercase(),
                color = Silver,
                fontSize = 10.sp,
                letterSpacing = 1.sp
            )
            Text(
                text = product.brand.uppercase(),
                color = Silver,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = product.name,
            color = White,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )
        Text(
            text = "$${String.format(Locale.US, "%.2f", product.price)}",
            color = White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Light
        )
    }
}

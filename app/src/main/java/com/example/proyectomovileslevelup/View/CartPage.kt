package com.example.proyectomovileslevelup.View

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.History
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
import coil.compose.AsyncImage
import java.util.Locale
import com.example.proyectomovileslevelup.Data.Auth.FireBaseAutenRepositorio
import com.example.proyectomovileslevelup.Data.CartItem
import com.example.proyectomovileslevelup.ViewModel.AutenViewModel
import com.example.proyectomovileslevelup.ViewModel.CartViewModel
import com.example.proyectomovileslevelup.Navigation.AppRoutes
import com.example.proyectomovileslevelup.ui.components.LevelUpBottomBar
import com.example.proyectomovileslevelup.ui.components.TopLogo
import com.example.proyectomovileslevelup.ui.theme.Black
import com.example.proyectomovileslevelup.ui.theme.DarkGrey
import com.example.proyectomovileslevelup.ui.theme.Silver
import com.example.proyectomovileslevelup.ui.theme.White
import kotlinx.coroutines.launch

@Composable
fun CartPage(navController: NavController, viewModel: CartViewModel = viewModel()) {

    val autenViewModel: AutenViewModel = viewModel(
        factory = AutenViewModel.Factory(FireBaseAutenRepositorio())
    )

    val cartItems by viewModel.cartItems.collectAsState()
    var itemToDelete by remember { mutableStateOf<CartItem?>(null) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    if (itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            containerColor = DarkGrey,
            title = {
                Text(
                    "¿Eliminar artículo?",
                    color = White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    "¿Estás seguro de que quieres eliminar ${itemToDelete?.product?.name} de tu carrito?",
                    color = Silver
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    itemToDelete?.let { viewModel.removeItem(it.product.id) }
                    itemToDelete = null
                }) {
                    Text("ELIMINAR", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) {
                    Text("CANCELAR", color = White)
                }
            }
        )
    }

    Scaffold(
        topBar = { TopLogo() },
        bottomBar = { LevelUpBottomBar(navController, autenViewModel) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Black
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Black)
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TU CARRITO",
                    color = White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                
                IconButton(onClick = { navController.navigate(AppRoutes.PurchaseHistory) }) {
                    Icon(Icons.Default.History, contentDescription = "Historial", tint = White)
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))

            if (cartItems.isEmpty()) {
                EmptyCartMessage()
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(cartItems) { item ->
                        CartItemRow(
                            item = item,
                            onUpdateQuantity = { delta ->
                                if (item.quantity == 1 && delta < 0) {
                                    itemToDelete = item
                                } else {
                                    viewModel.updateQuantity(item.product.id, delta)
                                }
                            },
                            onRemove = { itemToDelete = item }
                        )
                        HorizontalDivider(
                            color = DarkGrey,
                            thickness = 0.5.dp,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }
                }
                
                CartSummary(viewModel.getTotal()) {
                    viewModel.checkout()
                    scope.launch {
                        snackbarHostState.showSnackbar("¡Compra realizada con éxito! Revisa tu historial.")
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemRow(
    item: CartItem,
    onUpdateQuantity: (Int) -> Unit,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = item.product.imageUrl,
            contentDescription = item.product.name,
            modifier = Modifier
                .size(80.dp)
                .background(DarkGrey),
            contentScale = androidx.compose.ui.layout.ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = item.product.name, color = White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Text(text = "$${String.format(Locale.US, "%.2f", item.product.price)}", color = Silver, fontSize = 14.sp)
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { onUpdateQuantity(-1) },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = White)
                }
                Text(
                    text = "${item.quantity}",
                    color = White,
                    modifier = Modifier.padding(horizontal = 12.dp),
                    fontSize = 16.sp
                )
                IconButton(
                    onClick = { onUpdateQuantity(1) },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Increase", tint = White)
                }
            }
        }
        IconButton(onClick = onRemove) {
            Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Color.Gray)
        }
    }
}

@Composable
fun CartSummary(total: Double, onCheckout: () -> Unit) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "TOTAL ESTIMADO", color = Silver, fontSize = 14.sp, letterSpacing = 1.sp)
            Text(text = "$${String.format(Locale.US, "%.2f", total)}", color = White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onCheckout,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = White, contentColor = Black),
            shape = MaterialTheme.shapes.extraSmall
        ) {
            Text(text = "PROCEDER AL PAGO", fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
        }
    }
}

@Composable
fun EmptyCartMessage() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Tu carrito está vacío", color = Silver, fontSize = 16.sp)
    }
}

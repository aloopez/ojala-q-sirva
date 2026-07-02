package com.example.proyectomovileslevelup.Data

import com.example.proyectomovileslevelup.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object ChatRepository {

    private val productRepository = ProductRepository()
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val systemPrompt = """
    Eres el asesor de hardware de LevelUP PC's (tienda de PC en El Salvador).
    Ayudas a elegir componentes y explicas compatibilidad entre piezas.
    
    Reglas:
    - SOLO recomienda productos de la lista proporcionada, con precios y nombres exactos.
    - No tienes specs técnicas detalladas (núcleos, VRAM, etc.); si preguntan eso, 
      di que revisen la ficha del producto en la app.
    - Si la consulta es vaga (ej. "PC para gaming"), pregunta presupuesto o uso antes 
      de recomendar.
    - Si solo saludan, responde breve y pregunta en qué ayudas.
    - Respuestas cortas (3-4 líneas máx), claras y amigables.
    - Si preguntan algo fuera de hardware/PC, redirige amablemente al tema de la tienda.
""".trimIndent()

    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    suspend fun sendMessage(userMessage: String) {
        //1 Agregar el mensaje del usuario al historial
        _messages.update { it + ChatMessage(text = userMessage, isFromUser = true) }

        _isLoading.value = true

        try {
            //2 Construir el prompt con contexto + historial + nuevo mensaje
            val prompt = buildPrompt(userMessage)

            //3 Llamar a Gemini
            val response = generativeModel.generateContent(prompt)
            val responseText = response.text ?: "No pude generar una respuesta, intenta de nuevo."

            //4 Agregar la respuesta de Gemini al historial
            _messages.update { it + ChatMessage(text = responseText, isFromUser = false) }

        } catch (e: Exception) {
            println("DEBUG_CHAT: ${e.message}")
            e.printStackTrace()

            val mensajeError = if (e.message?.contains("quota", ignoreCase = true) == true) {
                "Hemos alcanzado el límite de consultas por ahora. Intenta de nuevo en unos minutos."
            } else {
                "Hubo un error al conectar con el asesor. Intenta de nuevo."
            }

            _messages.update {
                it + ChatMessage(
                    text = mensajeError,
                    isFromUser = false
                )
            }
        } finally {
            _isLoading.value = false
        }
    }


    private suspend fun buildPrompt(userMessage: String): String {
        val productos = productRepository.getProducts()

        val contextoProductos = if (productos.isNotEmpty()) {
            productos.joinToString("\n") { producto ->
                "- ${producto.name} (${producto.category}, ${producto.brand}): $${producto.price}"
            }
        } else {
            "No hay productos disponibles en este momento."
        }

        // NUEVO: construir el historial de la conversación
        val historial = _messages.value.joinToString("\n") { mensaje ->
            if (mensaje.isFromUser) "Usuario: ${mensaje.text}" else "Asesor: ${mensaje.text}"
        }

        return """
        $systemPrompt
        
        Productos disponibles actualmente en LevelUP PC's:
        $contextoProductos
        
        Historial de la conversación:
        $historial
        
        Nuevo mensaje del usuario: $userMessage
    """.trimIndent()
    }

        fun clearChat() {
            _messages.value = emptyList()
        }

    }


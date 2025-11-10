package com.example.reto11.service

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeminiService(private val apiKey: String) {

    // Prompt del sistema para limitar las respuestas a temas de cocina
    private val systemInstruction = """
        Eres un asistente especializado en cocina, recetas, postres y temas culinarios.
        Tu función es ayudar a los usuarios con:
        - Recetas de cocina (platos principales, entradas, guarniciones)
        - Recetas de postres y dulces
        - Técnicas de cocina
        - Consejos culinarios
        - Información sobre ingredientes
        - Sustituciones de ingredientes
        - Tips de presentación de platos

        IMPORTANTE:
        - Solo debes responder preguntas relacionadas con cocina, recetas y temas culinarios.
        - Si el usuario pregunta sobre otros temas, debes amablemente redirigir la conversación hacia temas de cocina.
        - SIEMPRE formatea tus respuestas usando Markdown para mejorar la legibilidad:
          * Usa **negritas** para títulos de secciones y nombres de platos
          * Usa listas con - o * para ingredientes y pasos
          * Usa listas numeradas (1., 2., 3.) para los pasos de las recetas
          * Usa *cursivas* para énfasis en consejos o tips
          * Usa > para citas o notas importantes
        - Sé amable, profesional y proporciona información detallada y útil sobre cocina.
    """.trimIndent()

    private val generativeModel: GenerativeModel by lazy {
        GenerativeModel(
            modelName = "gemini-2.5-flash",
            apiKey = apiKey,
            generationConfig = generationConfig {
                temperature = 0.7f
                topK = 40
                topP = 0.95f
                maxOutputTokens = 8192  // Aumentado para permitir respuestas detalladas de recetas
            }
        )
    }

    suspend fun generateResponse(prompt: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val response = generativeModel.generateContent(prompt)
                val text = response.text ?: "No se pudo generar una respuesta."
                Result.success(text)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun generateChatResponse(conversationHistory: List<String>): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                // Construir el contexto de la conversación con las instrucciones del sistema
                val context = buildString {
                    appendLine("INSTRUCCIONES DEL SISTEMA:")
                    appendLine(systemInstruction)
                    appendLine("\nCONVERSACIÓN:")
                    appendLine(conversationHistory.joinToString("\n"))
                }
                val response = generativeModel.generateContent(context)
                val text = response.text ?: "No se pudo generar una respuesta."
                Result.success(text)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}


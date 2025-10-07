package com.vanaspati.data.network

import android.graphics.Bitmap
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.vanaspati.BuildConfig

class GeminiService {

    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.0-flash-001",
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    suspend fun getPlantDetails(prompt: String): String {
        val response = generativeModel.generateContent(prompt)
        return response.text ?: "No details found."
    }

    suspend fun identifyPlant(imageBitmap: Bitmap): String {
        val prompt = "Identify the plant in this image."

        val inputContent = content {
            image(imageBitmap)
            text(prompt)
        }

        val response = generativeModel.generateContent(inputContent)
        return response.text ?: "Could not identify the plant"
    }
}

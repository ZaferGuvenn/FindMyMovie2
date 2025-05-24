package com.composemovie2.findmymovie.data.remote

import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeminiAiService(private val apiKey: String) {

    private val generativeModel = GenerativeModel(
        modelName = "gemini-pro",
        apiKey = apiKey
    )

    suspend fun getMovieRecommendations(prompt: String): String {
        return withContext(Dispatchers.IO) {
            try {
                val response = generativeModel.generateContent(prompt)
                response.text?.let {
                    Log.i("GeminiAiService", "Successfully received recommendations.")
                    return@withContext it
                } ?: run {
                    Log.e("GeminiAiService", "Response text is null.")
                    throw CustomGeminiException("Response text is null from Gemini API.")
                }
            } catch (e: Exception) {
                Log.e("GeminiAiService", "Error calling Gemini API: ${e.message}", e)
                // Depending on how you want to propagate errors, you might re-throw or return a specific error string.
                throw CustomGeminiException("Error generating content: ${e.message}", e)
            }
        }
    }
}

class CustomGeminiException(message: String, cause: Throwable? = null) : Exception(message, cause)

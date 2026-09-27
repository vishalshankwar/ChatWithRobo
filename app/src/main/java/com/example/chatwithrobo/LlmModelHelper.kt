package com.example.chatwithrobo

import android.content.Context
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import com.google.mediapipe.tasks.genai.llminference.LlmInference.LlmInferenceOptions
import com.google.mediapipe.tasks.genai.llminference.LlmInferenceSession
import com.google.mediapipe.tasks.genai.llminference.LlmInferenceSession.LlmInferenceSessionOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.io.File

class LlmModelHelper(private val context: Context) {

    private var llmInference: LlmInference? = null
    private var session: LlmInferenceSession? = null

    private fun copyModelFromAssets(): String {
        val outFile = File(context.filesDir, "model.litertlm")
        if (!outFile.exists()) {
            context.assets.open("model.litertlm").use { input ->
                outFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        }
        return outFile.absolutePath
    }

    fun initialize() {
        val modelPath = copyModelFromAssets()

        val options = LlmInferenceOptions.builder()
            .setModelPath(modelPath)
            .setMaxTokens(1024)
            .setPreferredBackend(LlmInference.Backend.CPU)
            .build()

        llmInference = LlmInference.createFromOptions(context, options)

        val sessionOptions = LlmInferenceSessionOptions.builder()
            .setTemperature(0.8f)
            .setTopK(40)
            .setTopP(0.9f)
            .build()

        session = LlmInferenceSession.createFromOptions(llmInference, sessionOptions)
    }

    fun generateResponse(prompt: String): Flow<String> = callbackFlow {
        val s = session ?: throw IllegalStateException("Model abhi initialize nahi hua")

        val instructedPrompt = buildPrompt(prompt)

        s.addQueryChunk(instructedPrompt)
        s.generateResponseAsync { partialResult, done ->
            trySend(partialResult)
            if (done) close()
        }
        awaitClose { }
    }

    private fun buildPrompt(userMessage: String): String {
        val systemInstruction = """
            You are a helpful assistant. Follow these rules strictly:
            1. Give SHORT and CONCISE answers by default. Only answer exactly what is asked, nothing extra.
            2. Do NOT add long explanations, extra background, or extra details unless the user explicitly asks for it (e.g. "explain in detail", "describe fully", "detail mein batao", "vistaar se samjhao").
            3. If the user asks for detail explicitly, THEN give a full detailed explanation.
            4. Always reply in the SAME language and style the user used. If the user writes in Hinglish (Hindi written in English letters, mixed with English words), reply in Hinglish — do NOT switch to pure Hindi (Devanagari) or pure formal English. If the user writes in plain English, reply in English. If the user writes in Hindi, reply in Hindi.
            5. Keep tone natural and conversational, not robotic or overly formal.
        """.trimIndent()

        return "$systemInstruction\n\nUser: $userMessage\nAssistant:"
    }

    fun close() {
        session?.close()
        llmInference?.close()
    }
}
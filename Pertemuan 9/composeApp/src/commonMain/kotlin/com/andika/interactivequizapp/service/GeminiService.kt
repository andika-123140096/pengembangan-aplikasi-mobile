package com.andika.interactivequizapp.service

import com.andika.interactivequizapp.BuildKonfig
import com.andika.interactivequizapp.model.*
import com.andika.interactivequizapp.viewmodel.SelectedFile
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi


class GeminiService {

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
    }

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(json)
        }
        install(Logging) {
            level = LogLevel.INFO
        }
    }

    private val apiKey = BuildKonfig.GEMINI_API_KEY
    private val modelName = BuildKonfig.GEMINI_MODEL_NAME
    private val baseUrl = "https://generativelanguage.googleapis.com/v1beta/models/$modelName:generateContent"

    
    @OptIn(ExperimentalEncodingApi::class)
    suspend fun generateTitle(prompt: String, files: List<SelectedFile>): String {
        val schema = Schema(
            type = "OBJECT",
            properties = mapOf("title" to Schema(type = "STRING", description = "A short and catchy title for the quiz")),
            required = listOf("title")
        )

        val tool = Tool(listOf(FunctionDeclaration("submit_title", "Submits the quiz title.", schema)))
        
        val parts = createParts(prompt, files)
        parts.add(Part(text = "Generate a catchy title for a quiz based on the provided context."))

        val response = callGemini(parts, tool, "submit_title")
        return response.jsonObject["title"]?.jsonPrimitive?.content ?: "Interactive Quiz"
    }

    
    @OptIn(ExperimentalEncodingApi::class)
    suspend fun generateQuestion(
        prompt: String,
        files: List<SelectedFile>,
        difficulty: String,
        index: Int,
        total: Int,
        excludedQuestions: List<String>,
    ): QuizQuestion {
        val schema = Schema(
            type = "OBJECT",
            properties = mapOf(
                "question" to Schema(type = "STRING", description = "The question text"),
                "options" to Schema(
                    type = "ARRAY",
                    items = Schema(type = "STRING"),
                    description = "Exactly 4 multiple-choice options. MUST include the correct answer."
                ),
                "correctAnswer" to Schema(
                    type = "STRING",
                    description = "The exact string of the correct answer. MUST match one of the options perfectly."
                ),
                "rationale" to Schema(type = "STRING", description = "Brief explanation why the answer is correct")
            ),
            required = listOf("question", "options", "correctAnswer", "rationale")
        )

        val tool = Tool(listOf(FunctionDeclaration("submit_question", "Submits one quiz question.", schema)))
        
        val parts = createParts(prompt, files)
        val exclusionText = if (excludedQuestions.isNotEmpty()) {
            "\nDO NOT repeat or use similar topics as these questions:\n${excludedQuestions.joinToString("\n")}"
        } else ""
        
        parts.add(Part(text = """
            Generate question #$index of $total for a multiple-choice quiz.
            Language: Indonesian.
            Difficulty: $difficulty
            
            REQUIREMENTS:
            1. Provide EXACTLY 4 options.
            2. The 'correctAnswer' MUST be one of the strings inside the 'options' array.
            3. Ensure the 'correctAnswer' matches the chosen option character-for-character.
            4. Focus on accuracy and relevance to the provided documents.
            $exclusionText
        """.trimIndent()))

        val response = callGemini(parts, tool, "submit_question")
        return json.decodeFromJsonElement<QuizQuestion>(response)
    }

    @OptIn(ExperimentalEncodingApi::class)
    private fun createParts(prompt: String, files: List<SelectedFile>): MutableList<Part> {
        val parts = mutableListOf<Part>()
        files.forEach { file ->
            if (file.name.lowercase().endsWith(".pdf")) {
                parts.add(Part(inlineData = InlineData("application/pdf", Base64.encode(file.content))))
            } else {
                parts.add(Part(text = "Document Content (${file.name}):\n${file.content.decodeToString()}"))
            }
        }
        if (prompt.isNotBlank()) {
            parts.add(Part(text = "User Additional Context/Instructions: $prompt"))
        }
        return parts
    }

    private suspend fun callGemini(parts: List<Part>, tool: Tool, functionName: String): kotlinx.serialization.json.JsonObject {
        val requestBody = GeminiRequest(
            contents = listOf(Content(parts = parts, role = "user")),
            tools = listOf(tool),
            toolConfig = ToolConfig(FunctionCallingConfig(mode = "ANY")),
            generationConfig = GenerationConfig(temperature = 0.7f)
        )

        val response: HttpResponse = client.post("$baseUrl?key=$apiKey") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }

        if (response.status.isSuccess()) {
            val geminiResponse = response.body<GeminiResponse>()
            val functionCall = geminiResponse.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.functionCall
                ?: throw Exception("Model failed to call $functionName. Respon: ${response.bodyAsText()}")
            
            if (functionCall.name != functionName) {
                throw Exception("Unexpected function call: ${functionCall.name}")
            }

            return functionCall.args
        } else {
            val errorBody = response.bodyAsText()
            throw Exception("API Error ${response.status}: $errorBody")
        }
    }
}

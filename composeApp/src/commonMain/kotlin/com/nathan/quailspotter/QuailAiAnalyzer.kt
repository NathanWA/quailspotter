package com.nathan.quailspotter

import com.aallam.openai.api.chat.*
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig
import com.nathan.quailspotter.domain.QuailAnalysisResult
import kotlinx.serialization.json.*
import kotlin.time.Duration.Companion.seconds

class QuailAiAnalyzer(
    apiKey: String
) {
    private val openAI = OpenAI(
        OpenAIConfig(
            token = apiKey,
            timeout = Timeout(socket = 60.seconds)
        )
    )

    suspend fun analyzeQuailImage(
        imageBytes: ByteArray,
        mimeType: String = "image/jpeg"
    ): QuailAnalysisResult {
        val base64 = encodeBase64(imageBytes)
        val dataUrl = "data:$mimeType;base64,$base64"

        val schema = buildJsonObject {
            put("type", "object")
            putJsonObject("properties") {
                putJsonObject("birds") {
                    put("type", "array")
                    putJsonObject("items") {
                        put("type", "object")
                        putJsonObject("properties") {
                            putJsonObject("id") { put("type", "integer") }
                            putJsonObject("sex") {
                                put("type", "string")
                                putJsonArray("enum") {
                                    add("male"); add("female"); add("uncertain")
                                }
                            }
                            putJsonObject("symbol") {
                                put("type", "string")
                                putJsonArray("enum") {
                                    add("♂"); add("♀"); add("?")
                                }
                            }
                            putJsonObject("genetics") { put("type", "string") }
                            putJsonObject("confidence") { put("type", "string") }
                            putJsonObject("reasoning") { put("type", "string") }
                            putJsonObject("box") {
                                put("type", "object")
                                putJsonObject("properties") {
                                    putJsonObject("x") { put("type", "number") }
                                    putJsonObject("y") { put("type", "number") }
                                    putJsonObject("width") { put("type", "number") }
                                    putJsonObject("height") { put("type", "number") }
                                }
                                putJsonArray("required") {
                                    add("x"); add("y"); add("width"); add("height")
                                }
                                put("additionalProperties", false)
                            }
                        }
                        putJsonArray("required") {
                            add("id")
                            add("sex")
                            add("symbol")
                            add("genetics")
                            add("confidence")
                            add("reasoning")
                            add("box")
                        }
                        put("additionalProperties", false)
                    }
                }
            }
            putJsonArray("required") { add("birds") }
            put("additionalProperties", false)
        }

        val response = openAI.chatCompletion(
            ChatCompletionRequest(
                model = ModelId("gpt-4o"),
                messages = listOf(
                    ChatMessage(
                        role = ChatRole.User,
                        content = listOf(
                            TextPart(quailPrompt),
                            ImagePart(dataUrl)
                        )
                    )
                ),
                responseFormat = ChatResponseFormat.jsonSchema(
                    JsonSchema(
                        name = "quail_analysis",
                        schema = schema,
                        strict = true
                    )
                )
            )
        )

        val json = response.choices.first().message.content ?: error("No structured output returned")
        return Json.decodeFromString<QuailAnalysisResult>(json)
    }

    private val quailPrompt = """
        Analyze this image of Coturnix quail.

        For every clearly visible bird:
        - Assign a unique ID.
        - Estimate probable genetics/phenotype using most common genetics including:
          sex-linked brown, splash, cardoon, calico, blau, pied, rosetta, tibetan, fawn, fee, sparkly, pansy, roux, ginger, wild-type/pharaoh, italian, manchurian, tuxedo, white.
        - Indicate carrier/split where appropriate.
        - Estimate likely sex:
          male, female, or uncertain.
        - Use symbol:
          ♂ for male, ♀ for female, ? for uncertain.
        - Give confidence as a percentage (e.g., "85%").
        - Give brief reasoning.
        - Return a bounding box around the whole visible bird.

        Bounding box rules:
        - Coordinates must be normalized from 0.0 to 1.0.
        - x and y are the top-left corner.
        - width and height are the visible bird area.
        - Do not include feeders, waterers, or other birds in a box when avoidable.

        Important caveat:
        These are provisional visual estimates only. Feather sexing is unreliable with sparkly, roux, pansy, and ginger combinations.
    """.trimIndent()
}

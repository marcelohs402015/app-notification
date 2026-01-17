package com.appnotification.data.remote.datasource

import com.appnotification.domain.model.Event
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GeminiDataSource @Inject constructor() {
    private val gson = Gson()

    suspend fun extractEventsFromEmail(
        apiKey: String,
        emailContent: String
    ): Result<List<Event>> = withContext(Dispatchers.IO) {
        try {
            val model = GenerativeModel(
                modelName = "gemini-pro",
                apiKey = apiKey
            )

            val prompt = buildExtractionPrompt(emailContent)

            val response = model.generateContent(prompt)
            val text = response.text ?: return@withContext Result.failure(
                Exception("Empty response from Gemini")
            )

            val events = parseEventsFromResponse(text)
            Result.success(events)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun buildExtractionPrompt(emailContent: String): String {
        return """
            Analyze the following email and extract any event information (meetings, appointments, etc.).
            Return a JSON array of events with the following structure:
            [
                {
                    "title": "Event title",
                    "description": "Event description",
                    "startDateTime": "ISO 8601 format",
                    "endDateTime": "ISO 8601 format",
                    "location": "Location if mentioned",
                    "attendees": ["email1@example.com", "email2@example.com"]
                }
            ]
            
            If no events are found, return an empty array [].
            
            Email content:
            $emailContent
        """.trimIndent()
    }

    private fun parseEventsFromResponse(response: String): List<Event> {
        return try {
            val jsonString = extractJsonFromResponse(response)
            val jsonArray = JsonParser.parseString(jsonString).asJsonArray

            jsonArray.mapNotNull { jsonElement ->
                try {
                    val jsonObject = jsonElement.asJsonObject
                    val title = jsonObject.get("title")?.asString ?: return@mapNotNull null
                    val description = jsonObject.get("description")?.asString ?: ""
                    val startDateTimeStr = jsonObject.get("startDateTime")?.asString
                        ?: return@mapNotNull null
                    val endDateTimeStr = jsonObject.get("endDateTime")?.asString
                        ?: return@mapNotNull null

                    val startDateTime = parseISO8601(startDateTimeStr)
                    val endDateTime = parseISO8601(endDateTimeStr)

                    val location = jsonObject.get("location")?.asString
                    val attendeesArray = jsonObject.get("attendees")?.asJsonArray
                    val attendees = attendeesArray?.map { it.asString } ?: emptyList()

                    Event(
                        title = title,
                        description = description,
                        startDateTime = startDateTime,
                        endDateTime = endDateTime,
                        location = location,
                        attendees = attendees,
                        sourceEmailId = "",
                        isScheduled = false
                    )
                } catch (e: Exception) {
                    null
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun extractJsonFromResponse(response: String): String {
        val jsonStart = response.indexOf('[')
        val jsonEnd = response.lastIndexOf(']') + 1
        return if (jsonStart >= 0 && jsonEnd > jsonStart) {
            response.substring(jsonStart, jsonEnd)
        } else {
            "[]"
        }
    }

    private fun parseISO8601(dateString: String): Long {
        return try {
            java.time.Instant.parse(dateString).toEpochMilli()
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    }
}

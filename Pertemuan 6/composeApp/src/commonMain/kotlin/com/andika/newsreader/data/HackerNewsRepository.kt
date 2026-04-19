package com.andika.newsreader.data

import com.andika.newsreader.model.NewsArticle
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.URLBuilder
import io.ktor.http.Url
import io.ktor.http.takeFrom
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull

class HackerNewsRepository(
    private val client: HttpClient = createHttpClient(),
) {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun fetchTopStories(limit: Int): List<NewsArticle> {
        val topStoryIds = fetchTopStoryIds().take(limit)

        return coroutineScope {
            topStoryIds.map { id ->
                async { fetchStory(id) }
            }.awaitAll().filterNotNull()
        }
    }

    private suspend fun fetchTopStoryIds(): List<Long> {
        val response = client.get("$BASE_URL/topstories.json?print=pretty").bodyAsText()
        val payload = json.parseToJsonElement(response).jsonArray

        return payload.mapNotNull { (it as? JsonPrimitive)?.longOrNull }
    }

    private suspend fun fetchStory(id: Long): NewsArticle? {
        val item = fetchItem(id) ?: return null

        val type = item["type"].asString()
        if (type != "story") {
            return null
        }

        val title = item["title"].asString() ?: return null
        val articleUrl = item["url"].asString()
        val author = item["by"].asString() ?: "unknown"

        val metadata = fetchMetadata(articleUrl)

        val description = metadata.description
            ?.takeIf { it.isNotBlank() }
            ?: "Article from $author on Hacker News"

        val imageUrl = metadata.imageUrl ?: articleUrl?.let(::faviconUrl)

        return NewsArticle(
            id = id,
            title = title,
            description = description,
            imageUrl = imageUrl,
            articleUrl = articleUrl,
            author = author,
        )
    }

    private suspend fun fetchItem(id: Long): JsonObject? {
        return runCatching {
            val raw = client.get("$BASE_URL/item/$id.json?print=pretty").bodyAsText()
            json.parseToJsonElement(raw).jsonObject
        }.getOrNull()
    }

    private suspend fun fetchMetadata(url: String?): Metadata {
        if (url.isNullOrBlank()) {
            return Metadata()
        }

        return runCatching {
            val html = client.get(url).bodyAsText()
            val metaTags = parseMetaTags(html)

            val description = metaTags["og:description"]
                ?: metaTags["twitter:description"]
                ?: metaTags["description"]

            val image = metaTags["og:image"]
                ?: metaTags["twitter:image"]

            Metadata(
                description = description?.let(::cleanHtml),
                imageUrl = image?.let { resolveUrl(url, it) },
            )
        }.getOrElse { Metadata() }
    }

    private fun parseMetaTags(html: String): Map<String, String> {
        val tags = mutableMapOf<String, String>()
        val metaTagRegex = Regex("<meta\\s+[^>]*>", RegexOption.IGNORE_CASE)
        val attrRegex = Regex("([a-zA-Z_:][a-zA-Z0-9_:\\-]*)\\s*=\\s*([\"'])(.*?)\\2")

        metaTagRegex.findAll(html).forEach { tagMatch ->
            val tag = tagMatch.value
            val attrs = mutableMapOf<String, String>()

            attrRegex.findAll(tag).forEach { attrMatch ->
                val key = attrMatch.groupValues[1].lowercase()
                val value = attrMatch.groupValues[3].trim()
                attrs[key] = value
            }

            val content = attrs["content"] ?: return@forEach
            val metaName = attrs["property"] ?: attrs["name"] ?: return@forEach
            tags[metaName.lowercase()] = decodeHtmlEntities(content)
        }

        return tags
    }

    private fun resolveUrl(baseUrl: String, maybeRelative: String): String {
        return runCatching {
            URLBuilder(baseUrl).takeFrom(maybeRelative).buildString()
        }.getOrDefault(maybeRelative)
    }

    private fun faviconUrl(url: String): String {
        val host = runCatching { Url(url).host }.getOrDefault("")
        if (host.isBlank()) {
            return ""
        }
        return "https://www.google.com/s2/favicons?domain=$host&sz=128"
    }

    private fun cleanHtml(input: String): String {
        val withoutParagraphs = input
            .replace("<p>", "\n")
            .replace("</p>", "")

        val withoutTags = withoutParagraphs.replace(Regex("<[^>]+>"), "")

        return decodeHtmlEntities(withoutTags)
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    private fun decodeHtmlEntities(input: String): String {
        return input
            .replace("&#x27;", "'")
            .replace("&#39;", "'")
            .replace("&quot;", "\"")
            .replace("&amp;", "&")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&nbsp;", " ")
    }

    private fun JsonElement?.asString(): String? =
        this?.let { (it as? JsonPrimitive)?.contentOrNull }

    private data class Metadata(
        val description: String? = null,
        val imageUrl: String? = null,
    )

    companion object {
        private const val BASE_URL = "https://hacker-news.firebaseio.com/v0"

        private fun createHttpClient(): HttpClient {
            return HttpClient {
                install(HttpTimeout) {
                    requestTimeoutMillis = 15_000
                    connectTimeoutMillis = 10_000
                    socketTimeoutMillis = 15_000
                }
                install(Logging) {
                    level = LogLevel.NONE
                }
            }
        }
    }
}

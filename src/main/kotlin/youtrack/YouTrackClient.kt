package youtrack

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.serialization.kotlinx.json.json
import java.io.File
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

@Serializable
data class YouTrackIssue(
    @SerialName("idReadable") val id: String,
    val summary: String? = null,
    val description: String? = null,
    val customFields: List<CustomField> = emptyList(),
    val links: List<YouTrackIssueLink> = emptyList(),
    val resolved: Long? = null,
    val created: Long? = null,
    val updated: Long? = null,
    val tags: List<IssueTag> = emptyList(),
) {
  fun getFieldValue(name: String): String? {
    val field = customFields.find { it.name == name } ?: return null
    val value = field.value ?: return null
    if (value is JsonObject) {
      return value["presentation"]?.jsonPrimitive?.content ?: value["name"]?.jsonPrimitive?.content
    }
    return value.jsonPrimitive.content
  }

  fun getDateFieldValue(name: String): Instant? {
    val field = customFields.find { it.name == name } ?: return null
    val value = field.value ?: return null
    val longValue = value.jsonPrimitive.content.toLongOrNull() ?: return null
    return Instant.fromEpochMilliseconds(longValue)
  }
}

@Serializable
data class LinkedYouTrackIssue(
    @SerialName("idReadable") val id: String,
    val summary: String? = null,
    val description: String? = null,
    val customFields: List<CustomField> = emptyList(),
    val resolved: Long? = null,
    val created: Long? = null,
)

@Serializable
data class CustomField(
    val name: String,
    val value: JsonElement? = null,
)

@Serializable data class IssueTag(val name: String)

@Serializable
data class YouTrackIssueLink(
    val direction: String,
    val linkType: YouTrackIssueLinkType,
    val issues: List<LinkedYouTrackIssue> = emptyList(),
)

@Serializable
data class YouTrackIssueLinkType(
    val name: String,
    val sourceToTarget: String? = null,
    val targetToSource: String? = null,
)

class YouTrackClient {
  val apiKey = File("src/test/kotlin/youtrack/youtrack.key").readText().trim()
  private val json = Json {
    ignoreUnknownKeys = true
    prettyPrint = true
  }
  private val cacheDir = File("build/youtrack-cache").apply { mkdirs() }
  val client =
      HttpClient(CIO) {
        install(ContentNegotiation) { json(json) }
        install(Logging) {
          logger = Logger.DEFAULT
          level = LogLevel.INFO
        }
      }

  suspend fun get(id: String): YouTrackIssue {
    val cacheFile = cacheDir.resolve("$id.json")
    if (cacheFile.exists()) {
      return json.decodeFromString<YouTrackIssue>(cacheFile.readText())
    }

    val issue =
        client
            .get("https://kulikov.youtrack.cloud/api/issues/$id") {
              header("Authorization", "Bearer $apiKey")
              parameter(
                  "fields",
                  "idReadable,summary,description,resolved,created,updated,tags(name),customFields(name,value(name,presentation)),links(direction,linkType(name,sourceToTarget,targetToSource),issues(idReadable,summary,resolved,created,customFields(name,value(name,presentation))))")
            }
            .body<YouTrackIssue>()

    cacheFile.writeText(json.encodeToString(YouTrackIssue.serializer(), issue))
    return issue
  }
}

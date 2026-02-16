package youtrack

import java.io.File
import kotlinx.coroutines.runBlocking
import markdown.sanitize
import markdown.writeMarkdown
import org.junit.jupiter.api.Test

class YouTrackTest {
  private val client = YouTrackClient()

  @Test
  fun `download and write youtrack issues`() = runBlocking {
    val ids =
        listOf(
            "Y-234",
            "Y-27",
            "Y-422",
            "Y-311",
            "Y-309",
            "Y-307",
            "Y-304",
            "Y-317",
            "Y-316",
            "Y-243",
            "Y-306",
            "Y-207",
            "Y-247",
            "Y-308",
            "Y-314")

    val outputDir = File("build/test-output")
    ids.map { id -> convertBoard(client, id) }
        .onEach { board -> writeMarkdown(board, outputDir) }
        .let { boards ->
          outputDir.resolve("index.md").bufferedWriter().use { writer ->
            boards.forEach { board -> writer.appendLine(" - [[${board.title.sanitize()} Kanban]]") }
          }
        }
  }
}

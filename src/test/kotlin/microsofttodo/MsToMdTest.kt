package microsofttodo

import java.io.File
import markdown.sanitize
import markdown.short
import markdown.writeMarkdown
import org.junit.jupiter.api.Test

class MsToMdTest {
  @Test
  fun `convert json to md`() {
    val outputDir = File("out")

    val boards =
        transform(json = File("in/mstodo_export.json")).map { board ->
          board.copy(
              projects =
                  board.projects.map { task ->
                    task.copy(created = task.created?.takeIf { it.short() != "2019-12-24" })
                  })
        }
    val nonEmptyBoards = boards.filter { it.projects.isNotEmpty() }
    nonEmptyBoards.forEach { board -> writeMarkdown(board, outputDir) }
    outputDir.resolve("index.md").bufferedWriter().use { writer ->
      nonEmptyBoards.forEach { board -> writer.appendLine(" - [[${board.title.sanitize()}]]") }
    }
  }
}

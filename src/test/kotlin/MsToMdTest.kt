import org.junit.jupiter.api.Test
import java.io.File

class MsToMdTest {
    @Test
    fun `convert json to md`() {
        val outputDir = File("out")

        val boards = transform(json = File("in/mstodo_export.json")).map { board ->
            board.copy(tasks = board.tasks.map { task ->
                task.copy(created = task.created?.takeIf { it.short() != "2019-12-24" })
            })
        }
        val nonEmptyBoards = boards.filter { it.tasks.isNotEmpty() }
        nonEmptyBoards.forEach { board ->
            writeMarkdown(board, outputDir)
        }
        outputDir.resolve("index.md").bufferedWriter().use { writer ->
            nonEmptyBoards.forEach { board ->
                writer.appendLine(" - [[${board.title.sanitize()}]]")
            }
        }
    }
}
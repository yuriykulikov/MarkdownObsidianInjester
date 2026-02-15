import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.io.BufferedWriter
import java.io.File

fun Instant.short(): String {
    val localDateTime = this.toLocalDateTime(TimeZone.UTC)
    return String.format("%04d-%02d-%02d", localDateTime.year, localDateTime.monthNumber, localDateTime.dayOfMonth)
}

/**
 * Remove illegal chars from file names
 */
fun String.sanitize(): String {
    return this.replace(Regex("[\\\\/:*?\"<>|#^\\[\\]]"), "_").trim()
}

/**
 * Writes [Board] tasks to a Kanban board Markdown.
 * All tasks with subtasks or descriptions are written as files.
 * Oneliners are written as tasks directly in the board.
 */
fun writeMarkdown(board: Board, outputDir: File) {
    val boardDir = File(outputDir, board.title.sanitize())
    boardDir.mkdirs()
    val boardFile = File(boardDir, "${board.title.sanitize()}.md")

    val (doneTasks, todoTasks) = board.tasks
        .partition { it.completed }

    boardFile.bufferedWriter().use { writer ->

        writer.appendLine("---")
        writer.appendLine("kanban-plugin: board")
        writer.appendLine("---")


        writer.appendLine("## TODO")
        todoTasks.forEach { task ->
            writer.appendTaskLine(task)
        }

        writer.appendLine("## Done")
        doneTasks.sortedBy { it.completedTime }
            .forEach { task ->
                writer.appendTaskLine(task)
            }

        writer.appendLine(
            """
            %% kanban:settings
            ```
            {"kanban-plugin":"board","list-collapse":[null,false]}
            ```
            %%
        """.trimIndent()
        )
    }

    board.tasks
        .filter { it.hasBody }
        .forEach { task ->
            writeProjectFile(task, boardDir)
        }
}

private fun writeProjectFile(task: Project, boardDir: File) {
    val taskFileName = "${task.title.sanitize()}.md"
    val dir = if (task.completed) boardDir.resolve("Archive") else boardDir
    dir.mkdirs()
    val taskFile = File(dir, taskFileName)

    taskFile.bufferedWriter().use { taskWriter ->
        if (task.actions.isNotEmpty() || (task.due != null && !task.completed)) {

            taskWriter.appendLine("## Actions")
            taskWriter.appendLine()
            if (!task.completed) {
                task.due?.let { taskWriter.appendLine("- [ ] Finish until \uD83D\uDCC5 ${it.short()}") }
            }
            task.actions
                .sortedBy { it.completed }
                .forEach { action ->
                    taskWriter.appendAction(action)
                }
        }

        task.description?.let { desc ->
            taskWriter.appendLine("## Description")
            taskWriter.appendLine()
            desc.lines().forEach { line -> taskWriter.appendLine(line) }
        }

        taskWriter.appendLine()

        if (task.created != null || task.due != null || task.completedTime != null) {
            taskWriter.appendLine("---")
            // TODO if completed tag or move to archive?
            task.created?.let {
                taskWriter.appendLine("Created: ${task.created.short()}")
            }
            task.due?.let { taskWriter.appendLine("Due: ${it.short()}") }
            task.completedTime?.let { taskWriter.appendLine("Completed At: ${it.short()}\n") }
        }
        if (task.completed) {
            taskWriter.appendLine("#archive")
        }
    }
}

private fun BufferedWriter.appendTaskLine(task: Project) {
    val flag = if (task.completed) "x" else " "
    val createdTimeStr = task.created?.let { " ➕ ${task.created.short()}" }.orEmpty()
    val dueStr =
        task.due?.let { " \uD83D\uDCC5 " + it.short() }
            .orEmpty()
    val completedString =
        task.completedTime?.let { " ✅ " + it.short() }
            .orEmpty()
    if (task.hasBody) {
        // created and due will be in the project note
        appendLine("- [$flag] [[${task.title.sanitize()}]]$completedString")
    } else {
        appendLine("- [$flag] ${task.title}$createdTimeStr$dueStr$completedString")
    }
}

/**
 * ➕ 2026-02-13 📅 2026-02-14 ✅ 2026-02-13
 */
private fun BufferedWriter.appendAction(action: Action) {
    val completion = if (action.completed) "x" else " "
    val createdStr = " ➕ ${action.created.short()}"
    val duestr =
        action.due?.let { " \uD83D\uDCC5 " + it.short() }.orEmpty()
    appendLine("- [$completion] ${action.title}$createdStr$duestr")
}
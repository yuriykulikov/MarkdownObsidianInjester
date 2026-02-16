package microsofttodo

import gtd.Action
import gtd.Board
import gtd.Project
import java.io.File
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json

fun transform(json: File): List<Board> {
  return Json.decodeFromString<List<TaskList>>(json.readText()).map { list -> mapList(list) }
}

private fun mapList(list: TaskList): Board {
  return Board(
      title = list.displayName,
      projects = list.tasks.map { mapTask(it) },
  )
}

private fun mapTask(task: Task): Project {
  check(task.categories.isEmpty())

  val actions =
      task.checklistItems
          ?.map {
            Action(
                title = it.displayName,
                created = Instant.parse(it.createdDatetime),
                completed = it.isChecked,
            )
          }
          .orEmpty()

  val syntheticActions =
      if (task.hasAttachments)
          listOf(
              Action(
                  title = "Download attachments",
                  created = Instant.parse(task.createdDateTime),
                  completed = false,
              ))
      else emptyList()

  return Project(
      title = task.title,
      created = task.createdDateTime.let { Instant.parse(it) },
      isCompleted = task.status == "completed",
      completed = task.completedDateTime?.dateTime?.let { Instant.parse(it) },
      description = task.body.content.takeIf { it.isNotBlank() },
      due =
          task.dueDateTime?.let { Instant.parse(it.dateTime) }
              ?: task.reminderDateTime?.let { Instant.parse(it.dateTime) },
      actions = actions + syntheticActions,
  )
}

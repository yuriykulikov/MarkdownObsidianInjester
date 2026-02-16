package microsofttodo

import java.io.File
import kotlinx.datetime.Instant as KxInstant
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/** Obsidian microsofttodo.Board */
@Serializable
data class Board(
    val title: String,
    val tasks: List<Project>,
)

@Serializable
data class Project(
    val title: String,
    val created: KxInstant? = null,
    val completed: Boolean,
    val description: String?,
    val actions: List<Action> = emptyList(),
    val due: KxInstant? = null,
    val completedTime: KxInstant?,
) {
  val hasBody = description != null || actions.isNotEmpty()
}

@Serializable
data class Action(
    val title: String,
    val created: KxInstant,
    val completed: Boolean,
    val due: KxInstant? = null,
)

fun transform(json: File): List<Board> {
  return Json.decodeFromString<List<TaskList>>(json.readText()).map { list -> mapList(list) }
}

private fun mapList(list: TaskList): Board {
  return Board(
      title = list.displayName,
      tasks = list.tasks.map { mapTask(it) },
  )
}

private fun mapTask(task: Task): Project {
  check(task.categories.isEmpty())

  val actions =
      task.checklistItems
          ?.map {
            Action(
                title = it.displayName,
                created = KxInstant.parse(it.createdDatetime),
                completed = it.isChecked,
            )
          }
          .orEmpty()

  val syntheticActions =
      if (task.hasAttachments)
          listOf(
              Action(
                  title = "Download attachments",
                  created = KxInstant.parse(task.createdDateTime),
                  completed = false,
              ))
      else emptyList()

  return Project(
      title = task.title,
      created = task.createdDateTime.let { KxInstant.parse(it) },
      completed = task.status == "completed",
      completedTime = task.completedDateTime?.dateTime?.let { KxInstant.parse(it) },
      description = task.body.content.takeIf { it.isNotBlank() },
      due =
          task.dueDateTime?.let { KxInstant.parse(it.dateTime) }
              ?: task.reminderDateTime?.let { KxInstant.parse(it.dateTime) },
      actions = actions + syntheticActions,
  )
}

package youtrack

import gtd.Action
import gtd.Board
import gtd.Project
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

/** Convert an Epic or Mission issue to a board with [Project]. */
suspend fun convertBoard(client: YouTrackClient, id: String): Board {
  val parent = client.get(id)
  return Board(
      title = (parent.summary ?: parent.id),
      projects =
          parent.links.flatMap { link ->
            link.issues
                .map { linkedIssue -> client.get(linkedIssue.id) }
                .map { fullIssue -> issueToProject(fullIssue) }
          })
}

fun issueToProject(fullIssue: YouTrackIssue): Project =
    Project(
        title = fullIssue.summary ?: fullIssue.id,
        isCompleted = fullIssue.resolved != null,
        description = fullIssue.description,
        actions =
            fullIssue.links
                .filter { it.linkType.name == "Subtask" && it.direction == "OUTWARD" }
                .flatMap { it.issues }
                .map { subtask ->
                  Action(
                      title = subtask.summary ?: subtask.id,
                      created =
                          subtask.created?.let { Instant.fromEpochMilliseconds(it) }
                              ?: Clock.System.now(),
                      completed = subtask.resolved != null)
                },
        created = fullIssue.created?.let { Instant.fromEpochMilliseconds(it) },
        completed = fullIssue.resolved?.let { Instant.fromEpochMilliseconds(it) },
        due = fullIssue.getDateFieldValue("Due Date"),
        stage = fullIssue.getFieldValue("Stage"),
        updated = fullIssue.updated?.let { Instant.fromEpochMilliseconds(it) },
        tags = fullIssue.tags.map { it.name },
        origin = "https://kulikov.youtrack.cloud/issue/${fullIssue.id}",
    )

package gtd

import kotlinx.datetime.Instant as KxInstant
import kotlinx.serialization.Serializable

/** Obsidian gtd.Board */
@Serializable
data class Board(
    val title: String,
    val projects: List<Project>,
)

@Serializable
data class Project(
    val title: String,
    val isCompleted: Boolean,
    val description: String?,
    val created: KxInstant? = null,
    val updated: KxInstant? = null,
    val completed: KxInstant? = null,
    val actions: List<Action> = emptyList(),
    val due: KxInstant? = null,
    val stage: String? = null,
    val tags: List<String> = emptyList(),
    val origin: String? = null,
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

import kotlinx.serialization.Serializable

/**
 * Classes which https://github.com/daylamtayari/Microsoft-To-Do-Export creates
 */
@Serializable
data class TaskList(
    val id: String,
    val displayName: String,
    val isOwner: Boolean,
    val isShared: Boolean,
    val wellKnownListName: String,
    val tasks: List<Task> = emptyList(),
)

@Serializable
data class Task(
    val id: String,
    val status: String,
    val title: String,
    val importance: String,
    val isReminderOn: Boolean,
    val createdDateTime: String,
    val lastModifiedDateTime: String,
    val hasAttachments: Boolean,
    val categories: List<String>,
    val body: ItemBody,
    val completedDateTime: DateTimeTimeZone? = null,
    val dueDateTime: DateTimeTimeZone? = null,
    val reminderDateTime: DateTimeTimeZone? = null,
    val startDateTime: DateTimeTimeZone? = null,
    val recurrence: Recurrence? = null,
    val checklistItems: List<ChecklistItem>? = null,
)

@Serializable
data class ItemBody(
    val content: String,
    val contentType: String,
)

@Serializable
data class DateTimeTimeZone(
    val dateTime: String,
    val timeZone: String,
)

@Serializable
data class ChecklistItem(
    val id: String,
    val displayName: String,
    val createdDatetime: String,
    val isChecked: Boolean,
)

@Serializable
data class Recurrence(
    val pattern: RecurrencePattern? = null,
    val range: RecurrenceRange? = null,
)

@Serializable
data class RecurrencePattern(
    val type: String? = null,
    val interval: Int? = null,
    val month: Int? = null,
    val dayOfMonth: Int? = null,
    val daysOfWeek: List<String>? = null,
    val firstDayOfWeek: String? = null,
    val index: String? = null,
)

@Serializable
data class RecurrenceRange(
    val type: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val recurrenceTimeZone: String? = null,
    val numberOfOccurrences: Int? = null,
)
# MarkdownObsidianInjester

A tool to ingest Microsoft To-Do tasks into Obsidian following a GTD-inspired hierarchy: **Board - Project - Action**.

## Howto

1. **Export from Microsoft To-Do**:
   Use [Microsoft-To-Do-Export](https://github.com/daylamtayari/Microsoft-To-Do-Export).
2. Place your exported JSON file at `in/mstodo_export.json`.
3. Run the conversion. Currently, this can be triggered by running the test in `src/test/kotlin/MsToMdTest.kt`.
4. The output will be generated in the `out/` directory.

## GTD Structure

The tool organizes tasks into a three-level hierarchy

### 1. Board (Task List)

Each Microsoft To-Do list becomes a **Board**.

- **Directory**: `out/Board Name/`
- **File**: `Board Name.md` (Obsidian Kanban board).
- **Workflow**: Simple tasks are listed directly on the board, while complex tasks are linked as separate **Projects**.

### 2. Project (Task with details)

Created for any task that contains a description, subtasks, or attachments.

- **File**: `out/Board Name/Project Title.md`
- **Archive**: Completed projects are moved to `out/Board Name/Archive/`.
- **Content**: Contains the project description and a list of **Actions**.

### 3. Action (Subtask)

Individual steps required to complete a Project.

- **Format**: Checklist items within a Project file.
- **Metadata**: Includes creation and due dates.

## Obsidian Plugins

- **Kanban Boards**: [Obsidian Kanban](https://github.com/mgmeyers/obsidian-kanban)
- **Tasks**: [Obsidian Tasks](https://github.com/obsidian-tasks-group/obsidian-tasks)

## Credits

[Microsoft-To-Do-Export](https://github.com/daylamtayari/Microsoft-To-Do-Export) for MS data export.

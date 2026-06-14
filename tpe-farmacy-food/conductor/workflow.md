# Workflow

## Development Process
1. Each task is implemented in a dedicated branch
2. All code must have >80% test coverage
3. Git commits are managed manually by the developer
4. Task summaries stored in `conductor/notes/`

## Phase Completion Verification and Checkpointing Protocol
At the end of each phase in a track's `plan.md`, a meta-task must be added:
`- [ ] Task: Conductor - User Manual Verification '<Phase Name>' (Protocol in workflow.md)`

This task requires a human-in-the-loop checkpoint before proceeding to the next phase.

## Task Summaries
- After completing each task, create a markdown file in `conductor/notes/`
- File naming: `YYYYMMDD-HHMM-task-description.md`
- Content: Brief summary of what was done, decisions made, and any blockers

## Git Staging and Cleanup Protocol
When committing changes that involve moving or deleting files (e.g., archiving tracks), always stage deletions explicitly using `git add -A` on the affected paths. This ensures that:
- Moved files (e.g., track folder to `conductor/archive/`) include the deletion of the original location in the same commit
- Modified conductor docs (`product.md`, `tech-stack.md`, `tracks.md`) are staged together
- No orphaned unstaged changes remain after a commit

**Example for archiving a track:**
```bash
git add -A <project>/conductor/tracks/<track_id>/ <project>/conductor/archive/<track_id>/ <project>/conductor/tracks.md <project>/conductor/product.md <project>/conductor/tech-stack.md <project>/conductor/product-guidelines.md && git commit -m "chore(conductor): Archive track '<description>'"
```

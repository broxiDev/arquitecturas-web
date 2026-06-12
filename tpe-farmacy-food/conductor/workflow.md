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

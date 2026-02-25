# Use Case UC1: Create Task

## Scope
**Personal Task Management System**

## Level
**User Goal**

## Primary Actor
**User**

---

## Stakeholders and Interests

**User**  
Wants to quickly create a task with the correct details so the task can be tracked and managed later.

**System**  
Wants to store tasks reliably and log the creation as an update.

---

## Preconditions

- The system is available.
- The user is interacting with the system.

---

## Success Guarantee

- A new **Task** exists in the system and has been saved.
- A new **Update** is created and stored with a timestamp and linked to the created **Task**.

---

## Main Success Scenario

1. User selects **Create Task**.
2. **View** displays the task creation form.
3. User enters a task title.
4. User optionally enters a description.
5. User optionally selects a priority level.
6. User optionally selects a due date.
7. User optionally selects a project from the list of existing projects.
8. User optionally adds one or more tags.
9. User confirms task creation.
10. System validates the input.
11. System creates a new **Task**.
12. System creates a new **Update** (timestamped with description **"Task Created"**) associated with the **Task**.
13. **View** displays a confirmation.

---

## Extensions

### 10a. Title is Empty

1. System detects an empty title.
2. System displays an error message: **"Title is required."**
3. User must enter a title before continuing.

---

## Special Requirements

- Task creation should complete within **2 seconds**.
- Updates must be **immutable and timestamped**.

---

## Technology and Data Variations List

**3a.** Title entered via keyboard input.

**4a.** Description entered via multiline input.

**5a.** Priority selected using keywords:

- `low`
- `medium`
- `high`

or numeric codes:

- `1`
- `2`
- `3`

**6a.** Due date entered in format:

`DD/MM/YYYY`

**7a.** Project selected by project name or project ID.

**8a.** Tags entered as a comma-separated list.

**12a.** Update timestamp uses system clock.

**13a.** Confirmation displayed in the **View** (console output).

---

## Frequency of Occurrence

**High**

---

## Open Issues

- What should be the default priority level if the user does not specify one?

- Should new tags be automatically created if the user enters tags that do not already exist?

- Should a task always have status **open** when it is created, or can the user specify the initial status?

-- Task Management System Database Schema

-- Projects table (defined first for foreign key reference)
CREATE TABLE IF NOT EXISTS projects (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT
);

-- Tasks table
CREATE TABLE IF NOT EXISTS tasks (
    id VARCHAR(36) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    due_date DATE,
    priority VARCHAR(20),
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    recurrence_type VARCHAR(20),
    recurrence_interval INTEGER,
    project_id VARCHAR(36),
    parent_task_id VARCHAR(36),
    FOREIGN KEY (parent_task_id) REFERENCES tasks(id) ON DELETE CASCADE,
    FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE SET NULL
);

-- Tags table
CREATE TABLE IF NOT EXISTS tags (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

-- Task tags junction table (many-to-many)
CREATE TABLE IF NOT EXISTS task_tags (
    task_id VARCHAR(36) NOT NULL,
    tag_id VARCHAR(36) NOT NULL,
    PRIMARY KEY (task_id, tag_id),
    FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE
);


-- Collaborators table
CREATE TABLE IF NOT EXISTS collaborators (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(20) NOT NULL
);

-- Project collaborators junction table (many-to-many)
CREATE TABLE IF NOT EXISTS project_collaborators (
    project_id VARCHAR(36) NOT NULL,
    collaborator_id VARCHAR(36) NOT NULL,
    PRIMARY KEY (project_id, collaborator_id),
    FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
    FOREIGN KEY (collaborator_id) REFERENCES collaborators(id) ON DELETE CASCADE
);

-- Assignments table (represents task-collaborator associations)
CREATE TABLE IF NOT EXISTS assignments (
    id VARCHAR(36) PRIMARY KEY,
    task_id VARCHAR(36) NOT NULL,
    collaborator_id VARCHAR(36) NOT NULL,
    UNIQUE (task_id, collaborator_id),
    FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE,
    FOREIGN KEY (collaborator_id) REFERENCES collaborators(id) ON DELETE CASCADE
);

-- Activities table (for ActivityLog)
CREATE TABLE IF NOT EXISTS activities (
    id VARCHAR(36) PRIMARY KEY,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    description TEXT NOT NULL
);
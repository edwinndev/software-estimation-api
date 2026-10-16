ALTER TABLE in_scope.user_stories
    ADD COLUMN acceptance_criteria TEXT NOT NULL DEFAULT '';

ALTER TABLE in_scope.user_stories
    DROP CONSTRAINT fk_user_stories_project,
    ADD CONSTRAINT fk_user_stories_project
        FOREIGN KEY (project_id) REFERENCES in_scope.projects (id) ON DELETE RESTRICT;

ALTER TABLE in_scope.tasks
    DROP CONSTRAINT fk_tasks_story,
    ADD CONSTRAINT fk_tasks_story
        FOREIGN KEY (story_id) REFERENCES in_scope.user_stories (id) ON DELETE RESTRICT;

INSERT INTO in_scope.permissions (code, label, group_code, group_label) VALUES
    ('backlog:read', 'Ver historias y tareas', 'backlog', 'Historias y tareas'),
    ('backlog:write', 'Crear y actualizar historias y tareas', 'backlog', 'Historias y tareas'),
    ('backlog:delete', 'Eliminar historias y tareas', 'backlog', 'Historias y tareas');

INSERT INTO in_scope.role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM in_scope.roles r
CROSS JOIN in_scope.permissions p
WHERE r.code = 'admin'
  AND p.code IN ('backlog:read', 'backlog:write', 'backlog:delete');

INSERT INTO in_scope.role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM in_scope.roles r
JOIN in_scope.permissions p ON p.code IN ('backlog:read', 'backlog:write')
WHERE r.code = 'estimator';

INSERT INTO in_scope.role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM in_scope.roles r
JOIN in_scope.permissions p ON p.code = 'backlog:read'
WHERE r.code = 'viewer';
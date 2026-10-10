CREATE SCHEMA IF NOT EXISTS in_scope;

-- tipos enumerados
CREATE TYPE in_scope.technical_role AS ENUM (
    'FRONTEND', 'BACKEND', 'FULLSTACK', 'QA', 'DEVOPS',
    'UI_UX_DESIGNER', 'PRODUCT_MANAGER', 'TECH_LEAD', 'FUNCTIONAL_ANALYST', 'OTHER'
);
CREATE TYPE in_scope.experience_level AS ENUM ('JUNIOR', 'MID', 'SENIOR', 'LEAD');
CREATE TYPE in_scope.project_status AS ENUM (
    'DRAFT', 'UNDER_REVIEW', 'ESTIMATED', 'APPROVED',
    'REJECTED', 'IN_PROGRESS', 'COMPLETED'
);
CREATE TYPE in_scope.story_priority AS ENUM ('LOW', 'MEDIUM', 'HIGH');
CREATE TYPE in_scope.story_status AS ENUM ('DRAFT', 'READY', 'IN_PROGRESS', 'DONE');
CREATE TYPE in_scope.task_status AS ENUM ('TODO', 'IN_PROGRESS', 'DONE');
CREATE TYPE in_scope.sprint_unit AS ENUM ('DAYS', 'WEEKS');
CREATE TYPE in_scope.risk_level AS ENUM ('LOW', 'MEDIUM', 'HIGH');

-- seguridad
CREATE TABLE in_scope.permissions
(
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code        VARCHAR(40)  NOT NULL UNIQUE,
    label       VARCHAR(150) NOT NULL,
    group_code  VARCHAR(40)  NOT NULL,
    group_label VARCHAR(100) NOT NULL
);

CREATE TABLE in_scope.roles
(
    id          UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    code        VARCHAR(40)  NOT NULL UNIQUE,
    name        VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255) NOT NULL DEFAULT '',
    is_system   BOOLEAN      NOT NULL DEFAULT FALSE,
    is_active   BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE in_scope.role_permissions
(
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    role_id       UUID NOT NULL,
    permission_id UUID NOT NULL,
    UNIQUE (role_id, permission_id),
    CONSTRAINT fk_role_permissions_role FOREIGN KEY (role_id) REFERENCES in_scope.roles (id),
    CONSTRAINT fk_role_permissions_permission FOREIGN KEY (permission_id) REFERENCES in_scope.permissions (id)
);

CREATE TABLE in_scope.users
(
    id            UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    role_id       UUID         NOT NULL,
    first_name    VARCHAR(100) NOT NULL,
    last_name     VARCHAR(100) NOT NULL,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash TEXT         NOT NULL,
    is_active     BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at    TIMESTAMP  NOT NULL DEFAULT now(),
    updated_at    TIMESTAMP  NULL,
    deleted_at    TIMESTAMP  NULL,
    created_by    UUID         NULL,
    updated_by    UUID         NULL,
    deleted_by    UUID         NULL,
    CONSTRAINT fk_users_role FOREIGN KEY (role_id) REFERENCES in_scope.roles (id)
);

CREATE TABLE in_scope.password_reset_tokens
(
    id          UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    email       VARCHAR(255) NOT NULL,
    code_hash   TEXT         NOT NULL,
    expires_at  TIMESTAMP  NOT NULL,
    verified_at TIMESTAMP  NULL,
    consumed_at TIMESTAMP  NULL,
    created_at  TIMESTAMP  NOT NULL DEFAULT now()
);

CREATE INDEX password_reset_tokens_email_idx ON in_scope.password_reset_tokens (email, expires_at DESC);

-- perfiles técnicos (hourly_rate = CER, costo estándar por hora)
CREATE TABLE in_scope.technical_profiles
(
    id               UUID PRIMARY KEY                     DEFAULT gen_random_uuid(),
    name             VARCHAR(100)                NOT NULL,
    email            VARCHAR(255)                NOT NULL UNIQUE,
    role             in_scope.technical_role   NOT NULL,
    experience_level in_scope.experience_level NOT NULL,
    hourly_rate      NUMERIC(12, 2)              NOT NULL,
    currency         CHAR(3)                     NOT NULL DEFAULT 'PEN',
    is_active        BOOLEAN                     NOT NULL DEFAULT TRUE,
    created_at       TIMESTAMP                 NOT NULL DEFAULT now(),
    updated_at       TIMESTAMP                 NULL,
    deleted_at       TIMESTAMP                 NULL,
    created_by       UUID                        NULL,
    updated_by       UUID                        NULL,
    deleted_by       UUID                        NULL
);

-- proyectos
CREATE TABLE in_scope.projects
(
    id          UUID PRIMARY KEY                   DEFAULT gen_random_uuid(),
    name        VARCHAR(150)              NOT NULL,
    description VARCHAR(500)              NOT NULL DEFAULT '',
    type        VARCHAR(40)               NOT NULL,
    start_date  DATE                      NOT NULL,
    end_date    DATE                      NOT NULL,
    owner_id    UUID                      NOT NULL,
    status      in_scope.project_status NOT NULL DEFAULT 'DRAFT',
    created_at  TIMESTAMP               NOT NULL DEFAULT now(),
    updated_at  TIMESTAMP               NULL,
    deleted_at  TIMESTAMP               NULL,
    created_by  UUID                      NULL,
    updated_by  UUID                      NULL,
    deleted_by  UUID                      NULL,
    CONSTRAINT fk_projects_owner FOREIGN KEY (owner_id) REFERENCES in_scope.users (id)
);

CREATE TABLE in_scope.project_status_history
(
    id              UUID PRIMARY KEY                   DEFAULT gen_random_uuid(),
    project_id      UUID                      NOT NULL,
    previous_status in_scope.project_status NULL,
    new_status      in_scope.project_status NOT NULL,
    changed_by      UUID                      NOT NULL,
    changed_at      TIMESTAMP               NOT NULL DEFAULT now(),
    CONSTRAINT fk_project_status_history_project FOREIGN KEY (project_id) REFERENCES in_scope.projects (id),
    CONSTRAINT fk_project_status_history_user FOREIGN KEY (changed_by) REFERENCES in_scope.users (id)
);

CREATE INDEX project_status_history_project_idx ON in_scope.project_status_history (project_id, changed_at DESC);

-- backlog (historias de usuario y tareas)
CREATE TABLE in_scope.user_stories
(
    id           UUID PRIMARY KEY                   DEFAULT gen_random_uuid(),
    project_id   UUID                      NOT NULL,
    code         VARCHAR(10)               NOT NULL,
    title        VARCHAR(150)              NOT NULL,
    description  TEXT                      NOT NULL,
    priority     in_scope.story_priority NOT NULL DEFAULT 'MEDIUM',
    status       in_scope.story_status   NOT NULL DEFAULT 'DRAFT',
    story_points SMALLINT                  NULL,
    position     INTEGER                   NOT NULL,
    created_at   TIMESTAMP               NOT NULL DEFAULT now(),
    updated_at   TIMESTAMP               NULL,
    deleted_at   TIMESTAMP               NULL,
    created_by   UUID                      NULL,
    updated_by   UUID                      NULL,
    deleted_by   UUID                      NULL,
    UNIQUE (project_id, code),
    CONSTRAINT fk_user_stories_project FOREIGN KEY (project_id) REFERENCES in_scope.projects (id) ON DELETE CASCADE
);

CREATE TABLE in_scope.tasks
(
    id             UUID PRIMARY KEY                 DEFAULT gen_random_uuid(),
    story_id       UUID                    NOT NULL,
    title          VARCHAR(150)            NOT NULL,
    description    TEXT                    NOT NULL,
    estimate_hours INTEGER                 NOT NULL,
    status         in_scope.task_status  NOT NULL DEFAULT 'TODO',
    position       INTEGER                 NOT NULL,
    created_at     TIMESTAMP             NOT NULL DEFAULT now(),
    updated_at     TIMESTAMP             NULL,
    deleted_at     TIMESTAMP             NULL,
    created_by     UUID                    NULL,
    updated_by     UUID                    NULL,
    deleted_by     UUID                    NULL,
    CONSTRAINT fk_tasks_story FOREIGN KEY (story_id) REFERENCES in_scope.user_stories (id) ON DELETE CASCADE
);

CREATE INDEX tasks_story_position_idx ON in_scope.tasks (story_id, position);

CREATE TABLE in_scope.task_assignments
(
    task_id              UUID NOT NULL,
    technical_profile_id UUID NOT NULL,
    PRIMARY KEY (task_id, technical_profile_id),
    CONSTRAINT fk_task_assignments_task FOREIGN KEY (task_id) REFERENCES in_scope.tasks (id) ON DELETE CASCADE,
    CONSTRAINT fk_task_assignments_profile FOREIGN KEY (technical_profile_id) REFERENCES in_scope.technical_profiles (id)
);

CREATE TABLE in_scope.task_hour_adjustments
(
    task_id              UUID          NOT NULL,
    technical_profile_id UUID          NOT NULL,
    hours                NUMERIC(8, 2) NOT NULL,
    reason               VARCHAR(500)  NOT NULL DEFAULT '',
    updated_at           TIMESTAMP   NOT NULL DEFAULT now(),
    updated_by           UUID          NULL,
    PRIMARY KEY (task_id, technical_profile_id),
    CONSTRAINT fk_task_hour_adjustments_assignment FOREIGN KEY (task_id, technical_profile_id)
        REFERENCES in_scope.task_assignments (task_id, technical_profile_id) ON DELETE CASCADE
);

-- configuración de la estimación (una fila por proyecto)
CREATE TABLE in_scope.sprint_configs
(
    project_id UUID PRIMARY KEY,
    velocity   INTEGER                 NOT NULL DEFAULT 5,
    duration   INTEGER                 NOT NULL DEFAULT 2,
    unit       in_scope.sprint_unit  NOT NULL DEFAULT 'DAYS',
    updated_at TIMESTAMP             NOT NULL DEFAULT now(),
    updated_by UUID                    NULL,
    CONSTRAINT fk_sprint_configs_project FOREIGN KEY (project_id) REFERENCES in_scope.projects (id) ON DELETE CASCADE
);

CREATE TABLE in_scope.risk_configs
(
    project_id         UUID PRIMARY KEY,
    level              in_scope.risk_level NOT NULL DEFAULT 'MEDIUM',
    contingency_margin NUMERIC(5, 2)         NOT NULL DEFAULT 15,
    updated_at         TIMESTAMP           NOT NULL DEFAULT now(),
    updated_by         UUID                  NULL,
    CONSTRAINT fk_risk_configs_project FOREIGN KEY (project_id) REFERENCES in_scope.projects (id) ON DELETE CASCADE
);

-- reportes (copias inmutables de una estimación)
CREATE TABLE in_scope.report_snapshots
(
    id                            UUID PRIMARY KEY                   DEFAULT gen_random_uuid(),
    project_id                    UUID                      NOT NULL,
    project_name                  VARCHAR(150)              NOT NULL,
    project_status                in_scope.project_status NOT NULL,
    project_type                  VARCHAR(40)               NOT NULL,
    project_owner                 VARCHAR(201)              NOT NULL,
    base_effort_points            INTEGER                   NOT NULL,
    total_sprints                 INTEGER                   NOT NULL,
    total_effort_hours            NUMERIC(12, 2)            NOT NULL,
    stories_total                 INTEGER                   NOT NULL,
    stories_with_points           INTEGER                   NOT NULL,
    tasks_total                   INTEGER                   NOT NULL,
    tasks_with_hours              INTEGER                   NOT NULL,
    base_time                     NUMERIC(10, 2)            NOT NULL,
    time_unit                     in_scope.sprint_unit    NOT NULL,
    base_cost                     NUMERIC(14, 2)            NOT NULL,
    risk_level                    in_scope.risk_level     NOT NULL,
    contingency_margin_percentage NUMERIC(5, 2)             NOT NULL,
    contingency_time              NUMERIC(10, 2)            NOT NULL,
    contingency_cost              NUMERIC(14, 2)            NOT NULL,
    total_time                    NUMERIC(10, 2)            NOT NULL,
    total_cost                    NUMERIC(14, 2)            NOT NULL,
    created_at                    TIMESTAMP               NOT NULL DEFAULT now(),
    created_by                    UUID                      NULL,
    CONSTRAINT fk_report_snapshots_project FOREIGN KEY (project_id) REFERENCES in_scope.projects (id) ON DELETE CASCADE
);

CREATE INDEX report_snapshots_project_idx ON in_scope.report_snapshots (project_id, created_at DESC);

-- archivos PDF de reportes guardados en Bunny (solo se registra la ruta, no el binario)
CREATE TABLE in_scope.report_files
(
    id           UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    report_id    UUID         NULL,
    type         VARCHAR(30)  NOT NULL,
    file_name    VARCHAR(255) NOT NULL,
    content_type VARCHAR(100) NOT NULL,
    size_bytes   BIGINT       NOT NULL,
    storage_path VARCHAR(500) NOT NULL UNIQUE,
    created_at   TIMESTAMP  NOT NULL DEFAULT now(),
    created_by   UUID         NULL,
    CONSTRAINT fk_report_files_report FOREIGN KEY (report_id) REFERENCES in_scope.report_snapshots (id) ON DELETE CASCADE
);

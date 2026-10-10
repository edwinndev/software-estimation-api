-- permisos
INSERT INTO in_scope.permissions (code, label, group_code, group_label) VALUES
    ('user:read', 'Ver usuarios', 'security', 'Seguridad'),
    ('user:write', 'Crear y editar usuarios', 'security', 'Seguridad'),
    ('user:delete', 'Eliminar usuarios', 'security', 'Seguridad'),
    ('profile:read', 'Ver perfiles técnicos', 'profiles', 'Perfiles técnicos'),
    ('profile:write', 'Crear y editar perfiles técnicos', 'profiles', 'Perfiles técnicos'),
    ('project:read', 'Ver proyectos', 'projects', 'Proyectos'),
    ('project:write', 'Crear y editar proyectos', 'projects', 'Proyectos'),
    ('estimation:read', 'Ver estimación', 'estimation', 'Estimación'),
    ('estimation:write', 'Editar estimación', 'estimation', 'Estimación'),
    ('cost:read', 'Ver costos', 'costs', 'Costos'),
    ('cost:write', 'Editar costos', 'costs', 'Costos'),
    ('risk:read', 'Ver riesgos', 'risks', 'Riesgos'),
    ('risk:write', 'Editar riesgos', 'risks', 'Riesgos'),
    ('report:read', 'Ver reportes', 'reports', 'Reportes e historial'),
    ('history:read', 'Ver historial', 'reports', 'Reportes e historial');

-- roles y sus permisos
INSERT INTO in_scope.roles (code, name, description, is_system) VALUES
    ('admin', 'ADMINISTRADOR', 'Acceso completo a la administración del sistema.', TRUE),
    ('estimator', 'ESTIMADOR', 'Gestión de estimaciones, recursos y costos asociados.', FALSE),
    ('viewer', 'CONSULTOR', 'Consulta de información y visualización de reportes.', FALSE);

INSERT INTO in_scope.role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM in_scope.roles r
CROSS JOIN in_scope.permissions p
WHERE r.code = 'admin';

INSERT INTO in_scope.role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM in_scope.roles r
JOIN in_scope.permissions p ON p.code IN (
    'profile:read', 'profile:write',
    'project:read', 'project:write',
    'estimation:read', 'estimation:write',
    'cost:read', 'cost:write',
    'risk:read', 'risk:write',
    'report:read', 'history:read'
)
WHERE r.code = 'estimator';

INSERT INTO in_scope.role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM in_scope.roles r
JOIN in_scope.permissions p ON p.code IN (
    'project:read', 'estimation:read', 'cost:read',
    'risk:read', 'report:read', 'history:read'
)
WHERE r.code = 'viewer';

-- administrador (contraseña: admin123)
INSERT INTO in_scope.users (role_id, first_name, last_name, email, password_hash, is_active)
SELECT r.id, 'Admin', 'Sistema', 'jcvargas.dev@gmail.com', '$2a$12$CN/iYj/WH8IbSCvyMfew4uR7BNpHkomogOAJ/y00tPiGM4CfNKADG', TRUE
FROM in_scope.roles r
WHERE r.code = 'admin';

UPDATE in_scope.users
SET created_by = id
WHERE email = 'jcvargas.dev@gmail.com';

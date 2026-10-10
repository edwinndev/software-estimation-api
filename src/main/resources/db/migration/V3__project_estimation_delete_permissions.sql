INSERT INTO in_scope.permissions (code, label, group_code, group_label) VALUES
    ('project:delete', 'Eliminar proyectos', 'projects', 'Proyectos'),
    ('estimation:delete', 'Eliminar estimaciones', 'estimation', 'Estimación');

INSERT INTO in_scope.role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM in_scope.roles r
CROSS JOIN in_scope.permissions p
WHERE r.code IN ('admin', 'estimator')
  AND p.code IN ('project:delete', 'estimation:delete')
ON CONFLICT (role_id, permission_id) DO NOTHING;

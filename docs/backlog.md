# Historias de usuario y tareas técnicas

Las rutas están bajo `/api/v1`. Los listados reciben `QueryRequest` y responden
con `PaginatedResponse`, de acuerdo con [`pagination.md`](./pagination.md).

## Historias de usuario

| Método | Ruta | Comportamiento |
|---|---|---|
| `POST` | `/projects/{projectId}/user-stories/search` | Busca dentro del backlog del proyecto. |
| `POST` | `/projects/{projectId}/user-stories` | Crea una historia y genera su código y posición. |
| `PUT` | `/user-stories/{storyId}` | Actualiza los campos editables; `position` opcional reordena el backlog. |
| `DELETE` | `/user-stories/{storyId}` | Elimina la historia; PostgreSQL elimina en cascada sus tareas y asignaciones. |

La búsqueda admite `search` (título, descripción o código), `priority` y
`status`. La respuesta paginada usa la clave `userStoryResponse`.

## Tareas técnicas

| Método | Ruta | Comportamiento |
|---|---|---|
| `POST` | `/tasks/search` | Busca tareas; admite `storyId` (`IN`), `status` y `search` (título o descripción). |
| `POST` | `/tasks` | Crea una tarea asociada a una historia existente. |
| `PUT` | `/tasks/{taskId}` | Actualiza la tarea; `position` opcional reordena las tareas de su historia. |
| `PUT` | `/tasks/{taskId}/responsibles` | Reemplaza la lista `profileIds`; una lista vacía la limpia. |
| `DELETE` | `/tasks/{taskId}` | Elimina la tarea y sus asignaciones. |

La respuesta paginada usa la clave `taskResponse`; cada tarea incluye
`estimateHours` y `profileIds`.

## Límites de integración

Los IDs de proyecto y perfil técnico deben existir en sus tablas, debido a las
claves foráneas del esquema. La API de proyectos y perfiles todavía no está
incluida aquí. Esta implementación tampoco agrega autenticación/autorización;
no debe exponerse como API de escritura en un entorno compartido o de producción
hasta que se integre la seguridad del backend.

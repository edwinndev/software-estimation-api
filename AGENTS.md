# Guías de desarrollo

Spring MVC (no reactivo), Java 25, Lombok. Estructura y arranque: `README.md`. Detalle de paginación/errores: `docs/`.

## Código

- Limpio y corto. Sin capas ni helpers de más.
- Compilar con JDK 25 sin errores ni warnings. No `@SuppressWarnings` a nivel de clase.
- Sin tests salvo que se pidan. Sin comentarios narrativos.
- Mensajes al usuario en español; código, enums y BD en inglés.
- Lombok + inyección por constructor (`@RequiredArgsConstructor`, `final`). Sin `@Autowired` en campos.
- `record` solo para DTOs/metadatos chicos. Entidades con Lombok.

## Capas

- Flujo: `rest` → `service` → `repository`. Controllers delgados; sin repositorio ni entidad en la respuesta (usar `mapper` → DTO).
- Transacciones en el service. Un controller/service/repository/mapper por recurso.
- Endpoints: `/api/v1/{recursos}`. Listados: `POST /search` con `QueryRequest` / `PaginatedResponse` (`common/`).
- Errores: excepciones existentes + `ErrorResponse`. Detalle en `docs/errors.md`.

## Base de datos

- Esquema `in_scope`, nombres calificados. Flyway dueño del DDL (`ddl-auto: none`).
- No editar migraciones aplicadas; agregar `V{n}__...sql`.
- UUID + `gen_random_uuid()`. ENUM solo listas fijas. Sin CHECK. Auditoría sin FK.

## Git

- Rama: `feature/{feature}`
- Commits en español (prefijo en inglés), máx. 15 palabras: `feat`, `fix`, `ref`, `style`, `docs`

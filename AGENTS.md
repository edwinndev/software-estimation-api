# Guías de desarrollo obligatorias

Backend de Software Estimation: Spring Boot 4.1 (Spring MVC, **no reactivo**), Java 25, PostgreSQL, Flyway, Lombok y springdoc. Lee `README.md` y `docs/` antes de escribir código.

## 1. Calidad del código

- El código debe ser limpio, pequeño y fácil de leer. Prefiere pocos métodos con nombres claros antes que métodos largos. No agregues capas, abstracciones ni helpers innecesarios.
- Compila con JDK 25. El build debe terminar sin errores y sin warnings. No silencies warnings con `@SuppressWarnings` a nivel de clase; si un cast es inevitable, aíslalo en un método pequeño con `@SuppressWarnings("unchecked")` solo en ese método.
- Elimina imports, campos y métodos sin uso.
- No agregues comentarios que narren lo que hace el código. Comenta solo la intención o restricción que no sea obvia.
- No escribas tests salvo que se pidan explícitamente. Nunca dejes archivos de prueba temporales.
- Los mensajes para el usuario (excepciones, validaciones) van en español. El código, los identificadores, los valores de enums y los nombres de base de datos van en inglés.

## 2. Sintaxis y Lombok

- Usa Lombok para eliminar boilerplate: `@Getter`, `@Setter`, `@Value`, `@Builder`, `@RequiredArgsConstructor`, `@Slf4j`, `@UtilityClass`.
- Inyecta dependencias por constructor con `@RequiredArgsConstructor` y campos `private final`. Nunca uses `@Autowired` en campos.
- Usa `record` solo para objetos pequeños e inmutables (cuerpos de petición, metadatos de paginación). Las entidades y las respuestas más completas son clases con Lombok. No conviertas todo en records.
- Sin números ni textos mágicos: usa constantes o enums.
- Usa `switch` como expresión, `List.of`, `Optional` y streams solo cuando hagan el código más claro.

## 3. Reglas de capas

La estructura de carpetas está en `README.md`. Respeta estas reglas:

- Flujo de la petición: `rest` -> `service` -> `repository`. Los controladores nunca tocan repositorios ni entidades.
- Las entidades nunca se devuelven desde un controlador. Siempre se convierten a un DTO de respuesta en `mapper`.
- Los controladores son delgados: sin lógica de negocio y sin try/catch. Los errores se lanzan como excepciones y los maneja `GlobalExceptionHandler`.
- Los servicios son dueños de las transacciones: `@Transactional` en escrituras y `@Transactional(readOnly = true)` en lecturas.
- Un controlador, servicio, repositorio y mapper por recurso (por ejemplo `ProjectController`, `ProjectService`, `ProjectRepository`, `ProjectMapper`).
- Los endpoints viven bajo `/api/v1/{recursos}` con sustantivos en plural y kebab-case. Usa `GET /{id}`, `POST`, `PUT /{id}`, `DELETE /{id}`. Los listados usan `POST /search`.
- Anota los controladores con OpenAPI (`@Tag`, `@Operation`) para que Swagger siga siendo útil.
- `common/` es solo para paginación y filtros genéricos. No pongas código de dominio ahí.

## 4. Paginación y filtros

Todo listado usa el contrato compartido de `common/`. No escribas paginación a mano. Detalle completo en `docs/pagination.md`.

- Entrada: `QueryRequest` (`filters` + `pagination`). Salida: `PaginatedResponse<T>` con las filas bajo `{entity}Response` (por ejemplo `userResponse`).
- `pageNumber` es base 0 y el tamaño por defecto es 20 (máximo 100).
- Cada recurso declara una sola vez sus campos permitidos para filtrar y ordenar con `QueryFields` (lista blanca) y llama a `QuerySupport.search(...)`. Los campos fuera de la lista se rechazan con 400.
- Los repositorios de recursos con búsqueda extienden `JpaSpecificationExecutor<Entity>`.
- Para acotar un listado (por ejemplo excluir eliminados) pasa un `Specification` base a la sobrecarga de `QuerySupport.search` que lo acepta.

## 5. Excepciones y errores

Detalle completo en `docs/errors.md`.

- Lanza las excepciones existentes: `BadRequestException` (400), `UnauthorizedException` (401), `ForbiddenException` (403), `ResourceNotFoundException` (404), `ConflictException` (409).
- Crea una excepción nueva solo si un caso HTTP real no está cubierto. Debe ser una subclase simple de `RuntimeException` con `@ResponseStatus`, más un `@ExceptionHandler` en `GlobalExceptionHandler` que llame a `buildResponse`.
- Toda respuesta de error usa `ErrorResponse` (`code`, `message`, `cause`, `path`, `timestamp`). Nunca devuelvas otra forma de error.
- Nunca expongas mensajes internos en un 500. Los errores inesperados se registran en el log y se responden con un mensaje fijo.
- `GlobalExceptionHandler` solo maneja excepciones. Nada de validaciones ni lógica de negocio ahí.

## 6. Base de datos y migraciones

- El esquema es `in_scope`. Usa siempre nombres completos en el SQL (`in_scope.users`).
- Flyway es dueño del esquema (`ddl-auto: none`). Nunca edites una migración ya aplicada: agrega una nueva `V{n}__descripcion.sql`.
- Las claves primarias son `UUID` con `gen_random_uuid()`. Las fechas son `TIMESTAMP` y se manejan como `LocalDateTime`.
- Tipos `ENUM` de PostgreSQL solo para listas fijas. Las listas que pueden crecer son `VARCHAR`. Sin restricciones `CHECK` y sin índices salvo que haya una necesidad clara.
- Las columnas de auditoría (`created_by`, `updated_by`, `deleted_by`) son `UUID` simples, sin claves foráneas. El borrado lógico (`deleted_at`) solo donde el modelo ya lo define.
- Los enums de las entidades usan `@Enumerated(EnumType.STRING)` y coinciden exactamente con los valores de la base.

## 7. Configuración

- La configuración vive en `src/main/resources/application.yaml` y se sobreescribe con variables de entorno, con un valor local seguro por defecto.
- Nunca dejes secretos ni credenciales en el código. Las contraseñas se guardan con hash (bcrypt).

## 8. GitFlow y commits

### Nombre de ramas:

- Crea siempre las ramas con el formato `feature/{feature}` (por ejemplo `feature/users`, `feature/projects`, `feature/reports`).

### Mensajes de commit:

- Escribe los mensajes de commit en español (manteniendo el prefijo en inglés).
- Mantenlos concisos (máximo 15 palabras).
- Usa estos prefijos según el tipo de cambio:
  - **`feat`**: Nueva funcionalidad (por ejemplo, `feat: agregar búsqueda paginada de usuarios`).
  - **`fix`**: Corrección de un error o comportamiento inesperado (por ejemplo, `fix: corregir respuesta 404 al buscar proyecto`).
  - **`ref`**: Refactor sin cambiar el comportamiento externo (por ejemplo, `ref: aplanar respuesta DTO de usuario`).
  - **`style`**: Formato o nombres sin cambios de lógica (por ejemplo, `style: reordenar imports en servicio de usuarios`).
  - **`docs`**: Solo documentación (por ejemplo, `docs: actualizar variables de entorno en README`).

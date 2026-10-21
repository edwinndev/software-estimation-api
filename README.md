# Software Estimation API

API REST para estimar software. Spring MVC (no reactivo) con PostgreSQL y migraciones Flyway.

| Tecnología          | Versión |
| ------------------- | ------- |
| Java                | 25      |
| Spring Boot         | 4.1.1   |
| PostgreSQL          | 13+     |
| Flyway              | gestionado por Spring Boot |
| springdoc (Swagger) | 3.1.0   |

## Requisitos

- JDK 25 (`java -version` debe mostrar 25). Con otra versión no compila.
- PostgreSQL en marcha con una base vacía.
- No hace falta instalar Maven: el proyecto trae `mvnw` / `mvnw.cmd`.

## Arranque

1. Crear la base de datos (una sola vez):

```sql
CREATE DATABASE in_scope_db;
```

2. Levantar la API desde esta carpeta:

```bash
# Linux / macOS
./mvnw spring-boot:run

# Windows
.\mvnw.cmd spring-boot:run
```

Al arrancar, Flyway crea el esquema `in_scope` con todas las tablas (`V1__schema.sql`) y los datos iniciales (`V2__seed.sql`). Solo hay esas dos migraciones.

La API queda en [http://localhost:8080](http://localhost:8080).

| URL                                                                 | Qué es                    |
| ------------------------------------------------------------------- | ------------------------- |
| [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) | Documentación interactiva |
| [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)         | Especificación OpenAPI    |

## Perfiles y secretos

| Perfil | Uso | Secretos |
| :--- | :--- | :--- |
| `dev` (default) | Local | Archivo `.env` (ver `.env.example`) |
| `prod` | Azure | Azure Key Vault + Managed Identity |

```bash
# Local
cp .env.example .env
./mvnw spring-boot:run

# Azure / producción
export SPRING_PROFILES_ACTIVE=prod
export AZURE_KEYVAULT_ENDPOINT=https://<tu-vault>.vault.azure.net/
java -jar target/inscope-api-0.0.1.jar
```

### Secretos a crear en Key Vault (prod)

| Nombre en Key Vault | Propiedad Spring |
| :--- | :--- |
| `spring-datasource-url` | `spring.datasource.url` |
| `spring-datasource-username` | `spring.datasource.username` |
| `spring-datasource-password` | `spring.datasource.password` |
| `spring-mail-username` | `spring.mail.username` |
| `spring-mail-password` | `spring.mail.password` |
| `in-scope-jwt-secret` | `in-scope.jwt.secret` |
| `in-scope-cors-allowed-origins` | `in-scope.cors.allowed-origins` |
| `in-scope-bunny-access-key` | `in-scope.bunny.access-key` |

En la VM/App Service solo defines:
- `SPRING_PROFILES_ACTIVE=prod`
- `AZURE_KEYVAULT_ENDPOINT=https://<vault>.vault.azure.net/`

Activa **Managed Identity** en la VM/App Service y asígnale rol **Key Vault Secrets User** (Get/List) sobre el vault.

Host/zone de Bunny y expiración JWT están en `application.yaml` (no van al vault).

## Usuario inicial

El seed crea un administrador:

| Campo      | Valor                    |
| ---------- | ------------------------ |
| Correo     | `jcvargas.dev@gmail.com` |
| Contraseña | `admin123`               |
| Rol        | Administrador            |

Cambiar la contraseña antes de cualquier despliegue real.

## Comandos útiles

```bash
./mvnw compile            # compilar
./mvnw clean package      # generar el jar en target/
./mvnw test               # tests (necesitan base de datos disponible)
```

`.mvn/jvm.config` silencia los avisos de `sun.misc.Unsafe` que Lombok emite en JDK 25. No requiere nada más.

## Estructura

```text
src/main/java/com/intecx/inscope/
  InScopeApplication.java         Punto de entrada
  rest/           Controladores REST (reciben la petición y delegan al servicio)
  service/        Interfaces de negocio (+ impl/ y support/ para helpers)
  repository/     Repositorios Spring Data JPA (extienden JpaSpecificationExecutor para listados)
  entity/         Entidades JPA (una por tabla)
  enumeration/    Enums del dominio (espejo de los tipos enumerados de PostgreSQL)
  dto/
    request/      Cuerpos de entrada
    response/     Cuerpos de salida
  mapper/         Conversión entidad ↔ DTO
  util/           Utilidades sueltas sin dependencia del dominio
  common/         Paginación y filtros genéricos (QueryRequest, QueryFields, QuerySupport, ...)
  exception/      Excepciones REST, ErrorResponse y GlobalExceptionHandler
  config/         CORS (WebConfig) y OpenAPI (OpenApiConfig)

src/main/resources/
  application.yaml
  db/migration/   V1__schema.sql (esquema completo) y V2__seed.sql (roles, permisos y admin)
  templates/email/  Plantillas FreeMarker (layout, otp, invitation, welcome)
  logo/             Logos (intecx-dark-logo.png embebido en correos)

src/test/java/    Pruebas (por ahora solo EstimationApiApplicationTests)
docs/             Documentación detallada
.mvn/             Wrapper de Maven y jvm.config
```

Flujo de una petición: `rest` → `service` → `repository` → base de datos, con `dto` entrando y saliendo y `mapper` convirtiendo entre `entity` y `dto`. Si algo falla, `exception` lo convierte en la respuesta de error uniforme.

Hoy solo hay código en `common`, `exception` y `config`, además del esquema y el seed. El resto de carpetas están listas y se llenan por recurso.

## Docs

- Paginación y filtros: `docs/pagination.md`
- Errores: `docs/errors.md`
- Convenciones del agente: `AGENTS.md`
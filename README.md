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

Al arrancar, Flyway crea el esquema `in_scope`, las tablas (`V1__schema.sql`) y los datos iniciales (`V2__seed.sql`).

La API queda en [http://localhost:8080](http://localhost:8080).

| URL                                                                 | Qué es                    |
| ------------------------------------------------------------------- | ------------------------- |
| [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) | Documentación interactiva |
| [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)         | Especificación OpenAPI    |

## Configuración y Variables de Entorno

La configuración del backend admite lectura directa desde el archivo `.env` o desde las variables de entorno del sistema operativo en producción (sin requerir Docker):

| Categoría | Variable | Valor por defecto | Descripción |
| :--- | :--- | :--- | :--- |
| **Servidor** | `SERVER_PORT` | `8080` | Puerto HTTP de la API |
| **Base de Datos** | `SPRING_DATASOURCE_URL`<br>`SPRING_DATASOURCE_USERNAME`<br>`SPRING_DATASOURCE_PASSWORD` | `jdbc:postgresql://localhost:5432/in_scope_db`<br>`postgres`<br>`root` | Conexión JDBC PostgreSQL |
| **Correo SMTP** | `SPRING_MAIL_HOST`<br>`SPRING_MAIL_PORT`<br>`SPRING_MAIL_USERNAME`<br>`SPRING_MAIL_PASSWORD` | `smtp.gmail.com`<br>`587`<br>`jheann.elec@gmail.com`<br>`ufuisgqplvayeigk` | Servidor SMTP para correos OTP |
| **CORS** | `IN_SCOPE_CORS_ALLOWED_ORIGINS` | `http://localhost:3000` | Orígenes HTTP permitidos |
| **Seguridad JWT** | `IN_SCOPE_JWT_SECRET`<br>`IN_SCOPE_JWT_EXPIRATION_MS`<br>`IN_SCOPE_JWT_REFRESH_EXPIRATION_MS` | *(secreto 64 bytes)*<br>`86400000` (24h)<br>`604800000` (7 días) | Clave de firma y expiración de tokens |

### Despliegue en Producción (sin Docker)

1. Crear el archivo `.env` en la raíz junto al JAR basándote en `.env.example`:
   ```bash
   cp .env.example .env
   # Editar .env con credenciales reales de producción
   ```
2. Compilar el ejecutable JAR:
   ```bash
   ./mvnw clean package -DskipTests
   ```
3. Ejecutar la API en el servidor:
   ```bash
   java -jar target/inscope-api-0.0.1.jar
   ```

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
  service/        Lógica de negocio
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
  db/migration/   V1__schema.sql (tablas) y V2__seed.sql (datos iniciales)
  templates/      Plantillas (por ahora vacía)

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
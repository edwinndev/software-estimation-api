# Errores

Todos los errores de la API tienen el mismo cuerpo, venga de donde venga. Lo arma `GlobalExceptionHandler`.

```json
{
  "code": 404,
  "message": "Proyecto no encontrado",
  "cause": "Not Found",
  "path": "GET http://localhost:8080/api/v1/projects/42",
  "timestamp": "2026-10-01T05:20:00.123456"
}
```

| Campo       | Qué es                                                                  |
| ----------- | ----------------------------------------------------------------------- |
| `code`      | Código HTTP                                                             |
| `message`   | Mensaje para mostrar al usuario                                         |
| `cause`     | Mensaje de la causa original si existe; si no, el texto del estado HTTP |
| `path`      | Método y URL de la petición                                             |
| `timestamp` | Momento del error (fecha y hora local)                                  |

## Excepciones propias

Se lanzan desde servicios y controladores. Cada una ya trae su código HTTP.

| Excepción                   | HTTP |
| --------------------------- | ---- |
| `BadRequestException`       | 400  |
| `UnauthorizedException`     | 401  |
| `ForbiddenException`        | 403  |
| `ResourceNotFoundException` | 404  |
| `ConflictException`         | 409  |

```java
throw new ResourceNotFoundException("Proyecto no encontrado");
throw new ConflictException("Ya existe un usuario con ese correo");
```

## Errores del framework que también se transforman

| Situación                                         | HTTP | Mensaje                                         |
| ------------------------------------------------- | ---- | ----------------------------------------------- |
| Falla `@Valid` en el cuerpo                       | 400  | Mensajes de las validaciones, separados por coma |
| JSON ilegible o con valores inválidos (por ejemplo un enum inexistente) | 400  | El cuerpo de la petición no es un JSON válido |
| Falta un parámetro obligatorio                    | 400  | Falta el parámetro obligatorio '...'            |
| Parámetro con tipo incorrecto                     | 400  | El parámetro '...' tiene un formato inválido    |
| Entidad, registro o ruta inexistente              | 404  | Recurso no encontrado                           |
| Método HTTP no permitido                          | 405  | Método ... no permitido                         |
| `Content-Type` no soportado                       | 415  | Tipo de contenido no soportado                  |
| `Accept` no soportado                             | 406  | Formato de respuesta no soportado               |
| Violación de integridad en la base (unique, FK)   | 409  | La operación viola una restricción de datos (registro duplicado o en uso) |
| Conflicto de versión (bloqueo optimista)          | 409  | El recurso fue modificado por otra persona. Recárgalo e inténtalo de nuevo |
| Archivo demasiado grande                          | 413  | El archivo supera el tamaño máximo permitido    |
| Cualquier otra excepción                          | 500  | Error interno del servidor (sin detalles internos) |

El 500 nunca expone el mensaje real de la excepción. El detalle queda solo en el log del servidor.

## Cómo agregar una excepción nueva

1. Crear la clase con su código HTTP:

```java
@ResponseStatus(HttpStatus.UNPROCESSABLE_CONTENT)
public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) {
        super(message);
    }
}
```

2. Agregar su método en `GlobalExceptionHandler`:

```java
@ExceptionHandler(BusinessRuleException.class)
public ResponseEntity<ErrorResponse> handleBusinessRuleException(
        BusinessRuleException ex, HttpServletRequest request) {
    return buildResponse(HttpStatus.UNPROCESSABLE_CONTENT, ex.getMessage(), ex, request);
}
```

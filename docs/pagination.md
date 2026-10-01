# Paginación y filtros

Contrato único para todos los listados. Coincide con el del cliente (`software-estimation-client/docs/users.md`).

- `pageNumber` es **base 0**.
- Tamaño por defecto: **20**. Máximo: **100**.
- Los listados se piden con `POST /<recurso>/search`.

## Petición: `QueryRequest`

```json
{
  "filters": [
    { "key": "search", "operator": "LK", "values": ["laura"] },
    { "key": "role", "operator": "EQ", "values": ["estimator"] }
  ],
  "pagination": {
    "pageNumber": 0,
    "pageSize": 20,
    "orderBy": "createdAt",
    "sortDirection": "DESC"
  }
}
```

Todo es opcional. Un cuerpo vacío devuelve la primera página con el orden por defecto del recurso.

### Reglas de `pagination`

| Campo           | Regla                                                             |
| --------------- | ----------------------------------------------------------------- |
| `pageSize`      | `<= 0` equivale a 20; más de 100 se recorta a 100                 |
| `pageNumber`    | Negativo equivale a 0; si se pasa del final devuelve la última página |
| `orderBy`       | Si falta se usa el orden por defecto del recurso                  |
| `sortDirection` | `ASC` o `DESC` en mayúsculas; por defecto `DESC`                  |

Valores fuera de rango se corrigen, no fallan. Siempre se agrega `id` como segundo criterio de orden para que las páginas sean estables.

### Filtros

Un filtro sin valores se ignora.

| Operador | Significado           | Valores               |
| -------- | --------------------- | --------------------- |
| `EQ`     | Igual                 | 1                     |
| `NE`     | Distinto              | 1                     |
| `LK`     | Contiene (solo texto) | 1                     |
| `IN`     | Está en la lista      | 1 o más               |
| `GT`     | Mayor que             | 1                     |
| `GE`     | Mayor o igual         | 1                     |
| `LT`     | Menor que             | 1                     |
| `LE`     | Menor o igual         | 1                     |
| `BT`     | Entre (inclusive)     | 2 (desde, hasta)      |

Qué operadores aplican según el tipo del campo:

| Tipo                                                  | Operadores                         |
| ----------------------------------------------------- | ---------------------------------- |
| Texto                                                 | `EQ`, `NE`, `LK`, `IN`             |
| Booleano, UUID, enum                                  | `EQ`, `NE`, `IN`                   |
| Números, fechas y timestamps                          | Todos menos `LK`                   |

Comportamientos a tener en cuenta:

- Texto: la comparación no distingue mayúsculas. En `LK` los caracteres `%` y `_` se escapan, se buscan literalmente.
- Enums en `values` de un filtro: se aceptan en cualquier combinación de mayúsculas y minúsculas.
- Fechas: un valor solo con fecha (`2026-03-12`) sobre un timestamp cubre el día completo en UTC.
- `key` puede apuntar a varios atributos a la vez (por ejemplo `search` busca en nombre y correo). Basta que uno coincida.

## Respuesta: `PaginatedResponse`

Las filas van bajo una clave propia de cada recurso (`userResponse`, `projectResponse`, ...) y los metadatos van planos al lado.

```json
{
  "userResponse": [
    {
      "id": "8f2a1c4e-3b9d-4a11-9c0e-2d7f6a1b0e33",
      "firstName": "Laura",
      "lastName": "Gómez",
      "email": "laura.gomez@intecx.com",
      "role": "estimator",
      "createdAt": "2026-03-12T14:20:00Z"
    }
  ],
  "pageNumber": 0,
  "pageSize": 20,
  "totalElements": 47,
  "totalPages": 3,
  "hasNext": true,
  "hasPrevious": false
}
```

## Errores propios de la paginación

Todos responden `400` con el formato de `docs/errors.md`:

- Filtrar por un campo que no está en la lista permitida.
- Ordenar por un campo que no está en la lista permitida.
- Usar un operador que no aplica al tipo del campo (por ejemplo `LK` sobre un número).
- Valor que no se puede convertir al tipo del campo (un UUID mal escrito, una fecha inválida, un enum inexistente).
- `BT` con menos de dos valores.

Los mensajes indican los campos u operadores permitidos.

## Dónde está el código

| Clase                        | Qué hace                                                       |
| ---------------------------- | -------------------------------------------------------------- |
| `QueryRequest`, `PaginationRequest`, `FilterRequest` | Cuerpo de la petición y normalización de valores |
| `QueryFields`                | Lista blanca de filtros y orden de cada recurso                |
| `QuerySupport`               | Punto de entrada: arma la consulta y devuelve la respuesta     |
| `FilterSpecifications`       | Convierte cada filtro en un `Specification` de JPA             |
| `FilterValues`               | Convierte los valores de texto al tipo del campo               |
| `PaginatedResponse`, `PageMeta` | Respuesta plana con metadatos                               |
| `PaginationConstants`        | Tamaño por defecto, máximo y primera página                    |

# Sistema de gestión de pedidos de restaurante — Backend

API REST desarrollada con **Java 21 y Spring Boot** para gestionar los pedidos, productos, categorías y terminales de un restaurante.

La aplicación permite administrar el ciclo de vida de los pedidos, consultar estadísticas de ventas y almacenar la información de forma persistente en una base de datos MySQL.

Este proyecto constituye el backend de un sistema de gestión de pedidos, consumido por una aplicación frontend desarrollada con React.

## Tecnologías utilizadas

- **Java 21**
- **Spring Boot 3.3.2**
- **Spring Web:** desarrollo de la API REST.
- **Spring Data JPA:** acceso y persistencia de datos.
- **Hibernate:** mapeo objeto-relacional.
- **MySQL / MariaDB:** sistema de gestión de bases de datos.
- **Spring Validation:** validación de datos de entrada.
- **Springdoc OpenAPI 2.5.0:** documentación interactiva mediante Swagger UI.
- **Lombok:** reducción de código repetitivo.
- **Maven:** gestión de dependencias y construcción del proyecto.
- **JUnit 5, Mockito y Spring Boot Test:** pruebas automatizadas.

## Funcionalidades

### 1. Gestión de productos

- Consultar todos los productos.
- Filtrar por nombre, categoría, estado activo y rango de precios.
- Ordenar por nombre, precio o categoría, en orden ascendente o descendente.
- Crear productos.
- Actualizar productos existentes.
- Desactivar productos sin eliminarlos de la base de datos.
- Asociar cada producto a una categoría.
- Utilizar `BigDecimal` para representar los precios y evitar problemas de precisión en operaciones monetarias.

### 2. Gestión de categorías

- Consultar todas las categorías.
- Crear nuevas categorías.
- Asociar productos a sus respectivas categorías.

### 3. Gestión de terminales

- Consultar los terminales disponibles.
- Asociar cada pedido a un terminal.

### 4. Gestión de pedidos

- Crear pedidos asociados a un terminal.
- Generar automáticamente un identificador y un código público para cada pedido.
- Consultar todos los pedidos o filtrarlos por estado.
- Consultar pedidos mediante su identificador o código público.
- Actualizar y eliminar pedidos.
- Añadir productos a un pedido.
- Eliminar productos de un pedido.
- Actualizar automáticamente el total del pedido al modificar sus productos.
- Impedir modificaciones de productos en pedidos con estados finales.

### 5. Gestión de estados

Los pedidos utilizan los siguientes estados:

| Estado | Descripción |
|---|---|
| `PENDING` | Pedido pendiente. |
| `IN_PREPARATION` | Pedido en preparación. |
| `COMPLETED` | Pedido preparado y listo para recoger. |
| `CANCELLED` | Pedido cancelado. |
| `PICKED_UP` | Pedido recogido por el cliente. |

Las transiciones permitidas son:

- `PENDING` → `IN_PREPARATION`
- `PENDING` → `CANCELLED`
- `IN_PREPARATION` → `COMPLETED`
- `IN_PREPARATION` → `CANCELLED`
- `COMPLETED` → `PICKED_UP`

El backend valida las transiciones para impedir cambios de estado no permitidos.

Los pedidos cancelados y recogidos se consideran estados finales.

### 6. Estadísticas de ventas

La API incorpora consultas estadísticas para analizar el rendimiento de los productos:

- Consultar el producto o productos más vendidos.
- Consultar el producto o productos menos vendidos.
- Consultar los tres productos más vendidos, incluyendo los empates en la tercera posición.

Para el cálculo de las ventas se tienen en cuenta los pedidos completados (`COMPLETED`) y recogidos (`PICKED_UP`).

## Endpoints de la API

La API utiliza el prefijo `/` y los siguientes endpoints:

### Productos — `/products`

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/products` | Consultar productos y aplicar filtros. |
| POST | `/products` | Crear un producto. |
| PUT | `/products/{id}` | Actualizar un producto. |
| PATCH | `/products/{id}/deactivate` | Desactivar un producto. |

Parámetros de consulta disponibles para `GET /products`:

- `name`: filtrar por nombre.
- `categoryId`: filtrar por categoría.
- `active`: filtrar por estado activo.
- `minPrice`: establecer un precio mínimo.
- `maxPrice`: establecer un precio máximo.
- `sortBy`: ordenar por nombre, precio o categoría.
- `sortDirection`: dirección ascendente (`asc`) o descendente (`desc`).

### Categorías — `/categories`

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/categories` | Consultar todas las categorías. |
| POST | `/categories` | Crear una categoría. |

### Terminales — `/terminals`

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/terminals` | Consultar los terminales disponibles. |

### Pedidos — `/orders`

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/orders` | Consultar todos los pedidos. |
| GET | `/orders?status={status}` | Consultar pedidos filtrados por estado. |
| GET | `/orders/{id}` | Consultar un pedido por su ID. |
| GET | `/orders/by-code/{code}` | Consultar un pedido mediante su código público. |
| POST | `/orders` | Crear un pedido. |
| PUT | `/orders/{id}` | Actualizar un pedido. |
| DELETE | `/orders/{id}` | Eliminar un pedido. |
| PATCH | `/orders/{id}/status?status={status}` | Actualizar el estado de un pedido. |
| POST | `/orders/{orderId}/products` | Añadir un producto a un pedido. |
| DELETE | `/orders/{orderId}/products/{productId}` | Eliminar un producto de un pedido. |

### Estadísticas — `/orders`

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/orders/most-sold` | Consultar los productos más vendidos. |
| GET | `/orders/least-sold` | Consultar los productos menos vendidos. |
| GET | `/orders/top-products` | Consultar los tres productos más vendidos. |

## Base de datos

El proyecto utiliza MySQL para almacenar la información de forma persistente. También es compatible con MariaDB.

### Configuración

Antes de iniciar la aplicación, es necesario disponer de un servidor MySQL o MariaDB en ejecución y crear la base de datos:

```sql
CREATE DATABASE proyecto1;
```

La configuración de conexión se encuentra en `src/main/resources/application.properties`:

```properties
spring.application.name=Proyecto1

# MYSQL
spring.datasource.url=jdbc:mysql://localhost:3306/proyecto1
spring.datasource.username=root
spring.datasource.password=

# JPA / HIBERNATE
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.open-in-view=false
```

Es posible modificar la URL, el usuario y la contraseña para adaptarlos al entorno local.

### Carga inicial de datos

La aplicación incorpora un `DataLoader` que inserta datos de ejemplo cuando la base de datos no contiene productos.

La carga inicial incluye:

- 3 categorías: FOOD, DRINK y DESSERT.
- 10 productos de ejemplo.
- 3 terminales: Barra, Terraza y Sala.
- 5 pedidos de ejemplo.

Esto permite probar las funcionalidades de la API sin tener que introducir manualmente todos los datos desde el principio.

## Configuración CORS

El proyecto incluye una configuración CORS mediante `CorsConfig`, que permite la comunicación entre el backend y el frontend desarrollado con React y Vite durante el entorno de desarrollo local.

La configuración establece:

- **Origen autorizado:** `http://localhost:5173`.
- **Métodos permitidos:** GET, POST, PUT, PATCH, DELETE y OPTIONS.
- **Cabeceras:** todas las cabeceras están permitidas.
- **Rutas:** se aplica a todos los endpoints (`/**`).

Esta configuración está orientada al desarrollo local y deberá adaptarse antes de desplegar la aplicación en producción.

## Instalación y ejecución

### Requisitos previos

- Java 21.
- MySQL o MariaDB.
- Git (opcional, para clonar el repositorio).

### Clonar el repositorio

```bash
git clone <https://github.com/fernandorossigomez/Proyecto2b>
```

### Acceder al directorio del proyecto

```bash
cd Proyecto1
```

### Ejecutar la aplicación

En Windows:

```bash
.\mvnw.cmd spring-boot:run
```

En Linux o macOS:

```bash
./mvnw spring-boot:run
```

La aplicación se iniciará en:

```text
http://localhost:8080
```

## Documentación API — Swagger UI

El proyecto incorpora documentación interactiva mediante Springdoc OpenAPI.

Una vez iniciada la aplicación, se puede acceder a Swagger UI desde:

```text
http://localhost:8080/swagger-ui/index.html
```

La especificación OpenAPI en formato JSON está disponible en:

```text
http://localhost:8080/v3/api-docs
```

Swagger permite consultar los endpoints, revisar los parámetros y modelos de datos y realizar peticiones directamente contra la API.

## Colección Postman

El proyecto incluye una colección de Postman para facilitar las pruebas manuales de la API.

Las peticiones están organizadas en las siguientes secciones:

- **Products:** consulta, creación y filtrado de productos.
- **Orders:** consulta, creación y eliminación de pedidos.
- **Order Products:** incorporación y eliminación de productos en pedidos.
- **Stats:** consultas de productos más vendidos, menos vendidos y ranking de ventas.

También se incluye una exportación de la API mediante `Proyecto1_API_export.json`.

## Pruebas automatizadas

El proyecto incorpora pruebas unitarias y de integración utilizando JUnit 5, Mockito y Spring Boot Test.

Las pruebas cubren principalmente los servicios de:

- `CategoryService`
- `OrderService`
- `ProductService`

También se incluye una prueba de contexto de la aplicación mediante `Proyecto1ApplicationTests`.

### Ejecución de los tests

En Windows:

```bash
.\mvnw.cmd clean test
```

En Linux o macOS:

```bash
./mvnw clean test
```

### Resultado de las pruebas

Última ejecución verificada:

| Clase de test | Tests |
|---|---:|
| `Proyecto1ApplicationTests` | 1 |
| `CategoryServiceTest` | 5 |
| `OrderServiceTest` | 36 |
| `ProductServiceTest` | 16 |
| **Total** | **58** |

**Resultado:** 58 tests ejecutados, 0 fallos, 0 errores y 0 omitidos.

## Estructura del proyecto

El proyecto sigue una arquitectura organizada por capas, separando controladores, servicios, repositorios, entidades y DTOs.

```text
Proyecto1/
├── .mvn/
│   └── wrapper/
├── postman/
│   └── collections/
│       └── Proyecto1 API/
│           ├── Order Products/
│           ├── Orders/
│           ├── Products/
│           └── Stats/
├── src/
│   ├── main/
│   │   ├── java/com/fernando/proyecto1/
│   │   │   ├── config/
│   │   │   │   ├── CorsConfig.java
│   │   │   │   └── DataLoader.java
│   │   │   ├── controller/
│   │   │   │   ├── CategoryController.java
│   │   │   │   ├── OrderController.java
│   │   │   │   ├── ProductController.java
│   │   │   │   └── TerminalController.java
│   │   │   ├── dto/
│   │   │   │   ├── AddProductRequest.java
│   │   │   │   ├── CategoryRequest.java
│   │   │   │   ├── CategoryResponse.java
│   │   │   │   ├── ErrorResponse.java
│   │   │   │   ├── OrderProductResponse.java
│   │   │   │   ├── OrderRequest.java
│   │   │   │   ├── OrderResponse.java
│   │   │   │   ├── ProductRequest.java
│   │   │   │   ├── ProductResponse.java
│   │   │   │   ├── ProductSalesResponse.java
│   │   │   │   └── ProductStatsResponse.java
│   │   │   ├── entity/
│   │   │   │   ├── CategoryEntity.java
│   │   │   │   ├── OrderEntity.java
│   │   │   │   ├── OrderProductEntity.java
│   │   │   │   ├── OrderStatus.java
│   │   │   │   ├── ProductEntity.java
│   │   │   │   └── TerminalEntity.java
│   │   │   ├── exceptions/
│   │   │   │   ├── CategoryNotFoundException.java
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   ├── OrderNotFoundException.java
│   │   │   │   ├── ProductNotFoundException.java
│   │   │   │   └── TerminalNotFoundException.java
│   │   │   ├── repository/
│   │   │   │   ├── CategoryRepository.java
│   │   │   │   ├── OrderProductRepository.java
│   │   │   │   ├── OrderRepository.java
│   │   │   │   ├── ProductRepository.java
│   │   │   │   └── TerminalRepository.java
│   │   │   └── service/
│   │   │       ├── CategoryService.java
│   │   │       ├── OrderService.java
│   │   │       └── ProductService.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/fernando/Proyecto1/
│           ├── Proyecto1ApplicationTests.java
│           └── service/
│               ├── CategoryServiceTest.java
│               ├── OrderServiceTest.java
│               └── ProductServiceTest.java
├── .gitignore
├── mvnw
├── mvnw.cmd
├── pom.xml
├── Proyecto1_API_export.json
└── README.md
```

## Gestión de errores

La aplicación incorpora excepciones personalizadas para gestionar situaciones como la búsqueda de productos, categorías, pedidos o terminales inexistentes.

`GlobalExceptionHandler` centraliza el tratamiento de excepciones y devuelve respuestas de error estructuradas mediante `ErrorResponse`.

## Estado del proyecto

El backend cuenta con las funcionalidades principales de gestión de productos, categorías, terminales y pedidos, además de estadísticas de ventas.

La API dispone de documentación interactiva, colección de Postman y pruebas automatizadas.

Este backend forma parte de un sistema de gestión de pedidos cuyo frontend está desarrollado con React.
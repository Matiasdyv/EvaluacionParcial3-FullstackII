# SaludTotal – Backend

API REST para la gestión de citas médicas de **SaludTotal** (DSY1104 – Desarrollo Fullstack II, Evaluación Parcial 3).
Se conecta con el frontend en React (repositorio aparte) a través de peticiones HTTP.

## Tecnologías
- Java 21 · Spring Boot 4.2 · Maven
- Spring Data JPA (Hibernate) + MySQL 8
- Spring Security + JWT (jjwt) · contraseñas cifradas con BCrypt
- Swagger / OpenAPI (springdoc)
- JUnit 5 + Mockito (pruebas unitarias)

## Arquitectura
```
React (frontend) → API Gateway (AWS) → EC2 (Spring Boot :8080) → MySQL
```
Código organizado por capas en `cl.duoc.saludtotal`:

| Paquete | Contenido |
|---|---|
| `controller` | Rutas REST (`/api/v1/...`) |
| `service` | Lógica de negocio (reglas de las citas, registro y login) |
| `repository` | Acceso a la base de datos (Spring Data JPA) |
| `model` | Entidades: Rol, Usuario, Especialidad, Medico, Cita, MensajeContacto |
| `dto` | Datos de entrada y salida de la API, con validaciones |
| `security` | JWT: generación, lectura y filtro de autenticación |
| `config` | Seguridad (roles, CORS), datos iniciales y Swagger |
| `exception` | Manejo central de errores (400, 401, 404, 409) |
| `validation` | Validador de RUT chileno |

## Roles
- **PACIENTE**: se registra desde la web; agenda, confirma y cancela sus propias citas.
- **ADMIN**: gestiona valores de consulta, mensajes de contacto, citas y usuarios.

## Cómo ejecutarlo en local
Requisitos: JDK 21 y MySQL 8 corriendo.

1. Crear la base de datos:
   ```sql
   CREATE DATABASE saludtotal CHARACTER SET utf8mb4;
   ```
2. Crear `src/main/resources/application-local.properties` (está en `.gitignore`, no se sube a GitHub):
   ```properties
   spring.datasource.password=TU_CLAVE_DE_MYSQL
   jwt.secret=UNA_CLAVE_LARGA_DE_AL_MENOS_32_CARACTERES
   ```
3. Ejecutar:
   ```powershell
   .\mvnw spring-boot:run
   ```
   Las tablas se crean solas y se cargan los roles, 5 especialidades con sus médicos y el administrador inicial.

Swagger: <http://localhost:8080/swagger-ui/index.html>

## Despliegue (EC2)
Se usa el perfil `prod`, que toma los datos sensibles de variables de entorno:

| Variable | Uso |
|---|---|
| `SPRING_PROFILES_ACTIVE=prod` | Activa `application-prod.properties` |
| `DB_HOST`, `DB_NAME`, `DB_USER`, `DB_PASSWORD` | Conexión a MySQL |
| `JWT_SECRET` | Clave para firmar los tokens |

```bash
./mvnw clean package -DskipTests
java -jar target/*.jar
```

## Pruebas
```powershell
.\mvnw test
```
Cubren la lógica de negocio de citas (horario del médico, doble reserva, propiedad de la cita), registro y login,
generación y validación del token JWT y el validador de RUT.

## Endpoints principales (versión v1)
| Método | Ruta | Acceso |
|---|---|---|
| POST | `/api/v1/auth/registro` | Público |
| POST | `/api/v1/auth/login` | Público |
| GET | `/api/v1/especialidades` | Público |
| GET | `/api/v1/medicos`, `/api/v1/medicos/{id}` | Público |
| PUT | `/api/v1/medicos/{id}/valor-consulta` | ADMIN |
| POST | `/api/v1/mensajes-contacto` | Público |
| GET | `/api/v1/mensajes-contacto` | ADMIN |
| PUT | `/api/v1/mensajes-contacto/{id}/responder` | ADMIN |
| POST | `/api/v1/citas` | Autenticado |
| GET | `/api/v1/citas/mias` | Autenticado |
| PUT | `/api/v1/citas/{id}/confirmar` · `/cancelar` | Autenticado (solo citas propias) |
| GET | `/api/v1/citas` | ADMIN |
| PUT | `/api/v1/citas/{id}/estado` | ADMIN |
| GET | `/api/v1/usuarios` | ADMIN |

Las rutas protegidas esperan el encabezado `Authorization: Bearer <token>`.

# Medical Appointment API

API REST para la gestión de turnos médicos, desarrollada con Java 21 y Spring Boot 3.
El enfoque principal del proyecto es la aplicación de buenas prácticas, testing unitario y
un manejo de excepciones granular.

## Stack

* **Core:** Java 21, Spring Boot 3.3
* **Persistencia:** Spring Data JPA + H2 Database
* **Mapeo:** MapStruct 1.6.3
* **Documentación:** Swagger UI (springdoc-openapi)
* **Testing:** JUnit 5, Mockito

## Decisiones de Diseño & Arquitectura

* **Separación de responsabilidades:** Controladores delgados, lógica de negocio en services.
* **Manejo centralizado de excepciones:** `@RestControllerAdvice` con respuestas estandarizadas (`ApiError`).
* **DTOs separados:** Request/Response DTOs para control de contratos de API.
* **Mapeo automático:** MapStruct para conversión Entity <-> DTO
* **Validaciones de Negocio:**
    * Cancelación/modificación de turnos con 48hs de anticipación mínima.
    * Validación de disponibilidad horaria para evitar solapamientos.
    * Ventana de completado de ±30 minutos del horario programado.
    * Control de estados (RESERVED, COMPLETED, CANCELLED)

## Instalación y Uso

### Prerrequisitos

* JDK 21
* Maven 3.x

### Pasos

1. Clonar el repositorio: 
```bash
    git clone https://github.com/usuario/proyecto.git`
```
2. Ejecutar la aplicación:
```bash
   ./mvnw spring-boot:run
```
3. Acceder a Swagger UI:
```
   http://localhost:8080/swagger-ui/index.html
```

## Documentación API

La API está completamente documentada con Swagger UI.

### Endpoints principales

**Especialidades**
- `GET /api/v1/specialties` - Listar especialidades
- `POST /api/v1/specialties` - Crear especialidad

**Médicos**
- `GET /api/v1/doctors` - Listar médicos
- `POST /api/v1/doctors` - Registrar médico

**Pacientes**
- `GET /api/v1/patients` - Listar pacientes
- `POST /api/v1/patients` - Registrar paciente

**Turnos**
- `POST /api/v1/appointments` - Reservar un turno
- `PUT /api/v1/appointment/{id}` Modificar un turno
- `PUT /api/v1/appointments/{id}/cancel?patientId={id}` Cancelar un turno
- `PUT /api/v1/appointments/{id}/complete` - Completar un turno

## Contacto

**Manuel Foulkes**
- GitHub: [manuFoulkes](https://github.com/manuFoulkes)
- LinkedIn: [Manuel Foulkes](https://linkedin.com/in/manuel-foulkes)
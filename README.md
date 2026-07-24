# 🐹 CuyFix - Incident Manager API

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue.svg)](https://www.docker.com/)
[![Render](https://img.shields.io/badge/Render-Deployed-success.svg)](https://cuyfix-backend.onrender.com)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

**CuyFix** es una API RESTful desarrollada con **Spring Boot 3** y **Java 21** para la gestión integral de incidencias, proyectos, equipos y auditorías de estado al estilo de herramientas de gestión de proyectos como Jira.

El sistema implementa arquitectura multicapa, seguridad basada en **JWT (JSON Web Tokens)** con rotación de tokens, persistencia en **PostgreSQL (Supabase)**, empaquetado con **Docker** en construcción multietapa y despliegue automatizado en **Render**.

---

## 🌐 Enlaces del Proyecto

* **API Base URL:** [https://cuyfix-backend.onrender.com](https://cuyfix-backend.onrender.com)
* **Documentación Swagger / OpenAPI UI:** [https://cuyfix-backend.onrender.com/swagger-ui.html](https://cuyfix-backend.onrender.com/swagger-ui.html)

---

## 🚀 Características Principales

- **Seguridad & Autenticación Stateless:**
  - Registro e inicio de sesión con encriptación de contraseñas mediante **BCrypt**.
  - Autenticación mediante **JWT (Access & Refresh Tokens)**.
  - Revocación y expiración explícita de tokens persistidos en base de datos.
- **Gestión de Proyectos & Equipos:**
  - Creación de proyectos con claves únicas (ej. `INC`, `PROJ`).
  - Sistema de invitación mediante **códigos de invitación alfanuméricos únicos**.
- **Gestión de Incidencias (Issues / Tickets):**
  - Códigos de ticket correlativos automáticos por proyecto (ej. `INC-1`, `INC-2`).
  - Asignación condicionada (solo a miembros activos o creadores del proyecto).
  - Manejo de tipos (`BUG`, `TASK`, `STORY`) y prioridades (`HIGH`, `MEDIUM`, `LOW`).
- **Auditoría e Historial de Cambios:**
  - Trazabilidad automática cada vez que un ticket cambia de estado (ej. de `BACKLOG` a `IN_PROGRESS`), registrando quién realizó el cambio y la fecha/hora exacta.
- **Documentación Viva:**
  - Documentación interactiva autogenerada con **Springdoc OpenAPI / Swagger UI** habilitada para probar Bearer Auth.

---

## 🛠️ Tecnologías y Herramientas

| Categoría | Tecnología / Herramienta |
| :--- | :--- |
| **Lenguaje** | Java 21 (LTS) |
| **Framework Principal** | Spring Boot 3.5.x (Spring Web, Data JPA, Security, Validation) |
| **Base de Datos** | PostgreSQL (Supabase Cloud) / H2 (Pruebas) |
| **Seguridad** | Spring Security 6 + JJWT (Java JWT v0.12.6) |
| **Documentación** | Springdoc OpenAPI 2.5 (Swagger UI) |
| **Pruebas** | JUnit 5 + Mockito 5 |
| **Contenedorización** | Docker (Multi-stage build con `eclipse-temurin:21`) |
| **Despliegue** | Render (Web Service) |

---

## 📂 Estructura del Proyecto

```text
com.grupo01.incident_manager/
├── config/             # Configuración de Spring Security, JWT, CORS y Swagger UI
│   └── security/       # Filtros JWT, proveedor de autenticación y servicios JWT
├── controller/         # Endpoints REST (Auth, Project, Member, Issue, History)
├── dtos/               # Records Java inmutables para peticiones y respuestas HTTP
├── exception/          # Manejador global de excepciones (@RestControllerAdvice)
├── model/              # Entidades JPA (User, Role, Project, Issue, History, UserToken)
├── repository/         # Repositorios JPA con Spring Data
└── service/            # Lógica de negocio y reglas del dominio
```

---

## 📋 Endpoints Principales

### 🔐 Autenticación (`/auth`)
- `POST /auth/register` — Registro de nuevos usuarios y generación inicial de tokens.
- `POST /auth/login` — Autenticación de usuarios y entrega de Access/Refresh Token.
- `POST /auth/refresh` — Renovación de Access Token mediante Refresh Token.

### 📁 Proyectos (`/api/v1/projects`)
- `POST /api/v1/projects` — Crear un nuevo proyecto.
- `GET /api/v1/projects` — Obtener proyectos donde el usuario es autor o miembro.
- `GET /api/v1/projects/{id}` — Detalle de un proyecto por su ID.
- `DELETE /api/v1/projects/{id}` — Eliminar proyecto y sus dependencias en cascada.

### 👥 Miembros del Proyecto (`/api/v1/project-members`)
- `POST /api/v1/project-members/join?inviteCode={code}&idUser={id}` — Unirse a un proyecto mediante código.
- `POST /api/v1/project-members` — Añadir un miembro directamente a un proyecto.
- `GET /api/v1/project-members/project/{idProject}` — Listar miembros de un proyecto.

### 📌 Incidencias / Tickets (`/api/v1/issues`)
- `POST /api/v1/issues` — Crear una nueva incidencia.
- `GET /api/v1/issues/project/{idProject}` — Listar todas las incidencias de un proyecto.
- `PATCH /api/v1/issues/{idIssue}/status?status={NEW_STATUS}` — Cambiar estado de una incidencia.

### 🕒 Auditoría e Historial (`/api/v1/histories`)
- `GET /api/v1/histories/issue/{idIssue}` — Ver el historial de auditoría de un ticket.

---

## 🧪 Pruebas Unitarias

El proyecto cuenta con suites de pruebas unitarias aisladas desarrolladas con **JUnit 5** y **Mockito** para asegurar la calidad en la capa de servicios:

```bash
# Ejecutar las pruebas unitarias del proyecto
./mvnw test -Dtest=AuthServiceTest,IssueServiceTest
```

- **`AuthServiceTest`:** Valida el registro, restricción de correos duplicados y login de usuarios.
- **`IssueServiceTest`:** Valida la generación correlativa de códigos de tickets (`INC-1`), la restricción de asignación a usuarios no miembros y las búsquedas por proyecto.

---

## ⚙️ Configuración de Variables de Entorno

Para ejecutar la aplicación localmente o en producción, configura las siguientes variables en tu archivo `application.yml` o servidor:

| Variable | Descripción |
| :--- | :--- |
| `SPRING_DATASOURCE_PASSWORD` | Contraseña del usuario de la base de datos PostgreSQL |
| `APPLICATION_SECURITY_JWT_SECRET-KEY` | Clave secreta codificada en Base64 (mínimo 256 bits) para firmas HMAC |

---
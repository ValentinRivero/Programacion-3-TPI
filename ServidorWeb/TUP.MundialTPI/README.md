# 🏆 FIFA 2026 - Sistema de Gestión de Entradas (TPI Programación III)

Plataforma oficial para la compra de tickets del Mundial FIFA 2026, desarrollada como Trabajo Práctico Integrador (TP1 - TP4) para la cátedra de Programación III.

El sistema incluye una Single Page Application (SPA) responsiva para el catálogo de partidos, un sistema de autenticación seguro, procesamiento de tickets en línea y un panel de administración completo de acceso restringido.

🚀 **Demo en vivo (Producción):** [https://mundial.irracional.net](https://mundial.irracional.net)

---

## 🏗️ Arquitectura del Sistema
El proyecto implementa una arquitectura N-Capas escalable y orientada a servicios, cumpliendo con todos los requerimientos de la asignatura:

* **Frontend (TP1):** Interfaz SPA construida con HTML5, CSS3 y Vanilla JavaScript. Diseño full-responsive adaptado a móviles (Mobile First), manipulación dinámica del DOM y consumo de APIs mediante `fetch`.
* **Backend y Compra de Tickets (TP2):** Web API RESTful desarrollada en **C# y .NET 10**. Separación clara de responsabilidades (Controladores, Servicios, Repositorios).
* **Autenticación y Seguridad (TP3):** Sistema robusto mediante **JWT (JSON Web Tokens)**. Contraseñas hasheadas en base de datos. Protección de endpoints y renderizado condicional en el frontend según el estado de la sesión.
* **Base de Datos y Despliegue (TP4):** Migración a **PostgreSQL** alojado en Supabase, utilizando Entity Framework Core. Despliegue continuo en **Microsoft Azure App Service** y protección de red / caché gestionada a través de **Cloudflare**.

---

## ⚙️ Credenciales de Evaluación (Panel Admin)
Por motivos de seguridad y buenas prácticas, las credenciales de la cuenta con rol `admin` (necesarias para evaluar los requerimientos de ABM del TP4) **no se publican en este repositorio**. 

Las mismas serán provistas al equipo docente a través del Campus Virtual o durante el coloquio de presentación.

---

## 🚀 Instalación y Ejecución Local
Para auditar y ejecutar el código fuente en un entorno local:

### Requisitos Previos
- .NET 10.0 SDK
- Editor de código (Visual Studio 2026 Community / VS Code)

### Pasos de ejecución
1. Clonar el repositorio y navegar a la raíz de la solución.
2. Como el proyecto utiliza credenciales seguras de PostgreSQL y llaves JWT en producción, es necesario configurar los secretos locales (`User Secrets`) para compilar. Ejecutar en la terminal de la carpeta `TUP.MundialTPI.WebApiApp`:
   ```bash
   dotnet user-secrets set "ConnectionStrings:DefaultConnection" "connection string"
   dotnet user-secrets set "Jwt__Key" "clave"

## Lista de Endpoints Principales

### Auth
- `POST /api/auth/register`: Registro de nuevos clientes. El rol se asigna forzosamente como `user`.
- `POST /api/auth/login`: Autenticación, validación BCrypt y despacho de JWT.

### Público y Transaccional
- `GET /api/partidos`: Catálogo en vivo de próximos encuentros mundiales.
- `GET /api/partidos/{id}`: Consulta granular de un partido específico.
- `POST /api/tickets`: Procesamiento de un nuevo ticket de compra.
- `GET /api/tickets/mis-tickets`: Historial individual de compras ligadas al Token JWT en sesión. (Requiere Autenticación).

### Panel de Administración (Requieren rol `admin`)
- `GET /api/admin/partidos`: Listado administrativo completo de partidos.
- `POST /api/admin/partidos`: Creación de partido con validación de modelo estricta.
- `PUT /api/admin/partidos/{id}`: Edición integral de partido.
- `DELETE /api/admin/partidos/{id}`: Borrado de partido (Detección de integridad referencial Tickets).
- `GET /api/admin/usuarios`: Listado maestro de cuentas y correos registrados.
- `GET /api/admin/tickets`: Listado centralizado de ventas.

# FIFA 2026 - Gestión de Entradas (TPI Programación III)

Plataforma oficial para la compra de tickets del Mundial FIFA 2026, desarrollada como Trabajo Práctico Integrador (TP1 - TP4) para la cátedra de Programación III de la TUP. 

El sistema incluye una Single Page Application (SPA) pública de catálogo de eventos deportivos, sistema de registro y login de usuarios, simulación de compra de tickets en línea y un panel de administración completo de acceso restringido.

## Requisitos del Sistema
- **.NET SDK 10.0** (Comprobado: v10.0.201)
- Navegador Web Moderno (Soporte pleno de HTML5/CSS3/Vanilla JS).

## Arquitectura y Base de Datos
El proyecto implementa una arquitectura en N-Capas que separa Entidades, Lógica de Negocio (Servicios), Acceso a Datos (EF) y la WebAPI pública.

Se utiliza **Entity Framework Core con SQLite** para una configuración de persistencia ligera. El archivo de base de datos (`mundial.db`) se genera y asocia automáticamente en el entorno backend al aplicar las migraciones de EF. Si es necesario crearlo o actualizarlo manualmente desde cero, ejecuta:
```bash
dotnet ef database update --project TUP.MundialTPI.DatosEF --startup-project TUP.MundialTPI.WebApiApp
```

## Instrucciones de Ejecución
1. Abrir la terminal y ubicarse en la carpeta raíz de la solución (`TUP.MundialTPI`).
2. Ejecutar el proyecto WebApiApp indicando la ruta:
   ```bash
   dotnet run --project TUP.MundialTPI.WebApiApp
   ```
   *(Alternativa: Navegar con `cd` hacia la carpeta `TUP.MundialTPI.WebApiApp` y allí ejecutar simplemente `dotnet run`)*
3. Abrir el navegador en la URL local de la terminal (usualmente `http://localhost:5021` o `https://localhost:5001`).
   - Se presentará directamente la interfaz web (`index.html`) provista por `UseDefaultFiles()`.
   - La documentación Swagger de la API está disponible en la ruta `/swagger`.

## Credenciales de Prueba (Semilla)
Al generarse la base de datos, se inyecta un usuario semilla con **rol de administrador** y contraseña encriptada (BCrypt) para que puedan realizarse pruebas del **Panel Admin**:
- **Email:** `admin@mundial.com`
- **Contraseña:** `admin123`

---

## Resumen de Trabajos Prácticos (TPs) Implementados

- **TP1 (Frontend Web - Catálogo):** 
  - Diseño visual nativo en HTML/CSS/JS (sin librerías).
  - Estética "glassmorphism" elegante.
  - Generación de cards dinámicas del catálogo consumiendo datos estructurados (originalmente Mock, luego API).

- **TP2 (Backend WebAPI y Compra):** 
  - Integración EF Core (SQLite) y N-Capas.
  - Creación de entidades base del dominio (`Partido`, `Estadio`, `Ticket`).
  - Refactorización del código Javascript del front-end para consumir las rutas reales `/api/partidos` y `/api/tickets`.

- **TP3 (Autenticación JWT y Modales):** 
  - Registro seguro de clientes (contraseñas hasheadas con BCrypt).
  - Generación, inyección y validación de tokens JWT.
  - Implementación de Single Page Application: Flujos de Modales asíncronos y sistema que detecta sesiones expiradas reanudando las compras luego del login transparente.

- **TP4 (Panel de Administración y Restricciones):** 
  - Manejo integral de Roles (`user` vs `admin`). Los roles no son inyectables vía cliente (DTo seguro).
  - Tablas visuales e interactivas en SPA que permiten administrar Partidos (CRUD completo), ver el padrón de Usuarios y un historial de todos los Tickets vendidos del sistema.
  - **Lógicas defensivas Backend:** Validaciones con DataAnnotations, protección `403 Forbidden` verificada, y restricción robusta anti-borrado que devuelve `409 Conflict` si se intenta borrar un evento que ya tiene tickets vendidos.
  - **Portal Usuario - "Mis Tickets":** Incorporación del perfil final de usuario con un visor privado de compras exitosas.

---

## Lista de Endpoints Principales

### Auth
- `POST /api/auth/register`: Registro de nuevos clientes. El rol se asigna forzosamente como `user`.
- `POST /api/auth/login`: Autenticación, validación BCrypt y despacho de JWT.

### Público y Transaccional
- `GET /api/partidos`: Catálogo en vivo de próximos encuentros mundiales.
- `GET /api/partidos/{id}`: Consulta granular de un partido específico.
- `POST /api/tickets`: Procesamiento de un nuevo ticket de compra. (Requiere Autenticación de Usuario).
- `GET /api/tickets/mis-tickets`: Historial individual de compras ligadas al Token JWT en sesión. (Requiere Autenticación).

### Panel de Administración (Requieren rol `admin`)
- `GET /api/admin/partidos`: Listado administrativo completo de partidos.
- `POST /api/admin/partidos`: Creación de partido con validación de modelo estricta.
- `PUT /api/admin/partidos/{id}`: Edición integral de partido.
- `DELETE /api/admin/partidos/{id}`: Borrado de partido (Detección de integridad referencial Tickets).
- `GET /api/admin/usuarios`: Listado maestro de cuentas y correos registrados.
- `GET /api/admin/tickets`: Listado centralizado de ventas.

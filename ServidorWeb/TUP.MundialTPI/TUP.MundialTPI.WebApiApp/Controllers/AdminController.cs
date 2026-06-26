using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using TUP.MundialTPI.DatosEF;
using TUP.MundialTPI.Entidades;
using TUP.MundialTPI.Entidades.DTOs;
using TUP.MundialTPI.Negocio;
using TUP.MundialTPI.Negocio.Interfaces;

namespace TUP.MundialTPI.WebApiApp.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    [Authorize(Roles = "admin")]
    public class AdminController : ControllerBase
    {
        private readonly IPartidoService _partidoService;
        private readonly IAuthService _authService;
        private readonly ITicketService _ticketService;
        private readonly AppDbContext _context;

        public AdminController(IPartidoService partidoService, IAuthService authService, ITicketService ticketService, AppDbContext context)
        {
            _partidoService = partidoService;
            _authService = authService;
            _ticketService = ticketService;
            _context = context;
        }

        // --- DASHBOARD ---
        [HttpGet("dashboard-stats")]
        public async Task<IActionResult> GetDashboardStats()
        {
            var totalUsuarios = await _context.Usuarios.CountAsync();
            var usuariosSuspendidos = await _context.Usuarios.CountAsync(u => !u.Activo);

            var ticketsValidos = await _context.Tickets.CountAsync(t => t.Activo);
            var ticketsAnulados = await _context.Tickets.CountAsync(t => !t.Activo);
            var totalRecaudado = await _context.Tickets.Where(t => t.Activo).SumAsync(t => (decimal?)t.Precio) ?? 0;

            var partidosAgotados = await _context.Partidos.CountAsync(p => p.EntradasDisponibles == 0 && p.Estado != "Oculto");

            var partidosPublicos = await _context.Partidos.Where(p => p.Estado != "Oculto").ToListAsync();
            var capacidadTotal = partidosPublicos.Sum(p => p.EntradasMaximas > 0 ? p.EntradasMaximas : p.EntradasDisponibles);
            var disponiblesTotal = partidosPublicos.Sum(p => p.EntradasDisponibles);
            var porcentajeOcupacion = capacidadTotal > 0 ? Math.Round((double)(capacidadTotal - disponiblesTotal) / capacidadTotal * 100, 1) : 0;

            return Ok(new
            {
                UsuariosActivos = totalUsuarios - usuariosSuspendidos,
                TicketsVendidos = ticketsValidos,
                TicketsAnulados = ticketsAnulados,
                Recaudacion = totalRecaudado,
                PartidosAgotados = partidosAgotados,
                Ocupacion = porcentajeOcupacion
            });
        }

        // --- PARTIDOS ---

        [HttpGet("partidos")]
        public async Task<IActionResult> GetPartidosAdmin([FromQuery] string? search, [FromQuery] int page = 1, [FromQuery] int pageSize = 15)
        {
            var query = _context.Partidos.AsQueryable();

            if (!string.IsNullOrEmpty(search))
            {
                search = search.ToLower();
                bool esNumero = int.TryParse(search, out int searchId);

                if (esNumero)
                {
                    query = query.Where(p => p.Id == searchId);
                }
                else
                {
                    query = query.Where(p => p.EquipoLocal.ToLower().Contains(search) ||
                                             p.EquipoVisitante.ToLower().Contains(search) ||
                                             p.Fase.ToLower().Contains(search));
                }
            }

            var total = await query.CountAsync();
            var items = await query.OrderByDescending(p => p.Id)
                                   .Skip((page - 1) * pageSize)
                                   .Take(pageSize)
                                   .ToListAsync();

            return Ok(new { items, total });
        }

        [HttpPost("partidos")]
        public async Task<IActionResult> CrearPartido([FromBody] CrearPartidoDTO dto)
        {
            if (dto.EquipoLocal.Trim().ToLower() == dto.EquipoVisitante.Trim().ToLower())
                return BadRequest(new { mensaje = "El equipo local y visitante no pueden ser el mismo país." });

            if (dto.FechaHora.Value < DateTime.UtcNow)
                return BadRequest(new { mensaje = "No se pueden programar partidos en el pasado." });

            var estadioExiste = await _context.Estadios.AnyAsync(e => e.Id == dto.EstadioId.Value);
            if (!estadioExiste)
                return BadRequest(new { mensaje = "El ID del estadio indicado no existe en la base de datos." });

            var nuevoPartido = new Partido
            {
                EquipoLocal = dto.EquipoLocal.Trim(),
                EquipoVisitante = dto.EquipoVisitante.Trim(),
                Fase = dto.Fase.Trim(),
                FechaHora = dto.FechaHora.Value.ToUniversalTime(),
                EntradasMaximas = dto.EntradasDisponibles,
                EntradasDisponibles = dto.EntradasDisponibles,
                EstadioId = dto.EstadioId.Value,
                Estado = "Oculto"
            };

            _context.Partidos.Add(nuevoPartido);
            await _context.SaveChangesAsync();

            return Ok(new { mensaje = "Partido programado correctamente.", id = nuevoPartido.Id });
        }

        [HttpPut("partidos/{id}")]
        public async Task<IActionResult> EditarPartidoTotal(int id, [FromBody] EditarPartidoDTO dto)
        {
            var partido = await _context.Partidos.FindAsync(id);
            if (partido == null) return NotFound(new { mensaje = "Partido no encontrado." });

            partido.EquipoLocal = dto.EquipoLocal.Trim();
            partido.EquipoVisitante = dto.EquipoVisitante.Trim();
            partido.Fase = dto.Fase.Trim();
            partido.FechaHora = dto.FechaHora.Value.ToUniversalTime();
            partido.EntradasDisponibles = dto.EntradasDisponibles;
            partido.EstadioId = dto.EstadioId.Value;
            partido.Estado = dto.Estado;

            await _context.SaveChangesAsync();
            return Ok(new { mensaje = "Partido actualizado correctamente." });
        }

        [HttpDelete("partidos/{id}")]
        public async Task<IActionResult> EliminarPartido(int id)
        {
            var partido = await _context.Partidos.FindAsync(id);
            if (partido == null) return NotFound(new { mensaje = "Partido no encontrado." });

            var tieneTicketsActivos = await _context.Tickets.AnyAsync(t => t.PartidoId == id && t.Activo);

            if (tieneTicketsActivos)
            {
                return BadRequest(new { mensaje = "No se puede eliminar: El partido tiene entradas válidas vendidas. Suspendelo, o anulá los tickets primero." });
            }

            var ticketsInactivos = await _context.Tickets.Where(t => t.PartidoId == id).ToListAsync();
            if (ticketsInactivos.Any())
            {
                _context.Tickets.RemoveRange(ticketsInactivos);
            }

            _context.Partidos.Remove(partido);

            await _context.SaveChangesAsync();

            return Ok(new { mensaje = "Partido eliminado definitivamente." });
        }

        // --- USUARIOS ---

        [HttpGet("usuarios")]
        public async Task<IActionResult> GetUsuariosAdmin([FromQuery] string? search, [FromQuery] int page = 1, [FromQuery] int pageSize = 15)
        {
            var query = _context.Usuarios.AsQueryable();

            if (!string.IsNullOrEmpty(search))
            {
                search = search.ToLower();
                bool esNumero = int.TryParse(search, out int searchId);

                query = query.Where(u => (esNumero && u.Id == searchId) ||
                                         (u.Nombre.ToLower().Contains(search.ToLower())) ||
                                         (u.Email.ToLower().Contains(search.ToLower())));
            }

            var total = await query.CountAsync();
            var items = await query.OrderByDescending(u => u.Id).Skip((page - 1) * pageSize).Take(pageSize)
                .Select(u => new { id = u.Id, nombre = u.Nombre, email = u.Email, rol = u.Rol, activo = u.Activo })
                .ToListAsync();

            return Ok(new { items, total });
        }

        // --- TICKETS ---

        [HttpGet("tickets")]
        public async Task<IActionResult> GetTicketsAdmin([FromQuery] string? search, [FromQuery] int page = 1, [FromQuery] int pageSize = 15)
        {
            var query = _context.Tickets
                .Include(t => t.Partido)
                .Include(t => t.Usuario)
                .AsQueryable();

            if (!string.IsNullOrEmpty(search))
            {
                search = search.ToLower();
                bool esNumero = int.TryParse(search, out int searchId);

                query = query.Where(t => (esNumero && t.Id == searchId) ||
                                         (t.Usuario != null && t.Usuario.Email.ToLower().Contains(search)) ||
                                         (t.Partido != null && t.Partido.EquipoLocal.ToLower().Contains(search)));
            }

            var total = await query.CountAsync();

            var items = await query.OrderByDescending(t => t.Id).Skip((page - 1) * pageSize).Take(pageSize)
                .Select(t => new {
                    id = t.Id,
                    partidoInfo = t.Partido != null ? t.Partido.EquipoLocal + " vs " + t.Partido.EquipoVisitante : "Partido Borrado",
                    usuarioEmail = t.Usuario != null ? t.Usuario.Email : "Usuario Borrado",
                    tipoEntrada = t.CategoriaId,
                    precio = t.Precio,
                    activo = t.Activo
                }).ToListAsync();

            return Ok(new { items, total });
        }

        [HttpPut("tickets/{id}/toggle")]
        public async Task<IActionResult> AnularTicket(int id)
        {
            var ticket = await _context.Tickets
                .Include(t => t.Partido)
                .FirstOrDefaultAsync(t => t.Id == id);

            if (ticket == null) return NotFound("Ticket no encontrado");
            if (!ticket.Activo) return BadRequest("El ticket ya fue anulado anteriormente.");

            ticket.Activo = false;

            ticket.Partido.EntradasDisponibles += 1;

            await _context.SaveChangesAsync();
            return Ok(new { mensaje = "Ticket anulado y stock restaurado" });
        }

        // SUSPENDER / ACTIVAR USUARIO
        [HttpPut("usuarios/{id}/toggle-estado")]
        public async Task<IActionResult> ToggleUsuarioEstado(int id)
        {
            var usuario = await _context.Usuarios.FindAsync(id);
            if (usuario == null) return NotFound(new { mensaje = "Usuario no encontrado" });

            // No dejamos que el admin se suspenda a sí mismo por error
            if (usuario.Rol == "admin") return BadRequest(new { mensaje = "No puedes suspender a otro administrador." });

            usuario.Activo = !usuario.Activo; // Invierte el estado
            await _context.SaveChangesAsync();
            return Ok(new { mensaje = usuario.Activo ? "Usuario reactivado" : "Usuario suspendido" });
        }

        // CAMBIAR ESTADO ESPECÍFICO DEL PARTIDO
        [HttpPut("partidos/{id}/estado")]
        public async Task<IActionResult> CambiarEstadoPartido(int id, [FromQuery] string nuevoEstado)
        {
            var partido = await _context.Partidos.FindAsync(id);
            if (partido == null) return NotFound(new { mensaje = "Partido no encontrado" });

            // Validar que el estado sea uno de los permitidos para evitar basura en la BD
            var estadosPermitidos = new[] { "Activo", "En Juego", "Finalizado", "Suspendido" };
            if (!estadosPermitidos.Contains(nuevoEstado))
                return BadRequest(new { mensaje = "Estado no válido." });

            partido.Estado = nuevoEstado;
            await _context.SaveChangesAsync();

            return Ok(new { mensaje = $"Estado del partido actualizado a {nuevoEstado}" });
        }

    }
}

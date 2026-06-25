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
            var totalTickets = await _context.Tickets.CountAsync();
            var totalRecaudado = await _context.Tickets.SumAsync(t => (decimal?)t.Precio) ?? 0;
            var partidosAgotados = await _context.Partidos.CountAsync(p => p.EntradasDisponibles == 0);

            return Ok(new
            {
                Usuarios = totalUsuarios,
                Tickets = totalTickets,
                Recaudacion = totalRecaudado,
                Agotados = partidosAgotados
            });
        }

        // --- PARTIDOS ---

        [HttpGet("partidos")]
        public async Task<IActionResult> GetPartidos()
        {
            var partidos = await _partidoService.GetAllAsync();
            return Ok(partidos);
        }

        [HttpPost("partidos")]
        public async Task<IActionResult> CrearPartido([FromBody] PartidoDTO dto)
        {
            if (!ModelState.IsValid)
                return BadRequest(ModelState);

            try
            {
                var partido = await _partidoService.CrearAsync(dto);
                return CreatedAtAction(nameof(GetPartidos), new { id = partido.Id }, partido);
            }
            catch (InvalidOperationException ex)
            {
                return BadRequest(new { mensaje = ex.Message });
            }
            catch (Exception ex)
            {
                return StatusCode(500, new { mensaje = "Error interno del servidor", detalle = ex.Message });
            }
        }

        [HttpPut("partidos/{id}")]
        public async Task<IActionResult> EditarPartido(int id, [FromBody] PartidoDTO dto)
        {
            if (!ModelState.IsValid)
                return BadRequest(ModelState);

            try
            {
                var partido = await _partidoService.EditarAsync(id, dto);
                return Ok(partido);
            }
            catch (KeyNotFoundException ex)
            {
                return NotFound(new { mensaje = ex.Message });
            }
            catch (InvalidOperationException ex)
            {
                return BadRequest(new { mensaje = ex.Message });
            }
            catch (Exception ex)
            {
                return StatusCode(500, new { mensaje = "Error interno del servidor", detalle = ex.Message });
            }
        }

        [HttpDelete("partidos/{id}")]
        public async Task<IActionResult> EliminarPartido(int id)
        {
            try
            {
                await _partidoService.EliminarAsync(id);
                return Ok(new { mensaje = "Partido eliminado exitosamente" });
            }
            catch (KeyNotFoundException ex)
            {
                return NotFound(new { mensaje = ex.Message });
            }
            catch (InvalidOperationException ex)
            {
                return Conflict(new { mensaje = ex.Message });
            }
            catch (Exception ex)
            {
                return StatusCode(500, new { mensaje = "Error interno del servidor", detalle = ex.Message });
            }
        }

        // --- USUARIOS ---

        [HttpGet("usuarios")]
        public async Task<IActionResult> GetUsuarios()
        {
            var usuarios = await _authService.GetAllUsuariosAsync();
            return Ok(usuarios);
        }

        // --- TICKETS ---

        [HttpGet("tickets")]
        public async Task<IActionResult> GetTickets()
        {
            var tickets = await _ticketService.GetAllTicketsAsync();
            
            // Proyectamos para evitar ciclos de referencia infinitos si el frontend no los maneja
            var resultado = tickets.Select(t => new
            {
                t.Id,
                t.PartidoId,
                PartidoInfo = $"{t.Partido.EquipoLocal} vs {t.Partido.EquipoVisitante}",
                t.UsuarioId,
                UsuarioEmail = t.Usuario.Email,
                t.TipoEntrada,
                t.Precio,
                t.FechaCompra
            });

            return Ok(resultado);
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

        // SUSPENDER / ACTIVAR PARTIDO
        [HttpPut("partidos/{id}/toggle-estado")]
        public async Task<IActionResult> TogglePartidoEstado(int id)
        {
            var partido = await _context.Partidos.FindAsync(id);
            if (partido == null) return NotFound(new { mensaje = "Partido no encontrado" });

            partido.Estado = partido.Estado == "Activo" ? "Suspendido" : "Activo";
            await _context.SaveChangesAsync();
            return Ok(new { mensaje = $"Partido {partido.Estado.ToLower()}" });
        }

    }
}

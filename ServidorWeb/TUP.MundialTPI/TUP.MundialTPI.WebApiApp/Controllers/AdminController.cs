using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using TUP.Mundial.Entidades;
using TUP.Mundial.Negocio;

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

        public AdminController(IPartidoService partidoService, IAuthService authService, ITicketService ticketService)
        {
            _partidoService = partidoService;
            _authService = authService;
            _ticketService = ticketService;
        }

        // --- PARTIDOS ---

        [HttpGet("partidos")]
        public async Task<IActionResult> GetPartidos()
        {
            var partidos = await _partidoService.GetAllAsync();
            return Ok(partidos);
        }

        [HttpPost("partidos")]
        public async Task<IActionResult> CrearPartido([FromBody] PartidoDto dto)
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
        public async Task<IActionResult> EditarPartido(int id, [FromBody] PartidoDto dto)
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
    }
}

using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Authorization;
using System.Security.Claims;
using TUP.MundialTPI.Entidades;
using TUP.MundialTPI.Negocio;
using TUP.MundialTPI.Negocio.Interfaces;
using TUP.MundialTPI.Entidades.DTOs;

namespace TUP.MundialTPI.WebApiApp.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class TicketsController : ControllerBase
    {
        private readonly ITicketService _ticketService;

        public TicketsController(ITicketService ticketService)
        {
            _ticketService = ticketService;
        }

        [Authorize]
        [HttpPost("comprar")]
        public async Task<IActionResult> Comprar([FromBody] ComprarTicketDTO dto)
        {
            var claimId = User.FindFirst(System.Security.Claims.ClaimTypes.NameIdentifier)?.Value;
            if (string.IsNullOrEmpty(claimId)) return Unauthorized();

            try
            {
                var tickets = await _ticketService.ComprarAsync(int.Parse(claimId), dto);
                return Ok(new
                {
                    mensaje = $"¡Compra exitosa! Adquiriste {tickets.Count} entrada(s).",
                    ticketsIds = tickets.Select(t => t.Id).ToList()
                });
            }
            catch (Exception ex) { return BadRequest(new { mensaje = ex.Message }); }
        }

        [Authorize]
        [HttpGet("mis-tickets")]
        public async Task<ActionResult> GetMisTickets([FromQuery] int pagina = 1, [FromQuery] int cantidad = 15)
        {
            var claimId = User.FindFirst(System.Security.Claims.ClaimTypes.NameIdentifier)?.Value;
            if (string.IsNullOrEmpty(claimId)) return Unauthorized();
            if (pagina < 1) pagina = 1;
            if (cantidad < 1) cantidad = 15;

            var tickets = await _ticketService.GetMisTicketsAsync(int.Parse(claimId), pagina, cantidad);

            var response = tickets.Select(t => new {
                id = t.Id,
                tipoEntrada = t.CategoriaId,
                activo = t.Activo,
                partido = new
                {
                    equipoLocal = t.Partido.EquipoLocal,
                    equipoVisitante = t.Partido.EquipoVisitante,
                    fase = t.Partido.Fase,
                    fechaHora = t.Partido.FechaHora,
                    estadio = t.Partido.Estadio != null ? new
                    {
                        nombre = t.Partido.Estadio.Nombre,
                        ciudad = t.Partido.Estadio.Ciudad
                    } : null
                }
            });

            return Ok(response);
        }
    }
}

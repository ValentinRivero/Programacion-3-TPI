using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Authorization;
using System.Security.Claims;
using TUP.Mundial.Entidades;
using TUP.Mundial.Negocio;

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
        [HttpPost]
        public async Task<IActionResult> Comprar([FromBody] ComprarTicketDto dto)
        {
            try
            {
                var userIdClaim = User.FindFirstValue(ClaimTypes.NameIdentifier);
                if (string.IsNullOrEmpty(userIdClaim)) return Unauthorized("Token inválido.");
                
                int usuarioId = int.Parse(userIdClaim);
                var ticket = await _ticketService.ComprarAsync(usuarioId, dto);
                return CreatedAtAction(nameof(Comprar), new { id = ticket.Id }, ticket);
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }

        [Authorize]
        [HttpGet("mis-tickets")]
        public async Task<IActionResult> GetMisTickets()
        {
            try
            {
                var userIdClaim = User.FindFirstValue(ClaimTypes.NameIdentifier);
                if (string.IsNullOrEmpty(userIdClaim)) return Unauthorized("Token inválido.");
                
                int usuarioId = int.Parse(userIdClaim);
                var tickets = await _ticketService.GetMisTicketsAsync(usuarioId);
                
                var resultado = tickets.Select(t => new
                {
                    t.Id,
                    Partido = $"{t.Partido.EquipoLocal} vs {t.Partido.EquipoVisitante}",
                    Estadio = $"{t.Partido.Estadio.Nombre}, {t.Partido.Estadio.Ciudad}",
                    FechaPartido = t.Partido.FechaHora,
                    t.TipoEntrada,
                    t.Precio,
                    t.FechaCompra
                });

                return Ok(resultado);
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }
    }
}

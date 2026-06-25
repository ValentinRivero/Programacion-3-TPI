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
        [HttpPost]
        public async Task<IActionResult> Comprar([FromBody] ComprarTicketDTO dto)
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
        public async Task<ActionResult> GetMisTickets()
        {
            var claimId = User.FindFirst(System.Security.Claims.ClaimTypes.NameIdentifier)?.Value;

            if (claimId == null)
            {
                return Unauthorized(new { mensaje = "Token inválido o sin ID de usuario." });
            }

            var tickets = await _ticketService.GetMisTicketsAsync(int.Parse(claimId));
            return Ok(tickets);
        }
    }
}

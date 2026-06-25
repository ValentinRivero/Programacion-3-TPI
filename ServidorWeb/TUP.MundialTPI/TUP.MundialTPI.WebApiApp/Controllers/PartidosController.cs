using Microsoft.AspNetCore.Mvc;
using TUP.MundialTPI.Entidades;
using TUP.MundialTPI.Negocio;
using TUP.MundialTPI.Negocio.Interfaces;

namespace TUP.MundialTPI.WebApiApp.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class PartidosController : ControllerBase
    {
        private readonly IPartidoService _partidoService;

        public PartidosController(IPartidoService partidoService)
        {
            _partidoService = partidoService;
        }

        [HttpGet]
        public async Task<ActionResult<IEnumerable<Partido>>> GetAll(
    [FromQuery] string? equipo,
    [FromQuery] string? fase,
    [FromQuery] int pagina = 1,
    [FromQuery] int cantidad = 10)
        {
            if (pagina < 1) pagina = 1;
            if (cantidad < 1 || cantidad > 50) cantidad = 15;

            var partidos = await _partidoService.GetAllAsync(equipo, fase, pagina, cantidad);

            return Ok(partidos);
        }

        [HttpGet("{id}")]
        public async Task<IActionResult> GetById(int id)
        {
            var partido = await _partidoService.GetByIdAsync(id);
            if (partido == null) return NotFound();
            return Ok(partido);
        }
    }
}

using Microsoft.AspNetCore.Mvc;
using TUP.Mundial.Negocio;

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
        public async Task<IActionResult> GetAll()
        {
            var partidos = await _partidoService.GetAllAsync();
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

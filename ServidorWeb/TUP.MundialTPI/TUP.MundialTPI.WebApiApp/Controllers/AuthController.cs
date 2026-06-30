using Microsoft.AspNetCore.Mvc;
using TUP.MundialTPI.Entidades;
using TUP.MundialTPI.Negocio;
using TUP.MundialTPI.Negocio.Interfaces;
using TUP.MundialTPI.Entidades.DTOs;

namespace TUP.MundialTPI.WebApiApp.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class AuthController : ControllerBase
    {
        private readonly IAuthService _authService;

        public AuthController(IAuthService authService)
        {
            _authService = authService;
        }

        [HttpPost("register")]
        public async Task<IActionResult> Register([FromBody] RegisterDTO dto)
        {
            try
            {
                var user = await _authService.RegisterAsync(dto);
                return Created("", new { id = user.Id, nombre = user.Nombre, email = user.Email });
            }
            catch (Exception ex)
            {
                return BadRequest(new { message = ex.Message });
            }
        }

        [HttpPost("login")]
        public async Task<IActionResult> Login([FromBody] LoginDTO dto)
        {
            try
            {
                // Un solo viaje a la base de datos
                var resultado = await _authService.LoginAsync(dto);

                return Ok(new
                {
                    token = resultado.Token,
                    user = new
                    {
                        id = resultado.User.Id,
                        nombre = resultado.User.Nombre,
                        email = resultado.User.Email,
                        rol = resultado.User.Rol
                    }
                });
            }
            catch (Exception ex)
            {
                return Unauthorized(new { message = ex.Message });
            }
        }
    }
}

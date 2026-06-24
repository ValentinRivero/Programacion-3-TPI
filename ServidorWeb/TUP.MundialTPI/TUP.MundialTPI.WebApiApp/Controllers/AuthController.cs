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
                var token = await _authService.LoginAsync(dto);
                var user = await _authService.GetUserByEmailAsync(dto.Email);
                return Ok(new { 
                    token, 
                    user = new { id = user!.Id, nombre = user.Nombre, email = user.Email, rol = user.Rol } 
                });
            }
            catch (Exception ex)
            {
                return Unauthorized(new { message = ex.Message });
            }
        }
    }
}

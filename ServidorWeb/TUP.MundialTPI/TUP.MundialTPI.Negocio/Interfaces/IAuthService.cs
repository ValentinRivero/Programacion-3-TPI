using System;
using System.Collections.Generic;
using System.Text;
using TUP.MundialTPI.Entidades;
using TUP.MundialTPI.Entidades.DTOs;

namespace TUP.MundialTPI.Negocio.Interfaces
{
    public interface IAuthService
    {
        Task<Usuario> RegisterAsync(RegisterDTO dto);
        Task<(string Token, Usuario User)> LoginAsync(LoginDTO dto);
        Task<Usuario?> GetUserByEmailAsync(string email);
        Task<List<Usuario>> GetAllUsuariosAsync();
    }
}

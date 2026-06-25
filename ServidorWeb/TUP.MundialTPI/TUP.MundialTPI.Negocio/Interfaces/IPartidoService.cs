using System;
using System.Collections.Generic;
using System.Text;
using TUP.MundialTPI.Entidades;
using TUP.MundialTPI.Entidades.DTOs;

namespace TUP.MundialTPI.Negocio.Interfaces
{
    public interface IPartidoService
    {
        Task<IEnumerable<Partido>> GetAllAsync(string? equipo = null, string? fase = null, int pagina = 1, int cantidad = 15);
        Task<Partido?> GetByIdAsync(int id);
        Task<Partido> CrearAsync(PartidoDTO dto);
        Task<Partido> EditarAsync(int id, PartidoDTO dto);
        Task EliminarAsync(int id);
    }
}

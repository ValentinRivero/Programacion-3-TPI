using System;
using System.Collections.Generic;
using System.Text;
using TUP.MundialTPI.Entidades;
using TUP.MundialTPI.Entidades.DTOs;

namespace TUP.MundialTPI.Negocio.Interfaces
{
    public interface ITicketService
    {
        Task<List<Ticket>> ComprarAsync(int usuarioId, ComprarTicketDTO dto);
        Task<List<Ticket>> GetAllTicketsAsync();
        Task<IEnumerable<Ticket>> GetMisTicketsAsync(int usuarioId);
    }
}

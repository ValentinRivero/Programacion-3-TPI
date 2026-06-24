using System;
using System.Collections.Generic;
using System.Text;
using TUP.MundialTPI.DatosEF;
using TUP.MundialTPI.Entidades;
using TUP.MundialTPI.Entidades.DTOs;
using TUP.MundialTPI.Negocio.Interfaces;
using Microsoft.EntityFrameworkCore;

namespace TUP.MundialTPI.Negocio
{
    public class TicketService : ITicketService
    {
        private readonly AppDbContext _context;
        public TicketService(AppDbContext context) => _context = context;

        public async Task<Ticket> ComprarAsync(int usuarioId, ComprarTicketDTO dto)
        {
            var partido = await _context.Partidos.FindAsync(dto.PartidoId);
            if (partido == null) throw new Exception("Partido no encontrado");
            if (partido.EntradasDisponibles < dto.Cantidad) throw new Exception("No hay suficientes entradas");

            partido.EntradasDisponibles -= dto.Cantidad;

            var ticket = new Ticket
            {
                PartidoId = dto.PartidoId,
                UsuarioId = usuarioId,
                TipoEntrada = dto.TipoEntrada,
                Precio = 250m * dto.Cantidad, // Precio simulado según el PRD
                FechaCompra = DateTime.UtcNow
            };

            _context.Tickets.Add(ticket);
            await _context.SaveChangesAsync();
            return ticket;
        }

        public async Task<List<Ticket>> GetAllTicketsAsync()
        {
            return await _context.Tickets
                .Include(t => t.Partido)
                .Include(t => t.Usuario)
                .ToListAsync();
        }

        public async Task<List<Ticket>> GetMisTicketsAsync(int usuarioId)
        {
            return await _context.Tickets
                .Include(t => t.Partido)
                .ThenInclude(p => p.Estadio)
                .Where(t => t.UsuarioId == usuarioId)
                .ToListAsync();
        }
    }
}

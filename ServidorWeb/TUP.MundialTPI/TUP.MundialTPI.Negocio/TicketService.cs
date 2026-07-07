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

        public async Task<List<Ticket>> ComprarAsync(int usuarioId, ComprarTicketDTO dto)
        {
            var partido = await _context.Partidos.FindAsync(dto.PartidoId);
            if (partido == null || partido.Estado != "Activo") throw new Exception("Partido no disponible.");
            if (partido.EntradasDisponibles < dto.Cantidad) throw new Exception("Stock insuficiente.");

            var categoria = await _context.CategoriasEntradas.FindAsync(dto.CategoriaId);
            if (categoria == null || !categoria.Activo) throw new Exception("Categoría no válida.");

            var nuevosTickets = new List<Ticket>();

            for (int i = 0; i < dto.Cantidad; i++)
            {
                nuevosTickets.Add(new Ticket
                {
                    UsuarioId = usuarioId,
                    PartidoId = dto.PartidoId,
                    CategoriaId = categoria.Id,
                    Precio = categoria.PrecioActual,
                    FechaCompra = DateTime.UtcNow,
                    Activo = true
                });
            }

            partido.EntradasDisponibles -= dto.Cantidad;

            _context.Tickets.AddRange(nuevosTickets);
            await _context.SaveChangesAsync();

            return nuevosTickets;
        }

        public async Task<List<Ticket>> GetAllTicketsAsync()
        {
            return await _context.Tickets
                .Include(t => t.Partido)
                .Include(t => t.Usuario)
                .ToListAsync();
        }
        public async Task<IEnumerable<Ticket>> GetMisTicketsAsync(int usuarioId, int pagina = 1, int cantidad = 15)
        {
            var query = _context.Tickets
                .Include(t => t.Partido)
                .ThenInclude(p => p.Estadio)
                .Where(t => t.UsuarioId == usuarioId)
                .OrderByDescending(t => t.FechaCompra)
                .AsQueryable();

            var ticketsPaginados = await query
                .Skip((pagina - 1) * cantidad)
                .Take(cantidad)
                .ToListAsync();

            return ticketsPaginados;
        }
    }
}

using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Text;
using TUP.MundialTPI.DatosEF;
using TUP.MundialTPI.Entidades;
using TUP.MundialTPI.Entidades.DTOs;
using TUP.MundialTPI.Negocio.Interfaces;

namespace TUP.MundialTPI.Negocio
{
    public class PartidoService : IPartidoService
    {
        private readonly AppDbContext _context;
        public PartidoService(AppDbContext context) => _context = context;

        public async Task<List<Partido>> GetAllAsync()
        {
            return await _context.Partidos.Include(p => p.Estadio).ToListAsync();
        }

        public async Task<Partido?> GetByIdAsync(int id)
        {
            return await _context.Partidos.Include(p => p.Estadio).FirstOrDefaultAsync(p => p.Id == id);
        }

        public async Task<Partido> CrearAsync(PartidoDTO dto)
        {
            var estadio = await _context.Estadios.FindAsync(dto.EstadioId);
            if (estadio == null) throw new InvalidOperationException("Estadio inexistente");

            var partido = new Partido
            {
                EquipoLocal = dto.EquipoLocal,
                EquipoVisitante = dto.EquipoVisitante,
                FechaHora = dto.FechaHora.Value,
                EstadioId = dto.EstadioId.Value,
                Fase = dto.Fase,
                EntradasDisponibles = dto.EntradasDisponibles
            };
            _context.Partidos.Add(partido);
            await _context.SaveChangesAsync();
            return partido;
        }

        public async Task<Partido> EditarAsync(int id, PartidoDTO dto)
        {
            var partido = await _context.Partidos.FindAsync(id);
            if (partido == null) throw new KeyNotFoundException("Partido no encontrado");

            var estadio = await _context.Estadios.FindAsync(dto.EstadioId);
            if (estadio == null) throw new InvalidOperationException("Estadio inexistente");

            partido.EquipoLocal = dto.EquipoLocal;
            partido.EquipoVisitante = dto.EquipoVisitante;
            partido.FechaHora = dto.FechaHora.Value;
            partido.EstadioId = dto.EstadioId.Value;
            partido.Fase = dto.Fase;
            partido.EntradasDisponibles = dto.EntradasDisponibles;

            await _context.SaveChangesAsync();
            return partido;
        }

        public async Task EliminarAsync(int id)
        {
            var partido = await _context.Partidos.FindAsync(id);
            if (partido == null) throw new KeyNotFoundException("Partido no encontrado");

            var tieneTickets = await _context.Tickets.AnyAsync(t => t.PartidoId == id);
            if (tieneTickets) throw new InvalidOperationException("No se puede eliminar: el partido tiene tickets asociados");

            _context.Partidos.Remove(partido);
            await _context.SaveChangesAsync();
        }
    }
}
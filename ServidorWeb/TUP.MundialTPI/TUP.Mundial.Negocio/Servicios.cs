using Microsoft.EntityFrameworkCore;
using TUP.Mundial.Entidades;
using TUP.MundialTPI.DatosEF;

namespace TUP.Mundial.Negocio
{
    public interface IPartidoService
    {
        Task<List<Partido>> GetAllAsync();
        Task<Partido?> GetByIdAsync(int id);
        Task<Partido> CrearAsync(PartidoDto dto);
        Task<Partido> EditarAsync(int id, PartidoDto dto);
        Task EliminarAsync(int id);
    }

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

        public async Task<Partido> CrearAsync(PartidoDto dto)
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

        public async Task<Partido> EditarAsync(int id, PartidoDto dto)
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

    public interface ITicketService
    {
        Task<Ticket> ComprarAsync(int usuarioId, ComprarTicketDto dto);
        Task<List<Ticket>> GetAllTicketsAsync();
        Task<List<Ticket>> GetMisTicketsAsync(int usuarioId);
    }

    public class TicketService : ITicketService
    {
        private readonly AppDbContext _context;
        public TicketService(AppDbContext context) => _context = context;

        public async Task<Ticket> ComprarAsync(int usuarioId, ComprarTicketDto dto)
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

using System;
using System.ComponentModel.DataAnnotations;

namespace TUP.Mundial.Entidades
{
    public class Estadio
    {
        public int Id { get; set; }
        public string Nombre { get; set; } = string.Empty;
        public string Ciudad { get; set; } = string.Empty;
        public string Pais { get; set; } = string.Empty;
        public int Capacidad { get; set; }
        public string ImagenUrl { get; set; } = string.Empty;
    }

    public class Partido
    {
        public int Id { get; set; }
        public string EquipoLocal { get; set; } = string.Empty;
        public string EquipoVisitante { get; set; } = string.Empty;
        public DateTime FechaHora { get; set; }
        public int EstadioId { get; set; }
        public Estadio Estadio { get; set; } = null!;
        public string Fase { get; set; } = string.Empty;
        public int EntradasDisponibles { get; set; }
    }

    public class Usuario
    {
        public int Id { get; set; }
        public string Nombre { get; set; } = string.Empty;
        public string Email { get; set; } = string.Empty;
        public string PasswordHash { get; set; } = string.Empty;
        public string Rol { get; set; } = "user";
    }

    public class Ticket
    {
        public int Id { get; set; }
        public int PartidoId { get; set; }
        public Partido Partido { get; set; } = null!;
        public int UsuarioId { get; set; }
        public Usuario Usuario { get; set; } = null!;
        public string TipoEntrada { get; set; } = "General";
        public decimal Precio { get; set; }
        public DateTime FechaCompra { get; set; }
    }

    public class ComprarTicketDto
    {
        public int PartidoId { get; set; }
        public string TipoEntrada { get; set; } = "General";
        public int Cantidad { get; set; }
    }

    public class LoginDto
    {
        public string Email { get; set; } = string.Empty;
        public string Password { get; set; } = string.Empty;
    }

    public class RegisterDto
    {
        public string Nombre { get; set; } = string.Empty;
        public string Email { get; set; } = string.Empty;
        public string Password { get; set; } = string.Empty;
    }

    public class PartidoDto
    {
        [Required(ErrorMessage = "El equipo local es obligatorio.")]
        public string EquipoLocal { get; set; } = string.Empty;

        [Required(ErrorMessage = "El equipo visitante es obligatorio.")]
        public string EquipoVisitante { get; set; } = string.Empty;

        [Required(ErrorMessage = "La fecha y hora son obligatorias.")]
        public DateTime? FechaHora { get; set; }

        [Required(ErrorMessage = "El estadio es obligatorio.")]
        public int? EstadioId { get; set; }
        
        public string Fase { get; set; } = string.Empty;
        public int EntradasDisponibles { get; set; }
    }
}

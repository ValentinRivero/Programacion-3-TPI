using System;
using System.ComponentModel.DataAnnotations;

namespace TUP.MundialTPI.Entidades.DTOs
{
    public class CrearPartidoDTO
    {
        [Required(ErrorMessage = "El equipo local es obligatorio.")]
        public string EquipoLocal { get; set; } = string.Empty;

        [Required(ErrorMessage = "El equipo visitante es obligatorio.")]
        public string EquipoVisitante { get; set; } = string.Empty;

        [Required(ErrorMessage = "La fecha y hora son obligatorias.")]
        public DateTime? FechaHora { get; set; }

        [Required(ErrorMessage = "El estadio es obligatorio.")]
        public int? EstadioId { get; set; }

        [Required(ErrorMessage = "La Fase del partido es obligatoria.")]
        public string Fase { get; set; } = string.Empty;

        [Range(1, 100000, ErrorMessage = "Debe haber al menos 1 entrada disponible.")]
        public int EntradasDisponibles { get; set; }
    }
}
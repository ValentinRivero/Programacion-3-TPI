using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Text;

namespace TUP.MundialTPI.Entidades.DTOs
{
    public class PartidoDTO
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

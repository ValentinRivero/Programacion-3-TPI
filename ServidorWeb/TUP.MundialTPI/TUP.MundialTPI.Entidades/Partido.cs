using System;
using System.Collections.Generic;
using System.Text;

namespace TUP.MundialTPI.Entidades
{
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
}

using System;
using System.Collections.Generic;
using System.Text;

namespace TUP.MundialTPI.Entidades
{
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
}

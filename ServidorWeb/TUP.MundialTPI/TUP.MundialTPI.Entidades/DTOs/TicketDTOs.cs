using System;
using System.Collections.Generic;
using System.Text;

namespace TUP.MundialTPI.Entidades.DTOs
{
    public class ComprarTicketDTO
    {
        public int PartidoId { get; set; }
        public string TipoEntrada { get; set; } = "General";
        public int Cantidad { get; set; }
    }
}

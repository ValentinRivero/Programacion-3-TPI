using System;
using System.Collections.Generic;
using System.Text;

namespace TUP.MundialTPI.Entidades.DTOs
{
    public class EditarPartidoDTO : CrearPartidoDTO
    {
        public string Estado { get; set; } = string.Empty;
    }
}

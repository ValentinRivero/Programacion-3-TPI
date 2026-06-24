using System;
using System.Collections.Generic;
using System.Text;

namespace TUP.MundialTPI.Entidades
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
}

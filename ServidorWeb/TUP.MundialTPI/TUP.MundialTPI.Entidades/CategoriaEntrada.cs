using System.ComponentModel.DataAnnotations;

namespace TUP.MundialTPI.Entidades
{
    public class CategoriaEntrada
    {
        public int Id { get; set; }

        [MaxLength(50)]
        public string Nombre { get; set; } = string.Empty;

        public decimal PrecioActual { get; set; }

        public bool Activo { get; set; } = true;
    }
}
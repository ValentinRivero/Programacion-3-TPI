namespace TUP.MundialTPI.Entidades
{
    public class Ticket
    {
        public int Id { get; set; }
        public int PartidoId { get; set; }
        public Partido Partido { get; set; } = null!;
        public int UsuarioId { get; set; }
        public Usuario Usuario { get; set; } = null!;
        public int CategoriaId { get; set; }
        public CategoriaEntrada Categoria { get; set; } = null!;
        public decimal Precio { get; set; }
        public DateTime FechaCompra { get; set; }
        public bool Activo { get; set; } = true;
    }
}
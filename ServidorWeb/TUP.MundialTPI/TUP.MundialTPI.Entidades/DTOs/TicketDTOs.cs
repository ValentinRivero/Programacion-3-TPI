using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Text;

namespace TUP.MundialTPI.Entidades.DTOs
{
    public class ComprarTicketDTO
    {
        public int PartidoId { get; set; }

        [Range(1, 4, ErrorMessage = "Podés comprar entre 1 y 4 entradas como máximo.")]
        public int Cantidad { get; set; } = 1;

        [Required(ErrorMessage = "La categoría es obligatoria.")]
        public int CategoriaId { get; set; }
    }
}

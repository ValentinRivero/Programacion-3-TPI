using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Text;

namespace TUP.MundialTPI.Entidades.DTOs
{
    public class LoginDTO
    {
        [Required(ErrorMessage = "El email es obligatorio.")]
        [EmailAddress(ErrorMessage = "El formato del email no es válido.")]
        public string Email { get; set; } = string.Empty;

        [Required(ErrorMessage = "La contraseña es obligatoria.")]
        public string Password { get; set; } = string.Empty;
    }

    public class RegisterDTO
    {
        [Required(ErrorMessage = "El nombre es obligatorio.")]
        [MinLength(3, ErrorMessage = "El nombre debe tener al menos 3 caracteres.")]
        public string Nombre { get; set; } = string.Empty;

        [Required(ErrorMessage = "El email es obligatorio.")]
        [EmailAddress(ErrorMessage = "El formato del email no es válido.")]
        public string Email { get; set; } = string.Empty;

        [Required(ErrorMessage = "La contraseña es obligatoria.")]
        [MinLength(4, ErrorMessage = "La contraseña debe tener al menos 4 caracteres.")]
        public string Password { get; set; } = string.Empty;
    }
}

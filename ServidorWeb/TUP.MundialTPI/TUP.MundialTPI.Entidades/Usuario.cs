using System;
using System.Collections.Generic;
using System.Text;

namespace TUP.MundialTPI.Entidades
{
    public class Usuario
    {
        public int Id { get; set; }
        public string Nombre { get; set; } = string.Empty;
        public string Email { get; set; } = string.Empty;
        public string PasswordHash { get; set; } = string.Empty;
        public string Rol { get; set; } = "user";
        public bool Activo { get; set; } = true;
    }
}

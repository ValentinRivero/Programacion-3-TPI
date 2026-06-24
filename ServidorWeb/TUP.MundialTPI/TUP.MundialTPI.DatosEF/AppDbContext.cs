using Microsoft.EntityFrameworkCore;
using TUP.MundialTPI.Entidades;

namespace TUP.MundialTPI.DatosEF
{
    public class AppDbContext : DbContext
    {
        public AppDbContext(DbContextOptions<AppDbContext> options) : base(options)
        {
        }

        public DbSet<Estadio> Estadios { get; set; } = null!;
        public DbSet<Partido> Partidos { get; set; } = null!;
        public DbSet<Usuario> Usuarios { get; set; } = null!;
        public DbSet<Ticket> Tickets { get; set; } = null!;

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            base.OnModelCreating(modelBuilder);

            // Relaciones
            modelBuilder.Entity<Partido>()
                .HasOne(p => p.Estadio)
                .WithMany()
                .HasForeignKey(p => p.EstadioId);

            modelBuilder.Entity<Ticket>()
                .HasOne(t => t.Partido)
                .WithMany()
                .HasForeignKey(t => t.PartidoId);

            modelBuilder.Entity<Ticket>()
                .HasOne(t => t.Usuario)
                .WithMany()
                .HasForeignKey(t => t.UsuarioId);

            modelBuilder.Entity<Usuario>()
                .HasIndex(u => u.Email)
                .IsUnique();

            // Seed Data
            modelBuilder.Entity<Usuario>().HasData(
                new Usuario { Id = 1, Nombre = "Admin User", Email = "admin@mundial.com", PasswordHash = "$2a$11$yD/piJffVpHeaIO5MxdNWOA3JItPuyf5hh3j1w94c/R924kGmQq6.", Rol = "admin" }
            );

            modelBuilder.Entity<Estadio>().HasData(
                new Estadio { Id = 1, Nombre = "Rose Bowl", Ciudad = "Pasadena", Pais = "Estados Unidos", Capacidad = 92542, ImagenUrl = "https://media.telemundo51.com/2025/03/argentinabrasil.jpg?quality=85&strip=all&resize=1200%2C675" },
                new Estadio { Id = 2, Nombre = "MetLife Stadium", Ciudad = "East Rutherford", Pais = "Estados Unidos", Capacidad = 82500, ImagenUrl = "https://www.directvsports.com/__export/1720258446592/sites/dsports/img/2024/07/06/20240706_063405942_732035_0_1533.jpg_554688468.jpg" },
                new Estadio { Id = 3, Nombre = "Estadio Azteca", Ciudad = "Ciudad de México", Pais = "México", Capacidad = 87523, ImagenUrl = "https://media.tycsports.com/files/2023/11/10/644251/mexico-se-enfrenta-ante-la-visita-alemania-por-la-fecha-1-del-grupo-f_862x485_wmk.webp" },
                new Estadio { Id = 4, Nombre = "BMO Field", Ciudad = "Toronto", Pais = "Canadá", Capacidad = 30000, ImagenUrl = "https://www.americanfootballinternational.com/wp-content/uploads/ITA-CAN.jpg" }
            );

            modelBuilder.Entity<Partido>().HasData(
                new Partido { Id = 1, EquipoLocal = "Argentina", EquipoVisitante = "Brasil", FechaHora = DateTime.Parse("2026-06-14T18:00:00Z").ToUniversalTime(), EstadioId = 1, Fase = "Final", EntradasDisponibles = 1240 },
                new Partido { Id = 2, EquipoLocal = "Francia", EquipoVisitante = "España", FechaHora = DateTime.Parse("2026-06-15T20:00:00Z").ToUniversalTime(), EstadioId = 2, Fase = "Semifinal", EntradasDisponibles = 500 },
                new Partido { Id = 3, EquipoLocal = "México", EquipoVisitante = "Alemania", FechaHora = DateTime.Parse("2026-06-20T16:00:00Z").ToUniversalTime(), EstadioId = 3, Fase = "Cuartos de final", EntradasDisponibles = 0 },
                new Partido { Id = 4, EquipoLocal = "Canadá", EquipoVisitante = "Italia", FechaHora = DateTime.Parse("2026-06-25T14:00:00Z").ToUniversalTime(), EstadioId = 4, Fase = "Fase de Grupos", EntradasDisponibles = 3200 }
            );
        }
    }
}

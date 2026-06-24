using System;
using Microsoft.EntityFrameworkCore.Migrations;
using Npgsql.EntityFrameworkCore.PostgreSQL.Metadata;

#nullable disable

#pragma warning disable CA1814 // Prefer jagged arrays over multidimensional

namespace TUP.MundialTPI.DatosEF.Migrations
{
    /// <inheritdoc />
    public partial class InitialPostgres : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.CreateTable(
                name: "Estadios",
                columns: table => new
                {
                    Id = table.Column<int>(type: "integer", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    Nombre = table.Column<string>(type: "text", nullable: false),
                    Ciudad = table.Column<string>(type: "text", nullable: false),
                    Pais = table.Column<string>(type: "text", nullable: false),
                    Capacidad = table.Column<int>(type: "integer", nullable: false),
                    ImagenUrl = table.Column<string>(type: "text", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Estadios", x => x.Id);
                });

            migrationBuilder.CreateTable(
                name: "Usuarios",
                columns: table => new
                {
                    Id = table.Column<int>(type: "integer", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    Nombre = table.Column<string>(type: "text", nullable: false),
                    Email = table.Column<string>(type: "text", nullable: false),
                    PasswordHash = table.Column<string>(type: "text", nullable: false),
                    Rol = table.Column<string>(type: "text", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Usuarios", x => x.Id);
                });

            migrationBuilder.CreateTable(
                name: "Partidos",
                columns: table => new
                {
                    Id = table.Column<int>(type: "integer", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    EquipoLocal = table.Column<string>(type: "text", nullable: false),
                    EquipoVisitante = table.Column<string>(type: "text", nullable: false),
                    FechaHora = table.Column<DateTime>(type: "timestamp with time zone", nullable: false),
                    EstadioId = table.Column<int>(type: "integer", nullable: false),
                    Fase = table.Column<string>(type: "text", nullable: false),
                    EntradasDisponibles = table.Column<int>(type: "integer", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Partidos", x => x.Id);
                    table.ForeignKey(
                        name: "FK_Partidos_Estadios_EstadioId",
                        column: x => x.EstadioId,
                        principalTable: "Estadios",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "Tickets",
                columns: table => new
                {
                    Id = table.Column<int>(type: "integer", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    PartidoId = table.Column<int>(type: "integer", nullable: false),
                    UsuarioId = table.Column<int>(type: "integer", nullable: false),
                    TipoEntrada = table.Column<string>(type: "text", nullable: false),
                    Precio = table.Column<decimal>(type: "numeric", nullable: false),
                    FechaCompra = table.Column<DateTime>(type: "timestamp with time zone", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Tickets", x => x.Id);
                    table.ForeignKey(
                        name: "FK_Tickets_Partidos_PartidoId",
                        column: x => x.PartidoId,
                        principalTable: "Partidos",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                    table.ForeignKey(
                        name: "FK_Tickets_Usuarios_UsuarioId",
                        column: x => x.UsuarioId,
                        principalTable: "Usuarios",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.InsertData(
                table: "Estadios",
                columns: new[] { "Id", "Capacidad", "Ciudad", "ImagenUrl", "Nombre", "Pais" },
                values: new object[,]
                {
                    { 1, 92542, "Pasadena", "https://media.telemundo51.com/2025/03/argentinabrasil.jpg?quality=85&strip=all&resize=1200%2C675", "Rose Bowl", "Estados Unidos" },
                    { 2, 82500, "East Rutherford", "https://www.directvsports.com/__export/1720258446592/sites/dsports/img/2024/07/06/20240706_063405942_732035_0_1533.jpg_554688468.jpg", "MetLife Stadium", "Estados Unidos" },
                    { 3, 87523, "Ciudad de México", "https://media.tycsports.com/files/2023/11/10/644251/mexico-se-enfrenta-ante-la-visita-alemania-por-la-fecha-1-del-grupo-f_862x485_wmk.webp", "Estadio Azteca", "México" },
                    { 4, 30000, "Toronto", "https://www.americanfootballinternational.com/wp-content/uploads/ITA-CAN.jpg", "BMO Field", "Canadá" }
                });

            migrationBuilder.InsertData(
                table: "Usuarios",
                columns: new[] { "Id", "Email", "Nombre", "PasswordHash", "Rol" },
                values: new object[] { 1, "admin@mundial.com", "Admin User", "$2a$11$yD/piJffVpHeaIO5MxdNWOA3JItPuyf5hh3j1w94c/R924kGmQq6.", "admin" });

            migrationBuilder.InsertData(
                table: "Partidos",
                columns: new[] { "Id", "EntradasDisponibles", "EquipoLocal", "EquipoVisitante", "EstadioId", "Fase", "FechaHora" },
                values: new object[,]
                {
                    { 1, 1240, "Argentina", "Brasil", 1, "Final", new DateTime(2026, 6, 14, 18, 0, 0, 0, DateTimeKind.Utc) },
                    { 2, 500, "Francia", "España", 2, "Semifinal", new DateTime(2026, 6, 15, 20, 0, 0, 0, DateTimeKind.Utc) },
                    { 3, 0, "México", "Alemania", 3, "Cuartos de final", new DateTime(2026, 6, 20, 16, 0, 0, 0, DateTimeKind.Utc) },
                    { 4, 3200, "Canadá", "Italia", 4, "Fase de Grupos", new DateTime(2026, 6, 25, 14, 0, 0, 0, DateTimeKind.Utc) }
                });

            migrationBuilder.CreateIndex(
                name: "IX_Partidos_EstadioId",
                table: "Partidos",
                column: "EstadioId");

            migrationBuilder.CreateIndex(
                name: "IX_Tickets_PartidoId",
                table: "Tickets",
                column: "PartidoId");

            migrationBuilder.CreateIndex(
                name: "IX_Tickets_UsuarioId",
                table: "Tickets",
                column: "UsuarioId");

            migrationBuilder.CreateIndex(
                name: "IX_Usuarios_Email",
                table: "Usuarios",
                column: "Email",
                unique: true);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(
                name: "Tickets");

            migrationBuilder.DropTable(
                name: "Partidos");

            migrationBuilder.DropTable(
                name: "Usuarios");

            migrationBuilder.DropTable(
                name: "Estadios");
        }
    }
}

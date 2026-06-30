using Microsoft.EntityFrameworkCore.Migrations;
using Npgsql.EntityFrameworkCore.PostgreSQL.Metadata;

#nullable disable

#pragma warning disable CA1814 // Prefer jagged arrays over multidimensional

namespace TUP.MundialTPI.DatosEF.Migrations
{
    /// <inheritdoc />
    public partial class AgregarTablaCategorias : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.CreateTable(
                name: "CategoriasEntradas",
                columns: table => new
                {
                    Id = table.Column<int>(type: "integer", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    Nombre = table.Column<string>(type: "character varying(50)", maxLength: 50, nullable: false),
                    PrecioActual = table.Column<decimal>(type: "numeric", nullable: false),
                    Activo = table.Column<bool>(type: "boolean", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_CategoriasEntradas", x => x.Id);
                });

            migrationBuilder.InsertData(
                table: "CategoriasEntradas",
                columns: new[] { "Id", "Activo", "Nombre", "PrecioActual" },
                values: new object[,]
                {
                    { 1, true, "Categoría 1", 250m },
                    { 2, true, "Categoría 2", 150m },
                    { 3, true, "Categoría 3", 100m }
                });
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(
                name: "CategoriasEntradas");
        }
    }
}

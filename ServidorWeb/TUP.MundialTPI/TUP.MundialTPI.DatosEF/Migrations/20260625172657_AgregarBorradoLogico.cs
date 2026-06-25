using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace TUP.MundialTPI.DatosEF.Migrations
{
    /// <inheritdoc />
    public partial class AgregarBorradoLogico : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<bool>(
                name: "Activo",
                table: "Usuarios",
                type: "boolean",
                nullable: false,
                defaultValue: false);

            migrationBuilder.AddColumn<string>(
                name: "Estado",
                table: "Partidos",
                type: "text",
                nullable: false,
                defaultValue: "");

            migrationBuilder.UpdateData(
                table: "Partidos",
                keyColumn: "Id",
                keyValue: 1,
                column: "Estado",
                value: "Activo");

            migrationBuilder.UpdateData(
                table: "Partidos",
                keyColumn: "Id",
                keyValue: 2,
                column: "Estado",
                value: "Activo");

            migrationBuilder.UpdateData(
                table: "Partidos",
                keyColumn: "Id",
                keyValue: 3,
                column: "Estado",
                value: "Activo");

            migrationBuilder.UpdateData(
                table: "Partidos",
                keyColumn: "Id",
                keyValue: 4,
                column: "Estado",
                value: "Activo");

            migrationBuilder.UpdateData(
                table: "Usuarios",
                keyColumn: "Id",
                keyValue: 1,
                column: "Activo",
                value: true);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "Activo",
                table: "Usuarios");

            migrationBuilder.DropColumn(
                name: "Estado",
                table: "Partidos");
        }
    }
}

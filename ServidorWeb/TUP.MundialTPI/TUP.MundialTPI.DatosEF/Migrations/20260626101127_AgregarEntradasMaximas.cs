using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace TUP.MundialTPI.DatosEF.Migrations
{
    /// <inheritdoc />
    public partial class AgregarEntradasMaximas : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<int>(
                name: "EntradasMaximas",
                table: "Partidos",
                type: "integer",
                nullable: false,
                defaultValue: 0);

            migrationBuilder.UpdateData(
                table: "Partidos",
                keyColumn: "Id",
                keyValue: 1,
                column: "EntradasMaximas",
                value: 0);

            migrationBuilder.UpdateData(
                table: "Partidos",
                keyColumn: "Id",
                keyValue: 2,
                column: "EntradasMaximas",
                value: 0);

            migrationBuilder.UpdateData(
                table: "Partidos",
                keyColumn: "Id",
                keyValue: 3,
                column: "EntradasMaximas",
                value: 0);

            migrationBuilder.UpdateData(
                table: "Partidos",
                keyColumn: "Id",
                keyValue: 4,
                column: "EntradasMaximas",
                value: 0);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "EntradasMaximas",
                table: "Partidos");
        }
    }
}

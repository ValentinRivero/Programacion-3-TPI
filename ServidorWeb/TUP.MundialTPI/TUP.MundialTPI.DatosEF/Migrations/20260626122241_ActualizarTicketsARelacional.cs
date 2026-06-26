using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace TUP.MundialTPI.DatosEF.Migrations
{
    /// <inheritdoc />
    public partial class ActualizarTicketsARelacional : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "TipoEntrada",
                table: "Tickets");

            migrationBuilder.AddColumn<int>(
                name: "CategoriaId",
                table: "Tickets",
                type: "integer",
                nullable: false,
                defaultValue: 0);

            migrationBuilder.CreateIndex(
                name: "IX_Tickets_CategoriaId",
                table: "Tickets",
                column: "CategoriaId");

            migrationBuilder.AddForeignKey(
                name: "FK_Tickets_CategoriasEntradas_CategoriaId",
                table: "Tickets",
                column: "CategoriaId",
                principalTable: "CategoriasEntradas",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_Tickets_CategoriasEntradas_CategoriaId",
                table: "Tickets");

            migrationBuilder.DropIndex(
                name: "IX_Tickets_CategoriaId",
                table: "Tickets");

            migrationBuilder.DropColumn(
                name: "CategoriaId",
                table: "Tickets");

            migrationBuilder.AddColumn<string>(
                name: "TipoEntrada",
                table: "Tickets",
                type: "text",
                nullable: false,
                defaultValue: "");
        }
    }
}

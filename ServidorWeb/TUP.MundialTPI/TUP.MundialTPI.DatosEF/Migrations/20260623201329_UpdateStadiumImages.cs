using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace TUP.MundialTPI.DatosEF.Migrations
{
    /// <inheritdoc />
    public partial class UpdateStadiumImages : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.UpdateData(
                table: "Estadios",
                keyColumn: "Id",
                keyValue: 1,
                column: "ImagenUrl",
                value: "https://media.telemundo51.com/2025/03/argentinabrasil.jpg?quality=85&strip=all&resize=1200%2C675");

            migrationBuilder.UpdateData(
                table: "Estadios",
                keyColumn: "Id",
                keyValue: 2,
                column: "ImagenUrl",
                value: "https://www.directvsports.com/__export/1720258446592/sites/dsports/img/2024/07/06/20240706_063405942_732035_0_1533.jpg_554688468.jpg");

            migrationBuilder.UpdateData(
                table: "Estadios",
                keyColumn: "Id",
                keyValue: 3,
                column: "ImagenUrl",
                value: "https://media.tycsports.com/files/2023/11/10/644251/mexico-se-enfrenta-ante-la-visita-alemania-por-la-fecha-1-del-grupo-f_862x485_wmk.webp");

            migrationBuilder.UpdateData(
                table: "Estadios",
                keyColumn: "Id",
                keyValue: 4,
                column: "ImagenUrl",
                value: "https://www.americanfootballinternational.com/wp-content/uploads/ITA-CAN.jpg");
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.UpdateData(
                table: "Estadios",
                keyColumn: "Id",
                keyValue: 1,
                column: "ImagenUrl",
                value: "https://images.unsplash.com/photo-1518605368461-1e1e108d1f2d?q=80&w=1000&auto=format&fit=crop");

            migrationBuilder.UpdateData(
                table: "Estadios",
                keyColumn: "Id",
                keyValue: 2,
                column: "ImagenUrl",
                value: "https://images.unsplash.com/photo-1508344928928-7165b67de128?q=80&w=1000&auto=format&fit=crop");

            migrationBuilder.UpdateData(
                table: "Estadios",
                keyColumn: "Id",
                keyValue: 3,
                column: "ImagenUrl",
                value: "https://images.unsplash.com/photo-1522778147829-047360bdc7f6?q=80&w=1000&auto=format&fit=crop");

            migrationBuilder.UpdateData(
                table: "Estadios",
                keyColumn: "Id",
                keyValue: 4,
                column: "ImagenUrl",
                value: "https://images.unsplash.com/photo-1628891435222-065922031e0b?q=80&w=1000&auto=format&fit=crop");
        }
    }
}

import { api } from './api.js';
import { auth } from './auth.js';
import { ui } from './ui.js';

const urlParams = new URLSearchParams(window.location.search);
const partidoId = urlParams.get('id');

async function cargarDetalle() {
    const errorDiv = document.getElementById('detalle-error');
    const contentDiv = document.getElementById('detalle-content');

    if (!partidoId) {
        errorDiv.textContent = 'No se especificó ningún partido. Volvé al catálogo para seleccionar uno.';
        errorDiv.style.display = 'block';
        return;
    }

    try {
        const partidoActual = await api.fetch(`/partidos/${partidoId}`);

        const cardContainer = document.getElementById('partido-info-container');
        cardContainer.appendChild(ui.createMatchCard(partidoActual));

        const btnAdentroDeTarjeta = cardContainer.querySelector('a.btn');
        if (btnAdentroDeTarjeta) btnAdentroDeTarjeta.style.display = 'none';

        contentDiv.style.display = 'block';

        if (partidoActual.entradasDisponibles === 0) {
            const btnComprar = document.getElementById('btn-comprar');
            btnComprar.disabled = true;
            btnComprar.textContent = 'Entradas Agotadas';
            btnComprar.className = 'btn btn-disabled btn-full btn-lg';
            document.getElementById('cantidad').disabled = true;
            document.getElementById('tipo-entrada').disabled = true;
        }

    } catch (error) {
        errorDiv.textContent = 'Error al cargar el partido: ' + ui.escapeHTML(error.message);
        errorDiv.style.display = 'block';
    }
}

function actualizarPrecio() {
    const select = document.getElementById('tipo-entrada');
    const opcionSel = select.options[select.selectedIndex];

    const precioUnitario = parseInt(opcionSel.getAttribute('data-precio')) || 0;
    const cantidad = parseInt(document.getElementById('cantidad').value) || 1;

    document.getElementById('precio-total').textContent = `$${precioUnitario * cantidad} USD`;
}

document.getElementById('tipo-entrada').addEventListener('change', actualizarPrecio);
document.getElementById('cantidad').addEventListener('input', actualizarPrecio);


document.getElementById('form-compra').addEventListener('submit', async (e) => {
    e.preventDefault();

    if (!auth.isAuthenticated()) {
        ui.showToast('Debés iniciar sesión para poder comprar entradas.', 'error');
        setTimeout(() => window.location.href = '/login.html', 1500);
        return;
    }

    const btnComprar = document.getElementById('btn-comprar');
    btnComprar.disabled = true;
    btnComprar.textContent = 'Procesando pago seguro...';

    const cantidad = parseInt(document.getElementById('cantidad').value);
    const categoriaId = parseInt(document.getElementById('tipo-entrada').value);

    try {
        const respuesta = await api.fetch('/tickets/comprar', {
            method: 'POST',
            body: JSON.stringify({
                partidoId: parseInt(partidoId),
                categoriaId: categoriaId,
                cantidad: cantidad
            })
        });

        ui.showToast(respuesta.mensaje || '¡Compra exitosa! Tus tickets ya están registrados.', 'success');

        setTimeout(() => window.location.href = '/mis_tickets.html', 2000);

    } catch (error) {
        ui.showToast('Ocurrió un error con la transacción: ' + ui.escapeHTML(error.message), 'error');
        btnComprar.disabled = false;
        btnComprar.textContent = 'Confirmar Pago';
    }
});

document.addEventListener('DOMContentLoaded', () => {
    ui.setupNavbar();
    cargarDetalle();
});
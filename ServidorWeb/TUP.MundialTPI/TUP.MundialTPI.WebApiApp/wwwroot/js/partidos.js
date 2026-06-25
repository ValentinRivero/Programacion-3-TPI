import { api } from './api.js';
import { ui } from './ui.js';

async function cargarPartidos() {
    const container = document.getElementById('matches-container');
    try {
        const partidos = await api.fetch('/partidos');
        container.innerHTML = '';

        if (!partidos || partidos.length === 0) {
            container.innerHTML = '<p>No hay partidos programados.</p>';
            return;
        }

        partidos.forEach(partido => {
            const card = ui.createMatchCard(partido);
            container.appendChild(card);
        });
    } catch (error) {
        container.innerHTML = `<p class="text-error">Error al cargar: ${ui.escapeHTML(error.message)}</p>`;
    }
}

document.addEventListener('DOMContentLoaded', () => {
    ui.setupNavbar();
    cargarPartidos();
});
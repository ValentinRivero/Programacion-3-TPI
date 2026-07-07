import { api } from './api.js';
import { ui } from './ui.js';

let paginaActual = 1;
const cantidadPorPagina = 15;

async function cargarPartidos() {
    const container = document.getElementById('matches-container');
    container.innerHTML = '<p>Cargando partidos...</p>';

    try {
        const partidos = await api.fetch(`/partidos?pagina=${paginaActual}&cantidad=${cantidadPorPagina}`);
        container.innerHTML = '';

        if (!partidos || partidos.length === 0) {
            container.innerHTML = '<p style="text-align: center; color: #666;">No hay partidos para mostrar en esta página.</p>';
            renderizarPaginacion(0);
            return;
        }

        partidos.forEach(partido => {
            const card = ui.createMatchCard(partido);
            container.appendChild(card);
        });

        const inputBusqueda = document.getElementById('search-partidos');
        if (inputBusqueda) {
            inputBusqueda.addEventListener('input', (e) => {
                const texto = e.target.value.toLowerCase();
                const tarjetas = container.querySelectorAll('.card');

                tarjetas.forEach(tarjeta => {
                    const contenidoVisible = tarjeta.innerText.toLowerCase();
                    tarjeta.style.display = contenidoVisible.includes(texto) ? 'flex' : 'none';
                });
            });
        }

        renderizarPaginacion(partidos.length);

    } catch (error) {
        container.innerHTML = `<p class="text-error">Error al cargar: ${ui.escapeHTML(error.message)}</p>`;
    }
}

function renderizarPaginacion(cantidadResultados) {
    let pagContainer = document.getElementById('pagination-partidos');

    if (!pagContainer) {
        pagContainer = document.createElement('div');
        pagContainer.id = 'pagination-partidos';
        Object.assign(pagContainer.style, {
            display: 'flex', justifyContent: 'center', alignItems: 'center', gap: '1.5rem', marginTop: '2rem', paddingBottom: '2rem'
        });
        document.querySelector('main.container').appendChild(pagContainer);
    }

    const prevDisabled = paginaActual === 1;
    const nextDisabled = cantidadResultados < cantidadPorPagina;

    pagContainer.innerHTML = `
        <button id="btn-prev-partido" class="btn btn-secondary" style="${prevDisabled ? 'opacity: 0.5; cursor: not-allowed;' : ''}" ${prevDisabled ? 'disabled' : ''}>← Anterior</button>
        <span style="font-weight: 800; color: var(--color-primary-dark); font-size: 1.1rem;">Página ${paginaActual}</span>
        <button id="btn-next-partido" class="btn btn-secondary" style="${nextDisabled ? 'opacity: 0.5; cursor: not-allowed;' : ''}" ${nextDisabled ? 'disabled' : ''}>Siguiente →</button>
    `;

    document.getElementById('btn-prev-partido').addEventListener('click', () => {
        if (!prevDisabled) {
            paginaActual--;
            cargarPartidos();
        }
    });

    document.getElementById('btn-next-partido').addEventListener('click', () => {
        if (!nextDisabled) {
            paginaActual++;
            cargarPartidos();
        }
    });
}

document.addEventListener('DOMContentLoaded', () => {
    ui.setupNavbar();
    cargarPartidos();
});
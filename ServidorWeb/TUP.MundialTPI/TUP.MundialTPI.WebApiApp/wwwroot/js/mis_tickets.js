import { api } from './api.js';
import { auth } from './auth.js';
import { ui } from './ui.js';


function createTicketCard(ticket) {
    const p = ticket.partido;
    const local = ui.escapeHTML(p.equipoLocal);
    const visitante = ui.escapeHTML(p.equipoVisitante);
    const fase = ui.escapeHTML(p.fase);
    const estadio = ui.escapeHTML(p.estadio?.nombre || 'Estadio a definir');
    const ciudad = ui.escapeHTML(p.estadio?.ciudad || '');
    const tipo = ui.escapeHTML(ticket.tipoEntrada);

    const article = document.createElement('article');
    article.className = 'card shadow-sm';
    article.style.display = 'flex';
    article.style.flexDirection = 'row';
    article.style.borderLeft = '6px solid var(--color-accent)';

    article.innerHTML = `
        <div style="padding: 1.5rem; flex: 1; display: flex; flex-direction: column; justify-content: center;">
            <span style="font-size: 0.8rem; font-weight: 800; color: var(--color-text-muted); text-transform: uppercase; letter-spacing: 1px;">
                TICKET #${ticket.id} • ${fase}
            </span>
            <h3 style="font-size: 1.5rem; color: var(--color-primary-dark); font-weight: 900; margin: 0.5rem 0;">
                ${local} vs ${visitante}
            </h3>
            <p style="margin: 0; color: var(--color-text-primary);">
                <strong>🏟 Estadio:</strong> ${estadio}, ${ciudad}
            </p>
        </div>
        <div style="padding: 1.5rem; background-color: var(--color-bg); border-left: 2px dashed rgba(0,0,0,0.1); min-width: 200px; display: flex; flex-direction: column; justify-content: center; text-align: center;">
            <span style="display: block; font-size: 0.9rem; font-weight: 600; color: var(--color-text-muted); margin-bottom: 0.5rem;">CATEGORÍA</span>
            <span style="display: block; font-size: 1.2rem; font-weight: 800; color: var(--color-primary);">${tipo.toUpperCase()}</span>
            
            <div style="margin-top: 1rem; height: 30px; background: repeating-linear-gradient(90deg, #333, #333 2px, transparent 2px, transparent 4px, #333 4px, #333 5px, transparent 5px, transparent 8px); opacity: 0.5;"></div>
        </div>
    `;

    return article;
}

async function cargarMisTickets() {
    const container = document.getElementById('tickets-container');

    if (!auth.isAuthenticated()) {
        window.location.href = '/login.html';
        return;
    }

    try {
        const tickets = await api.fetch('/tickets/mis-tickets');
        container.innerHTML = '';

        if (!tickets || tickets.length === 0) {
            container.innerHTML = `
                <div style="text-align: center; padding: 4rem; background: white; border-radius: 12px;" class="shadow-sm">
                    <p style="font-size: 1.2rem; color: var(--color-text-muted); margin-bottom: 1.5rem;">Aún no tenés entradas compradas.</p>
                    <a href="/partidos.html" class="btn btn-primary">Ver catálogo de partidos</a>
                </div>
            `;
            return;
        }

        tickets.forEach(ticket => {
            const card = createTicketCard(ticket);
            container.appendChild(card);
        });

    } catch (error) {
        container.innerHTML = `<p class="text-error">Error al cargar tus tickets: ${ui.escapeHTML(error.message)}</p>`;
    }
}

document.addEventListener('DOMContentLoaded', () => {
    ui.setupNavbar();
    cargarMisTickets();
});
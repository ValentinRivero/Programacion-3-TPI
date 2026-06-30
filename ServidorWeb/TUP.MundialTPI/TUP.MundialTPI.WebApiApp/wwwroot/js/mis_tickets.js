import { api } from './api.js';
import { auth } from './auth.js';
import { ui } from './ui.js';

// 1. Maquetado de la tarjeta
function createTicketCard(ticket) {
    const p = ticket.partido;
    if (!p) return document.createElement('div');

    const local = ui.escapeHTML(p.equipoLocal);
    const visitante = ui.escapeHTML(p.equipoVisitante);
    const fase = ui.escapeHTML(p.fase);
    const tipo = ui.escapeHTML(ticket.tipoEntrada);
    const esValido = ticket.activo !== false;

    // Armado inteligente de la ubicación
    let ubicacion = 'Estadio a definir';
    if (p.estadio && p.estadio.nombre) {
        ubicacion = ui.escapeHTML(p.estadio.nombre);
        if (p.estadio.ciudad) ubicacion += `, ${ui.escapeHTML(p.estadio.ciudad)}`;
    }

    // Formateo de Fecha y Hora (transforma la UTC a la hora de tu compu)
    let fechaStr = 'Fecha a confirmar';
    if (p.fechaHora) {
        const fecha = new Date(p.fechaHora);
        // Formato: 15/07/2026 - 18:00 hs
        fechaStr = fecha.toLocaleDateString('es-AR') + ' - ' + fecha.toLocaleTimeString('es-AR', { hour: '2-digit', minute: '2-digit', hour12: false }) + ' hs';
    }

    const article = document.createElement('article');
    article.className = 'card shadow-sm';

    Object.assign(article.style, {
        display: 'flex', flexDirection: 'row', overflow: 'hidden', position: 'relative',
        borderLeft: esValido ? '6px solid var(--color-accent)' : '6px solid var(--color-error)'
    });

    if (!esValido) {
        article.style.opacity = '0.7';
        article.style.filter = 'grayscale(100%)';
    }

    const infoAcceso = esValido
        ? `<span style="display: block; font-size: 0.9rem; font-weight: 600; color: var(--color-text-muted); margin-bottom: 0.5rem;">CATEGORÍA</span>
           <span style="display: block; font-size: 1.2rem; font-weight: 800; color: var(--color-primary);">${tipo.toUpperCase()}</span>
           <div style="margin-top: 1rem; height: 30px; background: repeating-linear-gradient(90deg, #333, #333 2px, transparent 2px, transparent 4px, #333 4px, #333 5px, transparent 5px, transparent 8px); opacity: 0.5;"></div>`
        : `<span style="display: block; font-size: 1.1rem; font-weight: 900; color: var(--color-error); margin-bottom: 0.5rem;">❌ ANULADO</span>
           <span style="font-size: 0.8rem; color: #666; font-weight: 600;">Entrada revocada<br>sin validez.</span>`;

    const marcaDeAgua = !esValido
        ? `<div style="position: absolute; inset: 0; display: flex; align-items: center; justify-content: center; z-index: 1; pointer-events: none;">
                <span style="color: var(--color-error); font-size: 4rem; font-weight: 900; opacity: 0.15; transform: rotate(-15deg); user-select: none;">REVOCADO</span>
           </div>`
        : '';

    article.innerHTML = `
        ${marcaDeAgua}
        <div style="padding: 1.5rem; flex: 1; display: flex; flex-direction: column; justify-content: center; z-index: 2;">
            <span style="font-size: 0.8rem; font-weight: 800; color: var(--color-text-muted); text-transform: uppercase; letter-spacing: 1px;">
                TICKET #${ticket.id} • ${fase}
            </span>
            <h3 style="font-size: 1.5rem; color: var(--color-primary-dark); font-weight: 900; margin: 0.5rem 0;">
                ${local} vs ${visitante}
            </h3>
            
            <div style="margin-top: 0.5rem; color: var(--color-text-primary); font-size: 0.95rem; display: flex; flex-direction: column; gap: 0.3rem;">
                <span><strong>📅 Fecha:</strong> ${fechaStr}</span>
                <span><strong>🏟 Estadio:</strong> ${ubicacion}</span>
            </div>
            
        </div>
        <div style="padding: 1.5rem; background-color: var(--color-bg); border-left: 2px dashed rgba(0,0,0,0.1); min-width: 200px; display: flex; flex-direction: column; justify-content: center; text-align: center; z-index: 2;">
            ${infoAcceso}
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

    const btnImprimir = document.getElementById('btn-imprimir');

    if (btnImprimir) {
        btnImprimir.addEventListener('click', () => {
            const contenidoTickets = document.getElementById('tickets-container').innerHTML;

            const ventana = window.open('', '_blank');

            ventana.document.write(`
                <!DOCTYPE html>
                <html lang="es">
                <head>
                    <title>Comprobante de Entradas - Mundial 2026</title>
                    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;800&display=swap" rel="stylesheet">
                    <link rel="stylesheet" href="${window.location.origin}/css/main.css">
                    <style>
                        /* Limpiamos la hoja para la impresora */
                        body { background: white !important; padding: 2rem; color: black; }
                        /* Evitamos que una tarjeta se corte a la mitad de la hoja */
                        .card { 
                            border: 2px solid #ccc !important; 
                            box-shadow: none !important; 
                            margin-bottom: 2rem; 
                            page-break-inside: avoid; 
                        }
                        /* Ocultamos cualquier mensaje de error de carga si lo hubiera */
                        .text-error { display: none; }
                    </style>
                </head>
                <body>
                    <div style="text-align: center; margin-bottom: 2rem; border-bottom: 2px solid #eee; padding-bottom: 1rem;">
                        <h2 style="color: #3e52b5; font-size: 2rem; font-family: 'Inter', sans-serif;">🏆 Comprobante Oficial - FIFA 2026</h2>
                        <p style="color: #666; font-family: sans-serif;">Documento generado el ${new Date().toLocaleDateString('es-AR')}</p>
                    </div>
                    
                    ${contenidoTickets}
                    
                    <script>
                        // Le damos medio segundo al navegador para que cargue la fuente y el CSS
                        setTimeout(() => {
                            window.print();
                            window.close(); // Cerramos la pestaña virtual apenas termina
                        }, 500);
                    </script>
                </body>
                </html>
            `);

            ventana.document.close();
        });
    }
});
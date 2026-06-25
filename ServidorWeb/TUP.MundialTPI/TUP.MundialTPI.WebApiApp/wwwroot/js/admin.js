import { api } from './api.js';
import { auth } from './auth.js';
import { ui } from './ui.js';

// 1
function initAdmin() {
    const rol = auth.getRol();
    if (!auth.isAuthenticated() || rol !== 'admin') {
        alert('Acceso denegado. No tienes permisos de administrador.');
        window.location.href = '/';
        return;
    }
    cargarDashboard();
}

if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initAdmin);
} else {
    initAdmin();
}

// 2
document.querySelectorAll('.menu-btn').forEach(btn => {
    btn.addEventListener('click', (e) => {

        if (e.target.tagName.toLowerCase() === 'a') return;

        document.querySelectorAll('.menu-btn').forEach(b => b.classList.remove('active'));
        e.target.classList.add('active');
       
        document.querySelectorAll('.admin-view').forEach(v => v.style.display = 'none');
        const targetId = e.target.getAttribute('data-target');
        document.getElementById(targetId).style.display = 'block';

        if (targetId === 'dashboard-view') cargarDashboard();
        if (targetId === 'partidos-view') cargarPartidos();
        if (targetId === 'usuarios-view') cargarUsuarios();
        if (targetId === 'tickets-view') cargarTickets();
    });
});

// 3
async function cargarDashboard() {
    try {
        const stats = await api.fetch('/admin/dashboard-stats');
        document.getElementById('kpi-container').innerHTML = `
            <div class="kpi-card"><h4>Usuarios Totales</h4><div class="value">${stats.usuarios}</div></div>
            <div class="kpi-card"><h4>Tickets Vendidos</h4><div class="value">${stats.tickets}</div></div>
            <div class="kpi-card"><h4>Recaudación</h4><div class="value">$${stats.recaudacion} USD</div></div>
            <div class="kpi-card" style="border-top-color: var(--color-error);"><h4>Partidos Agotados</h4><div class="value" style="color: var(--color-error);">${stats.agotados}</div></div>
        `;
    } catch (error) {
        console.error("Error al cargar stats:", error);
    }
}

async function cargarPartidos() {
    try {
        const partidos = await api.fetch('/admin/partidos');
        const tbody = document.getElementById('tbody-partidos');

        tbody.innerHTML = partidos.map(p => {
            const esActivo = p.estado === 'Activo';
            return `
            <tr>
                <td>${p.id}</td>
                <td>${ui.escapeHTML(p.equipoLocal)} vs ${ui.escapeHTML(p.equipoVisitante)}</td>
                <td>${ui.escapeHTML(p.fase)}</td>
                <td>
                    <span style="padding: 4px 8px; border-radius: 4px; font-size: 0.8rem; background: ${esActivo ? 'var(--color-success)' : 'var(--color-error)'}; color: white;">
                        ${p.estado || 'Activo'}
                    </span>
                </td>
                <td>
                    <button class="btn ${esActivo ? 'btn-error' : 'btn-primary'}" style="padding: 0.3rem 0.6rem; font-size: 0.8rem;" onclick="togglePartido(${p.id})">
                        ${esActivo ? 'Suspender' : 'Activar'}
                    </button>
                </td>
            </tr>
        `}).join('');
    } catch (e) { console.error(e); }
}

async function cargarUsuarios() {
    try {
        const usuarios = await api.fetch('/admin/usuarios');
        document.getElementById('tbody-usuarios').innerHTML = usuarios.map(u => {
            const esActivo = u.activo !== false;
            return `
            <tr>
                <td>${u.id}</td>
                <td>${ui.escapeHTML(u.nombre)}</td>
                <td>${ui.escapeHTML(u.email)}</td>
                <td>
                    <span style="padding: 4px 8px; border-radius: 4px; font-size: 0.8rem; background: ${esActivo ? 'var(--color-success)' : 'var(--color-error)'}; color: white;">
                        ${esActivo ? 'Activo' : 'Suspendido'}
                    </span>
                </td>
                <td>
                    ${u.rol !== 'admin' ? `
                        <button class="btn ${esActivo ? 'btn-error' : 'btn-primary'}" style="padding: 0.3rem 0.6rem; font-size: 0.8rem;" onclick="toggleUsuario(${u.id})">
                            ${esActivo ? 'Suspender' : 'Reactivar'}
                        </button>
                    ` : '<span class="text-muted">Inmutable</span>'}
                </td>
            </tr>
        `}).join('');
    } catch (e) { console.error(e); }
}

window.togglePartido = async function (id) {
    if (!confirm("¿Modificar el estado de este partido?")) return;
    try {
        await api.fetch('/admin/partidos/' + id + '/toggle-estado', { method: 'PUT' });
        cargarPartidos();
    } catch (e) { alert("Error al modificar partido."); }
}

window.toggleUsuario = async function (id) {
    if (!confirm("¿Modificar el acceso de este usuario?")) return;
    try {
        await api.fetch('/admin/usuarios/' + id + '/toggle-estado', { method: 'PUT' });
        cargarUsuarios();
    } catch (e) { alert("Error al modificar usuario."); }
}

async function cargarTickets() {
    try {
        const tickets = await api.fetch('/admin/tickets');
        document.getElementById('tbody-tickets').innerHTML = tickets.map(t => `
            <tr><td>#${t.id}</td><td>${ui.escapeHTML(t.partidoInfo)}</td><td>${ui.escapeHTML(t.usuarioEmail)}</td><td>${ui.escapeHTML(t.tipoEntrada)}</td><td>$${t.precio}</td></tr>
        `).join('');
    } catch (e) { console.error(e); }
}

// 4
window.eliminarPartido = async function(id) {
    if(!confirm("¿Seguro que deseas eliminar el partido " + id + "?")) return;
    try {
        await api.fetch('/admin/partidos/' + id, { method: 'DELETE' });
        cargarPartidos();
    } catch (e) {
        alert("Error al eliminar. Es posible que el partido ya tenga tickets asociados.");
    }
}
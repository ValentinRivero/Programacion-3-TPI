import { api } from './api.js';
import { auth } from './auth.js';
import { ui } from './ui.js';

// --- CONFIGURACIÓN ESTADO FRONTEND ---
let estado = {
    partidos: { data: [], page: 1, pageSize: 15, query: '' },
    usuarios: { data: [], page: 1, pageSize: 15, query: '' },
    tickets: { data: [], page: 1, pageSize: 15, query: '' },
    partidoSeleccionado: null
};

// --- 1. Ruteo e Inicialización ---
function initAdmin() {
    if (!auth.isAuthenticated() || auth.getRol() !== 'admin') {
        ui.showToast('Acceso denegado. No tienes permisos.', 'error');
        setTimeout(() => window.location.href = '/', 1500);
        return;
    }
    window.addEventListener('hashchange', handleRoute);
    handleRoute();
    setupEventListeners();

    window.crearFechaMask = ui.aplicarMascaraFecha('crear-fecha');
    window.editFechaMask = ui.aplicarMascaraFecha('edit-fecha');
}

function handleRoute() {
    const hash = window.location.hash || '#dashboard';

    // UI: Botones activos
    document.querySelectorAll('.menu-btn').forEach(btn => {
        btn.classList.remove('active');
        if (btn.getAttribute('data-hash') === hash) btn.classList.add('active');
    });

    // UI: Mostrar sección
    document.querySelectorAll('.admin-view').forEach(v => v.style.display = 'none');
    const view = document.querySelector(hash);
    if (view) view.style.display = 'block';

    // Cargar datos según la vista
    if (hash === '#dashboard') cargarDashboard();
    if (hash === '#partidos') cargarPartidos();
    if (hash === '#usuarios') cargarUsuarios();
    if (hash === '#tickets') cargarTickets();
}

// --- 2. Carga de Datos y Renderizado ---
async function cargarDashboard() {
    try {
        const stats = await api.fetch('/admin/dashboard-stats');
        document.getElementById('kpi-container').innerHTML = `
            <div class="kpi-card" style="border-left-color: #3b82f6;">
                <h4>Usuarios Activos</h4>
                <div class="value">${stats.usuariosActivos.toLocaleString()}</div>
            </div>
            <div class="kpi-card" style="border-left-color: #10b981;">
                <h4>Ingresos Netos</h4>
                <div class="value">$${stats.recaudacion.toLocaleString()}</div>
            </div>
            <div class="kpi-card" style="border-left-color: #8b5cf6;">
                <h4>Tickets Válidos</h4>
                <div class="value">${stats.ticketsVendidos.toLocaleString()}</div>
            </div>
            <div class="kpi-card" style="border-left-color: #f43f5e;">
                <h4>Tickets Anulados</h4>
                <div class="value">${stats.ticketsAnulados.toLocaleString()}</div>
            </div>
            <div class="kpi-card" style="border-left-color: #f59e0b;">
                <h4>Partidos Agotados</h4>
                <div class="value">${stats.partidosAgotados}</div>
            </div>
            <div class="kpi-card" style="border-left-color: #06b6d4;">
                <h4>Ocupación Global</h4>
                <div class="value">${stats.ocupacion}%</div>
            </div>
        `;
    } catch (e) { console.error(e); }
}

// PARTIDOS
async function cargarPartidos() {
    try {
        const queryParam = estado.partidos.query ? `&search=${encodeURIComponent(estado.partidos.query)}` : '';
        const res = await api.fetch(`/admin/partidos?page=${estado.partidos.page}&pageSize=${estado.partidos.pageSize}${queryParam}`);
        estado.partidos.data = res.items;
        estado.partidos.total = res.total;
        renderPartidos();
    } catch (e) { console.error(e); }
}

function renderPartidos() {
    document.getElementById('tbody-partidos').innerHTML = estado.partidos.data.map(p => {
        let colorBadge = 'badge-activo';
        if (p.estado === 'Suspendido') colorBadge = 'badge-suspendido';
        else if (p.estado === 'Finalizado') colorBadge = 'badge-suspendido';
        else if (p.estado === 'En Juego') colorBadge = 'badge-en-juego';
        else if (p.estado === 'Oculto') colorBadge = 'badge-oculto';

        return `
        <tr>
            <td style="font-weight: 600;">#${p.id}</td>
            <td><div style="font-weight: 700; color: var(--color-primary-dark);">${ui.escapeHTML(p.equipoLocal)}</div>
                <div style="font-weight: 700; color: var(--color-primary-dark);">vs ${ui.escapeHTML(p.equipoVisitante)}</div></td>
            <td>${ui.escapeHTML(p.fase)}</td>
            <td>
                <span class="badge ${colorBadge}">${p.estado}</span>
            </td>
            <td style="font-weight: 600; ${p.entradasDisponibles === 0 ? 'color: var(--color-error);' : ''}">
                ${p.entradasDisponibles} <span style="color: #94a3b8; font-size: 0.85rem; font-weight: normal;">/ ${p.entradasMaximas || p.entradasDisponibles}</span>
            </td>
            <td><button class="btn btn-outline" style="color:var(--color-primary); border-color:var(--color-primary); padding: 0.3rem 0.6rem;" onclick="abrirModalPartido(${p.id})">Detalles</button></td>
        </tr>
    `}).join('');

    actualizarPaginacion('partidos', 'btn-prev-partidos', 'btn-next-partidos', 'page-info-partidos');
}

// USUARIOS
async function cargarUsuarios() {
    try {
        const queryParam = estado.usuarios.query ? `&search=${encodeURIComponent(estado.usuarios.query)}` : '';
        const res = await api.fetch(`/admin/usuarios?page=${estado.usuarios.page}&pageSize=${estado.usuarios.pageSize}${queryParam}`);
        estado.usuarios.data = res.items;
        estado.usuarios.total = res.total;
        renderUsuarios();
    } catch (e) { console.error(e); }
}

function renderUsuarios() {
    document.getElementById('tbody-usuarios').innerHTML = estado.usuarios.data.map(u => {
        const esActivo = u.activo;
        return `
        <tr>
            <td style="font-weight: 600;">#${u.id}</td>
            <td>${ui.escapeHTML(u.nombre)}</td>
            <td>${ui.escapeHTML(u.email)}</td>
            <td><span class="badge ${esActivo ? 'badge-activo' : 'badge-suspendido'}">${esActivo ? 'Activo' : 'Suspendido'}</span></td>
            <td>
                ${u.rol !== 'admin' ? `
                    <button class="btn ${esActivo ? 'btn-error' : 'btn-success'}" style="padding: 0.4rem 0.8rem; font-size: 0.8rem;" onclick="toggleUsuario(${u.id})">
                        ${esActivo ? 'Desactivar' : 'Reactivar'}
                    </button>
                ` : '<span style="font-size: 0.8rem; font-weight: 600; color: #94a3b8;">Superadmin</span>'}
            </td>
        </tr>`
    }).join('');

    actualizarPaginacion('usuarios', 'btn-prev-usuarios', 'btn-next-usuarios', 'page-info-usuarios');
}

// TICKETS
async function cargarTickets() {
    try {
        const queryParam = estado.tickets.query ? `&search=${encodeURIComponent(estado.tickets.query)}` : '';
        const res = await api.fetch(`/admin/tickets?page=${estado.tickets.page}&pageSize=${estado.tickets.pageSize}${queryParam}`);
        estado.tickets.data = res.items;
        estado.tickets.total = res.total;
        renderTickets();
    } catch (e) { console.error(e); }
}

function renderTickets() {
    document.getElementById('tbody-tickets').innerHTML = estado.tickets.data.map(t => {
        const esActivo = t.activo !== false;
        return `
        <tr>
            <td style="font-weight: 600;">#${t.id}</td>
            <td>${ui.escapeHTML(t.partidoInfo)}</td>
            <td>${ui.escapeHTML(t.usuarioEmail)}</td>
            <td><span style="font-weight: 600; color: var(--color-primary-dark);">${ui.escapeHTML(t.tipoEntrada)}</span></td>
            
            <td style="color: #10b981; font-weight: 700;">$${t.precio}</td>
            
            <td><span class="badge ${esActivo ? 'badge-activo' : 'badge-suspendido'}">${esActivo ? 'Válido' : 'Anulado'}</span></td>
            <td>
                <button class="btn ${esActivo ? 'btn-error' : 'btn-disabled'}" style="padding: 0.3rem 0.6rem; font-size: 0.8rem;" onclick="toggleTicket(${t.id})" ${!esActivo ? 'disabled' : ''}>
                    ${esActivo ? 'Anular Ticket' : 'No modificable'}
                </button>
            </td>
        </tr>`
    }).join('');

    actualizarPaginacion('tickets', 'btn-prev-tickets', 'btn-next-tickets', 'page-info-tickets');
}

// UTILIDAD: Maneja la lógica visual de los botones de paginación
function actualizarPaginacion(entidad, idPrev, idNext, idInfo) {
    const totalPages = Math.ceil(estado[entidad].total / estado[entidad].pageSize) || 1;
    document.getElementById(idInfo).textContent = `Página ${estado[entidad].page} de ${totalPages}`;

    // Bloquear botones usando la propiedad 'disabled' de HTML
    document.getElementById(idPrev).disabled = estado[entidad].page === 1;
    document.getElementById(idNext).disabled = estado[entidad].page >= totalPages;

    // Un poco de estilo visual para notar si el botón está desactivado
    document.getElementById(idPrev).style.opacity = estado[entidad].page === 1 ? '0.5' : '1';
    document.getElementById(idNext).style.opacity = estado[entidad].page >= totalPages ? '0.5' : '1';
}

// --- 3. Eventos y Modal ---
function setupEventListeners() {
    document.querySelectorAll('.menu-btn[data-hash]').forEach(btn => {
        btn.addEventListener('click', (e) => {
            window.location.hash = e.currentTarget.getAttribute('data-hash');
        });
    });

    // --- FUNCIÓN AUXILIAR PARA LOS BUSCADORES ---
    const configurarBuscador = (inputId, btnId, estadoEntidad, funcionCarga) => {
        const input = document.getElementById(inputId);
        const btn = document.getElementById(btnId);

        const ejecutarBusqueda = () => {
            estadoEntidad.query = input.value.trim();
            estadoEntidad.page = 1;
            funcionCarga();
        };

        btn.addEventListener('click', ejecutarBusqueda);

        input.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                e.preventDefault();
                ejecutarBusqueda();
            }
        });
    };

    configurarBuscador('search-partidos', 'btn-search-partidos', estado.partidos, cargarPartidos);
    configurarBuscador('search-usuarios', 'btn-search-usuarios', estado.usuarios, cargarUsuarios);
    configurarBuscador('search-tickets', 'btn-search-tickets', estado.tickets, cargarTickets);

    // Paginadores Partidos
    document.getElementById('btn-prev-partidos').addEventListener('click', () => { if (estado.partidos.page > 1) { estado.partidos.page--; cargarPartidos(); } });
    document.getElementById('btn-next-partidos').addEventListener('click', () => { estado.partidos.page++; cargarPartidos(); });

    // Paginadores Usuarios
    document.getElementById('btn-prev-usuarios').addEventListener('click', () => { if (estado.usuarios.page > 1) { estado.usuarios.page--; cargarUsuarios(); } });
    document.getElementById('btn-next-usuarios').addEventListener('click', () => { estado.usuarios.page++; cargarUsuarios(); });

    // Paginadores Tickets
    document.getElementById('btn-prev-tickets').addEventListener('click', () => { if (estado.tickets.page > 1) { estado.tickets.page--; cargarTickets(); } });
    document.getElementById('btn-next-tickets').addEventListener('click', () => { estado.tickets.page++; cargarTickets(); });
}
// Ventana de Gestión de Partido
window.abrirModalPartido = async function (id) {
    const partido = estado.partidos.data.find(p => p.id === id);
    if (!partido) return;
    estado.partidoSeleccionado = partido;

    document.getElementById('edit-id').value = partido.id;
    document.getElementById('edit-local').value = partido.equipoLocal;
    document.getElementById('edit-visitante').value = partido.equipoVisitante;
    document.getElementById('edit-fase').value = partido.fase;
    document.getElementById('edit-entradas').value = partido.entradasDisponibles;
    document.getElementById('edit-estado').value = partido.estado;
    document.getElementById('edit-estadio').value = partido.estadioId || 1;

    if (partido.fechaHora) {
        const f = new Date(partido.fechaHora);
        const dd = String(f.getDate()).padStart(2, '0');
        const mm = String(f.getMonth() + 1).padStart(2, '0');
        const yyyy = f.getFullYear();
        const hh = String(f.getHours()).padStart(2, '0');
        const mins = String(f.getMinutes()).padStart(2, '0');

        if (window.editFechaMask) {
            window.editFechaMask.value = `${dd}/${mm}/${yyyy} ${hh}:${mins}`;
        }
    } else {
        if (window.editFechaMask) window.editFechaMask.value = '';
    }

    document.getElementById('modal-partido').style.display = 'flex';
}

document.getElementById('form-editar-partido').addEventListener('submit', async (e) => {
    e.preventDefault();
    if (!estado.partidoSeleccionado) return;
    const id = estado.partidoSeleccionado.id;

    const fechaCruda = document.getElementById('edit-fecha').value;
    const fechaValidada = ui.parseFechaMascara(fechaCruda);

    if (!fechaValidada) {
        ui.showToast('Por favor completá la fecha y hora correctamente', 'error');
        return;
    }

    const datosEditados = {
        equipoLocal: document.getElementById('edit-local').value,
        equipoVisitante: document.getElementById('edit-visitante').value,
        fase: document.getElementById('edit-fase').value,
        fechaHora: fechaValidada,
        entradasDisponibles: parseInt(document.getElementById('edit-entradas').value),
        estadioId: parseInt(document.getElementById('edit-estadio').value),
        estado: document.getElementById('edit-estado').value
    };

    if (!(await ui.confirm("¿Guardar las modificaciones de este partido?"))) return;

    try {
        await api.fetch(`/admin/partidos/${id}`, {
            method: 'PUT',
            body: JSON.stringify(datosEditados)
        });
        ui.showToast("Partido actualizado con éxito", "success");
        cerrarModal();
        cargarPartidos();
    } catch (e) {
        ui.showToast(e.message || "Error al modificar el partido.", "error");
    }
});

window.cerrarModal = function () {
    document.getElementById('modal-partido').style.display = 'none';
    estado.partidoSeleccionado = null;
}

document.getElementById('btn-eliminar-partido').addEventListener('click', async () => {
    if (!estado.partidoSeleccionado) return;

    if (!(await ui.confirm("⚠️ PELIGRO: ¿Estás seguro de eliminar físicamente este partido?"))) return;

    try {
        const respuesta = await api.fetch(`/admin/partidos/${estado.partidoSeleccionado.id}`, { method: 'DELETE' });

        cerrarModal();
        ui.showToast(respuesta.mensaje || "Partido eliminado", "success");
        cargarPartidos();

    } catch (error) {
        ui.showToast(error.message || "Error al eliminar el partido.", "error");
    }
});



// Botones Globales (Usuario y Ticket)
window.toggleUsuario = async function (id) {
    if (!(await ui.confirm("¿Modificar el acceso de este usuario?"))) return;
    try {
        await api.fetch('/admin/usuarios/' + id + '/toggle-estado', { method: 'PUT' });
        ui.showToast("Estado de usuario actualizado", "success");
        cargarUsuarios();
    } catch (e) {
        ui.showToast("Error al modificar usuario.", "error");
    }
}

window.toggleTicket = async function (id) {
    if (!(await ui.confirm("¿Anular este ticket y restaurar el stock del partido? Esta acción es irreversible."))) return;
    try {
        await api.fetch('/admin/tickets/' + id + '/toggle', { method: 'PUT' });
        ui.showToast("Ticket anulado correctamente", "success");
        cargarTickets();
    } catch (e) {
        ui.showToast("Error al anular el ticket.", "error");
    }
}

// --- SISTEMA DE CREACIÓN DE PARTIDOS ---

window.abrirModalCrear = function () {
    document.getElementById('form-crear-partido').reset();
    document.getElementById('modal-crear-partido').style.display = 'flex';
}

window.cerrarModalCrear = function () {
    document.getElementById('modal-crear-partido').style.display = 'none';
}

document.getElementById('form-crear-partido').addEventListener('submit', async (e) => {
    e.preventDefault();

    const local = document.getElementById('crear-local').value.trim();
    const visitante = document.getElementById('crear-visitante').value.trim();

    const fechaCruda = document.getElementById('crear-fecha').value;
    const fechaValidada = ui.parseFechaMascara(fechaCruda);

    if (!fechaValidada) {
        ui.showToast('Por favor completá la fecha y hora correctamente', 'error');
        return;
    }

    if (new Date(fechaValidada) < new Date()) {
        ui.showToast('No podés programar un partido en el pasado', 'error');
        return;
    }

    if (local.toLowerCase() === visitante.toLowerCase()) {
        ui.showToast('Los equipos no pueden ser iguales', 'error');
        return;
    }

    const nuevoPartido = {
        equipoLocal: local,
        equipoVisitante: visitante,
        fase: document.getElementById('crear-fase').value,
        fechaHora: fechaValidada,
        entradasDisponibles: parseInt(document.getElementById('crear-entradas').value),
        estadioId: parseInt(document.getElementById('crear-estadio').value)
    };

    if (!(await ui.confirm(`¿Confirmar la creación del partido ${local} vs ${visitante}?`))) return;

    try {
        await api.fetch('/admin/partidos', {
            method: 'POST',
            body: JSON.stringify(nuevoPartido)
        });

        ui.showToast('Partido programado con éxito', 'success');
        cerrarModalCrear();

        estado.partidos.page = 1;
        cargarPartidos();
        cargarDashboard();

    } catch (error) {
        ui.showToast(error.message || 'Error al conectar con el servidor', 'error');
    }
});

// Inicialización
if (document.readyState === 'loading') document.addEventListener('DOMContentLoaded', initAdmin);
else initAdmin();
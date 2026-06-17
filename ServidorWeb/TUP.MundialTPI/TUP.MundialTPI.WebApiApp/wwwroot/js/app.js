import { MOCK_PARTIDOS } from './mockData.js';

// --- AUTH LAYER ---
function getAuthToken() {
    return localStorage.getItem('jwt');
}

function setAuth(token, user) {
    localStorage.setItem('jwt', token);
    localStorage.setItem('user', JSON.stringify(user));
    actualizarNavbar();
}

function clearAuth() {
    localStorage.removeItem('jwt');
    localStorage.removeItem('user');
    actualizarNavbar();
}

async function fetchWithAuth(url, options = {}) {
    const token = getAuthToken();
    if (token) {
        options.headers = {
            ...options.headers,
            'Authorization': `Bearer ${token}`
        };
    }
    
    const response = await fetch(url, options);
    if (response.status === 401) {
        clearAuth();
        abrirModal('login-modal');
        document.getElementById('login-error').innerText = 'Tu sesión expiró. Inicia sesión nuevamente.';
        document.getElementById('login-error').style.display = 'block';
        throw new Error('Unauthorized');
    }
    return response;
}

// --- DATA LAYER ---
async function fetchPartidos() {
    try {
        const response = await fetch('/api/partidos');
        if (!response.ok) throw new Error('Network response was not ok');
        return await response.json();
    } catch (error) {
        console.error('Fetch error:', error);
        throw error;
    }
}

// --- PRESENTATION LAYER ---
function formatDate(dateString) {
    const options = { weekday: 'short', year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' };
    return new Date(dateString).toLocaleDateString('es-ES', options);
}

function createMatchCard(partido) {
    const isSoldOut = partido.entradasDisponibles === 0;
    const btnText = isSoldOut ? 'Agotado' : 'Comprar Entrada';
    const btnClass = isSoldOut ? 'btn-disabled' : 'btn-primary';

    return `
        <article class="card match-card" data-id="${partido.id}">
            <div class="card-image-wrapper">
                <img src="${partido.estadio.imagenUrl}" alt="Estadio ${partido.estadio.nombre}" class="card-image" loading="lazy">
                <span class="badge phase-badge">${partido.fase}</span>
            </div>
            <div class="card-content">
                <div class="match-teams">
                    <span class="team">${partido.equipoLocal}</span>
                    <span class="vs">vs</span>
                    <span class="team">${partido.equipoVisitante}</span>
                </div>
                <div class="match-details">
                    <p class="detail-item">
                        <span class="icon">📅</span> ${formatDate(partido.fechaHora)}
                    </p>
                    <p class="detail-item">
                        <span class="icon">🏟</span> ${partido.estadio.nombre}, ${partido.estadio.ciudad}
                    </p>
                    <p class="detail-item ${isSoldOut ? 'text-error' : 'text-success'}">
                        <span class="icon">🎟</span> ${isSoldOut ? 'Sin entradas disponibles' : `${partido.entradasDisponibles} entradas disponibles`}
                    </p>
                </div>
                <div class="card-footer">
                    <button class="btn ${btnClass} btn-full" ${isSoldOut ? 'disabled' : ''} onclick="intentarComprar(${partido.id})">${btnText}</button>
                </div>
            </div>
        </article>
    `;
}

function renderPartidos(partidos) {
    const container = document.getElementById('matches-grid');
    if (!partidos || partidos.length === 0) {
        container.innerHTML = '<p class="no-results" style="grid-column: 1/-1; text-align: center; color: var(--color-text-muted);">No se encontraron partidos disponibles.</p>';
        return;
    }
    container.innerHTML = partidos.map(p => createMatchCard(p)).join('');
}

function actualizarNavbar() {
    const token = getAuthToken();
    const authButtons = document.getElementById('auth-buttons');
    const userMenu = document.getElementById('user-menu');
    const userGreeting = document.getElementById('user-greeting');
    
    const linkAdmin = document.getElementById('link-admin');
    const linkMisTickets = document.getElementById('link-mis-tickets');

    if (token) {
        const user = JSON.parse(localStorage.getItem('user'));
        authButtons.style.display = 'none';
        userMenu.style.display = 'flex';
        userGreeting.innerText = `Hola, ${user.nombre}`;
        if (linkMisTickets) linkMisTickets.style.display = 'block';
        if (user.rol === 'admin' && linkAdmin) {
            linkAdmin.style.display = 'block';
        } else if (linkAdmin) {
            linkAdmin.style.display = 'none';
        }
    } else {
        authButtons.style.display = 'flex';
        userMenu.style.display = 'none';
        if (linkAdmin) linkAdmin.style.display = 'none';
        if (linkMisTickets) linkMisTickets.style.display = 'none';
    }
}

// --- INIT APP ---
async function init() {
    actualizarNavbar();
    try {
        const partidos = await fetchPartidos();
        renderPartidos(partidos);
    } catch (error) {
        console.error('Error fetching matches:', error);
        document.getElementById('matches-grid').innerHTML = '<p class="error-msg" style="grid-column: 1/-1; color: var(--color-error); text-align: center;">Error al cargar el catálogo de partidos.</p>';
    }
}

// --- LOGICA DE COMPRA ---
let partidoSeleccionado = null;
let pendingPartidoId = null; // Para reabrir modal de compra post-login

window.intentarComprar = function(partidoId) {
    if (!getAuthToken()) {
        pendingPartidoId = partidoId;
        abrirModal('login-modal');
    } else {
        abrirModalCompra(partidoId);
    }
}

window.abrirModalCompra = async function(partidoId) {
    const modal = document.getElementById('buy-modal');
    const modalBody = document.getElementById('modal-body');
    
    modalBody.innerHTML = '<div class="loader"></div>';
    abrirModal('buy-modal');

    try {
        const response = await fetch(`/api/partidos/${partidoId}`);
        partidoSeleccionado = await response.json();
        
        modalBody.innerHTML = `
            <p><strong>Partido:</strong> ${partidoSeleccionado.equipoLocal} vs ${partidoSeleccionado.equipoVisitante}</p>
            <p><strong>Fecha:</strong> ${formatDate(partidoSeleccionado.fechaHora)}</p>
            <hr style="margin: 1rem 0; opacity: 0.2;">
            <div style="margin-bottom: 1rem;">
                <label>Tipo de entrada:</label>
                <select id="tipo-entrada" class="input-search" style="width: 100%; margin-top: 0.5rem;">
                    <option value="General">General ($250)</option>
                    <option value="Platea">Platea ($400)</option>
                    <option value="VIP">VIP ($800)</option>
                </select>
            </div>
            <div>
                <label>Cantidad:</label>
                <input type="number" id="cantidad-entradas" value="1" min="1" max="${partidoSeleccionado.entradasDisponibles}" class="input-search" style="width: 100%; margin-top: 0.5rem;">
            </div>
        `;
    } catch (e) {
        modalBody.innerHTML = '<p class="error-msg">Error al cargar detalle del partido.</p>';
    }
}

// --- MANEJO DE MODALES GLOBAL ---
function abrirModal(id) {
    document.querySelectorAll('.modal-overlay').forEach(m => m.style.display = 'none');
    document.getElementById(id).style.display = 'flex';
}

function cerrarModales() {
    document.querySelectorAll('.modal-overlay').forEach(m => m.style.display = 'none');
    partidoSeleccionado = null;
    document.getElementById('login-error').style.display = 'none';
    document.getElementById('register-error').style.display = 'none';
}

// Cerrar con Esc y Click fuera
window.addEventListener('keydown', (e) => {
    if (e.key === 'Escape') cerrarModales();
});
document.querySelectorAll('.modal-overlay').forEach(modal => {
    modal.addEventListener('click', (e) => {
        if (e.target === modal) cerrarModales();
    });
});

// Eventos de botones
document.querySelectorAll('.btn-close').forEach(btn => btn.addEventListener('click', cerrarModales));
document.getElementById('btn-cancel')?.addEventListener('click', cerrarModales);
document.getElementById('btn-show-login')?.addEventListener('click', () => { pendingPartidoId = null; abrirModal('login-modal'); });
document.getElementById('btn-show-register')?.addEventListener('click', () => abrirModal('register-modal'));
document.getElementById('link-to-register')?.addEventListener('click', (e) => { e.preventDefault(); abrirModal('register-modal'); });
document.getElementById('link-to-login')?.addEventListener('click', (e) => { e.preventDefault(); abrirModal('login-modal'); });
document.getElementById('btn-logout')?.addEventListener('click', () => { clearAuth(); alert('Sesión cerrada correctamente'); });

// Evento confirmar compra
document.getElementById('btn-confirm')?.addEventListener('click', async () => {
    if (!partidoSeleccionado) return;
    
    const cantidad = document.getElementById('cantidad-entradas').value;
    const tipo = document.getElementById('tipo-entrada').value;

    const dto = {
        partidoId: partidoSeleccionado.id,
        tipoEntrada: tipo,
        cantidad: parseInt(cantidad)
    };

    try {
        const response = await fetchWithAuth('/api/tickets', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(dto)
        });

        if (response.ok) {
            alert('¡Compra exitosa! Ticket generado.');
            cerrarModales();
            init();
        } else {
            const err = await response.text();
            alert('Error en compra: ' + err);
        }
    } catch (e) {
        if(e.message !== 'Unauthorized') alert('Error de conexión al procesar la compra.');
    }
});

// Eventos Auth
document.getElementById('btn-login-submit')?.addEventListener('click', async () => {
    const email = document.getElementById('login-email').value;
    const password = document.getElementById('login-password').value;
    
    try {
        const response = await fetch('/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });
        
        if (response.ok) {
            const data = await response.json();
            setAuth(data.token, data.user);
            cerrarModales();
            
            if (pendingPartidoId) {
                abrirModalCompra(pendingPartidoId);
                pendingPartidoId = null;
            }
        } else {
            const err = await response.json();
            document.getElementById('login-error').innerText = err.message || 'Error al iniciar sesión';
            document.getElementById('login-error').style.display = 'block';
        }
    } catch (e) {
        document.getElementById('login-error').innerText = 'Error de red';
        document.getElementById('login-error').style.display = 'block';
    }
});

document.getElementById('btn-register-submit')?.addEventListener('click', async () => {
    const nombre = document.getElementById('register-nombre').value;
    const email = document.getElementById('register-email').value;
    const password = document.getElementById('register-password').value;
    
    try {
        const response = await fetch('/api/auth/register', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ nombre, email, password })
        });
        
        if (response.ok) {
            alert('Registro exitoso. Ahora puedes iniciar sesión.');
            abrirModal('login-modal');
        } else {
            const err = await response.json();
            document.getElementById('register-error').innerText = err.message || 'Error al registrarse';
            document.getElementById('register-error').style.display = 'block';
        }
    } catch (e) {
        document.getElementById('register-error').innerText = 'Error de red';
        document.getElementById('register-error').style.display = 'block';
    }
});

document.addEventListener('DOMContentLoaded', init);

// --- LOGICA ADMIN ---
function toggleAdminDashboard(show) {
    const hero = document.querySelector('.hero');
    const partidosSection = document.getElementById('partidos');
    const adminDashboard = document.getElementById('admin-dashboard');
    const misTicketsSection = document.getElementById('mis-tickets-section');

    if (show) {
        if (hero) hero.style.display = 'none';
        if (partidosSection) partidosSection.style.display = 'none';
        if (misTicketsSection) misTicketsSection.style.display = 'none';
        if (adminDashboard) adminDashboard.style.display = 'block';
        loadAdminData();
    } else {
        if (hero) hero.style.display = ''; 
        if (partidosSection) partidosSection.style.display = 'block';
        if (adminDashboard) adminDashboard.style.display = 'none';
        if (misTicketsSection) misTicketsSection.style.display = 'none';
        init();
    }
}

function toggleMisTicketsDashboard(show) {
    const hero = document.querySelector('.hero');
    const partidosSection = document.getElementById('partidos');
    const adminDashboard = document.getElementById('admin-dashboard');
    const misTicketsSection = document.getElementById('mis-tickets-section');

    if (show) {
        if (hero) hero.style.display = 'none';
        if (partidosSection) partidosSection.style.display = 'none';
        if (adminDashboard) adminDashboard.style.display = 'none';
        if (misTicketsSection) misTicketsSection.style.display = 'block';
        loadMisTickets();
    }
}

async function loadMisTickets() {
    try {
        const tbody = document.getElementById('tbody-mis-tickets');
        tbody.innerHTML = '<tr><td colspan="7" style="text-align:center; padding:1rem;">Cargando...</td></tr>';
        
        const res = await fetchWithAuth('/api/tickets/mis-tickets');
        const tickets = await res.json();
        
        if (tickets.length === 0) {
            tbody.innerHTML = '<tr><td colspan="7" style="text-align:center; padding:1rem; color: var(--color-text-muted);">No tienes tickets comprados aún.</td></tr>';
            return;
        }

        tbody.innerHTML = tickets.map(t => `
            <tr style="border-bottom: 1px solid rgba(255,255,255,0.1);">
                <td style="padding: 0.5rem;">#${t.id}</td>
                <td style="padding: 0.5rem;">${t.partido}</td>
                <td style="padding: 0.5rem;">${t.estadio}</td>
                <td style="padding: 0.5rem;">${formatDate(t.fechaPartido)}</td>
                <td style="padding: 0.5rem;">${t.tipoEntrada}</td>
                <td style="padding: 0.5rem;">$${t.precio}</td>
                <td style="padding: 0.5rem;">${formatDate(t.fechaCompra)}</td>
            </tr>
        `).join('');
    } catch (e) {
        console.error(e);
        document.getElementById('tbody-mis-tickets').innerHTML = '<tr><td colspan="7" style="text-align:center; padding:1rem; color: var(--color-error);">Error al cargar tus tickets.</td></tr>';
    }
}

document.getElementById('link-admin')?.addEventListener('click', (e) => {
    e.preventDefault();
    toggleAdminDashboard(true);
});

document.getElementById('link-mis-tickets')?.addEventListener('click', (e) => {
    e.preventDefault();
    toggleMisTicketsDashboard(true);
});

document.getElementById('link-catalogo')?.addEventListener('click', (e) => {
    e.preventDefault();
    toggleAdminDashboard(false);
});

document.getElementById('btn-volver-catalogo')?.addEventListener('click', () => {
    toggleAdminDashboard(false);
});
document.getElementById('link-catalogo')?.addEventListener('click', () => {
    toggleAdminDashboard(false);
});

document.querySelectorAll('.tab-btn').forEach(btn => {
    btn.addEventListener('click', (e) => {
        document.querySelectorAll('.tab-btn').forEach(b => { b.classList.remove('active'); b.style.opacity = '0.6'; });
        e.target.classList.add('active');
        e.target.style.opacity = '1';
        
        document.querySelectorAll('.tab-content').forEach(c => c.style.display = 'none');
        document.getElementById(e.target.getAttribute('data-target')).style.display = 'block';
    });
});

async function loadAdminData() {
    try {
        const resPartidos = await fetchWithAuth('/api/admin/partidos');
        const partidos = await resPartidos.json();
        const tbodyPartidos = document.getElementById('tbody-partidos');
        if(tbodyPartidos) tbodyPartidos.innerHTML = partidos.map(p => `
            <tr style="border-bottom: 1px solid rgba(255,255,255,0.1);">
                <td style="padding: 0.5rem;">${p.id}</td>
                <td style="padding: 0.5rem;">${p.equipoLocal}</td>
                <td style="padding: 0.5rem;">${p.equipoVisitante}</td>
                <td style="padding: 0.5rem;">${formatDate(p.fechaHora)}</td>
                <td style="padding: 0.5rem;">${p.fase}</td>
                <td style="padding: 0.5rem;">${p.entradasDisponibles}</td>
                <td style="padding: 0.5rem;">
                    <button class="btn btn-secondary" style="padding: 0.2rem 0.5rem; font-size: 0.9rem;" onclick="abrirEditarPartido(${p.id})">Editar</button>
                    <button class="btn btn-primary" style="padding: 0.2rem 0.5rem; font-size: 0.9rem; background: var(--color-error); margin-left: 0.5rem;" onclick="eliminarPartido(${p.id})">Borrar</button>
                </td>
            </tr>
        `).join('');

        const resUsuarios = await fetchWithAuth('/api/admin/usuarios');
        const usuarios = await resUsuarios.json();
        const tbodyUsuarios = document.getElementById('tbody-usuarios');
        if(tbodyUsuarios) tbodyUsuarios.innerHTML = usuarios.map(u => `
            <tr style="border-bottom: 1px solid rgba(255,255,255,0.1);">
                <td style="padding: 0.5rem;">${u.id}</td>
                <td style="padding: 0.5rem;">${u.nombre}</td>
                <td style="padding: 0.5rem;">${u.email}</td>
                <td style="padding: 0.5rem;">${u.rol}</td>
            </tr>
        `).join('');

        const resTickets = await fetchWithAuth('/api/admin/tickets');
        const tickets = await resTickets.json();
        const tbodyTickets = document.getElementById('tbody-tickets');
        if(tbodyTickets) tbodyTickets.innerHTML = tickets.map(t => `
            <tr style="border-bottom: 1px solid rgba(255,255,255,0.1);">
                <td style="padding: 0.5rem;">${t.id}</td>
                <td style="padding: 0.5rem;">${t.partidoInfo}</td>
                <td style="padding: 0.5rem;">${t.usuarioEmail}</td>
                <td style="padding: 0.5rem;">${t.tipoEntrada}</td>
                <td style="padding: 0.5rem;">$${t.precio}</td>
                <td style="padding: 0.5rem;">${formatDate(t.fechaCompra)}</td>
            </tr>
        `).join('');
    } catch (e) {
        console.error("Error loading admin data", e);
    }
}

window.eliminarPartido = async function(id) {
    if(!confirm("¿Seguro que deseas eliminar el partido " + id + "?")) return;
    try {
        const res = await fetchWithAuth('/api/admin/partidos/' + id, { method: 'DELETE' });
        if(res.ok) {
            loadAdminData();
        } else {
            const data = await res.json();
            alert("Error al eliminar: " + (data.mensaje || "Error"));
        }
    } catch (e) {
        if(e.message !== 'Unauthorized') alert("Error al eliminar");
    }
}

window.abrirEditarPartido = async function(id) {
    try {
        const res = await fetch('/api/partidos/' + id);
        const p = await res.json();
        document.getElementById('partido-id').value = p.id;
        document.getElementById('partido-local').value = p.equipoLocal;
        document.getElementById('partido-visitante').value = p.equipoVisitante;
        document.getElementById('partido-fecha').value = p.fechaHora.substring(0,16);
        document.getElementById('partido-fase').value = p.fase;
        document.getElementById('partido-estadio').value = p.estadioId;
        document.getElementById('partido-entradas').value = p.entradasDisponibles;
        
        document.getElementById('partido-modal-title').innerText = 'Editar Partido';
        document.getElementById('partido-error').style.display = 'none';
        abrirModal('partido-modal');
    } catch (e) {
        console.error(e);
    }
}

document.getElementById('btn-crear-partido')?.addEventListener('click', () => {
    document.getElementById('partido-id').value = '';
    document.getElementById('partido-local').value = '';
    document.getElementById('partido-visitante').value = '';
    document.getElementById('partido-fecha').value = '';
    document.getElementById('partido-fase').value = '';
    document.getElementById('partido-estadio').value = '';
    document.getElementById('partido-entradas').value = '0';
    document.getElementById('partido-modal-title').innerText = 'Nuevo Partido';
    document.getElementById('partido-error').style.display = 'none';
    abrirModal('partido-modal');
});

document.getElementById('btn-save-partido')?.addEventListener('click', async () => {
    const id = document.getElementById('partido-id').value;
    
    // Validate empty string parsing to null or handling manual validation gracefully
    const estadioIdStr = document.getElementById('partido-estadio').value;
    
    const dto = {
        equipoLocal: document.getElementById('partido-local').value,
        equipoVisitante: document.getElementById('partido-visitante').value,
        fechaHora: document.getElementById('partido-fecha').value || null,
        fase: document.getElementById('partido-fase').value,
        estadioId: estadioIdStr ? parseInt(estadioIdStr) : null,
        entradasDisponibles: parseInt(document.getElementById('partido-entradas').value || 0)
    };

    const url = id ? '/api/admin/partidos/' + id : '/api/admin/partidos';
    const method = id ? 'PUT' : 'POST';

    try {
        const res = await fetchWithAuth(url, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(dto)
        });
        if(res.ok) {
            cerrarModales();
            loadAdminData();
        } else {
            const data = await res.json();
            // Handle DataAnnotations errors correctly
            let msg = data.mensaje;
            if(!msg && data.errors) {
                msg = Object.values(data.errors).flat().join('<br>');
            }
            document.getElementById('partido-error').innerHTML = msg || "Error de validación";
            document.getElementById('partido-error').style.display = 'block';
        }
    } catch (e) {
        if(e.message !== 'Unauthorized') {
            document.getElementById('partido-error').innerText = "Error de conexión";
            document.getElementById('partido-error').style.display = 'block';
        }
    }
});

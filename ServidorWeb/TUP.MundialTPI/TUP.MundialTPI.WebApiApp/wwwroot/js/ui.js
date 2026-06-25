import { auth } from './auth.js';
export const ui = {
    setupNavbar() {
        const navLeft = document.getElementById('nav-left');
        const navRight = document.getElementById('nav-right');

        if (auth.isAuthenticated()) {
            const user = auth.getUser();

            // PASO 1: Empezamos creando el botón normal de "Mis Entradas"
            let linksIzquierda = `<a href="/mis_tickets.html" class="nav-link">Mis Entradas</a>`;

            // PASO 2: ACÁ ESTÁ LA MAGIA. Leemos el "rol" del usuario. 
            // Si dice "admin", le sumamos el botón del Panel Admin a los links.
            if (user && user.rol === 'admin') {
                linksIzquierda += `<a href="/admin.html" class="nav-link" style="color: var(--color-accent); font-weight: 800; margin-left: 1rem;">Panel Admin</a>`;
            }

            // Inyectamos los links terminados en la barra izquierda
            navLeft.innerHTML += linksIzquierda;

            // La barra derecha queda igual (Nombre y botón Salir)
            navRight.innerHTML = `
                <span style="font-weight: 600; color: white; margin-right: 1.5rem;">Hola, ${this.escapeHTML(user.nombre)}</span>
                <button id="btn-logout" class="btn btn-error">Salir</button>
            `;
            document.getElementById('btn-logout').addEventListener('click', () => {
                auth.clearSession();
                window.location.reload();
            });
        } else {
            navRight.innerHTML = `
                <a href="/login.html" class="btn btn-outline" style="margin-right: 0.5rem;">Iniciar Sesión</a>
                <a href="/login.html?modo=registro" class="btn btn-accent">Registrarme</a>
            `;
        }
    },
    escapeHTML(str) {
        if (!str) return '';
        const div = document.createElement('div');
        div.textContent = str;
        return div.innerHTML;
    },
    sanitizeInput(str) {
        if (!str) return '';
        return str.replace(/[^\w\s@.,áéíóúÁÉÍÓÚñÑ\-]/gi, '').trim();
    },
    createMatchCard(partido) {
        const local = this.escapeHTML(partido.equipoLocal);
        const visitante = this.escapeHTML(partido.equipoVisitante);
        const fase = this.escapeHTML(partido.fase);

        const codes = {
            'argentina': 'ar', 'brasil': 'br', 'francia': 'fr',
            'españa': 'es', 'méxico': 'mx', 'alemania': 'de',
            'canadá': 'ca', 'italia': 'it'
        };
        const flagL = codes[local.toLowerCase()] || 'un';
        const flagV = codes[visitante.toLowerCase()] || 'un';
        const agotado = partido.entradasDisponibles === 0;

        const article = document.createElement('article');
        article.className = 'card shadow-md';
        article.innerHTML = `
            <div style="position: relative; height: 160px; display: flex; background: #eee; overflow: hidden;">
                <span class="phase-badge">${fase}</span>
                <img src="https://flagcdn.com/w640/${flagL}.png" alt="${local}" style="width: 55%; height: 100%; object-fit: cover; clip-path: polygon(0 0, 100% 0, 80% 100%, 0% 100%); z-index: 2;">
                <img src="https://flagcdn.com/w640/${flagV}.png" alt="${visitante}" style="width: 55%; height: 100%; object-fit: cover; position: absolute; right: 0; z-index: 1;">
                <div style="position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); background: white; width: 40px; height: 40px; border-radius: 50%; display: flex; align-items: center; justify-content: center; z-index: 3; font-weight: 900; box-shadow: 0 4px 10px rgba(0,0,0,0.3); color: var(--color-accent);">VS</div>
            </div>
            <div class="card-content">
                <h3 style="text-align: center; color: var(--color-primary-dark); font-size: 1.2rem;">${local} vs ${visitante}</h3>
                <div>
                    <p>🏟 ${this.escapeHTML(partido.estadio?.nombre || 'Por definir')}</p>
                    <p class="${agotado ? 'text-error' : 'text-success'}">🎟 ${agotado ? 'Agotado' : `${partido.entradasDisponibles} disponibles`}</p>
                </div>
                <a href="/detalle.html?id=${partido.id}" class="btn ${agotado ? 'btn-disabled' : 'btn-accent'} btn-full">${agotado ? 'Ver detalles' : 'Comprar Entrada'}</a>
            </div>
        `;
        return article;
    }
};
import { auth } from './auth.js';
import { obtenerUrlBandera } from './banderas.js';

export const ui = {
    setupNavbar() {
        const navLeft = document.getElementById('nav-left');
        const navRight = document.getElementById('nav-right');

        if (!navLeft || !navRight) {
            return;
        }

        if (auth.isAuthenticated()) {
            const user = auth.getUser();

            let linksIzquierda = `<a href="/mis_tickets.html" class="nav-link">Mis Entradas</a>`;

            if (user && user.rol === 'admin') {
                if (!navLeft.innerHTML.includes('admin.html')) {
                    linksIzquierda += `<a href="/admin.html" class="nav-link" style="color: var(--color-accent); font-weight: 800; margin-left: 1rem;">Panel Admin</a>`;
                }
            }

            if (!navLeft.innerHTML.includes('mis_tickets.html')) {
                navLeft.innerHTML += linksIzquierda;
            }

            navRight.innerHTML = `
                <div style="display: flex; align-items: center; gap: 0.8rem;">
                    <span style="font-weight: 600; color: white; margin-right: 0.5rem;">Hola, ${this.escapeHTML(user.nombre)}</span>
                    
                    <button id="btn-qr-login" class="btn" style="background-color: #3498db; color: white; border: none; display: inline-flex; align-items: center; justify-content: center; height: 42px; padding: 0 1rem;">
                        QR
                    </button>
                    
                    <button id="btn-logout" class="btn btn-error" style="display: inline-flex; align-items: center; justify-content: center; height: 42px; padding: 0 1rem;">
                        Salir
                    </button>
                </div>
            `;

            document.getElementById('btn-logout').addEventListener('click', () => {
                auth.clearSession();
                window.location.reload();
            });

            const btnQr = document.getElementById('btn-qr-login');
            if (btnQr) {
                btnQr.addEventListener('click', () => {
                    const token = auth.getToken();
                    const user = auth.getUser();

                    if (!token || !user) {
                        ui.showToast("Error: No se encontró la sesión.", "error");
                        return;
                    }

                    const qrData = JSON.stringify({
                        token: token,
                        nombre: user.nombre
                    });

                    const overlay = document.createElement('div');
                    Object.assign(overlay.style, {
                        position: 'fixed', top: '0', left: '0', width: '100%', height: '100%',
                        background: 'rgba(15, 23, 42, 0.7)', display: 'flex', justifyContent: 'center',
                        alignItems: 'center', zIndex: '9999', backdropFilter: 'blur(3px)',
                        opacity: '0', transition: 'opacity 0.2s ease'
                    });

                    const modal = document.createElement('div');
                    Object.assign(modal.style, {
                        background: 'white', padding: '2.5rem', borderRadius: '16px',
                        textAlign: 'center', boxShadow: '0 20px 25px rgba(0,0,0,0.2)',
                        transform: 'scale(0.9)', transition: 'transform 0.2s ease'
                    });

                    modal.innerHTML = `
                        <h3 style="color: var(--color-primary-dark); margin-bottom: 0.5rem; font-size: 1.5rem; font-weight: 800;">Ingreso Rápido</h3>
                        <p style="color: #666; margin-bottom: 1.5rem; font-size: 0.95rem;">Escaneá este código con la cámara<br>de la app móvil para entrar.</p>
                        <div id="qr-contenedor" style="display: flex; justify-content: center; margin-bottom: 1.5rem; padding: 1rem; background: white; border: 2px solid #eee; border-radius: 12px;"></div>
                        <button id="btn-cerrar-qr" class="btn btn-secondary" style="width: 100%;">Cerrar</button>
                    `;

                    overlay.appendChild(modal);
                    document.body.appendChild(overlay);

                    requestAnimationFrame(() => {
                        overlay.style.opacity = '1';
                        modal.style.transform = 'scale(1)';
                    });

                    new QRCode(document.getElementById("qr-contenedor"), {
                        text: qrData, 
                        width: 300,
                        height: 300,
                        colorDark: "#1e293b",
                        colorLight: "#ffffff",
                        correctLevel: QRCode.CorrectLevel.L
                    });

                    document.getElementById('btn-cerrar-qr').addEventListener('click', () => {
                        overlay.style.opacity = '0';
                        modal.style.transform = 'scale(0.9)';
                        setTimeout(() => document.body.removeChild(overlay), 200);
                    });
                });
            }
        } else {
            navRight.innerHTML = `
                <a href="/login.html" class="btn btn-primary" style="margin-right: 0.5rem;">Iniciar Sesión</a>
                <a href="/login.html?modo=registro" class="btn btn-accent">Registrarme</a>
            `;
        }

        const btnHamburguesa = document.getElementById('btn-hamburguesa');
        const navMenu = document.getElementById('nav-menu');

        if (btnHamburguesa && navMenu) {
            btnHamburguesa.onclick = null;

            btnHamburguesa.onclick = () => {
                navMenu.classList.toggle('activo');
            };
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
        const agotado = partido.entradasDisponibles === 0;

        const urlBanderaL = obtenerUrlBandera(local);
        const urlBanderaV = obtenerUrlBandera(visitante);

        const article = document.createElement('article');
        article.className = 'card shadow-md';

        article.innerHTML = `
            <div style="position: relative; height: 160px; display: flex; background: #eee; overflow: hidden;">
                <span class="phase-badge">${fase}</span>
                <img src="${urlBanderaL}" alt="${local}" style="width: 55%; height: 100%; object-fit: cover; clip-path: polygon(0 0, 100% 0, 80% 100%, 0% 100%); z-index: 2;">
                <img src="${urlBanderaV}" alt="${visitante}" style="width: 55%; height: 100%; object-fit: cover; position: absolute; right: 0; z-index: 1;">
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
    },

    showToast(mensaje, tipo = 'success') {
        let container = document.getElementById('toast-container');
        if (!container) {
            container = document.createElement('div');
            container.id = 'toast-container';
            document.body.appendChild(container);
            Object.assign(container.style, {
                position: 'fixed', top: '20px', right: '20px', zIndex: '9999',
                display: 'flex', flexDirection: 'column', gap: '10px'
            });
        }

        const toast = document.createElement('div');
        toast.textContent = mensaje;

        const bgColor = tipo === 'error' ? '#e74c3c' : (tipo === 'info' ? '#3498db' : '#10b981');

        Object.assign(toast.style, {
            background: bgColor, color: 'white', padding: '12px 24px',
            borderRadius: '8px', boxShadow: '0 4px 12px rgba(0,0,0,0.15)',
            fontFamily: 'Inter, sans-serif', fontSize: '0.95rem', fontWeight: '600',
            opacity: '0', transform: 'translateY(-20px)', transition: 'all 0.3s ease'
        });

        container.appendChild(toast);

        setTimeout(() => { toast.style.opacity = '1'; toast.style.transform = 'translateY(0)'; }, 10);

        setTimeout(() => {
            toast.style.opacity = '0';
            toast.style.transform = 'translateY(-20px)';
            setTimeout(() => toast.remove(), 300);
        }, 3000);
    },

    confirm(mensaje) {
        return new Promise((resolve) => {
            const overlay = document.createElement('div');
            Object.assign(overlay.style, {
                position: 'fixed', top: '0', left: '0', width: '100%', height: '100%',
                background: 'rgba(15, 23, 42, 0.6)', display: 'flex', justifyContent: 'center',
                alignItems: 'center', zIndex: '10000', backdropFilter: 'blur(3px)',
                opacity: '0', transition: 'opacity 0.2s ease'
            });

            const box = document.createElement('div');
            Object.assign(box.style, {
                background: 'white', padding: '2rem', borderRadius: '16px',
                width: '90%', maxWidth: '400px', textAlign: 'center',
                boxShadow: '0 20px 25px -5px rgba(0,0,0,0.1)',
                transform: 'scale(0.9)', transition: 'transform 0.2s ease'
            });

            const text = document.createElement('p');
            text.textContent = mensaje;
            Object.assign(text.style, { margin: '0 0 1.5rem 0', color: '#1e293b', fontSize: '1.1rem', fontWeight: '600' });

            const btnContainer = document.createElement('div');
            Object.assign(btnContainer.style, { display: 'flex', gap: '1rem', justifyContent: 'center' });

            const btnCancel = document.createElement('button');
            btnCancel.textContent = 'Cancelar';
            btnCancel.className = 'btn btn-outline';
            Object.assign(btnCancel.style, { flex: '1', color: '#64748b', borderColor: '#cbd5e1' });

            const btnConfirm = document.createElement('button');
            btnConfirm.textContent = 'Aceptar';
            btnConfirm.className = 'btn btn-primary';
            Object.assign(btnConfirm.style, { flex: '1' });

            btnContainer.append(btnCancel, btnConfirm);
            box.append(text, btnContainer);
            overlay.append(box);
            document.body.appendChild(overlay);

            requestAnimationFrame(() => {
                overlay.style.opacity = '1';
                box.style.transform = 'scale(1)';
            });

            const close = (resultado) => {
                overlay.style.opacity = '0';
                box.style.transform = 'scale(0.9)';
                setTimeout(() => overlay.remove(), 200);
                resolve(resultado);
            };

            btnCancel.onclick = () => close(false);
            btnConfirm.onclick = () => close(true);
        });
    },

    aplicarMascaraFecha(elementId) {
        const input = document.getElementById(elementId);
        if (!input) return null;

        return IMask(input, {
            mask: 'DD/MM/YYYY HH:mm',
            blocks: {
                DD: { mask: IMask.MaskedRange, from: 1, to: 31, maxLength: 2 },
                MM: { mask: IMask.MaskedRange, from: 1, to: 12, maxLength: 2 },
                YYYY: { mask: '0000' },
                HH: { mask: IMask.MaskedRange, from: 0, to: 23, maxLength: 2 },
                mm: { mask: IMask.MaskedRange, from: 0, to: 59, maxLength: 2 }
            },
            lazy: false,
            placeholderChar: '_'
        });
    },

    parseFechaMascara(fechaMasked) {
        if (!fechaMasked || fechaMasked.includes('_')) return null;
        const [fecha, hora] = fechaMasked.split(' ');
        const [dia, mes, anio] = fecha.split('/');
        return `${anio}-${mes}-${dia}T${hora}`;
    }
};
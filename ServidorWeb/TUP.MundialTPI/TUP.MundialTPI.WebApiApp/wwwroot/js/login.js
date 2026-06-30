import { api } from './api.js';
import { auth } from './auth.js';
import { ui } from './ui.js';

let isLoginMode = true;

const form = document.getElementById('auth-form');
const btnToggle = document.getElementById('btn-toggle-mode');
const title = document.getElementById('form-title');
const btnSubmit = document.getElementById('btn-submit');
const groupNombre = document.getElementById('group-nombre');

btnToggle.addEventListener('click', () => {
    isLoginMode = !isLoginMode;
    if (isLoginMode) {
        title.textContent = 'Iniciar Sesión';
        btnSubmit.textContent = 'Ingresar';
        btnToggle.textContent = '¿No tenés cuenta? Registrate acá';
        groupNombre.style.display = 'none';
        document.getElementById('nombre').removeAttribute('required');
    } else {
        title.textContent = 'Crear Cuenta';
        btnSubmit.textContent = 'Registrarme';
        btnToggle.textContent = '¿Ya tenés cuenta? Iniciá sesión';
        groupNombre.style.display = 'block';
        document.getElementById('nombre').setAttribute('required', 'true');
    }
});

form.addEventListener('submit', async (e) => {
    e.preventDefault();
    btnSubmit.disabled = true;

    // SANITIZACIÓN
    const emailRaw = document.getElementById('email').value;
    const emailClean = ui.sanitizeInput(emailRaw);
    const passwordInput = document.getElementById('password');
    const password = passwordInput.value;

    try {
        if (isLoginMode) {

            const data = await api.fetch('/auth/login', {
                method: 'POST',
                body: JSON.stringify({ email: emailClean, password })
            });
            auth.setSession(data.token, data.user);
            window.location.href = '/';
        } else {

            const nombreRaw = document.getElementById('nombre').value;
            const nombreClean = ui.sanitizeInput(nombreRaw);

            await api.fetch('/auth/register', {
                method: 'POST',
                body: JSON.stringify({ nombre: nombreClean, email: emailClean, password })
            });

            ui.showToast('¡Cuenta creada con éxito! Ahora podés iniciar sesión.', 'success');

            passwordInput.value = '';

            setTimeout(() => {
                btnToggle.click();
            }, 1500);
        }
    } catch (error) {
        const mensaje = error.message || "Error de conexión con el servidor.";

        ui.showToast(mensaje, 'error');

        passwordInput.value = '';
        console.error("[Auth Error]:", error);
    } finally {
        btnSubmit.disabled = false;
    }
});

const parametrosURL = new URLSearchParams(window.location.search);
if (parametrosURL.get('modo') === 'registro') {
    btnToggle.click();
}
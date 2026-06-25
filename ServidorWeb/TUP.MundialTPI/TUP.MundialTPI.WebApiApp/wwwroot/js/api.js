import { auth } from './auth.js';

const API_BASE = '/api';

export const api = {
    async fetch(endpoint, options = {}) {
        const headers = { 'Content-Type': 'application/json', ...options.headers };

        if (auth.isAuthenticated()) {
            headers['Authorization'] = `Bearer ${auth.getToken()}`;
        }

        try {
            const response = await window.fetch(`${API_BASE}${endpoint}`, { ...options, headers });

            if (response.status === 401) {
                auth.clearSession();
                window.location.href = '/login.html?error=auth';
                throw new Error('Sesión expirada');
            }

            if (!response.ok) {
                const err = await response.json().catch(() => ({ mensaje: 'Error del servidor' }));
                throw new Error(err.mensaje || 'Error desconocido');
            }

            return response.status !== 204 ? await response.json() : true;
        } catch (error) {
            console.error(`[API] Falla en ${endpoint}:`, error.message);
            throw error;
        }
    }
};
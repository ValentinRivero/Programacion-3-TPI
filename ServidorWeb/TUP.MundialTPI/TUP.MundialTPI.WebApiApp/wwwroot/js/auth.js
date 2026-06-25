const TOKEN_KEY = 'mundial_jwt';
const USER_KEY = 'mundial_user';

export const auth = {
    getRol() {
        const user = this.getUser();
        return user ? user.rol : null;
    },
    setSession(token, user) {
        localStorage.setItem(TOKEN_KEY, token);
        localStorage.setItem(USER_KEY, JSON.stringify(user));
    },
    clearSession() {
        localStorage.removeItem(TOKEN_KEY);
        localStorage.removeItem(USER_KEY);
    },
    getToken() {
        return localStorage.getItem(TOKEN_KEY);
    },
    getUser() {
        try {
            return JSON.parse(localStorage.getItem(USER_KEY));
        } catch {
            this.clearSession();
            return null;
        }
    },
    //check de expiracion del token
    isAuthenticated() {
        const token = this.getToken();
        if (!token) return false;
        try {
            const payload = JSON.parse(atob(token.split('.')[1]));
            if (Date.now() >= payload.exp * 1000) {
                this.clearSession();
                return false;
            }
            return true;
        } catch {
            return false;
        }
    }
};
const diccionarioBanderas = {
    'argentina': 'ar', 'brasil': 'br', 'uruguay': 'uy', 'chile': 'cl',
    'colombia': 'co', 'ecuador': 'ec', 'perú': 'pe', 'paraguay': 'py',

    'francia': 'fr', 'españa': 'es', 'alemania': 'de', 'italia': 'it',
    'inglaterra': 'gb-eng', 'portugal': 'pt', 'países bajos': 'nl', 'croacia': 'hr',

    'méxico': 'mx', 'canadá': 'ca', 'estados unidos': 'us', 'costa rica': 'cr',

    'japón': 'jp', 'corea del sur': 'kr', 'marruecos': 'ma', 'senegal': 'sn', 'rusia': 'ru'
};

export function obtenerUrlBandera(nombrePais) {
    if (!nombrePais) return 'https://upload.wikimedia.org/wikipedia/commons/a/aa/FIFA_logo_without_slogan.svg';

    const paisLimpio = nombrePais.trim().toLowerCase();
    const codigo = diccionarioBanderas[paisLimpio];

    if (codigo) {
        return `https://flagcdn.com/w160/${codigo}.png`;
    } else {
        return 'https://upload.wikimedia.org/wikipedia/commons/a/aa/FIFA_logo_without_slogan.svg';
    }
}
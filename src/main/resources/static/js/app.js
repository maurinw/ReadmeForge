const API_URL = 'http://localhost:8080/api/readmes';

function getToken() {
    return localStorage.getItem('token');
}

function logout() {
    localStorage.removeItem('token');
    window.location.href = "/login";
}

function checkLogin() {
    if (!getToken()) {
        window.location.href = "/login";
    }
}

document.addEventListener('DOMContentLoaded', () => {
    checkLogin();
});

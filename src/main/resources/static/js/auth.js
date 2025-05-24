const API = 'http://localhost:8080/api/auth';

function register() {
    const username = document.getElementById('registerUsername').value;
    const password = document.getElementById('registerPassword').value;

    fetch(`${API}/register`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password })
    })
        .then(res => {
            if (res.ok) {
                alert("Registered successfully. Please log in.");
                window.location.href = "/login";
            } else {
                res.text().then(msg => alert("Registration failed: " + msg));
            }
        });
}

function login() {
    const username = document.getElementById('loginUsername').value;
    const password = document.getElementById('loginPassword').value;

    fetch(`${API}/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password })
    })
        .then(res => {
            if (!res.ok) throw new Error("Login failed");
            return res.json();
        })
        .then(data => {
            localStorage.setItem('token', data.token);
            alert("Logged in!");
            window.location.href = "/home";
        })
        .catch(err => alert(err.message));
}

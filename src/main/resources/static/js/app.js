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

function saveReadme() {
    const title = document.getElementById('readmeTitle').value;
    const content = document.getElementById('readmeContent').value;

    fetch(API_URL, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + getToken()
        },
        body: JSON.stringify({ title, content })
    })
        .then(response => {
            if (response.ok) {
                loadReadmes();
            } else {
                alert("Failed to save readme");
            }
        });
}

function loadReadmes() {
    fetch(API_URL, {
        headers: {
            'Authorization': 'Bearer ' + getToken()
        }
    })
        .then(res => res.json())
        .then(data => {
            const list = document.getElementById('readmeList');
            list.innerHTML = '';
            data.forEach(readme => {
                const li = document.createElement('li');
                li.innerText = readme.title;
                list.appendChild(li);
            });
        });
}

document.addEventListener('DOMContentLoaded', () => {
    checkLogin();
    loadReadmes();
});

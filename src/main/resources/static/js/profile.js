function loadReadmes() {
    fetch(API_URL, {
        headers: {
            'Authorization': 'Bearer ' + getToken()
        }
    })
        .then(res => {
            if (!res.ok) {
                throw new Error("Failed to load readmes, status: " + res.status);
            }
            return res.json();
        })
        .then(data => {
            const list = document.getElementById('readmeList');
            if (!list) {
                console.error("Element with id 'readmeList' not found in DOM.");
                return;
            }

            list.innerHTML = '';
            data.forEach(readme => {
                const li = document.createElement('li');
                li.innerText = readme.title;
                list.appendChild(li);
            });
        })
        .catch(err => {
            console.error("Error in loadReadmes():", err.message);
        });
}

document.addEventListener('DOMContentLoaded', () => {
    loadReadmes();
});

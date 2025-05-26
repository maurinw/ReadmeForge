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
                alert("Readme saved!");
            } else {
                alert("Failed to save readme");
            }
        });
}

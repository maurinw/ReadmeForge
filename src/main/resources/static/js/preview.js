const content = document.getElementById('readmeContent');
const preview = document.getElementById('previewContent');

function updatePreview() {
    const input= content.value;
    preview.innerHTML = marked.parse(input);
}

content.addEventListener('input', updatePreview);

updatePreview();
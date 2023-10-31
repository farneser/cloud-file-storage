const controlsPopup = document.getElementById("popup");

function openPopup(formId) {
    const formContainers = document.querySelectorAll(".form-container");

    formContainers.forEach((container) => {
        container.style.display = container.id === formId ? "flex" : "none";
    });

    controlsPopup.style.display = "flex";
}

function currentRename(button) {
    document.getElementById("renamePath").value = button.value;

    openPopup("rename-object-form")
}

function closePopup() {
    controlsPopup.style.display = "none";
}

controlsPopup.addEventListener("click", function (event) {
    if (event.target === controlsPopup) {
        closePopup();
    }
});

document.getElementById("create-folder").addEventListener("click", () => openPopup("create-folder-form"));
document.getElementById("post-file").addEventListener("click", () => openPopup("post-file-form"));
document.getElementById("post-folder").addEventListener("click", () => openPopup("post-folder-form"));

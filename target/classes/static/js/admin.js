document.addEventListener("DOMContentLoaded", function () {
    // Gérer les boutons de modification
    const editButtons = document.querySelectorAll(".btn-edit");

    editButtons.forEach(button => {
        button.addEventListener("click", function () {
            const userId = this.getAttribute("data-id");
            const userName = this.getAttribute("data-nom");
            const userEmail = this.getAttribute("data-email");
            const userAdmin = this.getAttribute("data-admin") === "true";

            // Pré-remplir le formulaire de la modale
            document.getElementById("editUserId").value = userId;
            document.getElementById("editNom").value = userName;
            document.getElementById("editEmail").value = userEmail;
            document.getElementById("editAdmin").checked = userAdmin;

            // Afficher la modale
            const editModal = new bootstrap.Modal(document.getElementById("editModal"));
            editModal.show();
        });
    });

    // Gérer le formulaire de modification
    const editForm = document.getElementById("editForm");
    if (editForm) {
        editForm.addEventListener("submit", function (event) {
            event.preventDefault();

            const userId = document.getElementById("editUserId").value;
            const updatedUser = {
                nom: document.getElementById("editNom").value,
                email: document.getElementById("editEmail").value,
                admin: document.getElementById("editAdmin").checked
            };

            fetch(`/admin/utilisateurs/update/${userId}`, {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(updatedUser)
            })
                .then(response => {
                    if (response.ok) {
                        location.reload();
                    }
                })
                .catch(error => console.error("Erreur lors de la requête PUT :", error));
        });
    }

    // Gérer les boutons de suppression
    const deleteButtons = document.querySelectorAll(".btn-delete");

    deleteButtons.forEach(button => {
        button.addEventListener("click", function () {
            const userId = this.getAttribute("data-id");

            fetch(`/admin/utilisateurs/delete/${userId}`, {
                method: "DELETE",
                headers: { "Content-Type": "application/json" }
            })
                .then(response => {
                    if (response.ok) {
                        location.reload();
                    }
                })
                .catch(error => console.error("Erreur lors de la requête DELETE :", error));
        });
    });
});

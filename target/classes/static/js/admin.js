document.addEventListener("DOMContentLoaded", function () {
    // Ouvrir la modale de modification
    const editButtons = document.querySelectorAll(".btn-edit");
    editButtons.forEach(button => {
        button.addEventListener("click", function () {
            const userId = this.getAttribute("data-id");
            const userName = this.getAttribute("data-nom");
            const userEmail = this.getAttribute("data-email");
            const userAdmin = this.getAttribute("data-admin") === "true";

            document.getElementById("editUserId").value = userId;
            document.getElementById("editNom").value = userName;
            document.getElementById("editEmail").value = userEmail;
            document.getElementById("editAdmin").checked = userAdmin;

            const editModal = new bootstrap.Modal(document.getElementById("editModal"));
            editModal.show();
        });
    });

    // Enregistrer les modifications
    document.getElementById("editForm").addEventListener("submit", function (event) {
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
                } else {
                    alert("Erreur lors de la modification.");
                }
            })
            .catch(error => console.error("Erreur :", error));
    });

    document.addEventListener("DOMContentLoaded", function () {
        const deleteButtons = document.querySelectorAll(".btn-delete");
        deleteButtons.forEach(button => {
            button.addEventListener("click", function () {
                const userId = this.getAttribute("data-id");
                if (confirm("Voulez-vous vraiment supprimer cet utilisateur ?")) {
                    fetch(`/admin/utilisateurs/delete/${userId}`, { method: "DELETE" })
                        .then(response => {
                            if (response.ok) {
                                alert("Utilisateur supprimé avec succès !");
                                location.reload();
                            } else {
                                alert("Erreur : Impossible de supprimer l'utilisateur.");
                            }
                        })
                        .catch(error => console.error("Erreur :", error));
                }
            });
        });
    });
    
});

document.addEventListener("DOMContentLoaded", function () {
    console.log("Admin.js chargé"); // Vérifier si le script est bien chargé

    // Fonction générique pour gérer les suppressions
    function setupDeleteButtons(className, endpoint) {
        console.log(`Configuration des boutons de suppression pour ${className}`);
        const deleteButtons = document.querySelectorAll(className);
        deleteButtons.forEach(button => {
            button.addEventListener("click", function () {
                const entityId = this.getAttribute("data-id");
                console.log(`Suppression : ID = ${entityId}, Endpoint = ${endpoint}`);
                fetch(`${endpoint}/${entityId}`, {
                    method: "DELETE",
                    headers: { "Content-Type": "application/json" }
                })
                    .then(response => {
                        if (response.ok) {
                            console.log(`Suppression réussie pour ID = ${entityId}`);
                            location.reload();
                        } else {
                            console.error(`Erreur lors de la suppression : ${response.statusText}`);
                        }
                    })
                    .catch(error => console.error(`Erreur lors de la requête DELETE (${endpoint}) :`, error));
            });
        });
    }

    // Gérer le formulaire de modification pour les utilisateurs
    const editUserForm = document.getElementById("editForm");
    if (editUserForm) {
        console.log("Formulaire de modification trouvé");
        editUserForm.addEventListener("submit", function (event) {
            event.preventDefault();
            const userId = document.getElementById("editUserId").value;
            const updatedUser = {
                nom: document.getElementById("editNom").value,
                email: document.getElementById("editEmail").value,
                admin: document.getElementById("editAdmin").checked
            };

            console.log(`Soumission du formulaire de modification : ID = ${userId}`, updatedUser);

            fetch(`/admin/utilisateurs/update/${userId}`, {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(updatedUser)
            })
                .then(response => {
                    if (response.ok) {
                        console.log(`Modification réussie pour ID = ${userId}`);
                        location.reload();
                    } else {
                        console.error(`Erreur lors de la modification : ${response.statusText}`);
                    }
                })
                .catch(error => console.error("Erreur lors de la requête PUT :", error));
        });
    } else {
        console.warn("Formulaire de modification non trouvé");
    }

    // Configurer les suppressions
    setupDeleteButtons(".btn-delete", "/admin/utilisateurs/delete"); // Utilisateurs
    setupDeleteButtons(".btn-delete-resolution", "/admin/resolutions/delete"); // Résolutions
    setupDeleteButtons(".btn-delete-indice", "/admin/indices/delete"); // Indices
    setupDeleteButtons(".btn-delete-enigme", "/admin/enigmes/delete"); // Énigmes
    setupDeleteButtons(".btn-delete-categorie", "/admin/categories/delete"); // Catégories

    // Gérer les boutons "Modifier"
    const editButtons = document.querySelectorAll(".btn-edit");
    console.log(`Nombre de boutons "Modifier" trouvés : ${editButtons.length}`);
    editButtons.forEach(button => {
        button.addEventListener("click", function () {
            const userId = this.getAttribute("data-id");
            const userName = this.getAttribute("data-nom");
            const userEmail = this.getAttribute("data-email");
            const userAdmin = this.getAttribute("data-admin") === "true";

            console.log(`Pré-remplissage du formulaire pour ID = ${userId}`);
            console.log(`Nom : ${userName}, Email : ${userEmail}, Admin : ${userAdmin}`);

            // Pré-remplir le formulaire de la modale
            document.getElementById("editUserId").value = userId;
            document.getElementById("editNom").value = userName;
            document.getElementById("editEmail").value = userEmail;
            document.getElementById("editAdmin").checked = userAdmin;

            // Afficher la modale
            const editModal = new bootstrap.Modal(document.getElementById("editModal"));
            editModal.show();
            console.log("Modale affichée");
        });
    });
});

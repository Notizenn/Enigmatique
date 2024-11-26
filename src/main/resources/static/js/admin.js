document.addEventListener("DOMContentLoaded", function () {
    console.log("Admin.js chargé");

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

    // Gérer le formulaire de modification pour toutes les entités
    const editForm = document.getElementById("editForm");
    if (editForm) {
        console.log("Formulaire de modification trouvé");
        editForm.addEventListener("submit", function (event) {
            event.preventDefault();
            const entityId = document.getElementById("editEntityId").value;
            const entityType = document.getElementById("editEntityType").value;
            let updatedData = {};
            let endpoint = "";

            switch (entityType) {
                case "utilisateur":
                    updatedData = {
                        nom: document.getElementById("editNom").value,
                        email: document.getElementById("editEmail").value,
                        admin: document.getElementById("editAdmin").checked
                    };
                    endpoint = `/admin/utilisateurs/update/${entityId}`;
                    break;
                case "enigme":
                    updatedData = {
                        titre: document.getElementById("editTitre").value,
                        description: document.getElementById("editDescription").value,
                        reponse: document.getElementById("editReponse").value,
                        niveau: document.getElementById("editNiveau").value
                    };
                    endpoint = `/admin/enigmes/update/${entityId}`;
                    break;
                case "categorie":
                    updatedData = {
                        nom: document.getElementById("editNom").value
                    };
                    endpoint = `/admin/categories/update/${entityId}`;
                    break;
                case "indice":
                    updatedData = {
                        description: document.getElementById("editDescription").value,
                        cout: document.getElementById("editCout").value,
                        enigmeId: document.getElementById("editEnigmeId").value
                    };
                    endpoint = `/admin/indices/update/${entityId}`;
                    break;
                case "statistique":
                    updatedData = {
                        score: document.getElementById("editScore").value,
                        temps: document.getElementById("editTemps").value
                    };
                    endpoint = `/admin/statistiques/update/${entityId}`;
                    break;
            }

            console.log(`Soumission du formulaire de modification : ID = ${entityId}`, updatedData);

            fetch(endpoint, {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(updatedData)
            })
                .then(response => {
                    if (response.ok) {
                        console.log(`Modification réussie pour ID = ${entityId}`);
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
    setupDeleteButtons(".btn-delete-enigme", "/admin/enigmes/delete"); // Énigmes
    setupDeleteButtons(".btn-delete-categorie", "/admin/categories/delete"); // Catégories
    setupDeleteButtons(".btn-delete-indice", "/admin/indices/delete"); // Indices
    setupDeleteButtons(".btn-delete-statistique", "/admin/statistiques/delete"); // Statistiques

    // Gérer les boutons "Modifier"
    const editButtons = document.querySelectorAll(".btn-edit");
    console.log(`Nombre de boutons "Modifier" trouvés : ${editButtons.length}`);
    editButtons.forEach(button => {
        button.addEventListener("click", function () {
            const entityId = this.getAttribute("data-id");
            const entityType = this.getAttribute("data-type");
            console.log(`Pré-remplissage du formulaire pour ID = ${entityId}, Type = ${entityType}`);

            // Vider le conteneur des champs de la modale
            const editFields = document.getElementById("editFields");
            editFields.innerHTML = "";

            document.getElementById("editEntityId").value = entityId;
            document.getElementById("editEntityType").value = entityType;

            switch (entityType) {
                case "utilisateur":
                    editFields.innerHTML = `
                        <div class="mb-3">
                            <label for="editNom" class="form-label">Nom</label>
                            <input type="text" class="form-control" id="editNom" value="${this.getAttribute("data-nom")}" required>
                        </div>
                        <div class="mb-3">
                            <label for="editEmail" class="form-label">Email</label>
                            <input type="email" class="form-control" id="editEmail" value="${this.getAttribute("data-email")}" required>
                        </div>
                        <div class="form-check">
                            <input type="checkbox" class="form-check-input" id="editAdmin" ${this.getAttribute("data-admin") === "true" ? "checked" : ""}>
                            <label class="form-check-label" for="editAdmin">Admin</label>
                        </div>`;
                    break;
                case "enigme":
                    editFields.innerHTML = `
                        <div class="mb-3">
                            <label for="editTitre" class="form-label">Titre</label>
                            <input type="text" class="form-control" id="editTitre" value="${this.getAttribute("data-titre")}" required>
                        </div>
                        <div class="mb-3">
                            <label for="editDescription" class="form-label">Description</label>
                            <textarea class="form-control" id="editDescription" required>${this.getAttribute("data-description")}</textarea>
                        </div>
                        <div class="mb-3">
                            <label for="editReponse" class="form-label">Réponse</label>
                            <input type="text" class="form-control" id="editReponse" value="${this.getAttribute("data-reponse")}" required>
                        </div>
                        <div class="mb-3">
                            <label for="editNiveau" class="form-label">Niveau</label>
                            <select class="form-control" id="editNiveau" required>
                                <option value="Facile" ${this.getAttribute("data-niveau") === "Facile" ? "selected" : ""}>Facile</option>
                                <option value="Moyen" ${this.getAttribute("data-niveau") === "Moyen" ? "selected" : ""}>Moyen</option>
                                <option value="Difficile" ${this.getAttribute("data-niveau") === "Difficile" ? "selected" : ""}>Difficile</option>
                            </select>
                        </div>`;
                    break;
                case "categorie":
                    editFields.innerHTML = `
                        <div class="mb-3">
                            <label for="editNom" class="form-label">Nom</label>
                            <input type="text" class="form-control" id="editNom" value="${this.getAttribute("data-nom")}" required>
                        </div>`;
                    break;
                case "indice":
                    editFields.innerHTML = `
                        <div class="mb-3">
                            <label for="editDescription" class="form-label">Description</label>
                            <textarea class="form-control" id="editDescription" required>${this.getAttribute("data-description")}</textarea>
                        </div>
                        <div class="mb-3">
                            <label for="editCout" class="form-label">Coût</label>
                            <input type="number" class="form-control" id="editCout" value="${this.getAttribute("data-cout")}" required>
                        </div>
                        <div class="mb-3">
                            <label for="editEnigmeId" class="form-label">Énigme</label>
                            <select class="form-control" id="editEnigmeId" required>
                                <option value="${this.getAttribute("data-enigmeId")}">${this.getAttribute("data-enigmeId")}</option>
                            </select>
                        </div>`;
                    break;
                case "statistique":
                    editFields.innerHTML = `
                        <div class="mb-3">
                            <label for="editScore" class="form-label">Score</label>
                            <input type="number" class="form-control" id="editScore" value="${this.getAttribute("data-score")}" required>
                        </div>
                        <div class="mb-3">
                            <label for="editTemps" class="form-label">Temps</label>
                            <input type="number" class="form-control" id="editTemps" value="${this.getAttribute("data-temps")}" required>
                        </div>`;
                    break;
            }

            // Afficher la modale
            const editModal = new bootstrap.Modal(document.getElementById("editModal"));
            editModal.show();
            console.log("Modale affichée");

            // Gérer la fermeture de la modale et réinitialiser les champs
            const cancelButton = document.querySelector("#editModal .btn-close, #editModal .btn-secondary");
            cancelButton.addEventListener("click", function () {
                editFields.innerHTML = "";
            });
        });
    });
});

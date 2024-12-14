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
                            refreshData();
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
                        // Fermer la modale après modification réussie
                        const editModal = bootstrap.Modal.getInstance(document.getElementById("editModal"));
                        if (editModal) {
                            editModal.hide();
                        }
                        removeModalBackdrop();
                        refreshData();
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
    function setupEditButtons() {
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
                                    <option value="${this.getAttribute("data-enigmeId")}">${this.getAttribute("data-enigmeTitre") || this.getAttribute("data-enigmeId")}</option>
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
            });
        });
    }

    setupEditButtons();

    // Fonction pour enlever le backdrop restant
    function removeModalBackdrop() {
        document.body.classList.remove('modal-open');
        document.body.style.overflow = ''; // Réinitialiser le défilement
        const modalBackdrop = document.querySelector('.modal-backdrop');
        if (modalBackdrop) {
            modalBackdrop.remove();
        }
    }

    // Ajouter des écouteurs pour les boutons "Annuler" et la croix de fermeture de la modale
    const editModalElement = document.getElementById("editModal");
    if (editModalElement) {
        editModalElement.addEventListener('hidden.bs.modal', function () {
            removeModalBackdrop();
        });
    }

    // Fonction pour rafraîchir les données affichées sans recharger la page
    function refreshData() {
        fetch('/admin/data')
            .then(response => {
                if (!response.ok) {
                    throw new Error(`Erreur HTTP ! statut : ${response.status}`);
                }
                return response.json();
            })
            .then(data => {
                console.log("Données reçues du serveur :", JSON.stringify(data).length, "caractères");
                updateTables(data);
            })
            .catch(error => console.error('Erreur lors du rafraîchissement des données:', error));
    }

    // Fonction pour mettre à jour les tableaux avec les nouvelles données
    function updateTables(data) {
        // Créer une "map" des énigmes pour un accès facile par ID
        const enigmesMap = {};
        if (data.enigmes) {
            data.enigmes.forEach(enigme => {
                enigmesMap[enigme.id] = enigme;
            });
        }

        // Mise à jour pour la table des utilisateurs
        const utilisateursTable = document.getElementById("utilisateursTable");
        utilisateursTable.innerHTML = "";
        if (data.utilisateurs) {
            data.utilisateurs.forEach(utilisateur => {
                utilisateursTable.innerHTML += `
                    <tr>
                        <td>${utilisateur.nom}</td>
                        <td>${utilisateur.email}</td>
                        <td>${utilisateur.admin ? 'Oui' : 'Non'}</td>
                        <td>
                            <button class="btn btn-warning btn-sm btn-edit" data-id="${utilisateur.id}" data-type="utilisateur" data-nom="${utilisateur.nom}" data-email="${utilisateur.email}" data-admin="${utilisateur.admin}" data-bs-toggle="modal" data-bs-target="#editModal">Modifier</button>
                            <button class="btn btn-danger btn-sm btn-delete" data-id="${utilisateur.id}">Supprimer</button>
                        </td>
                    </tr>`;
            });
        }

        // Mise à jour pour la table des énigmes
        const enigmesTable = document.getElementById("enigmesTable");
        enigmesTable.innerHTML = "";
        if (data.enigmes) {
            data.enigmes.forEach(enigme => {
                enigmesTable.innerHTML += `
                    <tr>
                        <td>${enigme.titre}</td>
                        <td>${enigme.description}</td>
                        <td>${enigme.niveau}</td>
                        <td>
                            <button class="btn btn-warning btn-sm btn-edit" data-id="${enigme.id}" data-type="enigme" data-titre="${enigme.titre}" data-description="${enigme.description}" data-reponse="${enigme.reponse}" data-niveau="${enigme.niveau}" data-bs-toggle="modal" data-bs-target="#editModal">Modifier</button>
                            <button class="btn btn-danger btn-sm btn-delete-enigme" data-id="${enigme.id}">Supprimer</button>
                        </td>
                    </tr>`;
            });
        }

        // Mise à jour pour la table des catégories
        const categoriesTable = document.getElementById("categoriesTable");
        categoriesTable.innerHTML = "";
        if (data.categories) {
            data.categories.forEach(categorie => {
                categoriesTable.innerHTML += `
                    <tr>
                        <td>${categorie.nom}</td>
                        <td>
                            <button class="btn btn-warning btn-sm btn-edit" data-id="${categorie.id}" data-type="categorie" data-nom="${categorie.nom}" data-bs-toggle="modal" data-bs-target="#editModal">Modifier</button>
                            <button class="btn btn-danger btn-sm btn-delete-categorie" data-id="${categorie.id}">Supprimer</button>
                        </td>
                    </tr>`;
            });
        }

        // Mise à jour pour la table des indices
        const indicesTable = document.getElementById("indicesTable");
        indicesTable.innerHTML = "";
        if (data.indices) {
            data.indices.forEach(indice => {
                const enigme = enigmesMap[indice.enigmeId];
                const enigmeTitre = enigme ? enigme.titre : "Inconnu";
                indicesTable.innerHTML += `
                    <tr>
                        <td>${indice.description}</td>
                        <td>${indice.cout}</td>
                        <td>${enigmeTitre}</td>
                        <td>
                            <button class="btn btn-warning btn-sm btn-edit" data-id="${indice.id}" data-type="indice" data-description="${indice.description}" data-cout="${indice.cout}" data-enigmeId="${indice.enigmeId}" data-enigmeTitre="${enigmeTitre}" data-bs-toggle="modal" data-bs-target="#editModal">Modifier</button>
                            <button class="btn btn-danger btn-sm btn-delete-indice" data-id="${indice.id}">Supprimer</button>
                        </td>
                    </tr>`;
            });
        }

        setupEditButtons();
        setupDeleteButtons(".btn-delete", "/admin/utilisateurs/delete");
        setupDeleteButtons(".btn-delete-enigme", "/admin/enigmes/delete");
        setupDeleteButtons(".btn-delete-categorie", "/admin/categories/delete");
        setupDeleteButtons(".btn-delete-indice", "/admin/indices/delete");
    }

    // Fonction d'ajout générique
    function ajouterEntite(formId, endpoint, data) {
        fetch(endpoint, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(data)
        })
            .then(response => {
                if (response.ok) {
                    console.log("Entité ajoutée avec succès");
                    setTimeout(refreshData, 500); // Ajouter un délai pour s'assurer que les données sont mises à jour
                } else {
                    return response.text().then(text => {
                        console.error("Erreur lors de l'ajout de l'entité :", response.status, text);
                    });
                }
            })
            .catch(error => console.error("Erreur lors de la requête POST :", error));
    }

    // Ajouter un écouteur pour le bouton d'ajout d'utilisateur
    document.querySelector("#utilisateurForm button[type=button]").addEventListener("click", function () {
        const utilisateurForm = document.getElementById("utilisateurForm");
        const formData = new FormData(utilisateurForm);
        const data = {
            nom: formData.get("nom"),
            email: formData.get("email"),
            motDePasse: formData.get("motDePasse"),
            admin: formData.get("admin") === "on"
        };
        ajouterEntite("utilisateurForm", "/admin/utilisateurs/add", data);
    });

    // Ajouter un écouteur pour le bouton d'ajout d'énigme
    document.querySelector("#enigmeForm button[type=button]").addEventListener("click", function () {
        const enigmeForm = document.getElementById("enigmeForm");
        const formData = new FormData(enigmeForm);
        const data = {
            titre: formData.get("titre"),
            description: formData.get("description"),
            reponse: formData.get("reponse"),
            niveau: formData.get("niveau")
        };
        ajouterEntite("enigmeForm", "/admin/enigmes/add", data);
    });

    // Ajouter un écouteur pour le bouton d'ajout de catégorie
    document.querySelector("#categorieForm button[type=button]").addEventListener("click", function () {
        const categorieForm = document.getElementById("categorieForm");
        const formData = new FormData(categorieForm);
        const data = {
            nom: formData.get("nom")
        };
        ajouterEntite("categorieForm", "/admin/categories/add", data);
    });

    // Ajouter un écouteur pour le bouton d'ajout d'indice
    document.querySelector("#indiceForm button[type=button]").addEventListener("click", function () {
        const indiceForm = document.getElementById("indiceForm");
        const formData = new FormData(indiceForm);
        const enigmeId = formData.get("enigmeId");
        if (!enigmeId || enigmeId === "") {
            console.error("L'ID de l'énigme est requis pour ajouter un indice.");
            return;
        }
        const data = {
            description: formData.get("description"),
            cout: parseInt(formData.get("cout")),
            enigmeId: parseInt(enigmeId)
        };
        ajouterEntite("indiceForm", "/admin/indices/add", data);
    });
});


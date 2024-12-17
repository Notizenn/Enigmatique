document.addEventListener("DOMContentLoaded", function () {

    // Stocker les données de catégories et énigmes
    let categoriesData = [];
    let enigmesData = [];

    // Fonction pour afficher des alertes (Succès, Erreur, etc.)
    function showAlert(message, type = 'success') {
        const alertContainer = document.querySelector('.container');
        if (!alertContainer) {
            console.error("Conteneur d'alertes non trouvé");
            return;
        }
        const alert = document.createElement('div');
        alert.className = `alert alert-${type} alert-dismissible fade show`;
        alert.role = 'alert';
        alert.innerHTML = `
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        `;
        alertContainer.prepend(alert);
    }

    // Fonction générique pour gérer les suppressions
    function setupDeleteButtons(className, endpoint, showSuccess = true, showError = true) {
        const deleteButtons = document.querySelectorAll(className);
        deleteButtons.forEach(button => {
            button.addEventListener("click", function () {
                const entityId = this.getAttribute("data-id");
                if (confirm("Êtes-vous sûr de vouloir supprimer cet élément ?")) {
                    fetch(`${endpoint}/${entityId}`, {
                        method: "DELETE",
                        headers: { "Content-Type": "application/json" }
                    })
                        .then(response => {
                            if (response.ok) {
                                // Suppression des alertes désactivée
                                // if (showSuccess) {
                                //     showAlert("Suppression réussie.", 'success');
                                // }
                                refreshData();
                            } else {
                                return response.text().then(text => {
                                    // Affichage des alertes d'erreur désactivé
                                    // if (showError) {
                                    //     showAlert(`Erreur lors de la suppression : ${response.statusText} - ${text}`, 'danger');
                                    // }
                                });
                            }
                        })
                        .catch(error => {
                            // Affichage des alertes d'erreur désactivé
                            // if (showError) {
                            //     showAlert(`Erreur lors de la requête DELETE : ${error}`, 'danger');
                            // }
                        });
                }
            });
        });
    }

    // Fonction pour gérer la soumission du formulaire de modification
    const editForm = document.getElementById("editForm");
    if (editForm) {
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
                        niveau: document.getElementById("editNiveau").value,
                        categorieId: parseInt(document.getElementById("editCategorieId").value)
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
                        enigmeId: parseInt(document.getElementById("editEnigmeId").value)
                    };
                    endpoint = `/admin/indices/update/${entityId}`;
                    break;
                default:
                    // showAlert(`Type d'entité inconnu : ${entityType}`, 'danger'); // Désactivé
                    return;
            }

            fetch(endpoint, {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(updatedData)
            })
                .then(response => {
                    if (response.ok) {
                        // showAlert("Modification réussie.", 'success'); // Désactivé
                        // Fermer la modale après modification réussie
                        const editModal = bootstrap.Modal.getInstance(document.getElementById("editModal"));
                        if (editModal) {
                            editModal.hide();
                        }
                        refreshData();
                    } else {
                        return response.text().then(text => {
                            // showAlert(`Erreur lors de la modification : ${response.statusText} - ${text}`, 'danger'); // Désactivé
                        });
                    }
                })
                .catch(error => {
                    // showAlert(`Erreur lors de la requête PUT : ${error}`, 'danger'); // Désactivé
                });
        });
    } else {
        // Formulaire de modification non trouvé
    }

    // Configurer les suppressions
    setupDeleteButtons(".btn-delete", "/admin/utilisateurs/delete", false, false); // Utilisateurs : Pas d'alertes
    setupDeleteButtons(".btn-delete-enigme", "/admin/enigmes/delete"); // Énigmes : Alertes activées
    setupDeleteButtons(".btn-delete-categorie", "/admin/categories/delete"); // Catégories : Alertes activées
    setupDeleteButtons(".btn-delete-indice", "/admin/indices/delete"); // Indices : Alertes activées

    // Fonction pour gérer les boutons "Modifier"
    function setupEditButtons() {
        const editButtons = document.querySelectorAll(".btn-edit");
        editButtons.forEach(button => {
            button.addEventListener("click", function () {
                const entityId = this.getAttribute("data-id");
                const entityType = this.getAttribute("data-type");

                // Vider le conteneur des champs de la modale
                const editFields = document.getElementById("editFields");
                if (!editFields) {
                    return;
                }
                editFields.innerHTML = "";

                const editEntityId = document.getElementById("editEntityId");
                const editEntityType = document.getElementById("editEntityType");
                if (!editEntityId || !editEntityType) {
                    return;
                }
                editEntityId.value = entityId;
                editEntityType.value = entityType;

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
                            </div>
                            <div class="mb-3">
                                <label for="editCategorieId" class="form-label">Catégorie</label>
                                <select class="form-control" id="editCategorieId" required>
                                    <option value="" disabled>Choisir une catégorie</option>
                                    <!-- Les options seront ajoutées dynamiquement -->
                                </select>
                            </div>`;
                        populateCategorieSelect(document.getElementById("editCategorieId"), this.getAttribute("data-categorieId"));
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
                                <label for="editEnigmeId" class="form-label">Énigme</label>
                                <select class="form-control" id="editEnigmeId" required>
                                    <option value="" disabled>Choisir une énigme</option>
                                    <!-- Les options seront ajoutées dynamiquement -->
                                </select>
                            </div>`;
                        populateEnigmeSelect(document.getElementById("editEnigmeId"), this.getAttribute("data-enigmeId"));
                        break;
                    default:
                        return;
                }

                // Afficher la modale
                const editModal = new bootstrap.Modal(document.getElementById("editModal"));
                editModal.show();
            });
        });
    }

    // Fonction pour populater le select des catégories
    function populateCategorieSelect(selectElement, selectedId = null) {
        if (!categoriesData || categoriesData.length === 0) {
            return;
        }

        // Vider les options existantes sauf la première
        selectElement.innerHTML = `<option value="" disabled>Choisir une catégorie</option>`;

        categoriesData.forEach(categorie => {
            const option = document.createElement('option');
            option.value = categorie.id;
            option.textContent = categorie.nom;
            if (selectedId && parseInt(selectedId) === categorie.id) {
                option.selected = true;
            }
            selectElement.appendChild(option);
        });
    }

    // Fonction pour populater le select des énigmes
    function populateEnigmeSelect(selectElement, selectedId = null) {
        if (!enigmesData || enigmesData.length === 0) {
            return;
        }

        // Vider les options existantes sauf la première
        selectElement.innerHTML = `<option value="" disabled>Choisir une énigme</option>`;

        enigmesData.forEach(enigme => {
            const option = document.createElement('option');
            option.value = enigme.id;
            option.textContent = enigme.titre;
            if (selectedId && parseInt(selectedId) === enigme.id) {
                option.selected = true;
            }
            selectElement.appendChild(option);
        });
    }

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
                // Stocker les catégories et énigmes pour utilisation ultérieure
                categoriesData = data.categories || [];
                enigmesData = data.enigmes || [];
                updateTables(data);
                // Populer les sélecteurs dans les formulaires d'ajout
                const enigmeFormCategorieSelect = document.getElementById("enigmeFormCategorieId");
                if (enigmeFormCategorieSelect) {
                    populateCategorieSelect(enigmeFormCategorieSelect);
                }
                const indiceFormEnigmeSelect = document.getElementById("indiceFormEnigmeId");
                if (indiceFormEnigmeSelect) {
                    populateEnigmeSelect(indiceFormEnigmeSelect);
                }
            })
            .catch(error => {
                // showAlert(`Erreur lors du rafraîchissement des données: ${error}`, 'danger'); // Désactivé
            });
    }

    // Fonction pour mettre à jour les tableaux avec les nouvelles données
    function updateTables(data) {
        // Créer une "map" des énigmes pour un accès facile par ID
        const enigmesMap = {};
        if (enigmesData) {
            enigmesData.forEach(enigme => {
                enigmesMap[enigme.id] = enigme;
            });
        }

        // Mise à jour pour la table des utilisateurs
        const utilisateursTable = document.getElementById("utilisateursTable");
        if (utilisateursTable) {
            utilisateursTable.innerHTML = "";
            if (data.utilisateurs) {
                data.utilisateurs.forEach(utilisateur => {
                    utilisateursTable.innerHTML += `
                        <tr>
                            <td>${utilisateur.nom}</td>
                            <td>${utilisateur.email}</td>
                            <td>${utilisateur.admin ? 'Oui' : 'Non'}</td>
                            <td>
                                <button class="btn btn-warning btn-sm btn-edit" 
                                        data-id="${utilisateur.id}" 
                                        data-type="utilisateur" 
                                        data-nom="${utilisateur.nom}" 
                                        data-email="${utilisateur.email}" 
                                        data-admin="${utilisateur.admin}" 
                                        data-bs-toggle="modal" 
                                        data-bs-target="#editModal">Modifier</button>
                                <button class="btn btn-danger btn-sm btn-delete" 
                                        data-id="${utilisateur.id}">Supprimer</button>
                            </td>
                        </tr>`;
                });
            }
        }

        // Mise à jour pour la table des énigmes
        const enigmesTable = document.getElementById("enigmesTable");
        if (enigmesTable) {
            enigmesTable.innerHTML = "";
            if (data.enigmes) {
                data.enigmes.forEach(enigme => {
                    enigmesTable.innerHTML += `
                        <tr>
                            <td>${enigme.titre}</td>
                            <td>${enigme.description}</td>
                            <td>${enigme.niveau}</td>
                            <td>${enigme.categorieNom}</td>
                            <td>
                                <button class="btn btn-warning btn-sm btn-edit" 
                                        data-id="${enigme.id}" 
                                        data-type="enigme" 
                                        data-titre="${enigme.titre}" 
                                        data-description="${enigme.description}" 
                                        data-reponse="${enigme.reponse}" 
                                        data-niveau="${enigme.niveau}" 
                                        data-categorieId="${enigme.categorieId}" 
                                        data-categorieName="${enigme.categorieNom}" 
                                        data-bs-toggle="modal" 
                                        data-bs-target="#editModal">Modifier</button>
                                <button class="btn btn-danger btn-sm btn-delete-enigme" 
                                        data-id="${enigme.id}">Supprimer</button>
                            </td>
                        </tr>`;
                });
            }
        }

        // Mise à jour pour la table des catégories
        const categoriesTable = document.getElementById("categoriesTable");
        if (categoriesTable) {
            categoriesTable.innerHTML = "";
            if (data.categories) {
                data.categories.forEach(categorie => {
                    categoriesTable.innerHTML += `
                        <tr>
                            <td>${categorie.nom}</td>
                            <td>
                                <button class="btn btn-warning btn-sm btn-edit" 
                                        data-id="${categorie.id}" 
                                        data-type="categorie" 
                                        data-nom="${categorie.nom}" 
                                        data-bs-toggle="modal" 
                                        data-bs-target="#editModal">Modifier</button>
                                <button class="btn btn-danger btn-sm btn-delete-categorie" 
                                        data-id="${categorie.id}">Supprimer</button>
                            </td>
                        </tr>`;
                });
            }
        }

        // Mise à jour pour la table des indices
        const indicesTable = document.getElementById("indicesTable");
        if (indicesTable) {
            indicesTable.innerHTML = "";
            if (data.indices) {
                data.indices.forEach(indice => {
                    const enigme = enigmesMap[indice.enigmeId];
                    const enigmeTitre = enigme ? enigme.titre : "Inconnu";
                    indicesTable.innerHTML += `
                        <tr>
                            <td>${indice.description}</td>
                            <td>${enigmeTitre}</td>
                            <td>
                                <button class="btn btn-warning btn-sm btn-edit" 
                                        data-id="${indice.id}" 
                                        data-type="indice" 
                                        data-description="${indice.description}" 
                                        data-enigmeId="${indice.enigmeId}" 
                                        data-enigmeTitre="${enigmeTitre}" 
                                        data-bs-toggle="modal" 
                                        data-bs-target="#editModal">Modifier</button>
                                <button class="btn btn-danger btn-sm btn-delete-indice" 
                                        data-id="${indice.id}">Supprimer</button>
                            </td>
                        </tr>`;
                });
            }
        }

        // Reconfigurer les boutons de modification et de suppression
        setupEditButtons();
        setupDeleteButtons(".btn-delete", "/admin/utilisateurs/delete", false, false); // Utilisateurs : Pas d'alertes
        setupDeleteButtons(".btn-delete-enigme", "/admin/enigmes/delete"); // Énigmes : Alertes activées
        setupDeleteButtons(".btn-delete-categorie", "/admin/categories/delete"); // Catégories : Alertes activées
        setupDeleteButtons(".btn-delete-indice", "/admin/indices/delete"); // Indices : Alertes activées
    }

    // Fonction d'ajout générique
    function ajouterEntite(formId, endpoint, data, showSuccess = true, showError = true) {
        fetch(endpoint, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(data)
        })
            .then(response => {
                if (response.ok) {
                    // showAlert("Entité ajoutée avec succès.", "success"); // Désactivé
                    setTimeout(refreshData, 500); // Ajouter un délai pour s'assurer que les données sont mises à jour
                    document.getElementById(formId).reset();
                } else {
                    return response.text().then(text => {
                        // if (showError) {
                        //     showAlert(`Erreur lors de l'ajout de l'entité : ${text}`, "danger");
                        // }
                    });
                }
            })
            .catch(error => {
                // if (showError) {
                //     showAlert(`Erreur lors de la requête POST : ${error}`, "danger");
                // }
            });
    }

    // Ajouter un écouteur pour le bouton d'ajout d'utilisateur
    const utilisateurFormButton = document.querySelector("#utilisateurForm button[type=button]");
    if (utilisateurFormButton) {
        utilisateurFormButton.addEventListener("click", function () {
            const utilisateurForm = document.getElementById("utilisateurForm");
            const formData = new FormData(utilisateurForm);
            const data = {
                nom: formData.get("nom"),
                email: formData.get("email"),
                motDePasse: formData.get("motDePasse"),
                admin: formData.get("admin") === "on"
            };
            ajouterEntite("utilisateurForm", "/admin/utilisateurs/add", data, false, false); // Pas d'alertes
        });
    } else {
        // Bouton d'ajout d'utilisateur non trouvé
    }

    // Ajouter un écouteur pour le bouton d'ajout d'énigme
    const enigmeFormButton = document.querySelector("#enigmeForm button[type=button]");
    if (enigmeFormButton) {
        enigmeFormButton.addEventListener("click", function () {
            const enigmeForm = document.getElementById("enigmeForm");
            const formData = new FormData(enigmeForm);
            const categorieId = formData.get("categorieId");
            if (!categorieId || categorieId === "") {
                // showAlert("La catégorie est requise pour ajouter une énigme.", "warning"); // Désactivé
                return;
            }
            const data = {
                titre: formData.get("titre"),
                description: formData.get("description"),
                reponse: formData.get("reponse"),
                niveau: formData.get("niveau"),
                categorieId: parseInt(categorieId) // Inclusion de categorieId
            };
            ajouterEntite("enigmeForm", "/admin/enigmes/add", data);
        });
    } else {
        // Bouton d'ajout d'énigme non trouvé
    }

    // Ajouter un écouteur pour le bouton d'ajout de catégorie
    const categorieFormButton = document.querySelector("#categorieForm button[type=button]");
    if (categorieFormButton) {
        categorieFormButton.addEventListener("click", function () {
            const categorieForm = document.getElementById("categorieForm");
            const formData = new FormData(categorieForm);
            const data = {
                nom: formData.get("nom")
            };
            ajouterEntite("categorieForm", "/admin/categories/add", data);
        });
    } else {
        // Bouton d'ajout de catégorie non trouvé
    }

   // Ajouter un écouteur pour le bouton d'ajout d'indice
    const indiceFormButton = document.querySelector("#indiceForm button[type=button]");
    if (indiceFormButton) {
        indiceFormButton.addEventListener("click", function () {
            const indiceForm = document.getElementById("indiceForm");
            const formData = new FormData(indiceForm);
            const enigmeId = formData.get("enigmeId");
            if (!enigmeId || enigmeId === "") {
                // showAlert("L'ID de l'énigme est requis pour ajouter un indice.", "warning"); // Désactivé
                return;
            }
            const data = {
                description: formData.get("description"),
                enigmeId: parseInt(enigmeId)
            };
            ajouterEntite("indiceForm", "/admin/indices/add", data);
        });
    } else {
        // Bouton d'ajout d'indice non trouvé
    }


    // Initialiser les données
    refreshData();
});
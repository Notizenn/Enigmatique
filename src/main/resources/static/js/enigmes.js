let user = null;
let enigmas = [];    
let currentEnigma = null;
let currentHintIndex = 0;

document.addEventListener('DOMContentLoaded', () => {
    fetch('/api/utilisateurs/current')
        .then(response => {
            if (!response.ok) {
                throw new Error("Impossible de récupérer l'utilisateur connecté");
            }
            return response.json();
        })
        .then(utilisateur => {
            user = utilisateur;
            // Récupérer toutes les énigmes
            return fetch('/api/enigmes');
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Erreur lors de la récupération des énigmes');
            }
            return response.json();
        })
        .then(data => {
            // Filtrer par catégorie
            const enigmesFiltrees = data.filter(e => e.categorie && e.categorie.nom === categorieCible);

            enigmas = enigmesFiltrees.map(e => ({
                ...e,
                question: e.description,
                faite: "non"
            }));

            return fetch(`/api/resolutions/user/${user.id}`);
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Erreur lors de la récupération des énigmes résolues');
            }
            return response.json();
        })
        .then(enigmesResoluesIds => {
            enigmas = enigmas.map(enigma => {
                if (enigmesResoluesIds.includes(enigma.id)) {
                    return { ...enigma, faite: "oui" };
                }
                return enigma;
            });
            updatePuzzleList();
        })
        .catch(error => {
            console.error(error);
        });
});

const puzzleList = document.getElementById('puzzleList');

function updatePuzzleList() {
    puzzleList.innerHTML = "";
    enigmas.forEach(enigma => {
        const icon = enigma.faite === "oui"
            ? "img/checkmark.png"
            : enigma.faite === "passé"
                ? "img/croix.png"
                : "img/secured-lock.png";

        const size = enigma.faite === "oui" ? "30" : "25";

        const listItem = document.createElement('div');
        listItem.className = 'list-group-item d-flex justify-content-between align-items-center';
        listItem.onclick = () => handleEnigmaClick(enigma.id);
        listItem.innerHTML = `
            <span>${enigma.titre}</span>
            <span class="badge">
                <i>
                    <img src="${icon}" width="${size}" alt="${enigma.faite}">
                </i>
            </span>
        `;
        puzzleList.appendChild(listItem);
    });
}

function handleEnigmaClick(enigmaId) {
    const enigma = enigmas.find(e => e.id === enigmaId);
    if (enigma.faite === "oui" || enigma.faite === "non") {
        showEnigma(enigma);
    } else {
        const modalBody = document.getElementById("messageModalBody");
        modalBody.textContent = "Vous devez terminer les énigmes précédentes avant de débloquer celle-ci.";
        const messageModal = new bootstrap.Modal(document.getElementById("messageModal"));
        messageModal.show();
    }
}

function showEnigma(enigma) {
    currentEnigma = enigma;
    currentHintIndex = 0;
    const hintButton = document.querySelector("button[onclick='afficherIndice()']");
    if (hintButton) hintButton.disabled = false;

    document.getElementById("enigmaModalLabel").textContent = enigma.titre;
    document.getElementById("enigmaQuestion").textContent = enigma.question;
    document.getElementById("enigmaHint").textContent = "";
    document.getElementById("enigmaHint").style.display = "none";
    document.getElementById("enigmaAnswer").value = "";
    document.getElementById("feedbackMessage").textContent = "";

    const enigmaModal = new bootstrap.Modal(document.getElementById("enigmaModal"), {
        backdrop: true,
        keyboard: true
    });
    enigmaModal.show();

    const backdrops = document.querySelectorAll('.modal-backdrop');
    if (backdrops.length > 1) {
        for (let i = 0; i < backdrops.length - 1; i++) {
            backdrops[i].remove();
        }
    }
}

function checkAnswer() {
    const answer = document.getElementById("enigmaAnswer").value.trim();
    const feedbackMessage = document.getElementById("feedbackMessage");

    if (answer === currentEnigma.reponse) {
        feedbackMessage.textContent = "Réponse correcte !";
        feedbackMessage.style.color = "green";
        currentEnigma.faite = "oui";

        // Appel au backend pour sauvegarder la résolution (pas de coût)
        fetch('/api/resolutions', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: new URLSearchParams({
                'utilisateurId': user.id,
                'enigmeId': currentEnigma.id
            })
        })
        .then(res => {
            if (!res.ok) {
                console.error("Erreur lors de la sauvegarde de la résolution, statut:", res.status);
            }
            setTimeout(() => {
                feedbackMessage.textContent = "";
                updatePuzzleList();
                playNextEnigma();
            }, 1000);
        })
        .catch(error => {
            console.error("Erreur réseau lors de la sauvegarde de la résolution:", error);
            setTimeout(() => {
                feedbackMessage.textContent = "";
                updatePuzzleList();
                playNextEnigma();
            }, 1000);
        });
    } else {
        feedbackMessage.textContent = "Réponse incorrecte.";
        feedbackMessage.style.color = "red";
    }
}

function skipEnigma() {
    const feedbackMessage = document.getElementById("feedbackMessage");
    feedbackMessage.textContent = "Vous avez passé cette énigme.";
    feedbackMessage.style.color = "orange";
    currentEnigma.faite = "passé";

    setTimeout(() => {
        feedbackMessage.textContent = "";
        updatePuzzleList();
        playNextEnigma();
    }, 1000);
}

function afficherIndice() {
    if (!currentEnigma.indices || currentHintIndex >= currentEnigma.indices.length) {
        const hintButton = document.querySelector("button[onclick='afficherIndice()']");
        if (hintButton) hintButton.disabled = true;
        return;
    }

    const indice = currentEnigma.indices[currentHintIndex];

    // Afficher l'indice directement
    document.getElementById("enigmaHint").style.display = "block";
    document.getElementById("enigmaHint").textContent = indice.description;
    currentHintIndex++;

    const hintButton = document.querySelector("button[onclick='afficherIndice()']");
    if (currentHintIndex >= currentEnigma.indices.length && hintButton) {
        hintButton.disabled = true;
    }
}

function afficherResolution() {
    // Afficher directement la résolution (réponse + pourquoi)
    const feedbackMessage = document.getElementById("feedbackMessage");
    feedbackMessage.innerHTML = `Réponse : ${currentEnigma.reponse}<br>` 
                                ;
    feedbackMessage.style.color = "green";

    setTimeout(() => {
        currentEnigma.faite = "oui";
        updatePuzzleList();
        playNextEnigma();
    }, 5000);
}

function playNextEnigma() {
    const allCompleted = enigmas.every(enigma => enigma.faite === "oui");

    if (allCompleted) {
        alert("Félicitations, vous avez terminé toutes les énigmes !");
    } else {
        const nextEnigma = enigmas.find(enigma => enigma.faite === "non");
        if (nextEnigma) {
            showEnigma(nextEnigma);
        } else {
            alert("Il ne reste que des énigmes passées !");
        }
    }
}

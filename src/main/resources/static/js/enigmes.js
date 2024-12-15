let user = {
    id: 1,
    nom: "Alice",
    email: "alice@example.com",
    motDePasse: "password123",
    sous: 1000,
    admin: false
};

let enigmas = [];    
let currentEnigma = null;
let currentHintIndex = 0;

document.addEventListener('DOMContentLoaded', () => {
    // Récupérer toutes les énigmes depuis l'API Spring Boot
    fetch('/api/enigmes')
        .then(response => {
            if (!response.ok) {
                throw new Error('Erreur lors de la récupération des énigmes');
            }
            return response.json();
        })
        .then(data => {
            // Adapter la structure si nécessaire en fonction de ce que renvoie votre API.
            // On suppose ici que chaque énigme a un champ 'description', 'titre', etc.
            enigmas = data.map(e => {
                return {
                    ...e,
                    question: e.description, 
                    faite: "non" // vous pouvez gérer cet état autrement, p.ex. avec une propriété de l'entité si dispo.
                };
            });

            document.getElementById('userFunds').textContent = `Sous: ${user.sous} €`;
            updatePuzzleList();
        })
        .catch(error => {
            console.error(error);
            // Gérer l'erreur, par exemple afficher un message
            // alert("Impossible de récupérer les énigmes : " + error.message);
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
    const hintButton = document.querySelector("button[onclick='acheterIndice()']");
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

        setTimeout(() => {
            feedbackMessage.textContent = "";
            updatePuzzleList();
            playNextEnigma();
            updateFundsDisplay();
        }, 1000);
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
        updateFundsDisplay();
    }, 1000);
}

function acheterIndice() {
    if (!currentEnigma.indices || currentHintIndex >= currentEnigma.indices.length) {
        const hintButton = document.querySelector("button[onclick='acheterIndice()']");
        if (hintButton) hintButton.disabled = true;
        return;
    }

    const indice = currentEnigma.indices[currentHintIndex];
    const feedbackMessage = document.getElementById("feedbackMessage");

    if (user.sous >= indice.cout) {
        user.sous -= indice.cout;
        document.getElementById("enigmaHint").style.display = "block";
        document.getElementById("enigmaHint").textContent = indice.description;
        currentHintIndex++;

        const hintButton = document.querySelector("button[onclick='acheterIndice()']");
        if (currentHintIndex >= currentEnigma.indices.length && hintButton) {
            hintButton.disabled = true;
        }
        updateFundsDisplay();
    } else {
        feedbackMessage.textContent = "Fonds insuffisants pour acheter cet indice.";
        feedbackMessage.style.color = "red";
    }
}

function acheterResolution() {
    if (!currentEnigma.resolution) {
        console.error("L'énigme ne fournit pas de solution complète.");
        return;
    }

    const feedbackMessage = document.getElementById("feedbackMessage");

    if (user.sous >= currentEnigma.resolution.cout) {
        user.sous -= currentEnigma.resolution.cout;
        feedbackMessage.innerHTML = `Réponse : ${currentEnigma.reponse}<br>Pourquoi : ${currentEnigma.resolution.description}`;
        feedbackMessage.style.color = "green";

        setTimeout(() => {
            currentEnigma.faite = "oui";
            updatePuzzleList();
            playNextEnigma();
            updateFundsDisplay();
        }, 5000);
    } else {
        feedbackMessage.textContent = "Fonds insuffisants pour acheter la résolution.";
        feedbackMessage.style.color = "red";
    }
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

function updateFundsDisplay() {
    document.getElementById('userFunds').textContent = `Sous: ${user.sous} €`;
}

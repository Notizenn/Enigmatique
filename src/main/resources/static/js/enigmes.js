// Base de données à changer
const enigmas = [
    {
        id: 1,
        titre: "Équation mystérieuse",
        question: "Si 3x + 5 = 20, quelle est la valeur de x ?",
        reponse: "5",
        indice: "Réorganise l'équation pour isoler x.",
        categories: [
            {
                id: 1,
                nom: "Maths"
            }
        ],
        faite: "non"
    },
    {
        id: 2,
        titre: "Les trois coffres",
        question: "Un coffre contient un trésor. Deux indices sont vrais, un est faux : Coffre A : Le trésor est ici. Coffre B : Le trésor n’est pas dans A. Coffre C : Le trésor n’est pas ici. Où est le trésor ?",
        reponse: "B",
        indice: "Analyse les phrases et trouve l’incohérence.",
        categories: [
            {
                id: 2,
                nom: "Logique"
            }
        ],
        faite: "non"
    },
    {
        id: 3,
        titre: "Le nombre mystérieux",
        question: "Quel est le seul nombre qui est égal à son double moins 2 ?",
        reponse: "2",
        indice: "Écris une équation : x = 2x - 2.",
        categories: [
            {
                id: 3,
                nom: "Maths"
            }
        ],
        faite: "non"
    }
];





let currentEnigma = null;

const puzzleList = document.getElementById('puzzleList');
enigmas.forEach(enigma => {
    const icon = enigma.faite === "oui"
        ? "img/checkmark.png"       
        : enigma.faite === "passé"
            ? "img/cross.png"       
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


// Détermine si toutes les énigmes sont terminées ou passées automatiquement à la prochaine énigme non résolue.
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


// Gère le clic sur une énigme : affiche l'énigme si elle est débloquée, sinon affiche une popup avec un message de restriction.
function handleEnigmaClick(enigmaId) {
    const enigma = enigmas.find(e => e.id === enigmaId);
    if (enigma.faite === "oui") {
        showEnigma(enigma);
    } else {
        const modalBody = document.getElementById("messageModalBody");
        modalBody.textContent = "Vous devez terminer les énigmes précédentes avant de débloquer celle-ci.";

        const messageModal = new bootstrap.Modal(document.getElementById("messageModal"));
        messageModal.show();
    }
}

// Affiche une popup contenant les détails de l'énigme sélectionnée, tout en évitant la création de fonds noirs (backdrop) multiples.
function showEnigma(enigma) {
    currentEnigma = enigma;
    document.getElementById("enigmaModalLabel").textContent = enigma.titre;
    document.getElementById("enigmaQuestion").textContent = enigma.question;
    document.getElementById("enigmaHint").textContent = enigma.indice;
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


// Vérifie la réponse de l'énigme en cours, met à jour son état, et passe à l'énigme suivante si la réponse est correcte.
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
        }, 1000);
    } else {
        feedbackMessage.textContent = "Réponse incorrecte.";
        feedbackMessage.style.color = "red";
    }
}

// Marque l'énigme actuelle comme "passée", met à jour la liste et passe automatiquement à l'énigme suivante.
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


// Met à jour dynamiquement la liste des énigmes en fonction de leur état ("non", "oui", ou "passé") avec les icônes correspondantes.
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




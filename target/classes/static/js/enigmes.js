// Enigmas array with "faite" property
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
        faite: false
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
        faite: false
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
        faite: false
    }
];





let currentEnigma = null;

// Populate the enigma list dynamically
const puzzleList = document.getElementById('puzzleList');
enigmas.forEach(enigma => {
    const listItem = document.createElement('div');
    listItem.className = 'list-group-item d-flex justify-content-between align-items-center';
    listItem.onclick = () => handleEnigmaClick(enigma.id);
    listItem.innerHTML = `
        <span>${enigma.titre}</span>
        <span class="badge">
            <i>
                <img src="${enigma.faite ? 'img/checkmark.png' : 'img/secured-lock.png'}" 
                     width="${enigma.faite ? '30' : '25'}" 
                     alt="${enigma.faite ? 'Validée' : 'Verrouillée'}">
            </i>
        </span>
    `;
    puzzleList.appendChild(listItem);
});

function playNextEnigma() {
    const nextEnigma = enigmas.find(enigma => !enigma.faite);
    if (nextEnigma) {
        showEnigma(nextEnigma);
    } else {
        alert("Félicitations, vous avez terminé toutes les énigmes !");
    }
}

function handleEnigmaClick(enigmaId) {
    const enigma = enigmas.find(e => e.id === enigmaId);
    if (enigma.faite) {
        showEnigma(enigma);
    } else {
        alert("Vous devez terminer les énigmes précédentes avant de débloquer celle-ci.");
    }
}

function showEnigma(enigma) {
    currentEnigma = enigma;
    document.getElementById("enigmaModalLabel").textContent = enigma.titre;
    document.getElementById("enigmaQuestion").textContent = enigma.question;
    document.getElementById("enigmaHint").textContent = enigma.indice;
    document.getElementById("enigmaHint").style.display = "none";
    document.getElementById("enigmaAnswer").value = ""; // Clear input field
    document.getElementById("feedbackMessage").textContent = ""; // Clear feedback
    const enigmaModal = new bootstrap.Modal(document.getElementById("enigmaModal"));
    enigmaModal.show();
}

function checkAnswer() {
    const answer = document.getElementById("enigmaAnswer").value.trim();
    const feedbackMessage = document.getElementById("feedbackMessage");

    if (answer === currentEnigma.reponse) {
        feedbackMessage.textContent = "Réponse correcte !";
        feedbackMessage.style.color = "green";
        setTimeout(() => {
            feedbackMessage.textContent = "";
            currentEnigma.faite = true;
            updatePuzzleList();
            playNextEnigma();
        }, 1000);
    } else {
        feedbackMessage.textContent = "Réponse incorrecte.";
        feedbackMessage.style.color = "red";
    }
}


function updatePuzzleList() {
    puzzleList.innerHTML = ""; // Efface la liste existante
    enigmas.forEach(enigma => {
        const listItem = document.createElement('div');
        listItem.className = 'list-group-item d-flex justify-content-between align-items-center';
        listItem.onclick = () => handleEnigmaClick(enigma.id);
        listItem.innerHTML = `
            <span>${enigma.titre}</span>
            <span class="badge">
                <i>
                    <img src="${enigma.faite ? 'img/checkmark.png' : 'img/secured-lock.png'}" 
                         width="${enigma.faite ? '30' : '25'}" 
                         alt="${enigma.faite ? 'Validée' : 'Verrouillée'}">
                </i>
            </span>
        `;
        puzzleList.appendChild(listItem);
    });
}


document.getElementById('enigmaModal').addEventListener('hidden.bs.modal', () => {
    const backdrop = document.querySelector('.modal-backdrop');
    if (backdrop) {
        backdrop.parentNode.removeChild(backdrop);
    }
});


function showHint() {
    const hintElement = document.getElementById("enigmaHint");
    hintElement.style.display = "inline"; // Affiche l'indice
}

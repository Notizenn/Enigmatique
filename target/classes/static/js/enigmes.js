let user = {
    id: 1,
    nom: "Alice",
    email: "alice@example.com",
    motDePasse: "password123",
    sous: 1000,
    admin: false
};

// Base de données des énigmes avec indices et coût
const enigmas = [
    {
        id: 1,
        titre: "Équation mystérieuse",
        question: "Si 3x + 5 = 20, quelle est la valeur de x ?",
        reponse: "5",
        indices: [
            { id: 1, description: "Réorganise l'équation pour isoler x.", cout: 10 }
        ],
        resolution: { description: "Réorganisez comme suit : 3x = 15, donc x = 5.", cout: 30 },
        categories: [{ id: 1, nom: "Maths" }],
        faite: "non"
    },
    {
        id: 2,
        titre: "Les trois coffres",
        question: "Un coffre contient un trésor. Deux indices sont vrais, un est faux : Coffre A : Le trésor est ici. Coffre B : Le trésor n’est pas dans A. Coffre C : Le trésor n’est pas ici. Où est le trésor ?",
        reponse: "B",
        indices: [
            { id: 1, description: "Analyse les phrases et trouve l’incohérence.", cout: 15 }
        ],
        resolution: { description: "Le trésor est dans le Coffre B.", cout: 30 },
        categories: [{ id: 2, nom: "Logique" }],
        faite: "non"
    },
    {
        id: 3,
        titre: "Le nombre mystérieux",
        question: "Quel est le seul nombre qui est égal à son double moins 2 ?",
        reponse: "2",
        indices: [
            { id: 1, description: "Écris une équation : x = 2x - 2.", cout: 10 },
            { id: 2, description: "Réarrange l'équation pour isoler x.", cout: 10 },
            { id: 3, description: "Tu trouveras que x = 2.", cout: 10 }
        ],
        resolution: { description: "Écris l'équation x = 2x - 2. Après simplification, x = 2.", cout: 30 },
        categories: [{ id: 3, nom: "Maths" }],
        faite: "non"
    }
];

let currentEnigma = null;
let currentHintIndex = 0;

const puzzleList = document.getElementById('puzzleList');
document.getElementById('userFunds').textContent = `Sous: ${user.sous} €`;
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
    hintButton.disabled = currentHintIndex >= enigma.indices.length;
    
    hintButton.disabled = currentHintIndex >= enigma.indices.length;
    
    hintButton.disabled = false;
    currentEnigma = enigma;
    currentHintIndex = 0;
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

function acheterIndice() {
    const hintButton = document.querySelector("button[onclick='acheterIndice()']");
    
    
    if (currentHintIndex < currentEnigma.indices.length) {
        const hintButton = document.querySelector("button[onclick='acheterIndice()']");
        hintButton.disabled = false;
        const indice = currentEnigma.indices[currentHintIndex];
        if (user.sous >= indice.cout) {
            user.sous -= indice.cout;
            document.getElementById("enigmaHint").style.display = "block";
            document.getElementById("enigmaHint").textContent = indice.description;
            currentHintIndex++;
        if (currentHintIndex >= currentEnigma.indices.length) {
            hintButton.disabled = true;
                hintButton.disabled = true;
            }
            updateFundsDisplay();
        } else {
            document.getElementById("feedbackMessage").textContent = "Fonds insuffisants pour acheter cet indice.";
document.getElementById("feedbackMessage").style.color = "red";
        }
    } else {
        
        hintButton.disabled = true;
    }
}

function acheterResolution() {
    if (user.sous >= currentEnigma.resolution.cout) {
        user.sous -= currentEnigma.resolution.cout;

            document.getElementById("feedbackMessage").innerHTML = `Réponse : ${currentEnigma.reponse}<br>Pourquoi : ${currentEnigma.resolution.description}`;
document.getElementById("feedbackMessage").style.color = "green";

        // Attendre 5 secondes avant de passer à l'énigme suivante
        setTimeout(() => {
            currentEnigma.faite = "oui";
            updatePuzzleList();
            playNextEnigma();
            updateFundsDisplay();
        }, 5000);
    } else {
        document.getElementById("feedbackMessage").textContent = "Fonds insuffisants pour acheter la résolution.";
document.getElementById("feedbackMessage").style.color = "red";
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

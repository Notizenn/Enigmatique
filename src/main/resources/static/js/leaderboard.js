const statistiques = [
    {
        id: 1,
        utilisateur: { id: 1, nom: "Utilisateur1" },
        enigmesResolues: 15,
        indicesUtilises: 3,
        enigmesAvecAchatReponse: 2,
        score: 450,
        enigmesResoluesMaths: 7,
        enigmesResoluesLogique: 5,
        enigmesResoluesCrypto: 3
    },
    {
        id: 2,
        utilisateur: { id: 2, nom: "Utilisateur2" },
        enigmesResolues: 10,
        indicesUtilises: 2,
        enigmesAvecAchatReponse: 1,
        score: 600,
        enigmesResoluesMaths: 4,
        enigmesResoluesLogique: 4,
        enigmesResoluesCrypto: 2
    },
    {
        id: 3,
        utilisateur: { id: 3, nom: "Utilisateur3" },
        enigmesResolues: 20,
        indicesUtilises: 3,
        enigmesAvecAchatReponse: 2,
        score: 50,
        enigmesResoluesMaths: 7,
        enigmesResoluesLogique: 5,
        enigmesResoluesCrypto: 3
    },
    {
        id: 4,
        utilisateur: { id: 4, nom: "Utilisateur4" },
        enigmesResolues: 10,
        indicesUtilises: 2,
        enigmesAvecAchatReponse: 1,
        score: 100,
        enigmesResoluesMaths: 4,
        enigmesResoluesLogique: 4,
        enigmesResoluesCrypto: 2
    },
    {
        id: 5,
        utilisateur: { id: 5, nom: "Utilisateur5" },
        enigmesResolues: 5,
        indicesUtilises: 3,
        enigmesAvecAchatReponse: 2,
        score: 450,
        enigmesResoluesMaths: 7,
        enigmesResoluesLogique: 5,
        enigmesResoluesCrypto: 3
    },
    {
        id: 6,
        utilisateur: { id: 6, nom: "Utilisateur6" },
        enigmesResolues: 25,
        indicesUtilises: 2,
        enigmesAvecAchatReponse: 1,
        score: 500,
        enigmesResoluesMaths: 4,
        enigmesResoluesLogique: 4,
        enigmesResoluesCrypto: 2
    }
];

function createLeaderboard() {
    const leaderboardBody = document.querySelector("#leaderboard tbody");
    leaderboardBody.innerHTML = "";

    statistiques.sort((a, b) => b.score - a.score);

    statistiques.forEach((stat, index) => {
        if (stat.utilisateur.id === 1) { // Affiche uniquement les stats de l'utilisateur courant
            document.getElementById("indicesUtilises").textContent = stat.indicesUtilises;
            document.getElementById("achatsReponses").textContent = stat.enigmesAvecAchatReponse;
            document.getElementById("enigmesMaths").textContent = stat.enigmesResoluesMaths;
            document.getElementById("enigmesLogique").textContent = stat.enigmesResoluesLogique;
            document.getElementById("enigmesCrypto").textContent = stat.enigmesResoluesCrypto;
        }

        const row = document.createElement("tr");
        row.innerHTML = `
            <td>${index + 1}</td>
            <td>${stat.utilisateur.nom}</td>
            <td>${stat.score}</td>
            <td>${stat.enigmesResolues}</td>
        `;
        if (stat.utilisateur.id === 1) {
            row.classList.add("table-warning"); // Change the color of the current user's row
        }
        leaderboardBody.appendChild(row);
    });
}

document.addEventListener("DOMContentLoaded", createLeaderboard);
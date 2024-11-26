let user = {
    id: 1,
    nom: "Alice",
    email: "alice@example.com",
    motDePasse: "password123",
    sous: 1000, 
    admin: false
};

function updateUserCoinsDisplay() {
    document.getElementById('userCoins').textContent = user.sous;
}

updateUserCoinsDisplay();

const purchaseModal = new bootstrap.Modal(document.getElementById('purchaseModal'));
let coinsToAdd = 0;

document.querySelectorAll('.btn-acheter').forEach(button => {
    button.addEventListener('click', event => {
        event.preventDefault();
        const packText = button.parentElement.querySelector('ul li').textContent;
        coinsToAdd = parseInt(packText.match(/\d+/)[0]);
        document.getElementById('purchaseModal').querySelector('.modal-body').textContent = 
            `Vous avez acheté ${coinsToAdd} Énigmacoins !`;
        purchaseModal.show();
    });
});

document.getElementById('confirmPurchase').addEventListener('click', () => {
    user.sous += coinsToAdd;
    updateUserCoinsDisplay();
    console.log(`Nouveau solde pour ${user.nom}: ${user.sous} Énigmacoins`);
    coinsToAdd = 0;
});
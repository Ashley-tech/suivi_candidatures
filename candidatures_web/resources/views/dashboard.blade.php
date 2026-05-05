<!DOCTYPE html>
<html lang="{{ str_replace('_', '-', app()->getLocale()) }}">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">

        <title>Candidatures - Accueil</title>

        <!-- Fonts -->
        <link rel="preconnect" href="https://fonts.bunny.net">
        <link href="https://fonts.bunny.net/css?family=figtree:400,600&display=swap" rel="stylesheet" />

        <link rel="stylesheet" href="{{ asset('css/app.css') }}">
    </head>
    <body style="padding-top: 80px;">
        <header>
            <nav>
                <ul class="nav_links">
                    <li><b>Suivi des candidatures</b></li>
                    <li class="nav_right user-menu" id="user-name"></li>
                </ul>
            </nav>
        </header>
        <section>
            <div id="menu" style="display: flex; flex-direction: column; align-items: center; gap: 10px;">
                <h1>Suivi des candidatures</h1>
                <h2 id="menu-title">Menu</h2>
                <button style="width:40%;" id="candidatures-button">Vos candidatures</button>
                <button style="width:40%;" id="cv-button">Vos CVs</button>
                <button style="width:40%;" id="profile-button">Profil</button>
                <button style="width:40%;" id="deconnect-button">Déconnexion</button>
            </div>
            <div id="confirm_deconnect" style="display: none; flex-direction: column; align-items: center; gap: 10px;">
                <h1>Suivi des candidatures</h1>
                <h2>Êtes-vous sûr de vouloir vous déconnecter ?</h2>
                <button style="width:40%;" id="deconnect">Oui</button>
                <button style="width:40%;" id="non">Non</button>
            </div>
        </section>
        <footer>
            <p>&copy; 2026 Candidatures. Tous droits réservés.</p>
        </footer>
    </body>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script>
        let user = sessionStorage.getItem("login");
        async function chargement() {
            if (!user) {
                location.href = "/login";
            }
            const response = await fetch("/api/compte/find-by-email", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ email: user })
            });
            const data = await response.json();
            console.log("Réponse API get-by-email :", data);
            document.getElementById("user-name").innerHTML = data.compte.prenom + " " + data.compte.nom + document.getElementById("user-name").innerHTML;
            document.getElementById("menu-title").innerText = "Bienvenue, " + data.compte.prenom + " !";
        }
        chargement();
        $("#deconnect-button").on("click", function() {
            document.getElementById("menu").style.display = "none";
            document.getElementById("confirm_deconnect").style.display = "flex";
        });
        $("#deconnect").on("click", function() {
            sessionStorage.removeItem("login");
            location.href = "/login";
        });
        $("#non").on("click", function() {
            document.getElementById("menu").style.display = "flex";
            document.getElementById("confirm_deconnect").style.display = "none";
        });
        $("#profile-button").on("click", function() {
            location.href = "/profile";
        });
    </script>
</html>

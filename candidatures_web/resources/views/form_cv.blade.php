<!DOCTYPE html>
<html lang="{{ str_replace('_', '-', app()->getLocale()) }}">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">

        <title>Candidatures - Profil</title>

        <!-- Fonts -->
        <link rel="preconnect" href="https://fonts.bunny.net">
        <link href="https://fonts.bunny.net/css?family=figtree:400,600&display=swap" rel="stylesheet" />
        <link rel="icon" type="image/x-icon" href="{{ asset('favicon.ico') }}" />
        <link rel="stylesheet" href="{{ asset('css/app.css') }}">
    </head>
    <body>
        <header>
            <nav>
                <ul class="nav_links">
                    <li style="cursor: pointer;" id="home-link"><b>Suivi des candidatures</b></li>
                    <li class="nav_right user-menu" id="user-name"></li>
                </ul>
            </nav>
        </header>
        <section>
            <h1>Suivi des candidatures</h1>
            <h2>Nouveau CV</h2>
            <form id="cv-form" method="POST" action="/api/cv/upload" enctype="multipart/form-data">
                @csrf
                <label for="cv_file">Fichier CV* :</label><br>
                <input type="file" id="cv_file" name="cv_file" accept=".pdf,.doc,.docx" required><br><br>
                <button type="submit" id="validate" style="font-size: 20px;">Ajouter le CV</button>
            </form>
            <p id="error-message"></p>
        </section>
        <footer>
            <p>&copy; 2026 Candidatures. Tous droits réservés.</p>
        </footer>
    </body>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script>
        let user = sessionStorage.getItem("login");
        let id=null;
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
            id = data.compte.id;
        }
        chargement();
        $("#retour" ).on( "click", function( event ) {
            location.href = "/profile";
        });
        $("form").on("submit", async function(event) {
            event.preventDefault();
            $("#error-message").text("");
            const file = $("#cv_file")[0].files[0];
            const compte = id;
            console.log("Fichier sélectionné :", file);
            try {
                const formData = new FormData();
                formData.append("cv", file);
                formData.append("compte", compte);

                const response = await fetch("/api/cv/upload", {
                    method: "POST",
                    body: formData
                });

                const data = await response.json();

                if (!data.success) {
                    throw new Error(data.message || "Erreur lors de l'upload");
                }

                console.log("Succès :", data);

                $("#error-message").css("color", "green");
                $("#error-message").text("CV uploadé avec succès !");
                location.href = "/profile/cvs";
            } catch (error) {
                console.error(error);

                $("#error-message").css("color", "red");
                $("#error-message").text("Erreur : " + error.message);
            }
        });
    </script>
</html>

<!DOCTYPE html>
<html lang="{{ str_replace('_', '-', app()->getLocale()) }}">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">

        <title>Candidatures - Vos candidatures</title>

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
        <section class="content">
            <h1>Suivi des candidatures</h1>
            <h2>Vos candidatures</h2>
            <button id="add-candidature" style="margin-bottom: 20px; font-size: 23px;">Ajouter une candidature</button>
            <button id="retour" style="margin-bottom: 20px; font-size: 23px;">Retour</button>
            <table id="candidatures-table" style="width: 100%; border-collapse: collapse;">
                <thead>
                    <tr>
                        <th style="border: 1px solid #ddd; padding: 8px;">Offre</th>
                        <th style="border: 1px solid #ddd; padding: 8px;">Date de candidature</th>
                        <th style="border: 1px solid #ddd; padding: 8px;">Statut</th>
                        <th></th>
                        <th></th>
                    </tr>
                </thead>
                <tbody id="candidatures-body">
                    <!-- Les candidatures seront ajoutées ici dynamiquement -->
                </tbody>
            </table>
        </section>
        <footer>
            <p>&copy; 2026 Candidatures. Tous droits réservés.</p>
        </footer>
    </body>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script>
        let user = sessionStorage.getItem("login");
        let id = null;
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
            const response0 = await fetch("/api/compte/" + id + "/candidatures");
            const candidaturesData = await response0.json();
            console.log("Réponse API candidatures :", candidaturesData);
            let ro = null;
            let dataObj = null;
            for (let i = 0; i < candidaturesData.length; i++) {
                ro = await fetch("/api/offres/"+candidaturesData[i].offre);
                dataObj = await ro.json();
                candidaturesData[i].offre = dataObj;
                const candidatureElement = $(`
                    <tr style="border: 1px solid #ddd; padding: 8px;">
                        <td style="border: 1px solid #ddd; padding: 8px;">${dataObj.titre}</td>
                        <td style="border: 1px solid #ddd; padding: 8px;">${new Date(candidaturesData[i].date_candidature).toLocaleDateString()}</td>
                        <td style="border: 1px solid #ddd; padding: 8px;">${candidaturesData[i].statut}</td>
                        <td><button class="view-offre" data-offre-id="${candidaturesData[i].offre}" style="font-size: 16px;" onclick="location.href='/candidature/${candidaturesData[i].id}'">Voir l'offre</button></td>
                        <td><button class="delete-candidature" data-candidature-id="${candidaturesData[i].id}" style="font-size: 16px; color: white;">Supprimer</button></td>
                    </tr>
                `);
                candidatureElement.find('.delete-candidature').on('click', function() {
                    deleteCandidature(candidaturesData[i]);
                });
                $("#candidatures-body").append(candidatureElement);
            }
        }
        chargement();
        $("#retour" ).on( "click", function( event ) {
            location.href = "/dashboard";
        });
        $("#home-link").on("click", function() {
            location.href = "/dashboard";
        });
        async function deleteCandidature(candidature) {
            var confirmDelete = confirm("Êtes-vous sûr de vouloir supprimer votre candidature de "+candidature.offre.titre+" ? Cette action est irréversible.");
            if (confirmDelete){
                const response = await fetch("/api/candidatures/" + candidature.id, {
                    method: "DELETE"
                });
                const data = await response.json();
                if (data.success) {
                    alert("Candidature supprimée avec succès.");
                    location.reload();
                } else {
                    alert("Erreur lors de la suppression de la candidature : " + (data.message || "Erreur inconnue"));
                }
            }
        }
        $("#add-candidature").on("click", function() {
            location.href = "/candidatures/new";
        });
    </script>
</html>

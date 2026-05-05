<!DOCTYPE html>
<html lang="{{ str_replace('_', '-', app()->getLocale()) }}">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">

        <title>Candidatures - CVs</title>

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
            <h2>Vos CVs</h2>
            <button id="add-cv" style="margin-bottom: 20px; font-size: 23px;">Ajouter un CV</button>
            <button id="retour" style="margin-bottom: 20px; font-size: 23px;">Retour</button>
            <div id="cvs-container" style="display: flex; flex-direction: column; gap: 20px;">
                <!-- Les CVs seront ajoutés ici dynamiquement -->
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
            const cvsResponse = await fetch("/api/compte/" + data.compte.id + "/cvs");
            const cvsData = await cvsResponse.json();
            console.log("Réponse API cvs :", cvsData);
            if (cvsData.length === 0) {
                $("#cvs-container").html("<p style=\"color: #ff0000;\">Aucun CV trouvé. Cliquez sur 'Ajouter un CV' ci-dessus pour en ajouter un.</p>");
            } else {
                let cvs = 0;
                cvsData.forEach(cv => {
                    if (cv.visible == 1) {
                        let iconName = "";
                        if (cv.mime_type == "application/pdf") {
                            iconName = "pdf"
                        } else {
                            iconName = "doc"
                        }
                        const cvElement = $(`
                            <div style="border: 1px solid #ccc; padding: 10px; border-radius: 5px;">
                                <p><img src="/images/${iconName}.webp" alt="${iconName.toUpperCase()} icon" style="width: 23px; height: 30px; vertical-align: middle; margin-right: 10px;"> ${cv.nom}</p>
                                <p><strong>Date d'upload :</strong> ${new Date(cv.date_upload).toLocaleDateString()}</p>
                                <a href="${cv.download_url}" target="_blank">Télécharger</a>
                                <button type="button" class="delete-cv" data-cv-id="${cv.id}" style="margin-left: 20px; color: white;font-size: 16px;">Supprimer</button>
                            </div>
                        `);
                        cvElement.find('.delete-cv').on('click', function() {
                            deleteCV(cv);
                        });
                        $("#cvs-container").append(cvElement);
                        cvs++;
                    }
                });
                if (cvs === 0) {
                    $("#cvs-container").html("<p style=\"color: #ff0000;\">Aucun CV visible trouvé. Cliquez sur 'Ajouter un CV' ci-dessus pour en ajouter un.</p>");
                }
            }
        }
        chargement();
        $("#retour" ).on( "click", function( event ) {
            location.href = "/profile";
        });
        $("#add-cv").on("click", function() {
            location.href = "/profile/cvs/new";
        });
        $("#home-link").on("click", function() {
            location.href = "/dashboard";
        });
        async function deleteCV(cv){
            var confirmation = confirm("Êtes-vous sûr de vouloir supprimer le CV \"" + cv.nom + "\" ?");
            if (confirmation){
                const response = await fetch("/api/cv/" + cv.id, {
                    method: "DELETE",
                    headers: {
                        "Content-Type": "application/json"
                    }
                });
                const d = await response.json();
                console.log("Réponse API delete CV :", d);
                if (d.success) {
                    alert("CV supprimé avec succès.");
                    location.reload();
                } else {
                    alert(d.message || "Une erreur est survenue lors de la suppression du CV.");
                }
            }
        }
    </script>
</html>

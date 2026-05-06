<!DOCTYPE html>
<html lang="{{ str_replace('_', '-', app()->getLocale()) }}">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">

        <title>Candidatures - Détails sur une offre de candidature</title>

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
            <h2 id="candidature-title"></h2>
            <button id="modify-offre" style="margin-bottom: 20px; font-size: 23px;" onclick="location.href = '/candidature/{{ $offre_id }}/modifier'">Modifier l'offre</button>
            <button id="delete-candidature" style="margin-bottom: 20px; font-size: 23px; color: white;">Supprimer votre candidature</button>
            <button id="retour" style="margin-bottom: 20px; font-size: 23px;">Retour</button>
            <p id="type"></p>
            <p id="description"></p>
            <p id="entreprise"></p>
            <p id="adresse"></p>
            <p id="adresse_complement"></p>
            <p id="code_postal"></p>
            <p id="ville"></p>
            <p id="pays"></p>
            <p id="nom_recruteur"></p>
            <p id="email_recruteur"></p>
            <p id="telephone_recruteur"></p>
            <p id="periode"></p>
             <p id="salaire_min"></p>
             <p id="salaire_max"></p>
             <p id="date_publication"></p>
             <p id="status">
                <div class="no_display" id="status-text">
                    <input type="text" id="new-status" placeholder="Statut" style="font-size: 16px; padding: 5px;" />
                    <button id="save-status" style="font-size: 16px; margin-left: 10px;" onclick="updateStatus()">Enregistrer</button>
                    <button id="cancel-update" style="font-size: 16px; margin-left: 10px;" onclick="document.getElementById('status-text').style.display = 'none'">Annuler</button>
                </div>
             </p>
             <p id="date_candidature"></p>
             <p id="score"></p>
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
            const csResponse = await fetch("/api/candidatures/");
            const candidaturesData = await csResponse.json();
            const candidature = candidaturesData.find(c => c.id == "{{ $offre_id }}");
            console.log(candidature)
            const offreResponse = await fetch("/api/offres/" + candidature.offre);
            const offreData = await offreResponse.json();
            console.log(offreData);
            document.getElementById("candidature-title").innerHTML = "Détails de votre candidature pour l'offre : " + (offreData.titre ?? "");
            document.getElementById("type").innerHTML = "<b>Type de contrat : </b>" + (offreData.type ?? "");
            offreData.description = (offreData.description ?? "").replace(/\n/g, "<br>");
            document.getElementById("description").innerHTML = "<b>Description : </b>" + offreData.description;
            document.getElementById("entreprise").innerHTML = "<b>Entreprise : </b>" + (offreData.nom_entreprise ?? "");
            document.getElementById("adresse").innerHTML = "<b>Adresse : </b>" + (offreData.adresse_entreprise ?? "");
            document.getElementById("adresse_complement").innerHTML = "<b>Complément d'adresse : </b>" + (offreData.adresse_comp_entreprise ?? "");
            document.getElementById("code_postal").innerHTML = "<b>Code postal : </b>" + (offreData.cp_entreprise ?? "");
            document.getElementById("ville").innerHTML = "<b>Ville : </b>" + (offreData.ville_entreprise ?? "");
            document.getElementById("pays").innerHTML = "<b>Pays : </b>" + (offreData.pays_entreprise ?? "");
            document.getElementById("nom_recruteur").innerHTML = "<b>Nom du recruteur : </b>" + (offreData.prenom_recruteur ?? "") + " " + (offreData.nom_recruteur ?? "");
            document.getElementById("email_recruteur").innerHTML = "<b>Email du recruteur : </b>" + (offreData.email_entreprise ?? "");
            document.getElementById("telephone_recruteur").innerHTML = "<b>Téléphone du recruteur : </b>" + (offreData.tel_entreprise ?? "");
            document.getElementById("periode").innerHTML = "<b>Période : </b>" + (offreData.periode ?? "");
            document.getElementById("salaire_min").innerHTML = "<b>Salaire minimum : </b>" + (offreData.salaire_min ?? "");
            document.getElementById("salaire_max").innerHTML = "<b>Salaire maximum : </b>" + (offreData.salaire_max ?? "");
            document.getElementById("date_publication").innerHTML = "<b>Date de publication : </b>" + (offreData.date_publication ?? "");
            document.getElementById("status").innerHTML = "<b>Statut : </b>" + (candidature.statut ?? "")+" <button id='update-status' style='font-size: 16px; margin-left: 10px;' onclick='activerModificationStatus()'>Mettre à jour le statut de votre candidature</button>";
            document.getElementById("date_candidature").innerHTML = "<b>Date de candidature : </b>" + (candidature.date_candidature ?? "");
            document.getElementById("score").innerHTML = "<b>Score : </b>" + (candidature.score_matching ?? "");
            if (candidature.score_matching == ""){
                document.getElementById("score").innerHTML += "<button id='calcul-score' style='font-size: 16px; margin-left: 10px;' onclick='calculScore()'>Calculer le score de matching</button>";
            } else {
                document.getElementById("score").innerHTML += "<button id='recalcul-score' style='font-size: 16px; margin-left: 10px;' onclick='recalculScore()'>Recalculer le score de matching</button>";
            }
        }
        chargement();
        $("#retour" ).on( "click", function( event ) {
            location.href = "/candidatures";
        });
        $("#home-link").on("click", function() {
            location.href = "/dashboard";
        });
        $("#delete-candidature").on("click", async function() {
            var confirmDelete = confirm("Êtes-vous sûr de vouloir supprimer votre candidature ?");
            if (confirmDelete){
                const r = await fetch("/api/candidatures/");
                const candidaturesData = await r.json();
                const candidature = candidaturesData.find(c => c.id == "{{ $offre_id }}");
                const response = await fetch("/api/candidatures/" + candidature.id, {
                    method: "DELETE"
                });
                const data = await response.json();
                if (data.success) {
                    location.reload();
                } else {
                    alert("Erreur lors de la suppression de la candidature : " + (data.message || "Erreur inconnue"));
                }
            }
        });
        async function calculScore() {
            const r = await fetch("/api/candidatures/");
            const candidaturesData = await r.json();
            const candidature = candidaturesData.find(c => c.id == "{{ $offre_id }}");
            const response = await fetch("/api/candidature/" + candidature.id + "/save-score", {
                method: "PATCH"
            });
            const data = await response.json();
            if (data.success) {
                alert("Score de matching calculé avec succès : " + data.score + ". Ancien score : " + data.base_score);
                location.reload();
            } else {
                alert("Erreur lors du calcul du score de matching : " + (data.message || "Erreur inconnue"));
            }
        }
        async function recalculScore() {
            var confirmRecalc = confirm("Êtes-vous sûr de vouloir recalculer le score de matching ? Cette action écrasera l'ancien score.");
            if (confirmRecalc) {
                const r = await fetch("/api/candidatures/");
                const candidaturesData = await r.json();
                const candidature = candidaturesData.find(c => c.id == "{{ $offre_id }}");
                const response = await fetch("/api/candidature/" + candidature.id + "/save-score", {
                    method: "PATCH"
                });
                const data = await response.json();
                if (data.success) {
                    alert("Score de matching recalculé avec succès : " + data.score + ". Ancien score : " + data.base_score);
                    location.reload();
                } else {
                    alert("Erreur lors du recalcul du score de matching : " + (data.message || "Erreur inconnue"));
                }
            }
        }
        async function updateStatus() {
            const response = await fetch("/api/candidature/" + "{{ $offre_id }}" + "/statut", {
                method: "PATCH",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ statut: document.getElementById("new-status").value })
            });
            const data = await response.json();
            if (data.success) {
                location.reload();
            } else {
                alert("Erreur lors de la mise à jour du statut de la candidature : " + (data.message || "Erreur inconnue"));
            }
        }
        async function activerModificationStatus() {
            document.getElementById("status-text").style.display = "block";
            const r = await fetch("/api/candidatures/");
            const candidaturesData = await r.json();
            const candidature = candidaturesData.find(c => c.id == "{{ $offre_id }}");
            document.getElementById("new-status").value = candidature.statut ?? "";
        }
    </script>
</html>

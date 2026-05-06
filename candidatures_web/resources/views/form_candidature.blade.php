<!DOCTYPE html>
<html lang="{{ str_replace('_', '-', app()->getLocale()) }}">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">

        <title>Candidatures - Nouvelle candidature</title>

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
            <h2>Nouvelle candidature</h2>
            <button id="retour" style="margin-bottom: 20px; font-size: 23px;">Retour</button><br /><br />
                <label for="type_offre">Type d'offre :</label><br>
                <select id="type_offre" name="type_offre" style="font-size: 16px; padding: 5px; margin-bottom: 20px;">
                    <option value="">--Sélectionnez un type d'offre--</option>
                    <option value="Alternance">Alternance</option>
                    <option value="Stage">Stage</option>
                    <option value="CDI">CDI</option>
                    <option value="CDD">CDD</option>
                    <option value="Freelance">Freelance</option>
                    <option value="Autre">Autre</option>
                </select><br>
                <label for="titre">Titre de l'offre* :</label><br>
                <input type="text" id="titre" name="titre" required style="font-size: 16px; padding: 5px; margin-bottom: 20px;"><br /><br />
                <label for="description">Description de l'offre :</label><br>
                <textarea id="description" name="description" style="font-size: 16px; padding: 5px; margin-bottom: 20px;" rows="5" cols="50"></textarea><br /><br />
                <label for="entreprise">Entreprise :</label><br>
                <input type="text" id="entreprise" name="entreprise" style="font-size: 16px; padding: 5px; margin-bottom: 20px;"><br /><br />
                <label for="adresse">Adresse de l'entreprise :</label><br>
                <input type="text" id="adresse" name="adresse" style="font-size: 16px; padding: 5px; margin-bottom: 20px;"><br /><br />
                <label for="adresse_comp">Complément d'adresse de l'entreprise :</label><br>
                <input for="adresse_comp" type="text" id="adresse_comp" name="adresse_comp" placeholder="Complément d'adresse" style="font-size: 16px; padding: 5px; margin-bottom: 20px;"><br /><br />
                <label for="code_postal">Code postal de l'entreprise :</label><br>
                <input type="text" id="code_postal" name="code_postal" style="font-size: 16px; padding: 5px; margin-bottom: 20px;"><br /><br />
                <label for="ville">Ville de l'entreprise :</label><br>
                <input type="text" id="ville" name="ville" style="font-size: 16px; padding: 5px; margin-bottom: 20px;"><br /><br />
                <label for="pays">Pays de l'entreprise :</label><br>
                <input type="text" id="pays" name="pays" style="font-size: 16px; padding: 5px; margin-bottom: 20px;"><br /><br />
                <label for="date_candidature">Date de candidature :</label><br>
                <input type="date" id="date_candidature" name="date_candidature" style="font-size: 16px; padding: 5px; margin-bottom: 20px;"><br /><br />
                <label for="statut">Statut de la candidature :</label><br>
                <input type="text" id="statut" name="statut" style="font-size: 16px; padding: 5px; margin-bottom: 20px;"><br /><br />
                <label for="nom_recruteur">Nom du recruteur :</label><br>
                <input type="text" id="nom_recruteur" name="nom_recruteur" style="font-size: 16px; padding: 5px; margin-bottom: 20px;"><br /><br />
                <label for="prenom_recruteur">Prénom du recruteur :</label><br>
                <input type="text" id="prenom_recruteur" name="prenom_recruteur" style="font-size: 16px; padding: 5px; margin-bottom: 20px;"><br /><br />
                <label for="email_recruteur">Email du recruteur :</label><br>
                <input type="email" id="email_recruteur" name="email_recruteur" style="font-size: 16px; padding: 5px; margin-bottom: 20px;"><br /><br />
                <label for="tel_recruteur">Téléphone du recruteur :</label><br>
                <input type="text" id="tel_recruteur" name="tel_recruteur" style="font-size: 16px; padding: 5px; margin-bottom: 20px;"><br /><br />
                <label for="periode">Période de l'offre :</label><br>
                <input type="text" id="periode" name="periode" style="font-size: 16px; padding: 5px; margin-bottom: 20px;"><br /><br />
                <label for="salaire_min">Salaire minimum (en €) :</label><br>
                <input type="number" id="salaire_min" min="0" name="salaire_min" style="font-size: 16px; padding: 5px; margin-bottom: 20px;"><br /><br />
                <label for="salaire_max">Salaire maximum (en €) :</label><br>
                <input type="number" id="salaire_max" min="1" name="salaire_max" style="font-size: 16px; padding: 5px; margin-bottom: 20px;"><br /><br />
                <label for="date_publication">Date de publication de l'offre :</label><br>
                <input type="date" id="date_publication" name="date_publication" style="font-size: 16px; padding: 5px; margin-bottom: 20px;"><br /><br />
                <label for="cvs">CV* :</label><br>
                <select id="cvs" name="cvs" style="font-size: 16px; padding: 5px; margin-bottom: 20px;" required>
                    <option value="">--Sélectionnez un CV--</option>
                </select><br /><br />
                <button id="validate" style="font-size: 20px;">Ajouter la candidature</button>
            <p style="color: #ff0000;" id="error-message">{{ $error ?? '' }}</p>
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
            const response0 = await fetch("/api/compte/" + id + "/cvs");
            const cvsData = await response0.json();
            // Remplir le select avec les CVs
            const cvsSelect = document.getElementById("cvs")
            cvsData.forEach(cv => {
                if (cv.visible == 1) {
                    const option = document.createElement("option");
                    option.value = cv.id;
                    option.textContent = cv.nom;
                    cvsSelect.appendChild(option);
                }
            });
        }
        chargement();
        $("#retour" ).on( "click", function( event ) {
            location.href = "/candidatures";
        });
        $("#home-link").on("click", function() {
            location.href = "/dashboard";
        });
        $("#validate").on("click", async function(event) {
            event.preventDefault();

            const type = $("#type_offre").val();
            const titre = $("#titre").val();
            const description = $("#description").val();
            const entreprise = $("#entreprise").val();
            const adresse = $("#adresse").val();
            const adresse_comp = $("#adresse_comp").val();
            const code_postal = $("#code_postal").val();
            const ville = $("#ville").val();
            const pays = $("#pays").val();
            let date_candidature = $("#date_candidature").val();
            const statut = $("#statut").val();
            const nom_recruteur = $("#nom_recruteur").val();
            const prenom_recruteur = $("#prenom_recruteur").val();
            const email_recruteur = $("#email_recruteur").val();
            const tel_recruteur = $("#tel_recruteur").val();
            const periode = $("#periode").val();
            const salaire_min = $("#salaire_min").val();
            const salaire_max = $("#salaire_max").val();
            const date_publication = $("#date_publication").val();
            const cv = $("#cvs").val();
            const token = $('input[name="_token"]').val();
            $("#error-message").text("");

            if (cv == "") {
                $("#error-message").text("Veuillez sélectionner un CV.");
                return;
            }
            if (code_postal != "" && !checkregex(/^\d{5}$/, code_postal)) {
                $("#error-message").text("Le code postal doit être composé de 5 chiffres.");
                return;
            }
            if (date_candidature == ""){
                date_candidature = new Date().toISOString().split('T')[0];
            }
            if (email_recruteur != "" && !checkregex(/^[^\s@]+@[^\s@]+\.[^\s@]+$/, email_recruteur)) {
                $("#error-message").text("L'email du recruteur n'est pas valide.");
                return;
            }
            if (tel_recruteur != "" && !checkregex(/^\d{10}$/, tel_recruteur)) {
                $("#error-message").text("Le téléphone du recruteur doit être composé de 10 chiffres.");
                return;
            }
            if (salaire_min != "" && salaire_min < 0) {
                $("#error-message").text("Le salaire minimum doit être supérieur ou égal à 0.");
                return;
            }
            if (salaire_max != "" && salaire_max <= 0) {
                $("#error-message").text("Le salaire maximum doit être supérieur à 0.");
                return;
            }
            if (salaire_max != "" && salaire_min != "" && parseFloat(salaire_max) < parseFloat(salaire_min)) {
                $("#error-message").text("Le salaire maximum doit être supérieur ou égal au salaire minimum.");
                return;
            }
            if (date_publication != "" && date_candidature != "" && new Date(date_publication) > new Date(date_candidature)) {
                $("#error-message").text("La date de publication de l'offre doit être antérieure ou égale à la date de candidature.");
                return;
            }
            let ido = null;
            $.ajax({
                url: "/api/offres",
                method: "POST",
                data: {
                    type: type,
                    titre: titre,
                    description: description,
                    nom_entreprise: entreprise,
                    adresse_entreprise: adresse,
                    adresse_comp_entreprise: adresse_comp,
                    cp_entreprise: code_postal,
                    ville_entreprise: ville,
                    pays_entreprise: pays,
                    nom_recruteur: nom_recruteur,
                    prenom_recruteur: prenom_recruteur,
                    email_entreprise: email_recruteur,
                    tel_entreprise: tel_recruteur,
                    periode: periode,
                    salaire_min: salaire_min,
                    salaire_max: salaire_max,
                    date_publication: date_publication,
                    _token: token
                },
                success: function(response) {
                    console.log("Réponse API login :", response);
                    if (response.success) {
                        ido = response.offre_id;
                        $.ajax({
                            url: "/api/candidatures",
                            method: "POST",
                            data: {
                                offre: ido,
                                compte: id,
                                date_candidature: date_candidature,
                                statut: statut,
                                cv: cv,
                                _token: token
                            },
                            success: function(response) {
                                console.log("Réponse API login :", response);
                                if (response.success) {
                                    $("#error-message").text("Candidature ajoutée avec succès ! Redirection vers la page de suivi des candidatures...");
                                    $("#error-message").css("color", "green");
                                    location.href = "/candidatures";
                                } else {
                                    $("#error-message").text("Une erreur est survenue lors de l'ajout de la candidature. Veuillez réessayer.");
                                    return;
                                }
                            },
                            error: function(xhr) {
                                console.log("Erreur API login :", xhr.status, xhr.responseJSON);
                                const message = xhr.responseJSON?.message || "Une erreur est survenue lors de la connexion.";
                                $("#error-message").text(message);
                            }
                        });
                    } else {
                        $("#error-message").text("Une erreur est survenue lors de l'ajout de l'offre. Veuillez réessayer.");
                        return;
                    }
                },
                error: function(xhr) {
                    console.log("Erreur API login :", xhr.status, xhr.responseJSON);
                    const message = xhr.responseJSON?.message || "Une erreur est survenue lors de la connexion.";
                    $("#error-message").text(message);
                }
            });
        });

        function checkregex(regExp, value) {
            return regExp.test(value);
        }
    </script>
</html>

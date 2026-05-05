<!DOCTYPE html>
<html lang="{{ str_replace('_', '-', app()->getLocale()) }}">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">

        <title>Candidatures - Modification de compte</title>

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
            <h2>Modification de vos informations</h2>
            <button id="retour" style="font-size: 20px;">Retour</button><br /><br />
            <form id="modify-form" method="PATCH">
                @csrf
                <label for="email">Sexe :</label><br>
                <select id="sexe" name="sexe">
                    <option value="" disabled selected>Sexe</option>
                    <option value="M">Homme</option>
                    <option value="F">Femme</option>
                </select><br /><br />
                <label for="nom">Nom* :</label><br>
                <input type="text" id="nom" name="nom" required><br /><br />
                <label for="prenom">Prénom* :</label><br>
                <input type="text" id="prenom" name="prenom" required><br /><br />
                <label for="email">Email* :</label><br>
                <input type="email" id="email" name="email" required><br /><br />
                <label for="mdp">Mot de passe :</label><br>
                <input type="password" id="mdp" name="mdp" placeholder="(Pas de modification)"><button id="display-password" style="font-size: 16px;" type="button">Afficher</button><br /><br />
                <label for="mdp_confirmation">Confirmer le mot de passe :</label><br>
                <input type="password" id="mdp_confirmation" name="mdp_confirmation" placeholder="(Pas de modification)"><button id="display-password-confirmation" style="font-size: 16px;" type="button">Afficher</button><br /><br />
                <label for="date_naissance">Date de naissance :</label><br>
                <input type="date" id="date_naissance" name="date_naissance"><br /><br />
                <label for="nationalite">Nationalité :</label><br>
                <input type="text" id="nationalite" name="nationalite"><br /><br />
                <label for="titre">Votre situation actuelle :</label><br>
                <input type="text" id="titre" name="titre"><br /><br />
                <label for="address">Adresse :</label><br>
                <input type="text" id="address" name="address"><br /><br />
                <label for="address_complement">Complément d'adresse :</label><br>
                <input type="text" id="address_complement" name="address_complement"><br /><br />
                <label for="postal_code">Code postal :</label><br>
                <input type="text" id="postal_code" name="postal_code"><br /><br />
                <label for="city">Ville :</label><br>
                <input type="text" id="city" name="city"><br /><br />
                <label for="country">Pays :</label><br>
                <input type="text" id="country" name="country"><br /><br />
                <label for="phone">Téléphone :</label><br>
                <input type="text" id="phone" name="phone"><br /><br />
                <label for="website">Votre site web :</label><br>
                <input type="text" id="website" name="website"><br /><br /><br />
                <button type="submit" id="login" style="font-size: 20px;">Valider les modifications</button>
            </form>
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
        let old_password = null;
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
            id = data.compte.id;
            old_password = data.compte.mdp;
            document.getElementById("modify-form").action = "/api/compte/" + id;
            document.getElementById("sexe").value = data.compte.sexe;
            document.getElementById("prenom").value = data.compte.prenom;
            document.getElementById("nom").value = data.compte.nom;
            document.getElementById("email").value = data.compte.email;
            document.getElementById("date_naissance").value = data.compte.date_naissance;
            document.getElementById("nationalite").value = data.compte.nationalite;
            document.getElementById("titre").value = data.compte.titre;
            document.getElementById("address").value = data.compte.adresse;
            document.getElementById("address_complement").value = data.compte.adresse_comp;
            document.getElementById("postal_code").value = data.compte.cp;
            document.getElementById("city").value = data.compte.ville;
            document.getElementById("country").value = data.compte.pays;
            document.getElementById("phone").value = data.compte.numero;
            document.getElementById("website").value = data.compte.website;
        }
        chargement();
        $("#retour" ).on( "click", function( event ) {
            var conf = confirm("Êtes-vous sûr de vouloir annuler vos modifications ?");
            if (conf) {
                location.href = "/profile";
            }
        });
        $("#display-password" ).on( "click", function( event ) {
            event.preventDefault();
            var passwordInput = $("#mdp");
            if (passwordInput.attr("type") === "password") {
                passwordInput.attr("type", "text");
            } else {
                passwordInput.attr("type", "password");
            }
        });
        $("#display-password-confirmation" ).on( "click", function( event ) {
            event.preventDefault();
            var passwordInput = $("#mdp_confirmation");
            if (passwordInput.attr("type") === "password") {
                passwordInput.attr("type", "text");
            } else {
                passwordInput.attr("type", "password");
            }
        });
        $("form").on("submit", async function(event) {
            event.preventDefault();

            const sexe = $("#sexe").val();
            const nom = $("#nom").val();
            const prenom = $("#prenom").val();
            const email = $("#email").val();
            const mdp = $("#mdp").val();
            const mdp_confirmation = $("#mdp_confirmation").val();
            const date_naissance = $("#date_naissance").val();
            const nationalite = $("#nationalite").val();
            const titre = $("#titre").val();
            const address = $("#address").val();
            const address_complement = $("#address_complement").val();
            const postal_code = $("#postal_code").val();
            const city = $("#city").val();
            const country = $("#country").val();
            const phone = $("#phone").val();
            const website = $("#website").val();
            const token = $('input[name="_token"]').val();
            let new_mdp = null;

            $("#error-message").text("");

            if (mdp != "" || mdp_confirmation != "") {
                if (mdp != mdp_confirmation) {
                    $("#error-message").text("Les mots de passe ne correspondent pas.");
                    return;
                }
                if (mdp.length < 8) {
                    $("#error-message").text("Le mot de passe doit contenir au moins 8 caractères.");
                    return;
                }
                new_mdp = mdp;
            } else {
                new_mdp = old_password;
            }
            if (postal_code != "" && !checkregex(/^\d{5}$/, postal_code)) {
                $("#error-message").text("Le code postal doit être composé de 5 chiffres.");
                return;
            }
            if (phone != "" && !checkregex(/^\d{10}$/, phone)) {
                $("#error-message").text("Le numéro de téléphone doit être composé de 10 chiffres.");
                return;
            }
            if (website != "" && !checkregex(/^(https?:\/\/)?([\w-]+(\.[\w-]+)+)(\/[\w-]*)*\/?$/, website) && !checkregex(/^[\w-]+(\.[\w-]+)+$/, website) && !checkregex(/^(http?:\/\/)?([\w-]+(\.[\w-]+)+)(\/[\w-]*)*\/?$/, website)) {
                $("#error-message").text("L'URL du site web n'est pas valide.");
                return;
            }

            $.ajax({
                url: "/api/compte/" + id,
                method: "PATCH",
                data: {
                    sexe: sexe,
                    nom: nom,
                    prenom: prenom,
                    email: email,
                    date_naissance: date_naissance,
                    mdp: new_mdp,
                    nationalite: nationalite,
                    titre: titre,
                    address: address,
                    address_comp: address_complement,
                    cp: postal_code,
                    ville: city,
                    pays: country,
                    numero: phone,
                    website: website,
                    _token: token
                },
                success: function(response) {
                    console.log("Réponse API modify compte :", response);
                    if (response.success) {
                        $("#error-message").text("Compte modifié avec succès !");
                        $("#error-message").css("color", "green");
                        sessionStorage.setItem("login", email);
                        location.href = "/profile";
                    } else {
                        $("#error-message").text("Une erreur est survenue lors de la modification du compte.");
                    }
                },
                error: function(xhr) {
                    console.log("Erreur API modify compte :", xhr.status, xhr.responseJSON);
                    const message = xhr.responseJSON?.message || "Une erreur est survenue lors de la modification du compte.";
                    $("#error-message").text(message);
                }
            });
        });

        function checkregex(regExp, value) {
            return regExp.test(value);
        }
    </script>
</html>

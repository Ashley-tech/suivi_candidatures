<!DOCTYPE html>
<html lang="{{ str_replace('_', '-', app()->getLocale()) }}">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">

        <title>Candidatures - Inscription</title>

        <!-- Fonts -->
        <link rel="preconnect" href="https://fonts.bunny.net">
        <link href="https://fonts.bunny.net/css?family=figtree:400,600&display=swap" rel="stylesheet" />

        <link rel="stylesheet" href="{{ asset('css/app.css') }}">
        
    </head>
    <body>
        <h1>Suivi des candidatures</h1><h2>Inscription</h2>
        <button id="retour" style="font-size: 20px;">Retour à la connexion</button><br /><br />
        <form method="POST" action="/api/comptes">
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
            <label for="email_confirmation">Reconfirmation email* :</label><br />
            <input type="email" id="email_confirmation" name="email_confirmation" required><br /><br />
            <label for="mdp">Mot de passe* :</label><br>
            <input type="password" id="mdp" name="mdp" required><button id="display-password" style="font-size: 16px;" type="button">Afficher</button><br /><br />
            <label for="mdp_confirmation">Confirmer le mot de passe* :</label><br>
            <input type="password" id="mdp_confirmation" name="mdp_confirmation" required><button id="display-password-confirmation" style="font-size: 16px;" type="button">Afficher</button><br /><br />
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
            <button type="submit" id="login" style="font-size: 20px;">S'inscrire</button>
        </form>
        <p style="color: #ff0000;" id="error-message">{{ $error ?? '' }}</p>
    </body>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/js-cookie@3.0.5/dist/js.cookie.min.js"></script>
    <script>
        $("#retour" ).on( "click", function( event ) {
            location.href = "/login";
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
            const email_confirmation = $("#email_confirmation").val();
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

            $("#error-message").text("");

            if (email !== email_confirmation) {
                $("#error-message").text("Les adresses email ne correspondent pas.");
                return;
            }
            if (mdp !== mdp_confirmation) {
                $("#error-message").text("Les mots de passe ne correspondent pas.");
                return;
            }
            if (mdp.length < 8) {
                $("#error-message").text("Le mot de passe doit contenir au moins 8 caractères.");
                return;
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
            const r = await fetch("/api/compte/find-by-email", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "X-CSRF-TOKEN": token
                },
                body: JSON.stringify({ email: email })
            });

            const d = await r.json();
            console.log("Réponse API find-by-email :", d);

            if (d.found) {
                $("#error-message").text("Un compte avec cette adresse email existe déjà. Veuillez utiliser une adresse email différente.");
                return;
            }

            $.ajax({
                url: "/api/comptes",
                method: "POST",
                data: {
                    sexe: sexe,
                    nom: nom,
                    prenom: prenom,
                    email: email,
                    date_naissance: date_naissance,
                    mdp: mdp,
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
                    console.log("Réponse API login :", response);
                    if (response.success) {
                        $("#error-message").text("Inscription réussie ! Vous allez recevoir un email de confirmation. Redirection vers la page de connexion...");
                        $("#error-message").css("color", "green");
                        const response1 = fetch("/api/test-mail", {
                            method: "POST",
                            headers: {
                                "Content-Type": "application/json",
                                "X-CSRF-TOKEN": token
                            },
                            body: JSON.stringify({ email, subject: "Confirmation de votre inscription", content: "Bonjour "+prenom+",<br /><br />Votre compte a bien été créé sur le site de suivi des candidatures.<br /><br />Cordialement, <br />L'équipe de suivi des candidatures" })
                        });
                        location.href = "/login";
                    } else {
                        $("#error-message").text("Une erreur est survenue lors de l'inscription. Veuillez réessayer.");
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

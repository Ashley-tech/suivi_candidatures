<!DOCTYPE html>
<html lang="{{ str_replace('_', '-', app()->getLocale()) }}">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">

        <title>Candidatures - Nouveau mot de passe</title>

        <!-- Fonts -->
        <link rel="preconnect" href="https://fonts.bunny.net">
        <link href="https://fonts.bunny.net/css?family=figtree:400,600&display=swap" rel="stylesheet" />

        <link rel="stylesheet" href="{{ asset('css/app.css') }}">
        
    </head>
    <body>
        <h1>Suivi des candidatures</h1><h2>Nouveau mot de passe</h2>
        <p id="mail">Mail : {{ $email ?? '' }}</p>
        <form method="PATCH" action="/api/compte/{{ $compte_id }}/update-pwd">
            @csrf
            <label for="mdp">Nouveau mot de passe* :</label><br>
            <input type="password" id="mdp" name="mdp" required><button id="display-password" style="font-size: 16px;" type="button">Afficher</button><br>
            <label for="mdp_confirmation">Confirmer le mot de passe* :</label><br>
            <input type="password" id="mdp_confirmation" name="mdp_confirmation" required><button id="display-password-confirmation" style="font-size: 16px;" type="button">Afficher</button><br><br>
            <button type="submit" id="validate" style="font-size: 20px;">Changer le mot de passe</button>
        </form>
        <button id="retour" style="font-size: 20px;">Retour à la connexion</button>
        <p style="color: #ff0000;" id="error-message">{{ $error ?? '' }}</p>
    </body>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/js-cookie@3.0.5/dist/js.cookie.min.js"></script>
    <script>
        $("#retour" ).on( "click", function( event ) {
            location.href = "/login";
        });
        const params = new URLSearchParams(window.location.search);
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

        async function chargement(){
            const id = "{{ $compte_id }}";

            if (!id) {
                console.error("Pas d'ID dans l'URL");
                return;
            }

            try {
                const response = await fetch("/api/comptes/" + id, {
                    method: "GET",
                    headers: {
                        "Content-Type": "application/json",
                        "X-CSRF-TOKEN": $('input[name="_token"]').val()
                    }
                });
                
                if (!response.ok) {
                    throw new Error("Erreur API: " + response.status);
                }
                
                const data = await response.json();
                console.log("Réponse API get compte :", data);
                $("#mail").text("Mail : " + data.email);
            } catch (error) {
                console.error("Erreur :", error);
                document.getElementById("error-message").textContent = error.message;
            }
        }
        chargement();

        function checkregex(regExp, value) {
            return regExp.test(value);
        }

        $("form").on("submit", function(event) {
            event.preventDefault();

            const mdp = $("#mdp").val(); // ton backend attend le champ mdp
            const mdp_confirmation = $("#mdp_confirmation").val();
            const token = $('input[name="_token"]').val();

            $("#error-message").text("");
            if (mdp !== mdp_confirmation) {
                $("#error-message").text("Les mots de passe ne correspondent pas.");
                return;
            }
            if (mdp.length < 8) {
                $("#error-message").text("Le mot de passe doit contenir au moins 8 caractères.");
                return;
            }

            $.ajax({
                url: "/api/compte/{{ $compte_id }}/update-pwd",
                method: "PATCH",
                data: {
                    mdp: mdp,
                    _token: token
                },
                success: function(response) {
                    console.log("Réponse API update-pwd :", response);
                    if (response.success) {
                        $("#error-message").text("Mot de passe mis à jour avec succès ! Retour à la page de connexion");
                        $("#error-message").css("color", "green");
                        const response2 = fetch("/api/test-mail", {
                            method: "POST",
                            headers: {
                                "Content-Type": "application/json",
                                "X-CSRF-TOKEN": token
                            },
                            body: JSON.stringify({ email: response.compte.email, subject: "Confirmation de la réinitialisation de votre mot de passe", content: "Bonjour,<br /><br />Votre mot de passe a bien été réinitialisé.<br /><br />Cordialement, <br />L'équipe de suivi des candidatures" })
                        });
                        location.href = "/login";
                    } else {
                        $("#error-message").text("Une erreur est survenue lors de la mise à jour du mot de passe.");
                    }
                },
                error: function(xhr) {
                    console.log("Erreur API update-pwd :", xhr.status, xhr.responseJSON);
                    const message = xhr.responseJSON?.message || "Une erreur est survenue lors de la mise à jour du mot de passe.";
                    $("#error-message").text(message);
                }
            });
        });
    </script>
</html>

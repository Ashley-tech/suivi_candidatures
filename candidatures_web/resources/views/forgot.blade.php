<!DOCTYPE html>
<html lang="{{ str_replace('_', '-', app()->getLocale()) }}">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">

        <title>Candidatures - Mot de passe oublié</title>

        <!-- Fonts -->
        <link rel="preconnect" href="https://fonts.bunny.net">
        <link href="https://fonts.bunny.net/css?family=figtree:400,600&display=swap" rel="stylesheet" />

        <link rel="stylesheet" href="{{ asset('css/app.css') }}">
        
    </head>
    <body>
        <h1>Suivi des candidatures</h1><h2>Mot de passe oublié ?</h2>
        <form method="POST" action="/api/compte/find-by-email">
            @csrf
            <label for="email">Email* :</label><br>
            <input type="email" id="email" name="email" required><br>
            <button type="submit" id="login" style="font-size: 20px;">Envoyer un lien de réinitialisation</button>
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

        $("form").on("submit", async function(event) {
            event.preventDefault();

            const email = $("#email").val();
            const token = $('input[name="_token"]').val();

            $("#error-message").text("");

            try {
                const response = await fetch("/api/compte/find-by-email", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                        "X-CSRF-TOKEN": token
                    },
                    body: JSON.stringify({ email: email })
                });

                const data = await response.json();
                console.log("Réponse API find-by-email :", data);

                if (data.found) {
                    $("#error-message").text("Email trouvé ! Un mail va vous être envoyé avec les instructions pour réinitialiser votre mot de passe.");
                    $("#error-message").css("color", "green");
                    const response1 = await fetch("/api/test-mail", {
                        method: "POST",
                        headers: {
                            "Content-Type": "application/json",
                            "X-CSRF-TOKEN": token
                        },
                        body: JSON.stringify({ email, subject: "Réinitialisation de votre mot de passe", content: "Bonjour,<br /><br />Voici le <a href='http://127.0.0.1:8000/"+data.compte.id+"/new_password'>lien</a> pour réinitialiser votre mot de passe.<br /><br />Cordialement, <br />L'équipe de suivi des candidatures" })
                    });
                } else {
                    $("#error-message").text("Email non trouvé.");
                    $("#error-message").css("color", "red");
                }
            } catch (error) {
                console.log("Erreur API find-by-email :", error);
                $("#error-message").text("Une erreur est survenue lors de la connexion.");
            }
        });
    </script>
</html>

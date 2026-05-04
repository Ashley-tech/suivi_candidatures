<!DOCTYPE html>
<html lang="{{ str_replace('_', '-', app()->getLocale()) }}">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">

        <title>Candidatures - Connexion</title>

        <!-- Fonts -->
        <link rel="preconnect" href="https://fonts.bunny.net">
        <link href="https://fonts.bunny.net/css?family=figtree:400,600&display=swap" rel="stylesheet" />

        <link rel="stylesheet" href="{{ asset('css/app.css') }}">
        
    </head>
    <body>
        <h1>Suivi des candidatures</h1><h2>Connexion</h2>
        <form method="POST" action="/api/login">
            @csrf
            <label for="email">Email :</label><br>
            <input type="email" id="email" name="email" required><br>
            <label for="password">Mot de passe :</label><br>
            <input type="password" id="password" name="password" required><button style="font-size: 16px;" id="display-password">Afficher</button><button style="font-size: 16px;" id="forgot-password">Mot de passe oublié</button><br><br>
            <button type="submit" id="login" style="font-size: 20px;">Se connecter</button>
        </form>
        <p style="color: #ff0000;" id="error-message">{{ $error ?? '' }}</p>
    </body>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/js-cookie@3.0.5/dist/js.cookie.min.js"></script>
    <script>
        $("form").on("submit", function(event) {
            event.preventDefault();

            const email = $("#email").val();
            const mdp = $("#password").val(); // ton backend attend le champ mdp
            const token = $('input[name="_token"]').val();

            $("#error-message").text("");

            $.ajax({
                url: "/api/login",
                method: "POST",
                data: {
                    email: email,
                    mdp: mdp,
                    _token: token
                },
                success: function(response) {
                    console.log("Réponse API login :", response);
                    if (response.success) {
                        $("#error-message").text("Compte connecté avec succès !");
                        $("#error-message").css("color", "green");
                        //sessionStorage.setItem("login", email);
                        location.href = "/dashboard";
                    } else {
                        $("#error-message").text("Email ou mot de passe incorrect.");
                    }
                },
                error: function(xhr) {
                    console.log("Erreur API login :", xhr.status, xhr.responseJSON);
                    const message = xhr.responseJSON?.message || "Une erreur est survenue lors de la connexion.";
                    $("#error-message").text(message);
                }
            });
        });
        $("#forgot-password" ).on( "click", function( event ) {
            location.href = "/forgot-password";
        });
        $("#display-password" ).on( "click", function( event ) {
            event.preventDefault();
            var passwordInput = $("#password");
            if (passwordInput.attr("type") === "password") {
                passwordInput.attr("type", "text");
            } else {
                passwordInput.attr("type", "password");
            }
        });
    </script>
</html>

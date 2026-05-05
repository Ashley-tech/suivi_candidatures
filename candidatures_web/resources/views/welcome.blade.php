<!DOCTYPE html>
<html lang="{{ str_replace('_', '-', app()->getLocale()) }}">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">

        <title>Candidatures - Bienvenue</title>

        <!-- Fonts -->
        <link rel="preconnect" href="https://fonts.bunny.net">
        <link href="https://fonts.bunny.net/css?family=figtree:400,600&display=swap" rel="stylesheet" />
        <link rel="icon" type="image/x-icon" href="{{ asset('favicon.ico') }}" />
        <link rel="stylesheet" href="{{ asset('css/app.css') }}">
        
    </head>
    <body>
        <h1>Bienvenue sur le suivi de candidatures</h1>
        <p style="font-size: 30px">Cette application a pour but de suivre les candidatures que vous avez faites, et de vous aider à organiser votre recherche d'emploi.<br />
        Vous pourrez aussi matcher vos candidatures avec les offres d'emploi que vous aurez notées.<br />
        Vous pouvez aussi mettre à jour votre profil et vos coordonnées, ainsi que le statut de vos candidatures à tout moment.<br /><br />Bon courage pour votre recherche !</p>
        <button id="continuer" style="position: fixed; bottom: 20px; right: 20px;">Continuer</button>
    </body>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/js-cookie@3.0.5/dist/js.cookie.min.js"></script>
    <script>
        $("#continuer" ).on( "click", function( event ) {
            //if (!Cookies.get("login")) {
            if (!sessionStorage.getItem("login")) {
                location.href = "/login";
            }else{
                location.href = "/dashboard";
            }
        });
    </script>
</html>

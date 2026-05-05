<!DOCTYPE html>
<html lang="{{ str_replace('_', '-', app()->getLocale()) }}">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">

        <title>Candidatures - Accueil</title>

        <!-- Fonts -->
        <link rel="preconnect" href="https://fonts.bunny.net">
        <link href="https://fonts.bunny.net/css?family=figtree:400,600&display=swap" rel="stylesheet" />
        <link rel="icon" type="image/x-icon" href="{{ asset('favicon.ico') }}" />
        <link rel="stylesheet" href="{{ asset('css/app.css') }}">
    </head>
    <body style="padding-top: 80px;">
        <header>
            <nav>
                <ul class="nav_links">
                    <li style="cursor: pointer;" id="home-link"><b>Suivi des candidatures</b></li>
                    <li class="nav_right user-menu" id="user-name"></li>
                </ul>
            </nav>
        </header>
        <section>
            <h1>Suivi des candidatures</h1>
            <h2>Vos informations</h2>
            <table>
                <tbody>
                    <tr>
                        <th>Sexe :</th>
                        <td id="sexe"></td>
                        <th>Nom :</th>
                        <td id="nom"></td>
                        <th>Prénom :</th>
                        <td id="prenom"></td>
                    </tr>
                    <tr>
                        <th>Email :</th>
                        <td id="email"></td>
                        <th>Date de naissance :</th>
                        <td id="date_naissance"></td>
                        <th>Mot de passe :</th>
                        <td id="mdp"></td>
                    </tr>
                    <tr>
                        <th>Nationalité :</th>
                        <td id="nationalite"></td>
                        <th>Titre :</th>
                        <td id="titre"></td>
                        <th>Adresse :</th>
                        <td id="adresse"></td>
                    </tr>
                    <tr>
                        <th>Complément d'adresse :</th>
                        <td id="complement_adresse"></td>
                        <th>Code postal :</th>
                        <td id="code_postal"></td>
                        <th>Ville :</th>
                        <td id="ville"></td>
                    </tr>
                    <tr>
                        <th>Pays :</th>
                        <td id="pays"></td>
                        <th>Numéro de téléphone :</th>
                        <td id="telephone"></td>
                        <th>Site web :</th>
                        <td id="site_web"></td>
                    </tr>
                    <tr>
                        <th>Date de création du compte :</th>
                        <td id="created_at"></td>
                        <td colspan="4"></td>
                    </tr>
                    <tr>
                        <td colspan="2"><button id="edit-profile" style="font-size: 16px;width: 100%;">Modifier les informations</button></td>
                        <td colspan="2"><button id="cv" style="font-size: 16px;width: 100%;">Vos CV</button></td>
                        <td colspan="2"><button id="home-button" style="font-size: 16px;width: 100%;">Retour au menu</button></td>
                    </tr>
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
            document.getElementById("sexe").innerText = data.compte.sexe;
            document.getElementById("prenom").innerText = data.compte.prenom;
            document.getElementById("nom").innerText = data.compte.nom;
            document.getElementById("email").innerText = data.compte.email;
            document.getElementById("date_naissance").innerText = data.compte.date_naissance;
            for (let i = 0; i < data.compte.mdp.length - 2; i++) {
                document.getElementById("mdp").innerText += "*";
            }
            document.getElementById("nationalite").innerText = data.compte.nationalite;
            document.getElementById("titre").innerText = data.compte.titre;
            document.getElementById("adresse").innerText = data.compte.adresse;
            document.getElementById("complement_adresse").innerText = data.compte.adresse_comp;
            document.getElementById("code_postal").innerText = data.compte.cp;
            document.getElementById("ville").innerText = data.compte.ville;
            document.getElementById("pays").innerText = data.compte.pays;
            document.getElementById("telephone").innerText = data.compte.numero;
            document.getElementById("site_web").innerText = data.compte.website;
            document.getElementById("created_at").innerText = data.compte.created_at;
            document.getElementById("user-name").innerHTML = data.compte.prenom + " " + data.compte.nom + document.getElementById("user-name").innerHTML;
        }
        chargement();
        $("#home-link").on("click", function() {
            location.href = "/dashboard";
        });
        $("#home-button").on("click", function() {
            location.href = "/dashboard";
        });
        $("#edit-profile").on("click", function() {
            location.href = "/profile/edit";
        });
        $("#cv").on("click", function() {
            location.href = "/profile/cvs";
        });
    </script>
</html>

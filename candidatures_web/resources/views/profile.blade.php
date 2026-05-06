<!DOCTYPE html>
<html lang="{{ str_replace('_', '-', app()->getLocale()) }}">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">

        <title>Candidatures - Profil</title>

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
                    <tr>
                        <td colspan="2"><button id="deconnecter" onclick="document.getElementById('confirm_deconnecter').style.display = 'flex';document.getElementById('confirm_del_account').style.display = 'none';" style="font-size: 16px;width: 100%; background-color: #ff0000; color: white;">Se déconnecter</button></td>
                        <td colspan="2"><button id="delete-account" onclick="document.getElementById('confirm_deconnecter').style.display = 'none';document.getElementById('confirm_del_account').style.display = 'flex';" style="font-size: 16px;width: 100%; background-color: #ff0000; color: white;">Supprimer mon compte</button></td>
                        <td colspan="2"></td>
                    </tr>
                </tbody>
            </table>
            <div id="confirm_del_account" style="display: none; flex-direction: column; align-items: center; gap: 10px;">
                <h2>Êtes-vous sûr de vouloir supprimer votre compte ? Cette action est irréversible.</h2>
                <button style="width:40%;" id="delete-account">Oui, supprimer mon compte</button>
                <button style="width:40%;" id="cancel-delete" onclick="document.getElementById('confirm_del_account').style.display = 'none';">Non, conserver mon compte</button>
            </div>
            <div id="confirm_deconnecter" style="display: none; flex-direction: column; align-items: center; gap: 10px;">
                <h2>Êtes-vous sûr de vouloir vous déconnecter ?</h2>
                <button style="width:40%;" id="deconnect">Oui</button>
                <button style="width:40%;" id="cancel-deconnect" onclick="document.getElementById('confirm_deconnecter').style.display = 'none';">Non</button>
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
            document.getElementById("sexe").innerText = data.compte.sexe;
            document.getElementById("prenom").innerText = data.compte.prenom;
            document.getElementById("nom").innerText = data.compte.nom;
            document.getElementById("email").innerText = data.compte.email;
            document.getElementById("date_naissance").innerText = data.compte.date_naissance;
            for (let i = 0; i < data.compte.mdp.length; i++) {
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
        $("#deconnect").on("click", function() {
            sessionStorage.removeItem("login");
            location.href = "/login";
        });
        $("#delete-account").on("click", async function() {
            const response = await fetch("/api/compte/find-by-email", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ email: user })
            });
            const data = await response.json();
            console.log("Réponse API get-by-email pour suppression de compte :", data);
            const deleteCandidatures = await fetch("/api/compte/" + data.compte.id + "/candidatures", {
                method: "GET",
                headers: {
                    "Content-Type": "application/json"
                }
            });
            const deleteCandidaturesData = await deleteCandidatures.json();
            console.log("Réponse API get candidatures pour suppression de compte :", deleteCandidaturesData);
            for (let i = 0; i < deleteCandidaturesData.length; i++) {
                await fetch("/api/candidature/" + deleteCandidaturesData[i].id, {
                    method: "DELETE",
                    headers: {
                        "Content-Type": "application/json"
                    }
                });
            }
            const deleteCVs = await fetch("/api/compte/" + data.compte.id + "/cvs", {
                method: "GET",
                headers: {
                    "Content-Type": "application/json"
                }
            });
            const cvsData = await deleteCVs.json();
            console.log("Réponse API get CVs pour suppression de compte :", cvsData);
            for (let i = 0; i < cvsData.length; i++) {
                await fetch("/api/cv/" + cvsData[i].id, {
                    method: "DELETE",
                    headers: {
                        "Content-Type": "application/json"
                    }
                });
            }
            const deleteResponse = await fetch("/api/compte/" + data.compte.id, {
                method: "DELETE",
                headers: {
                    "Content-Type": "application/json"
                }
            });
            const deleteData = await deleteResponse.json();
            console.log("Réponse API delete compte :", deleteData);
            if (deleteData.success) {
                sessionStorage.removeItem("login");
                location.href = "/login";
            } else {
                alert("Une erreur est survenue lors de la suppression du compte. Veuillez réessayer.");
            }
        });
    </script>
</html>

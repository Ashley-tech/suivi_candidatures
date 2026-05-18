# suivi_candidatures

Outils requis :
-XAMPP : puis démarer ls serveurs Apache et MySQL (ne pas modifier les ports par défaut de MySQL)
-Android Studio (version >= 2022.3.1 pour plus d'assurance) pour tester l'application Android
-XCode pour tester l'application iOS (nécessite donc un Mac)

Créer .env puis copier tout le contenu de .env.example ou bien, modifier .env.example pour le renommer en .env
Dans MySQL, créer une base de données et l'appeler "db_candidatures"

composer install (nécessair pour installer tous les composants et ainsi faire marcher l'application web)
php artisan generate:key (Parfois nécessaire)
Refaire le cache : php artisan route:cache
Afficher la liste des routes existants : php artisan route:liste
Migrer la base de données : php artisan migrate
Remigrer la base de données : php artisan migrate:fresh
Pour tester les tests unitaures : php artisan test

Penser à modifier le nombre maximum de poids dans */mysql/bin/my.ini :
Chercher max_allowed_packet=1M et augmenter la taille jusqu'à 64M ou même 128M
Puis redémarrer les 2 serveurs pour que ça fasse effet.

Vérifier avec la requete SHOW VARIABLES LIKE 'max_allowed_packet';

Pour insérer des fichiers .docx/.doc :
dans php.ini, penser à augmenter upload_max_filesize (2M -> 10M) et post_max_size (8M -> 12M)
Et redémarrer le serveur du projet web

Attention, les CV en PDF se modifient légèrement en les insérant via l'application Android et les textes risquent de ne pas être extraits.

Possibilité de jouer avec sessionStorage ou Cookies (js-cookie). Il faudra commenter un des 2 pour jouer avec l'autre.
L'inconvénient est que le cookie est vide lors de l'ouverture d'un nouvel onglet
# Warsteroïds : La Menace Dinosaure

## Présentation

**WARSTEROIDES** est un jeu multijoueur en ligne de type **Shoot 'em up
spatial** développé dans le cadre d'une SAÉ à l'Université de Lille.

Les joueurs contrôlent un vaisseau spatial et doivent survivre le plus
longtemps possible dans un espace rempli d'ennemis, d'astéroïdes et de
projectiles.
Le but est de **détruire un maximum d'ennemis tout en évitant les
collisions** afin d'obtenir le meilleur score.

Le jeu propose : - un mode **solo ou coopératif** - des **bonus** - un
**classement des meilleurs scores** - une interface inspirée des jeux
d'arcade rétro.

---
# Sommaire

-   [Fonctionnalités](#fonctionnalités)
-   [Structure du projet](#structure-du-projet)
-   [Stack technique](#stack-technique)
-   [Installation](#installation)
-   [Gameplay](#gameplay)
-   [Diagrammes client / serveur](#communication-client--serveur)
-   [Difficultés rencontrées](#difficultés-rencontrées)
-   [Améliorations apportées](#améliorations-apportées)
-   [Améliorations possibles](#améliorations-possibles)
-   [Équipe](#équipe)

---

# Fonctionnalités

## Page d'accueil

La page d'accueil permet au joueur : 
- de **choisir un pseudo** 
- de **sélectionner un personnage**

---
## Gameplay

Le jeu se déroule sur un **plateau 2D** représenté via une balise
**Canvas**.

Le joueur contrôle un **dinosaure** qui peut : 
- se déplacer 
- tirer sur les ennemis 
- récupérer des bonus 
- jouer avec d'autres joueurs

---

### Ennemis

Les ennemis apparaissent progressivement et deviennent plus difficiles: 
- apparition plus fréquente 
- vitesse plus élevée 

---

### Bonus

Différents bonus peuvent apparaître : 
- vie supplémentaire
- Invinssibilité provisoire
- boost de +300 au score
- tir double

---

## Écran "Rejouer"

Lorsque le joueur perd toutes ses vies :

L'écran affiche : 
-  le **temps de survie** 
-  le **nombre d'ennemis détruits** 
-  le **score final**

Un bouton permet de **recommencer une partie**.

---

## Tableau des scores

Un classement affiche les **10 meilleurs scores**.

  Pseudo    Score   énnemis tuer
  --------- ------- ------------
  Player1   1200    50
  Player2   950     42


---
# Structure du projet

```
├── 📁 client
│   ├── 📁 constant
│   │   └── 📄 index.ts
│   ├── 📁 public
│   │   ├── 📁 assets
│   │   │   ├── 📁 Effect
│   │   │   │   └── 📁 explosion
│   │   │   │       ├── 🖼️ Explosion_Two1.png
│   │   │   │       ├── 🖼️ Explosion_Two10.png
│   │   │   │       ├── 🖼️ Explosion_Two2.png
│   │   │   │       ├── 🖼️ Explosion_Two3.png
│   │   │   │       ├── 🖼️ Explosion_Two4.png
│   │   │   │       ├── 🖼️ Explosion_Two5.png
│   │   │   │       ├── 🖼️ Explosion_Two6.png
│   │   │   │       ├── 🖼️ Explosion_Two7.png
│   │   │   │       ├── 🖼️ Explosion_Two8.png
│   │   │   │       └── 🖼️ Explosion_Two9.png
│   │   │   ├── 📁 Ennemi
│   │   │   │   ├── 📁 histoire
│   │   │   │   │   ├── 🖼️ spr_butterfly_purple.png
│   │   │   │   │   ├── 🖼️ spr_caterpilar_01_green.png
│   │   │   │   │   ├── 🖼️ spr_caterpillar_02_green.png
│   │   │   │   │   ├── 🖼️ spr_centipede_red.png
│   │   │   │   │   ├── 🖼️ spr_fly_01_pink.png
│   │   │   │   │   ├── 🖼️ spr_fly_02_blue.png
│   │   │   │   │   ├── 🖼️ spr_mantis_yellow.png
│   │   │   │   │   ├── 🖼️ spr_snail_yellow.png
│   │   │   │   │   └── 🖼️ spr_spider_purple.png
│   │   │   │   ├── 🖼️ comete.png
│   │   │   │   ├── 🖼️ meteor.png
│   │   │   │   └── 🖼️ vaisseauAlien.png
│   │   │   ├── 📁 bg
│   │   │   │   ├── 📁 histoire
│   │   │   │   │   ├── 🖼️ Forest_Layer1.png
│   │   │   │   │   ├── 🖼️ montagne.png
│   │   │   │   │   ├── 🖼️ spr_flameville_layer00_vec.png
│   │   │   │   │   ├── 🖼️ spr_flameville_layer01_vec.png
│   │   │   │   │   ├── 🖼️ spr_flameville_layer02_vec.png
│   │   │   │   │   ├── 🖼️ spr_flameville_layer03_vec.png
│   │   │   │   │   ├── 🖼️ spr_flameville_layer04_vec.png
│   │   │   │   │   ├── 🖼️ spr_flameville_layer05_vec.png
│   │   │   │   │   ├── 🖼️ spr_flameville_layer06_vec.png
│   │   │   │   │   └── 🖼️ spr_flameville_layer07_vec.png
│   │   │   │   ├── 🖼️ spr_cluster1.png
│   │   │   │   ├── 🖼️ spr_cluster2.png
│   │   │   │   ├── 🖼️ spr_overlay_sky_starsblue.png
│   │   │   │   ├── 🖼️ spr_overlay_sky_starspurple.png
│   │   │   │   ├── 🖼️ spr_overlay_sky_starsred.png
│   │   │   │   ├── 🖼️ spr_planet_black.png
│   │   │   │   ├── 🖼️ spr_planet_blue.png
│   │   │   │   ├── 🖼️ spr_planet_pink.png
│   │   │   │   ├── 🖼️ spr_star_green.png
│   │   │   │   ├── 🖼️ spr_star_red.png
│   │   │   │   └── 🖼️ spr_star_red2.png
│   │   │   ├── 📁 logo
│   │   │   │   ├── 🖼️ mission1.png
│   │   │   │   ├── 🖼️ mission2.png
│   │   │   │   ├── 🖼️ mission3.png
│   │   │   │   └── 🖼️ mission4.png
│   │   │   ├── 📁 mission
│   │   │   │   ├── 🖼️ mission1.png
│   │   │   │   ├── 🖼️ mission2.png
│   │   │   │   ├── 🖼️ mission3.png
│   │   │   │   └── 🖼️ mission4.png
│   │   │   ├── 📁 tir
│   │   │   ├── 🖼️ 1-removebg-preview.png
│   │   │   ├── 🖼️ 2-removebg-preview.png
│   │   │   ├── 🖼️ 3-removebg-preview.png
│   │   │   ├── 🖼️ bonusinvi.png
│   │   │   ├── 🖼️ bonustir.png
│   │   │   ├── 🖼️ bonusvie.png
│   │   │   ├── 🖼️ bonusx3.png
│   │   │   ├── 🖼️ go-removebg-preview.png
│   │   │   ├── 🖼️ heart.png
│   │   │   ├── 🖼️ heartVide.png
│   │   │   ├── 🖼️ logoJeuxSansFond.png
│   │   │   ├── 🖼️ perso1.png
│   │   │   ├── 🖼️ perso2.png
│   │   │   ├── 🖼️ perso3.png
│   │   │   ├── 🖼️ perso4.png
│   │   │   └── 🖼️ perso5.png
│   │   ├── 📁 css
│   │   │   ├── 🎨 Accueil.css
│   │   │   ├── 🎨 ChoixJeu.css
│   │   │   ├── 🎨 ChoixMission.css
│   │   │   ├── 🎨 Classement.css
│   │   │   ├── 🎨 Credits.css
│   │   │   ├── 🎨 Game.css
│   │   │   ├── 🎨 Lobby.css
│   │   │   ├── 🎨 Rejouer.css
│   │   │   └── 🎨 Style.css
│   │   └── 📁 font
│   │       ├── 📄 Roboto_Condensed-Bold.ttf
│   │       └── 📄 Universa-DEMO.otf
│   ├── 📁 src
│   │   ├── 📁 Router
│   │   │   ├── 📄 Route.ts
│   │   │   ├── 📄 Router.test.ts
│   │   │   └── 📄 Router.ts
│   │   ├── 📁 backgroundCanva
│   │   │   ├── 📁 InsectWorld
│   │   │   │   └── 📄 Iworld.ts
│   │   │   ├── 📁 earth
│   │   │   │   └── 📄 Earth.ts
│   │   │   └── 📁 space
│   │   │       ├── 📄 Astre.ts
│   │   │       ├── 📄 Space.ts
│   │   │       ├── 📄 SpaceElement.ts
│   │   │       └── 📄 Stars.ts
│   │   ├── 📁 view
│   │   │   ├── 📁 histoire
│   │   │   │   ├── 📄 ChoixMission.ts
│   │   │   │   ├── 📄 DefileView.ts
│   │   │   │   └── 📄 MissionView.ts
│   │   │   ├── 📄 AcceuilView.ts
│   │   │   ├── 📄 ChoixJeuView.ts
│   │   │   ├── 📄 ClassementView.ts
│   │   │   ├── 📄 CreditView.ts
│   │   │   ├── 📄 GameView.ts
│   │   │   ├── 📄 LobbyView.ts
│   │   │   ├── 📄 RejouerView.ts
│   │   │   └── 📄 View.ts
│   │   ├── 📄 Partie.ts
│   │   └── 📄 main.ts
│   └── 🌐 index.html
├── 📁 common
│   ├── 📁 entite
│   │   ├── 📄 Bonus.ts
│   │   ├── 📄 BonusType.ts
│   │   ├── 📄 Ennemi.ts
│   │   ├── 📄 EnnemiType.ts
│   │   ├── 📄 Entite.ts
│   │   ├── 📄 Impact.ts
│   │   ├── 📄 Joueur.test.ts
│   │   └── 📄 Joueur.ts
│   ├── 📁 histoire
│   │   └── 📄 MissionConfig.ts
│   ├── 📄 GameState.ts
│   └── 📄 Lobby.ts
├── 📁 docs
│   └── 📁 readme
│       └── 📁 asset
│           └── 🖼️ diagramSequence.svg
├── 📁 server
│   ├── 📁 Histoire
│   │   ├── 📄 BossManager.ts
│   │   ├── 📄 MissionServer.test.ts
│   │   └── 📄 MissionServer.ts
│   ├── 📁 Partie
│   │   ├── 📄 GestionnaireCollisions.ts
│   │   ├── 📄 PartieServeur.ts
│   │   └── 📄 SpawnManager.ts
│   ├── 📄 LobbyManager.ts
│   └── 📄 serveur.ts
├── ⚙️ .env-example
├── ⚙️ .gitignore
├── ⚙️ .prettierrc
├── 📝 README.md
├── ⚙️ package-lock.json
├── ⚙️ package.json
└── ⚙️ tsconfig.json
```

---
# Stack technique

Frontend : - HTML5 - CSS3 - JavaScript - Canvas API

Backend : - Node.js - Socket.io

Outils : - Git / GitLab - Tests unitaires

---

# Installation

Suivez ces étapes pour récupérer le projet et le lancer en local :

```bash
# cloner le dépôt
git clone <url-du-repo>
cd jsae

# installer les dépendances
npm install

# Ouvrez deux terminaux
# démarrer le serveur de développement
npm run server

# démarrer la vue client
npm run client:start
```

Le serveur écoute sur `http://localhost:8000`. Ouvrez cette adresse dans votre navigateur pour jouer.

---

# Gameplay

Objectif : 
- survivre le plus longtemps possible 
- détruire des ennemis
- obtenir le meilleur score

Calcul du score :

    Score = ennemis détruits × gains de chaque énemie

---

# Communication Client / Serveur

Le jeu utilise **Socket.io** pour synchroniser les joueurs en temps
réel.

Diagrame de séquance des webSocket : 

![Diagramme de séquence](/docs/readme/asset/diagramSequence.svg)

---

# Difficultés rencontrées

Le développement de **Warsteroïds** a présenté plusieurs défis techniques et organisationnels, typiques d'un premier projet multijoueur en temps réel :

1. **Migration client-serveur**  
   La plus grosse difficulté a été de passer d'un jeu solo jouable coder sans architecture client-server à un jeu multijoueurs qui demandait une architecture client-server. Le problème étant de divisé le code éxistant entre ce qui allait dans /client, celui allant dans /server et dans /common.

2. **Adaptation responsive et tailles d'écran différentes**  
   Le canvas n'était pas responsive au départ: sur petit écran, le jeu devenait injouable (éléments trop petits ou hors champ).

Malgré ces obstacles, ces difficultés nous ont énormément appris sur le développement temps réel et l'importance d'une architecture client-serveur solide dès le début.

---

# Améliorations apportées

**Mode Histoire**
Nous avons implémenté un *Mode Histoire solo* composé de *4 missions*.
Ce mode permet aux joueurs de découvrir le jeu à travers une progression narrative structurée. Chaque mission propose :

- Des objectifs spécifiques
- Une difficulté progressive

---

# Améliorations possibles

- **Transitions cinématographiques :** Rendre les passages entre les missions du mode Histoire beaucoup plus fluides et immersives, avec un style "film" (effets de caméra, fondu enchaîné, etc.).
- **Boss en multijoueur :** Intégrer des combats de boss épiques dans le mode multijoueur, pour apporter plus de challenge et de coopération entre les joueurs.

---

# Ce dont on est fier

- **Mode Histoire :** Avoir intégré le mode histoire est l'une des plus grandes fierté qu'on a, car c'est de cette idée qu'est née notre jeu Warstéroïds.
- **Audio Papope :** Avoir ajouter un audio alors qu'on pensait ne jamais réussir, surtout que c'est papope

---
## Équipe

- **Hugo Straseele** – Etudiant développeur
- **Valentino Paganin** – Etudiant développeur
- **Eva Chen** – Etudiante développeuse

---

_Réalisé par l'équipe Les Papas Pingouin_ — Université de Lille, semestre 4 JSAE.

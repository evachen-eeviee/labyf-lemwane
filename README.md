# LABYFÉLEMWANE

## Présentation générale

**Labyfélemwane** est un jeu de labyrinthes développé dans le cadre de la *SAE 3.02 - Développement d'applications*.  
Ce projet consiste à concevoir une application permettant aux joueurs de parcourir des labyrinthes aléatoires et des labyrinthes parfaits.  
Pour ce faire, deux modes sont disponibles : le mode libre (avec des labyrinthes paramétrables) et le mode progression (avec des défis à remporter).  
  
Ce projet a été réalisé par groupe de 4 étudiants (cités ci-après).

## Lancement du jeu 

Pour exécuter le jeu, il suffit d'utiliser Maven et d'exécuter la commande suivante :   

```
mvn javafx:run
``` 

Assurez-vous au préalable que vos environnements Maven et JavaFX sont correctement configurés dans votre IDE.  

## Structure

* /src/main/java : code source principal  
* /src/main/ressources : Ressources utilisées pour JavaFX (images,CSS)  
* /src/test : Tests unitaires (du code source)  
* README.md : Présentation du projet  
* UML.md : Présentation du l'UML du projet  
* suivi.md : Journal de bord de l'équipe (hebdomadaire)  
* rapport.md : Rapport attendu pour la ressource *R3.02 - Développement efficace*  

## Fonctionnalités 

### Jalon 1 :

Génération de labyrinthes aléatoires.  

* Mode Libre :  
  * Choix de la taille du labyrinthe  
  * Choix du pourcentage de murs générés  
  * Garantie de chemin entre entrée et sortie du labyrinthe  
* Mode Progression :  
  * Profil de joueur (pour enregistrer les défis)  
  * Des étapes constituées de défis à remporter  
  * Des tailles et des pourcentages de murs (difficultés) imposés  
  * Vision du joueur restreinte selon la difficulté des étapes et défis  

### Jalon 2 : 

Génération de labyrinthes parfaits.  

* Mode Libre :  
  * Choix de la taille du labyrinthe parfait  
  * Choix d'un chemin minimal à faire pour gagner la sortie du labyrinthe  
  * Garantie de chemin entre entrée/sortie  
* Mode Progression :  
  * Introduction de labyrinthes parfaits dans les étapes et défis  
  * Nouvelles fonctionnalités pour la restriction de la vue du joueur  
    
Ajout de personnalisation de l'application. 

## Crédits 

Sujet fourni par l'équipe enseignante du BUT Informatique.  

Réalisation :  
* CHEN Eva  
* MANGIN Maxence  
* VIGNERON Loïse  
* USAL Antoine  










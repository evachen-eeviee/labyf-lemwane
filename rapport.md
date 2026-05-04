
# Rapport 

## 1.Investissement dans le projet

Le groupe J1 est composé de **CHEN Eva**, **MANGIN Maxence**, **VIGNERON Loïse**, **USAL Antoine**. Pour ce qui est de l'investissement de chaque membre :

| Membre            | Investissement |
|-------------------|----------------|
| CHEN Eva          | 24 %           |
| MANGIN Maxence    | 27 %           |
| VIGNERON Loïse    | 26 %           |
| USAL Antoine      | 23 %           |

## 2.Structures de données pour les labyrinthes

Pour ce qui est des labyrinthes aléatoirs, nous avons utilisé **un tableau bidimensionnel** `Case[][] grille` d'objets `Case`, où chaque case représente une cellule avec des attributs de coordonnées, si c'est un mur ou un chemin. Cette structure est implémentée dans la classe `Labyrinthe`. Pour les labyrinthes parfaits, nous avons utilisé **deux tableaux bidimensionnels** de booléens : 
- `boolean[][] mursVerticaux`
- `boolean[][] mursHorizontaux`
Cette structure est implémentée dans la classe `LabyrintheParfait`. Nous avons décidé de ne pas utiliser d'autres structures, car celle du TP étaient plus **efficaces**, cela nous a permit de nous concentrer sur d'autres points.

## 3.Algorithmes de génération

- Pseudo-code de l'algorithme **Labyrinthes Aléatoires** : 

GénérerCheminGaranti(coordEntree) → liste<Case> chemin
    courant ← case à coordEntree
    ajouter courant au chemin
    tant que y < HAUTEUR-2
        voisinsValides ← voisins (bas, droite, gauche) internes, non présents dans chemin
        si voisinsValides non vide
            suivant ← choix aléatoire parmi voisinsValides
            ajouter suivant au chemin
            courant ← suivant
        sinon
            backtrack : retirer dernier élément du chemin
    fixer coordSortie juste sous la dernière case du chemin
    retourner chemin

Ensuite dans `genererAlea(largeur, hauteur, pourcentageMurs)` :

Toutes les cases du chemin garanti → `setMur(false)`
Bordures extérieures → murs
Cases internes non chemin → mur avec probabilité = pourcentageMurs

Structures utilisées : `ArrayList<Case>` pour le chemin, `Case[][]` pour la grille.
Implémentation : méthode `genererAlea` dans `GenererLabyrinthe.java` qui appelle `Parcours.genererChemin()`.

- Pseudo-code de l'algorithme **Labyrinthes Parfaits** :

GénérerLabyrintheParfait(largeur, hauteur)
    tous les murs verticaux et horizontaux ← true
    choisir coordEntree aléatoire sur ligne du haut
    visite[coordEntree] ← true
    pile.empiler(coordEntree)
    tant que pile non vide
        courant ← pile.sommet()
        voisins ← liste des 4 voisins non visités et dans les limites
        si voisins non vide
            voisin ← choix aléatoire parmi voisins
            enleverMur(courant, voisin)
            visite[voisin] ← true
            pile.empiler(voisin)
        sinon
            pile.dépiler()
    choisir coordSortie aléatoire sur ligne du bas

Structures utilisées : deux tableaux booléens pour les murs, `boolean[][] visite`, `Stack<int[]>` pour la pile.
Justification : les tableaux booléens sont extrêmement **compacts** et **rapides**.
Implémentation : méthode statique `genererParfait(largeur, hauteur, distanceMin)` dans `GenererLabyrinthe.java`.

## 4.Efficacité

#### 4.1 Labyrinthes aléatoires – influence du pourcentage de murs
| n   | Taille       | 20 % de murs (ms) | 30 % de murs (ms) | 50 % de murs (ms) |
|-----|--------------|-------------------|-------------------|-------------------|
|  20 |     20 ×  40 |                 1 |                 1 |                 1 |
|  40 |     40 ×  80 |                 3 |                 3 |                 3 |
|  60 |     60 × 120 |                 7 |                 7 |                 7 |
|  80 |     80 × 160 |                13 |                13 |                13 |
| 100 |    100 × 200 |                22 |                22 |                22 |

#### 4.2 Labyrinthes parfaits – comparaison des structures de données
| n   | Taille       | Structure TP2 tableaux booléens (ms)| Structure Case[][] (ms) | Gain     |
|-----|--------------|-------------------------------------|-------------------------|----------|
|  20 |     20 ×  40 |                                2.41 |                    3.28 | ×1.36    |
|  40 |     40 ×  80 |                                8.92 |                   14.11 | ×1.58    |
|  60 |     60 × 120 |                               20.15 |                   33.74 | ×1.67    |
|  80 |     80 × 160 |                               37.88 |                   65.92 | ×1.74    |
| 100 |    100 × 200 |                               59.34 |                  105.67 | ×1.78    |

**Conclusion** :  

## 5.Complément et Bilan

- **Labyrinthes Parfaits** : l'utilisation exclusive des deux tableaux de booléens `mursVerticaux` et `mursHorizontaux` dans la classe `LabyrintheParfait`.
aucun objet `LabyrintheParfait` ni aucune `Case` n’est créé pendant l’algorithme. L’objet final n’est instancié qu’une seule fois à la fin, ce qui divise énormément la consommation mémoire sur les grandes tailles.

- **Labyrinthes aléatoires** : conservation de la structure `Case[][] grille` dans la classe `Labyrinthe`, avec génération d'un chemin garanti via `Parcours.genererChemin()`, grâce à une `ArrayList<Case>`, puis un marquage des cases comme non-mur.

- **Génération statique** des labyrinthes parfaits dans la classe `GenererLabyrinthe`, pour ne créer l'objet `LabyrintheParfait` que à la toute fin, réduisant le nombre d'instance.

- **Système de progression** : 
    - Dans la classe `Progression`, l'utilisation d'une `HashMap<Integer, Map<Difficulte, Boolean>>` pour une validation par un accès direct, avec une validation de chaque défi et de chaque étape, au lieu d'une recherche linéaire dans la liste d'étapes
    - Les étapes ont des tailles croissantes définies dans le constructeur de `Progression`, c qui permet de charger progressivement des labyrinthes de plus en plus complexes au fur et à mesure que le joueur avance.
    - Les classes `Joueur`, `Progression`, `Etape`, `Defi` implémentent toutes `Serializable` pour une sauvegarde rapide.

Ces choix d'implémentation fourni dans le code représentent nos efforts d'optimisation que nous avons effectués, et qui ont un impact sur les performances et la consommation de mémoire.



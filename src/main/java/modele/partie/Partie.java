package modele.partie;

import modele.labyrinthe.Labyrinthe;

/**

Classe Partie
 */
public class Partie {
    /** Labyrinthe associé à la partie. */
    protected Labyrinthe labyrinthe;

    /**
     * Construit une nouvelle partie avec un labyrinthe spécifié.
     *
     * @param lab Le labyrinthe associé à la partie.
     */
    public Partie(Labyrinthe lab) {
        this.labyrinthe = lab;
    }

    /**
     * Vérifie si la partie est terminée en comparant les coordonnées du joueur avec celles de la sortie.
     * Si la partie est terminée, le mouvement du  joueur est bloqué.
     *
     * @param laby Le labyrinthe de la partie.
     * @return true si le joueur a atteint la sortie, false sinon.
     */
    public boolean terminerPartie(Labyrinthe laby) {
        int[] sortie = laby.getCoordSortie();
        int[] joueur = laby.getCoordJoueur();
        if (sortie[0] == joueur[0] && sortie[1] == joueur[1]) {
            laby.bloquerMouvement();
            return true;
        }
        return false;
    }
}
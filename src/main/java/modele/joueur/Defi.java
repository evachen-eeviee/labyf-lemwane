package modele.joueur;

import modele.labyrinthe.Difficulte;
import java.io.Serializable;

/**Classe Défi */
public class Defi implements Serializable {
    /** Largeur du labyrinthe du défi. */
    private int largeur;
    /** Hauteur du labyrinthe du défi. */
    private int hauteur;
    /** Niveau de difficulté du défi (facile, normal, difficile). */
    private Difficulte difficulte;
    /** Indique si le défi est terminé. */
    private boolean termine = false;

    /**
     * Construit un nouveau défi avec les dimensions et la difficulté spécifiées.
     *
     * @param l Largeur du labyrinthe.
     * @param h Hauteur du labyrinthe.
     * @param dif Niveau de difficulté du défi.
     * 
     */
    public Defi(int l, int h, Difficulte dif) {
        this.largeur = l;
        this.hauteur = h;
        this.difficulte = dif;
    }

    /**
     * Marque le défi comme terminé.
     */
    public void defiTermine() {
        this.termine = true;
    }

    /**
     * Retourne la largeur du labyrinthe
     *
     * @return La largeur du labyrinthe.
     */
    public int getLargeur() {
        return this.largeur;
    }

    /**
     * Retourne la hauteur du labyrinthe.
     * 
     *
     * @return La hauteur du labyrinthe
     */
    public int getHauteur() {
        return this.hauteur;
    }

    /**
     * Retourne la difficulté du défi
     *
     * @return La difficulté du défi.
     */
    public Difficulte getDifficulte() {
        return this.difficulte;
    }

    /**
     * Vérifie si le défi est terminé
     *
     * @return true si le défi est terminé, false sinon.
     */
    public boolean isTermine() {
        return this.termine;
    }
}
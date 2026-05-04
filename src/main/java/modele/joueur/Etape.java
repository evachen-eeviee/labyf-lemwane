package modele.joueur;

import modele.labyrinthe.Difficulte;
import java.io.Serializable;

/**Classe Etape */
public class Etape implements Serializable {
    /** Largeur par défaut du labyrinthe. */
    private int largeur;
/** hauteur par défaut du labyrinthe. */
    private int hauteur;
    /** Hauteur par défaut du labyrinthe. */
    private Defi[] defis;

    /**
     * Construit une nouvelle étape avec des dimensions par défaut pour les labyrinthes.
     * 
     *
     * @param largeur Largeur par défaut du labyrinthe.
     * @param hauteur Hauteur par défaut du labyrinthe.
     */
    public Etape(int largeur, int hauteur) {
        this.largeur = largeur;
        this.hauteur = hauteur;
        this.defis = new Defi[3];
       
        this.defis[0] = new Defi(largeur, hauteur, Difficulte.FACILE);
        this.defis[1] = new Defi(largeur, hauteur, Difficulte.NORMAL);
        this.defis[2] = new Defi(largeur, hauteur, Difficulte.DIFFICILE);
    }

    /**
     * Retourne la largeur par défaut du labyrinthe.
     *
     * @return La largeur du labyrinthe.
     */
    public int getLargeur() {
        return largeur;
    }

    /**
     * Retourne la hauteur par défaut du labyrinthe
     *
     * @return La hauteur du labyrinthe.
     */
    public int getHauteur() {
        return hauteur;
    }

    /**
     * Retourne le tableau des défis associés à l'étape.
     *
     * @return Un tableau contenant les défis pour chaque difficulté.
     */
    public Defi[] getDefis() {
        return defis;
    }

    /**
     * Retourne le défi associé à une difficulté donnée.
     *
     * @param difficulte La difficulté du défi recherché.
     * @return Le défi correspondant, ou null si aucun défi ne correspond.
     */
    public Defi getDefi(Difficulte difficulte) {
        for (Defi defi : defis) {
            if (defi.getDifficulte() == difficulte) {
                return defi;
            }
        }
        return null; 
    }

    /**
     * Retourne un string du défi associé à une difficulté donnée.
     *
     * @param difficulte La difficulté du défi.
     * @return La représentation textuelle du défi.
     * @throws NullPointerException si aucun défi ne correspond à la difficulté.
     */
    public String stringGetDefi(Difficulte difficulte) {
        return getDefi(difficulte).toString();
    }

    /**
     * Vérifie si l'étape est terminée, cad si au moins un défi est validé.
     *
     * @return true si au moins un défi est terminé, false sinon.
     */
    public boolean isEtapeTerminee() {
        for (Defi defi : defis) {
            if (defi.isTermine()) {
                return true; 
            }
        }
        return false;
    }

    /**
     * Retourne un string l'étape.
     *
     * @return Une chaîne décrivant la largeur, la hauteur et le nombre de défis.
     */
    @Override
    public String toString() {
        return "Etape{largeur=" + largeur + ", hauteur=" + hauteur + ", defis=" + defis.length + "}";
    }
}
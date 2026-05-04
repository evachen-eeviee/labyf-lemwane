package modele.partie;

import modele.labyrinthe.Labyrinthe;

/** Classe PartieLibre
 */
public class PartieLibre extends Partie {
    /**
     * Construit une nouvelle partie libre avec un labyrinthe spécifié.
     *
     * @param lab Le labyrinthe associé à la partie libre.
     */
    public PartieLibre(Labyrinthe lab) {
        super(lab);
    }

    /**
     * Retourne le labyrinthe associé à la partie libre.
     *
     * @return Le labyrinthe de la partie
     */
    public Labyrinthe getLabyrinthe() {
        return labyrinthe;
    }
}
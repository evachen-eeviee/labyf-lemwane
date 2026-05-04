package modele.partie;

import modele.joueur.*;
import modele.labyrinthe.Difficulte;
import modele.labyrinthe.Labyrinthe;

/**
Classe PartieProgression
 */
public class PartieProgression extends Partie {
    /** Joueur associé à la partie. */
    private Joueur joueur;

    /**
     * Construit une nouvelle partie en mode progression avec un labyrinthe et un joueur.
     *
     * @param lab Le labyrinthe associé à la partie.
     * @param joueur Le joueur participant à la partie.
     */
    public PartieProgression(Labyrinthe lab, Joueur joueur) {
        super(lab);
        this.joueur = joueur;
    }

    /**
     * Vérifie si la partie est terminée en comparant les coordonnées du joueur avec celles de la sortie.
     * Si la partie est terminée, le défi correspondant est marqué comme terminé, la progression est sauvegardée,
     * et l'étape suivante est activée si nécessaire.
     *
     * @param laby Le labyrinthe de la partie.
     * @param prog La progression du joueur.
     *
     * @param diff La difficulté du défi en cours.
     * @return true si le joueur a atteint la sortie, false sinon.
     */
    public boolean terminerPartie(Labyrinthe laby, Progression prog, Difficulte diff) {
        int[] sortie = laby.getCoordSortie();
        int[] joueur = laby.getCoordJoueur();

        if (sortie[0] == joueur[0] && sortie[1] == joueur[1]) {
            laby.bloquerMouvement();
            Defi defiActuel = prog.getEtapeActuelle().getDefi(diff);
            defiActuel.defiTermine();
            Sauvegarde.sauvegarderProfil(this.joueur, prog);
            if (prog.getEtapeActuelle().isEtapeTerminee()) {
                prog.avancerEtape();
            }
            return true;
        }
        return false;
    }

    /**
     * Retourne le joueur associé à la partie.
     *
     * @return Le joueur de la partie.
     */
    public Joueur getJoueur() {
        return joueur;
    }

    /**
     * Retourne le labyrinthe associé à la partie.
     *
     * @return Le labyrinthe de la partie.
     */
    public Labyrinthe getLabyrinthe() {
        return labyrinthe;
    }
}
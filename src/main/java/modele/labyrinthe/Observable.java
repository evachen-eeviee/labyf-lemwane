package modele.labyrinthe;

import java.util.ArrayList;
import java.util.List;

/**
Classe Observable
 */
public abstract class Observable {
    /** Liste des observateurs enregistrés. */
    private final List<Observateur> observateurs = new ArrayList<>();

    /**
     * Ajoute un observateur à la liste.
     *
     * @param o Observateur à ajouter.
     */
    public void ajouterObservateur(Observateur o) {
        observateurs.add(o);
    }

    /**
     * Supprime un observateur de la liste.
     *
     * @param o Observateur à supprimer.
     */
    public void supprimerObservateur(Observateur o) {
        observateurs.remove(o);
    }

    /**
     * Notifie tous les observateurs enregistrés d'un changement.
     */
    public void notifierObservateurs() {
        for (Observateur o : observateurs) {
            o.mettreAJour(this);
        }
    }
}
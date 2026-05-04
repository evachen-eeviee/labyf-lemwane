package modele.labyrinthe;

/**
Interface Observateur
 */
public interface Observateur {
    /**
     * Met à jour l'observateur lorsqu'un changement survient dans l'objet observé.
     *
     * @param o L'objet observable qui a changé.
     */
    void mettreAJour(Observable o);
}
package modele.labyrinthe;

/**
Enum Difficulte
 */
public enum Difficulte {
    /** Difficulté facile, avec un pourcentage de 0.3 (par exemple, pour 30% de murs). */
    FACILE(0.3),   
    /** Difficulté normale, avec un pourcentage de 0.5 (par exemple, pour 50% de murs). */
    NORMAL(0.4),
    /** Difficulté difficile, avec un pourcentage de 0.8 (par exemple, pour 80% de murs). */
    DIFFICILE(0.5);

    /** Pourcentage associé à la difficulté, utilisé pour calculer la complexité du labyrinthe. */
    private final double pourcentage;

    /**
     * Construit une difficulté avec un pourcentage spécifié.
     *
     * @param pourcentage Pourcentage de complexité associé à la difficulté.
     */
    Difficulte(double pourcentage) {
        this.pourcentage = pourcentage;
    }

    /**
     * Retourne le pourcentage associé à la difficulté.
     * 
     *
     * @return Le pourcentage de la difficulté.
     */
    public double getPourcentage() {
        return pourcentage;
    }
}
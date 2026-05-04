package modele.labyrinthe;

/**
Classe Case
 */
public class Case {
    /** Coordonnée x de la case. */
    private int x;
    /** Coordonnée y de la case. */
    private int y;

    /** Prédécesseur de la case dans un parcours. */
    private Case predecesseur;
    /** Indique si la case est un mur. */
    private boolean estMur;
    /** Indique si la case fait partie d'un chemin. */
    private boolean estChemin;

    /** Indique si la case a été visitée . */
    private boolean estVisitee;
    private boolean murHaut;
    private boolean murBas;
    private boolean murGauche;
    private boolean murDroite;

    /**
     * Construit une nouvelle case aux coordonnées spécifiées.
     *
     * @param x Coordonnée x de la case.
     * @param y Coordonnée y de la case.
     */
    public Case(int x, int y) {
        this.x = x;
        this.y = y;
        this.predecesseur = null;
        this.estMur = false;
        this.estChemin = false;
        this.estVisitee = false;
        this.murHaut = true;
        this.murBas = true;
        this.murGauche = true;
        this.murDroite = true;
    }

    // Getters pour les murs
    public boolean estMurHaut() { return murHaut; }
    public boolean estMurBas() { return murBas; }
    public boolean estMurGauche() { return murGauche; }
    public boolean estMurDroite() { return murDroite; }

    // Setters pour les murs
    public void setMurHaut(boolean val) { murHaut = val; }
    public void setMurBas(boolean val) { murBas = val; }
    public void setMurGauche(boolean val) { murGauche = val; }
    public void setMurDroite(boolean val) { murDroite = val; }

    /**
     * Retourne les coordonnées de la case.
     *
     * @return Un tableau contenant les coordonnées [x, y].
     */
    public int[] getCoordonnees() {
        return new int[]{this.x, this.y};
    }

    /**
     * Retourne le prédécesseur de la case dans un parcours.
     *
     * @return Le prédécesseur de la case, ou null si aucun.
     */
    public Case getPredecesseur() {
        return this.predecesseur;
    }

    /**
     * Vérifie si la case est un mur.
     *
     * @return true si la case est un mur, false sinon.
     */
    public boolean estMur() {
        return estMur;
    }

    /**
     * Vérifie si la case fait partie d'un chemin.
     *
     * @return true si la case est un chemin, false sinon.
     */
    public boolean estChemin() {
        return estChemin;
    }

    /**
     * Vérifie si la case a été visitée.
     *
     * @return true si la case a été visitée, false sinon.
     */
    public boolean estVisitee() {
        return estVisitee;
    }

    /**
     * Définit si la case est un mur.
     *
     * @param estMur true si la case est un mur, false sinon.
     */
    public void setMur(boolean estMur) {
        this.estMur = estMur;
    }

    /**
     * Définit si la case fait partie d'un chemin.
     *
     * @param estChemin true si la case est un chemin, false sinon.
     */
    public void setChemin(boolean estChemin) {
        this.estChemin = estChemin;
    }

    /**
     * Définit si la case a été visitée.
     *
     * @param estVisitee true si la case a été visitée, false sinon.
     */
    public void setEstVisitee(boolean estVisitee) {
        this.estVisitee = estVisitee;
    }

    /**
     * Définit le prédécesseur de la case dans un parcours.
     *
     * @param predecesseur Le prédécesseur de la case.
     */
    public void setPredecesseur(Case predecesseur) {
        this.predecesseur = predecesseur;
    }

    /**
     * Compare cette case avec un autre objet pour vérifier l'égalité basée sur les coordonnées.
     *
     * @param obj L'objet à comparer.
     * @return true si les coordonnées sont identiques, false sinon.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        Case other = (Case) obj;
        return this.x == other.x && this.y == other.y;
    }

    /**
     * Retourne une représentation textuelle de la case .
     *
     * @return Une chaîne de la forme "x,y".
     */
    @Override
    public String toString() {
        return x + "," + y;
    }
}
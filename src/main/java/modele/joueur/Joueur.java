package modele.joueur;

import java.io.Serializable;

/**Classe Joueur */
public class Joueur implements Serializable {
    /** Nom du joueur. */
    private String nom;
    /** Progression du joueur dans le jeu. */
    private Progression progression;
    /** Score total du joueur. */
    private int score;
    /** Mot de passe du joueur. */
    private String motDePasse;

    /**
     * Construit un joueur avec les paramètres spécifiés
     *
     * @param nom Nom du joueur.
     * @param prog Progression du joueur.
     * @param score Score initial du joueur.
     * @param motDePasse Mot de passe du joueur.
     */
    public Joueur(String nom, Progression prog, int score, String motDePasse) {
        this.nom = nom.trim();
        this.progression = prog;
        this.score = score;
        this.motDePasse = motDePasse;
    }

    /**
     * Construit un joueur avec un nom et un mot de passe, initialisant la progression et le score à zéro.
     *
     * @param nom Nom du joueur.
     * @param motDePasse Mot de passe du joueur.
     */
    public Joueur(String nom, String motDePasse) {
        this(nom, new Progression(), 0, motDePasse);
    }

    /**
     * Retourne le nom du joueur.
     *
     * @return Le nom du joueur.
     */
    public String getNom() {
        return this.nom;
    }

    /**
     * Retourne la progression du joueur.
     *
     * @return La progression du joueur.
     */
    public Progression getProgression() {
        return this.progression;
    }

    /**
     * Retourne le score du joueur.
     *
     * @return Le score total.
     */
    public int getScore() {
        return this.score;
    }

    /**
     * Retourne le mot de passe du joueur.
     *
     * @return Le mot de passe.
     */
    public String getMotDePasse() {
        return this.motDePasse;
    }

    /**
     * Ajoute des points au score du joueur.
     *
     * @param points Points à ajouter au score.
     */
    public void ajouterScore(int points) {
        this.score += points;
    }

    /**
     * Compare cet objet avec un autre pour vérifier l'égalité basée sur le nom du joueur.
     *
     * @param obj Objet à comparer.
     * @return true si les noms des joueurs sont identiques, false sinon
     * 
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (obj.getClass() != this.getClass()) return false;
        Joueur other = (Joueur) obj;
        return nom.equalsIgnoreCase(other.nom);
    }
}
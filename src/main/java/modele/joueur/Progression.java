package modele.joueur;

import modele.labyrinthe.Difficulte;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**Classe Progression */
public class Progression implements Serializable {
    /** Liste des étapes du jeu. */
    private List<Etape> etapes;
    /** Numéro de l'étape actuelle . */
    private int etapeActuelle;
    /** État de validation des défis pour chaque étape. */
    private Map<Integer, Map<Difficulte, Boolean>> validation;
    /** Chemin du fichier CSV pour la sauvegarde. */
    private static final String PATH_FILE_CSV = "profil_joueur.csv";

    /**
     * Construit une progression avec une liste d'étapes et une étape actuelle.
     *
     * @param etapes Liste des étapes du jeu.
     * @param etapeActuelle Numéro de l'étape actuelle.
     */
    public Progression(List<Etape> etapes, int etapeActuelle) {
        if (etapes == null) {
            this.etapes = new ArrayList<>();
            this.etapeActuelle = 0;
        } else {
            this.etapes = etapes;
            this.etapeActuelle = etapeActuelle;
        }

        this.validation = new HashMap<>();
        for (int i = 0; i < this.etapes.size(); i++) {
            Map<Difficulte, Boolean> diffValidation = new HashMap<>();
            diffValidation.put(Difficulte.FACILE, false);
            diffValidation.put(Difficulte.NORMAL, false);
            diffValidation.put(Difficulte.DIFFICILE, false);
            this.validation.put(i, diffValidation);
        }
    }

    public Progression(List<Etape> etapes, int etapeActuelle, Map<Integer, Map<Difficulte, Boolean>> validation) {
        this.etapes = etapes != null ? etapes : new ArrayList<>();
        this.etapeActuelle = etapeActuelle;
        this.validation = validation != null ? validation : new HashMap<>();
    }

    /**
     * Construit une progression par défaut avec trois étapes prédéfinies.
     */
    public Progression() {
        this(new ArrayList<>(), 1);
        this.addEtape(new Etape(10, 10));
        this.addEtape(new Etape(20, 20));
        this.addEtape(new Etape(40, 40));
        this.addEtape(new Etape(9,12));
        this.addEtape(new Etape(35, 35));
        this.addEtape(new Etape(40, 40));
    }

    /**
     * Retourne l'étape actuelle du joueur.
     *
     * @return L'étape actuelle, ou null si aucune étape n'est disponible.
     */
    public Etape getEtapeActuelle() {
        if (etapes.isEmpty()) {
            return null;
        }
        return this.etapes.get(this.etapeActuelle);
    }

     /**
     * Retourne le nombre total d'étapes.
     *
     * @return le nombre d'étapes.
     */
    public int getNombreEtapes() {
        return etapes.size();
    }

    /**
     * Retourne la liste des étapes du jeu.
     *
     * @return La liste des étapes.
     */
    public List<Etape> getEtapes() {
        return this.etapes;
    }

    /**
     * Retourne le numéro de l'étape actuelle.
     *
     * @return Le numéro de l'étape actuelle.
     */
    public int getIntEtapeActuelle() {
        return this.etapeActuelle;
    }

    /**
     * Retourne la carte des validations des défis pour chaque étape.
     *
     * @return Une carte associant chaque étape à ses validations par difficulté.
     */
    public Map<Integer, Map<Difficulte, Boolean>> getValidation() {
        return validation;
    }

    /**
     * Passe à l'étape suivante si l'étape actuelle est terminée.
     *
     * @return true si le joueur a avancé à l'étape suivante, false sinon.
     */
    public boolean avancerEtape() {
        if (etapes.isEmpty() || etapeActuelle >= etapes.size() - 1) {
            return false;
        }
        if (getEtapeActuelle().isEtapeTerminee()) {
            this.etapeActuelle++;
            return true;
        }
        return false;
    }

    /**
     * Ajoute une étape à la progression et initialise ses validations.
     *
     * @param e Étape à ajouter.
     */
    public void addEtape(Etape e) {
        this.etapes.add(e);
        Map<Difficulte, Boolean> diffValidation = new HashMap<>();
        diffValidation.put(Difficulte.FACILE, false);
        diffValidation.put(Difficulte.NORMAL, false);
        diffValidation.put(Difficulte.DIFFICILE, false);
        this.validation.put(etapes.size() - 1, diffValidation);
    }

    /**
     * Valide un défi pour une étape donnée à une difficulté spécifique
     *
     * @param numEtape Numéro de l'étape.
     * @param difficulte Difficulté du défi à valider.
     */
    public void validerDefi(int numEtape, Difficulte difficulte) {
        if (validation.containsKey(numEtape)) {
            validation.get(numEtape).put(difficulte, true);
            Etape etape = etapes.get(numEtape);
            Defi defi = etape.getDefi(difficulte);
            if (defi != null) {
                defi.defiTermine();
            }
        }
    }

    /**
     * Affiche le numéro de l'étape actuelle.
     * 
     * 
     *
     * @return Le numéro de l'étape actuelle, ou -1 si aucune étape n'est disponible.
     */
    public int afficherEtape() {
        Etape etape = getEtapeActuelle();
        if (etape == null) {
            return -1;
        } else {
            return this.etapeActuelle;
        }
    }

    /**
     * Affiche la prochaine difficulté non complétée pour l'étape actuelle.
     *
     * @return Le nom de la difficulté non complétée, ou "Aucune difficulté en cours" si toutes sont complétées.
     */
    public String afficherDifficulte() {
        Etape etape = getEtapeActuelle();
        if (etape == null) {
            return "Pas d'étape disponible";
        }
        Difficulte[] ordreDifficultes = {Difficulte.FACILE, Difficulte.NORMAL, Difficulte.DIFFICILE};
        for (Difficulte diff : ordreDifficultes) {
            boolean complete = validation.get(this.etapeActuelle).get(diff);
            if (!complete) {
                return diff.toString();
            }
        }
        return "Aucune difficulté en cours";
    }

    /**
     * Récupère un tableau des booléens indiquant l'état de validation des défis pour toutes les étapes.
     * 
     *
     * @return Un tableau de booléens représentant les validations pour chaque étape.
     */
    public boolean[] recupererBooleens() {
        int totalBooleens = etapes.size() * 3;
        boolean[] booleens = new boolean[totalBooleens];
        int index = 0;

        for (int i = 0; i < etapes.size(); i++) {
            Map<Difficulte, Boolean> diffValidation = validation.get(i);
            booleens[index++] = diffValidation.get(Difficulte.FACILE);
            booleens[index++] = diffValidation.get(Difficulte.NORMAL);
            booleens[index++] = diffValidation.get(Difficulte.DIFFICILE);
        }

        return booleens;
    }

    /**
     * Détermine si chaque étape est entièrement validée en fonction des booléens fournis.
     *
     * @param booleens Tableau de booléens représentant les validations des défis.
     * @return Un tableau indiquant si chaque étape est entièrement validée.
     * @throws IllegalArgumentException si le tableau de booléens est vide ou non divisible par 3.
     */
    public boolean[] recupererEtapeValider(boolean[] booleens) {
        if (booleens.length == 0 || booleens.length % 3 != 0) {
            System.err.println("Le nombre de booléens n'est pas divisible par 3 ou le tableau est vide.");
            return new boolean[0];
        }

        int nombreEtapes = booleens.length / 3;
        boolean[] resultat = new boolean[nombreEtapes];

        for (int i = 0; i < nombreEtapes; i++) {
            int index = i * 3;
            resultat[i] = booleens[index] && booleens[index + 1] && booleens[index + 2];
        }

        return resultat;
    }

     /**
     * Vérifie si une étape donnée est accessible au joueur.
     * 
     * Une étape est considérée accessible si :
     *  - c'est la première étape
     *                OU
     *  - l'étape précédente est entièrement validée (FACILE + NORMAL + DIFFICILE).
     *
     * @param numeroEtape Numéro de l'étape (commence à 1).
     * @return true si l'étape peut être jouée, false sinon.
     */
    public boolean estEtapeDisponible(int numeroEtape) {
        if (numeroEtape <= 1) return true; // première étape toujours dispo

        int prevIndex = numeroEtape - 2;
        Map<Difficulte, Boolean> validationPrecedente = validation.get(prevIndex);
        if (validationPrecedente == null) return false;

        boolean unValide = validationPrecedente.get(Difficulte.FACILE)
                || validationPrecedente.get(Difficulte.NORMAL)
                || validationPrecedente.get(Difficulte.DIFFICILE);

        return unValide;
    }

    public void setValidation(Map<Integer, Map<Difficulte, Boolean>> validation) {
        this.validation = validation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if(o.getClass()!=this.getClass())return false;
        Progression other = (Progression) o;
        return etapeActuelle == other.etapeActuelle && etapes.equals(other.etapes) && validation.equals(other.validation);
    }
}
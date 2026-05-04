package Controleur;

import modele.joueur.Progression;
import modele.labyrinthe.Difficulte;

public class ProgressionController {
    private Progression progression;

    public ProgressionController(Progression progression) {
        this.progression = progression;
    }
    public boolean[] getEtapesValidees() {
        boolean[] booleens = progression.recupererBooleens();
        return progression.recupererEtapeValider(booleens);
    }

    public String[] getDefisEtape(int indexEtape) {
        if (indexEtape < 0 || indexEtape >= progression.getEtapes().size()) {
            return new String[]{"Aucune étape disponible"};
        }

        String[] defis = new String[3];
        defis[0] = "Facile : " + (progression.getValidation().get(indexEtape).get(Difficulte.FACILE) ? "Terminé" : "Non terminé");
        defis[1] = "Normal : " + (progression.getValidation().get(indexEtape).get(Difficulte.NORMAL) ? "Terminé" : "Non terminé");
        defis[2] = "Difficile : " + (progression.getValidation().get(indexEtape).get(Difficulte.DIFFICILE) ? "Terminé" : "Non terminé");

        return defis;
    }

    public boolean getValidationDefi(int indexEtape, Difficulte diff) {
        if (progression == null || indexEtape < 0 || indexEtape >= progression.getEtapes().size()) {
            return false;
        }
        return progression.getValidation().get(indexEtape).get(diff);
    }
}

package Controleur;

import modele.joueur.Joueur;
import modele.joueur.Sauvegarde;
import java.util.List;
public class ChargerProfilController {

    public Joueur chargerProfil(String nom, String motDePasse) {
        List<Joueur> joueurs = Sauvegarde.chargerSer();

        for (Joueur j : joueurs) {
            if (j.getNom().equals(nom) && j.getMotDePasse().equals(motDePasse)) {
                System.out.println("Profil chargé avec succès : " + nom);
                return j;
            }
        }
        System.out.println("Nom ou mot de passe incorrect.");
        return null;
    }
}

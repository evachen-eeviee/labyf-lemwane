package TestJoueur;

import modele.joueur.Joueur;
import modele.joueur.Progression;
import modele.joueur.Sauvegarde;
import modele.labyrinthe.Difficulte;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SauvegardeTest {
    private static Joueur joueur;
    private static Progression progression;

    @BeforeEach
    public void setUp(){
        progression=new Progression();
        progression.validerDefi(0, Difficulte.FACILE);
        progression.validerDefi(1, Difficulte.NORMAL);
        joueur = new Joueur("Oli", progression, 100, "1234");
    }

    @Test
    public void testCreerEtSauvegarderJoueurCSV(){
        Sauvegarde.creerEtSauvegarderJoueur("CSVjoueur", "mdp");
        Object [] charge = Sauvegarde.chargerProfil("CSVjoueur", "mdp");
        assertNotNull(charge[0]);

        Joueur csvjoueur = (Joueur) charge[0];
        assertEquals("CSVjoueur", csvjoueur.getNom());
        assertEquals("mdp", csvjoueur.getMotDePasse());
    }

//    @Test
//    public void testSavesETChargementProfilCSV(){
//        Sauvegarde.sauvegarderProfil(joueur, progression);
//        Object[] charge = Sauvegarde.chargerProfil("Oli","1234");
//        assertNotNull(charge[0]);
//
//        Joueur joueurCharge = (Joueur) charge[0];
//        Progression progChargee = (Progression) charge[1];
//
//        assertNotNull(joueurCharge);
//        assertNotNull(progChargee);
//        assertEquals(joueur.getNom(), joueurCharge.getNom());
//        assertEquals(joueur.getMotDePasse(), joueurCharge.getMotDePasse());
//        assertEquals(joueur.getScore(), joueurCharge.getScore());
//        assertEquals(progChargee, progression); // voir pour faire un equals dans Progression
//    }

    @Test
    public void testSauvegardeEtChargementSer(){
        Sauvegarde.majJoueurSer(joueur);

        List<Joueur> joueursCharges = Sauvegarde.chargerSer();

        assertTrue(joueursCharges.contains(joueur));
    }
}

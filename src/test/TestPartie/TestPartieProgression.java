package TestPartie;

import modele.labyrinthe.Difficulte;
import modele.labyrinthe.Labyrinthe;
import modele.partie.PartieProgression;
import modele.joueur.Joueur;
import modele.joueur.Progression;
import modele.joueur.Etape;
import modele.joueur.Defi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestPartieProgression {
    private Labyrinthe laby;
    private Joueur joueur;
    private PartieProgression partie;
    private Progression progression;
    private Difficulte difficulte;

    @BeforeEach
    void setUp() {
        laby = new Labyrinthe(5, 5, null);
        laby.setCoordEntree(0, 0);
        laby.setCoordSortie(4, 4);

        joueur= new Joueur("TheSky59", "mdp");

        progression = new Progression();
        difficulte = Difficulte.FACILE;

        partie = new PartieProgression(laby, joueur);
    }

    @Test
    void testConstructeurEtGetters() {
        assertNotNull(partie.getLabyrinthe());
        assertNotNull(partie.getJoueur());
        assertEquals(joueur, partie.getJoueur());
    }


    @Test
    void testTerminerPartieQuandJoueurALaSortie() {
        // 🔹 Place le joueur sur la sortie
        int[] sortie = laby.getCoordSortie();
        laby.setCoordJoueur(sortie[0], sortie[1]);

        boolean resultat = partie.terminerPartie(laby, progression, difficulte);

        assertTrue(resultat);
        assertTrue(laby.isMouvementBloque());
    }

}


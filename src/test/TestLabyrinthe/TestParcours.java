package TestLabyrinthe;

import modele.labyrinthe.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class TestParcours {


    private Labyrinthe lab;
    private Parcours parcours;

    @BeforeEach
    public void setUp() {
        lab = new Labyrinthe(5, 5, null);

        for (int x = 0; x < lab.getLARGEUR(); x++) {
            for (int y = 0; y < lab.getHAUTEUR(); y++) {
                lab.getCase(x, y).setMur(false);
            }
        }

        // On fixe entrée et sortie
        lab.setCoordEntree(1, 0);
        lab.setCoordSortie(lab.getLARGEUR() - 2, lab.getHAUTEUR() - 2);

        parcours = new Parcours(lab);
    }
    @Test
    void testGenererCheminLongueurEtMurs() {
        List<Case> chemin = parcours.genererChemin();

        assertFalse(chemin.isEmpty(), "Le chemin ne doit pas être vide");

        // Toutes les cases du chemin doivent être accessibles (pas de murs)
        for (Case c : chemin) {
            assertFalse(c.estMur(), "Toutes les cases du chemin doivent être accessibles");
        }
    }

    @Test
    void testSortieFixee() {
        List<Case> chemin = parcours.genererChemin();
        Case sortie = lab.getCase(lab.getCoordSortie()[0], lab.getCoordSortie()[1]);
        assertTrue(chemin.contains(sortie) || chemin.get(chemin.size()-1).equals(sortie)
                        || Math.abs(chemin.get(chemin.size()-1).getCoordonnees()[0] - sortie.getCoordonnees()[0]) <= 1);
    }

    @Test
    void testGenererCheminNonVide() {
        List<Case> chemin = parcours.genererChemin();
        assertNotNull(chemin);
        assertFalse(chemin.isEmpty());
        for (Case c : chemin) {
            assertFalse(c.estMur());
        }
    }

    @Test
    void testPlusCourtChemin() {
        List<Case> chemin = parcours.plusCourtChemin();

        assertNotNull(chemin);
        assertFalse(chemin.isEmpty());
        assertTrue(chemin.contains(lab.getCase(lab.getCoordEntree()[0], lab.getCoordEntree()[1])));
        assertTrue(chemin.contains(lab.getCase(lab.getCoordSortie()[0], lab.getCoordSortie()[1])));
    }

    @Test
    void testGetVoisinsParcoursCentre() {
        Case centre = lab.getCase(2, 2);
        List<Case> voisins = parcours.getVoisinsParcours(centre);
        assertEquals(4, voisins.size());
        assertTrue(voisins.contains(lab.getCase(1, 2)));
        assertTrue(voisins.contains(lab.getCase(3, 2)));
        assertTrue(voisins.contains(lab.getCase(2, 1)));
        assertTrue(voisins.contains(lab.getCase(2, 3)));
    }

    @Test
    void testGetVoisinsParcoursMur() {
        lab.getCase(2, 3).setMur(true);
        Case centre = lab.getCase(2, 2);
        List<Case> voisins = parcours.getVoisinsParcours(centre);
        assertEquals(3, voisins.size());
        assertFalse(voisins.contains(lab.getCase(2, 3)));
    }

    @Test
    void testGetVoisinsCheminExclutChemin() {
        Case centre = lab.getCase(2, 2);

        Set<Case> chemin = Set.of(lab.getCase(2, 1)); // case déjà visitée
        List<Case> voisins = parcours.getVoisinsChemin(centre, chemin);

        assertFalse(voisins.contains(lab.getCase(2, 1)));
        assertTrue(voisins.contains(lab.getCase(1, 2)));
        assertTrue(voisins.contains(lab.getCase(3, 2)));
        assertTrue(voisins.contains(lab.getCase(2, 3)));
    }
}

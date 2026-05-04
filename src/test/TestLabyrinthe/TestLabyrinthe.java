package TestLabyrinthe;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import modele.labyrinthe.GenererLabyrinthe;
import modele.labyrinthe.Labyrinthe;
import modele.labyrinthe.Case;

public class TestLabyrinthe {
    private int largeur;
    private int hauteur;
    private  double pourcentageMurs;

    private Labyrinthe laby;
    @BeforeEach
    public void setUp(){
        largeur = 5;
        hauteur = 5;
        pourcentageMurs = 0.5;
        laby = GenererLabyrinthe.genererAlea(largeur, hauteur, pourcentageMurs);
    }

    @Test
    public void testGrilleNonNulle() {
        assertNotNull(laby.getGrille(), "La grille ne doit pas être null");
    }

    @Test
    public void testDimensions() {
        assertEquals(largeur, laby.getLARGEUR());
        assertEquals(hauteur, laby.getHAUTEUR());
    }

    @Test
    public void testCheminNonMur() {
        for (int x = 0; x < laby.getLARGEUR(); x++) {
            for (int y = 0; y < laby.getHAUTEUR(); y++) {
                Case c = laby.getCase(x, y);
                if (c.estChemin()) {
                    assertFalse(c.estMur());
                }
            }
        }
    }

    @Test
    public void testCoordonneesCases() {
        Case[][] grille = laby.getGrille();
        for(int x = 0; x < largeur; x++){
            for(int y = 0; y < hauteur; y++){
                int[] coor = grille[x][y].getCoordonnees();
                assertArrayEquals(new int[]{x, y}, coor);
            }
        }
    }
}

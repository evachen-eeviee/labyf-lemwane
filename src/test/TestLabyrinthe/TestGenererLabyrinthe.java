package TestLabyrinthe;

import modele.labyrinthe.GenererLabyrinthe;
import modele.labyrinthe.Labyrinthe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
public class TestGenererLabyrinthe {
    private int largeur;
    private int hauteur;

    private double pourcentage;
    private Labyrinthe laby;

    @BeforeEach
    public void setUp(){
        largeur=5;
        hauteur=5;
        pourcentage=0.5;
        laby= GenererLabyrinthe.genererAlea(largeur,hauteur,pourcentage);
    }

    @Test
    public void testGrilleNonNulle() {
        assertNotNull(laby.getGrille());
    }

    @Test
    public void testDimensions() {
        assertEquals(largeur, laby.getLARGEUR());
        assertEquals(hauteur, laby.getHAUTEUR());
    }

}

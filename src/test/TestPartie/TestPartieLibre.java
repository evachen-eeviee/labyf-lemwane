package TestPartie;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import modele.labyrinthe.GenererLabyrinthe;
import modele.labyrinthe.Labyrinthe;
import modele.partie.PartieLibre;

import static org.junit.jupiter.api.Assertions.*;

public class TestPartieLibre {

    private Labyrinthe laby;
    private PartieLibre partie;

    @BeforeEach
    public void setUp(){
        laby=new Labyrinthe(10,10, null);
        partie=new PartieLibre(laby);
    }

    @Test
    void testDemarrerPartie() {
        assertNotNull(partie.getLabyrinthe(), "Le labyrinthe ne doit pas être null après la construction");

        Labyrinthe nouveauLabyrinthe = GenererLabyrinthe.genererAlea(20, 20, 0.3);
        assertNotNull(nouveauLabyrinthe, "Le labyrinthe généré par GenererLabyrinthe ne doit pas être null");
        assertEquals(20, nouveauLabyrinthe.getGrille().length, "La largeur du labyrinthe généré doit être 20");
        assertEquals(20, nouveauLabyrinthe.getGrille()[0].length, "La hauteur du labyrinthe généré doit être 20");
    }

    @Test
    void testTerminerPartie() {
        assertDoesNotThrow(() -> partie.terminerPartie(laby), "terminerPartie ne doit pas lever d'exception");
        assertEquals(laby, partie.getLabyrinthe(), "terminerPartie ne doit pas modifier le labyrinthe de l'instance");
    }

}

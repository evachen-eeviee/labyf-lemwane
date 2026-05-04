package TestLabyrinthe;

import modele.labyrinthe.Case;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;


public class TestCase {

    private Case c;

    @BeforeEach
    public void setUp(){
        c=new Case(1,2);
    }
    @Test
    public void testEstMur() {
        assertFalse(c.estMur(), "Une case nouvellement créée ne devrait pas être un mur par défaut");
        c.setMur(true);
        assertTrue(c.estMur(), "La case devrait être un mur après appel de setMur(true)");
    }

    @Test
    public void testGetCoordonnees() {
        int[] coo  = c.getCoordonnees();
        assertArrayEquals(new int[]{1, 2}, coo, "Les coordonnées doivent correspondre à celles définies dans le constructeur");
        assertNotNull(coo, "Le tableau des coordonnées ne doit pas être null");
        assertEquals(2, coo.length, "Le tableau des coordonnées doit contenir exactement deux éléments");
    }

    @Test
    public void testSetMur() {
        assertFalse(c.estMur(), "La case ne devrait pas être un mur au départ");
        c.setMur(true);
        assertTrue(c.estMur(), "La case devrait être un mur après setMur(true)");
        c.setMur(false);
        assertFalse(c.estMur(), "La case ne devrait plus être un mur après setMur(false)");
    }

    @Test
    public void testSetCheminEtVisitee(){
        assertFalse(c.estChemin());
        c.setChemin(true);
        assertTrue(c.estChemin());

        assertFalse(c.estVisitee());
        c.setEstVisitee(true);
        assertTrue(c.estVisitee());
    }

    @Test
    public void testPred(){
        Case pred = new Case(0,1);
        assertNull(c.getPredecesseur());
        c.setPredecesseur(pred);
        assertEquals(pred, c.getPredecesseur());
    }

    @Test
    public void testEqualsEtToString(){
        Case same = new Case(1,2);
        Case diff = new Case(2,3);

        assertEquals(c, same);
        assertNotEquals(c, diff);

        assertEquals("1,2", c.toString());
    }
}

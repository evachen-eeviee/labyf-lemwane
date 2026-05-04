package TestLabyrinthe;

import modele.labyrinthe.Direction;
import modele.labyrinthe.LabyrintheParfait;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestLabyrintheParfaitTest {

    private LabyrintheParfait laby;

    @BeforeEach
    void setUp() {
        laby = new LabyrintheParfait(10, 8, 3);
    }

    @Test
    void testDimensions() {
        assertEquals(10, laby.getLARGEUR());
        assertEquals(8, laby.getHAUTEUR());
    }

    @Test
    void testMursInitialises() {
        for (int x = 0; x < laby.mursVerticaux.length; x++) {
            for (int y = 0; y < laby.mursVerticaux[0].length; y++) {
                assertTrue(laby.mursVerticaux[x][y]);
            }
        }
        for (int x = 0; x < laby.murHorizontaux.length; x++) {
            for (int y = 0; y < laby.murHorizontaux[0].length; y++) {
                assertTrue(laby.murHorizontaux[x][y]);
            }
        }
    }

    @Test
    void testEntreeEtSortieCorrectes() {
        int[] entree = laby.getCoordEntree();
        int[] sortie = laby.getCoordSortie();

        assertEquals(0, entree[1]);
        assertEquals(laby.getHAUTEUR() - 1, sortie[1]);

        assertTrue(entree[0] >= 1 && entree[0] < laby.getLARGEUR() - 1);
        assertTrue(sortie[0] >= 1 && sortie[0] < laby.getLARGEUR() - 1);
    }

    @Test
    void testEnleverMur() {
        laby.enleverMur(2, 2, 2, 3);
        assertFalse(laby.murHorizontaux[2][2]);

        laby.enleverMur(4, 5, 5, 5);
        assertFalse(laby.mursVerticaux[4][5]);
    }

    @Test
    void testMurEntreCases() {
        assertTrue(getPrivateMurEntre(laby, 0, 0, 1, 0));
        laby.enleverMur(0, 0, 1, 0);
        assertFalse(getPrivateMurEntre(laby, 0, 0, 1, 0));
    }

    private boolean getPrivateMurEntre(LabyrintheParfait l, int x1, int y1, int x2, int y2) {
        try {
            var m = LabyrintheParfait.class.getDeclaredMethod("murEntreCases", int.class, int.class, int.class, int.class);
            m.setAccessible(true);
            return (boolean) m.invoke(l, x1, y1, x2, y2);
        } catch (Exception e) {
            fail(e);
            return false;
        }
    }

    @Test
    void testDeplacementBloqueParMur() {
        int[] start = laby.getCoordJoueur().clone();

        laby.deplacerJoueur(Direction.BAS);
        assertArrayEquals(start, laby.getCoordJoueur());
    }

    @Test
    void testDeplacementPossible() {
        int x = laby.getCoordEntree()[0];
        int y = laby.getCoordEntree()[1];

        laby.enleverMur(x, y, x, y + 1);

        laby.deplacerJoueur(Direction.BAS);

        assertEquals(x, laby.getCoordJoueur()[0]);
        assertEquals(y + 1, laby.getCoordJoueur()[1]);
    }
}
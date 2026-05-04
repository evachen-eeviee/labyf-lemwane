package TestJoueur;

import modele.joueur.Defi;
import modele.joueur.Etape;
import modele.labyrinthe.Difficulte;
import org.apache.commons.lang3.builder.Diff;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EtapeTest {

    private Etape etape;

    @BeforeEach
    void setUp() {
        etape = new Etape(10,15);
    }

    @Test
    void testGetters(){
        assertEquals(10, etape.getLargeur());
        assertEquals(15, etape.getHauteur());

        Defi[] defis = etape.getDefis();
        assertNotNull(defis);
        assertEquals(3, defis.length);
        assertEquals(Difficulte.FACILE, defis[0].getDifficulte());
        assertEquals(Difficulte.NORMAL, defis[1].getDifficulte());
        assertEquals(Difficulte.DIFFICILE, defis[2].getDifficulte());
    }

    @Test
    void testEtapeTerminee(){
        assertFalse(etape.isEtapeTerminee());
        etape.getDefi(Difficulte.NORMAL).defiTermine();
        assertTrue(etape.isEtapeTerminee());
    }

}
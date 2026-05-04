package TestJoueur;

import modele.joueur.Defi;
import modele.labyrinthe.Difficulte;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;


class DefiTest {

    private Defi defi;
    @BeforeEach
    void setUp(){
        defi = new Defi(10,15,Difficulte.FACILE);
    }
    @Test
    void testConstructeurEtGetters() {
        assertEquals(10, defi.getLargeur());
        assertEquals(15, defi.getHauteur());
        assertEquals(Difficulte.FACILE, defi.getDifficulte());
        assertFalse(defi.isTermine());
    }

    @Test
    void testDefiTermine() {
        assertFalse(defi.isTermine());
        defi.defiTermine();
        assertTrue(defi.isTermine());
    }

    @Test
    void testConstructeurValeursLimites() {

        Difficulte difficulte = Difficulte.FACILE;
        Defi defi = new Defi(1, 1, difficulte);

        assertEquals(1, defi.getLargeur(), "La largeur doit être 1");
        assertEquals(1, defi.getHauteur(), "La hauteur doit être 1");
        assertEquals(difficulte, defi.getDifficulte(), "La difficulté doit correspondre");
        assertFalse(defi.isTermine(), "Le défi ne doit pas être terminé par défaut");
    }
}
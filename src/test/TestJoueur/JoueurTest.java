package TestJoueur;
import modele.joueur.Joueur;
import modele.joueur.Progression;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JoueurTest {

    private Joueur joueur;
    private Progression progression;

    @BeforeEach
    void setUp(){
        progression=new Progression();
        joueur = new Joueur("Maxou", progression, 100, "mdp");
    }

    @Test
    void testGetters(){
        assertEquals("Maxou", joueur.getNom());
        assertEquals(progression, joueur.getProgression());
        assertEquals(100, joueur.getScore());
        assertEquals("mdp", joueur.getMotDePasse());
    }

    @Test
    void testAjouterScore(){
        joueur.ajouterScore(50);
        assertEquals(150, joueur.getScore());
    }

    @Test
    void testEquals(){
        Joueur j2 = new Joueur("Antoine", new Progression(), 0, "pwd");
        assertFalse(joueur.equals(j2));
        Joueur j3 = new Joueur("Maxou", new Progression(), 0, "123");
        assertTrue(joueur.equals(j3));
        assertFalse(joueur.equals(null));
        assertFalse(joueur.equals("pasJoueur"));
    }
}
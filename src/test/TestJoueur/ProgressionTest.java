package TestJoueur;

import modele.joueur.Defi;
import modele.joueur.Etape;
import modele.joueur.Progression;
import modele.labyrinthe.Difficulte;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ProgressionTest {

    private List<Etape> etapes;
    private int etapeActuelle;
    private Etape etape;

    @BeforeEach
    void setUp() {
        etape = new Etape(10,10);
        etapes = new ArrayList<>();
        etapes.add(etape);
        etapeActuelle = 0;
    }

    @Test
    void testConstructeurParametre() {
        Progression progression = new Progression(etapes, etapeActuelle);
        assertEquals(etapes, progression.getEtapes(), "La liste d'étapes doit correspondre");
        assertEquals(etape, progression.getEtapeActuelle(), "L'étape actuelle doit correspondre");
    }

    @Test
    void testConstructeurParDefaut() {
        Progression progression = new Progression();
        assertNotNull(progression.getEtapes(), "La liste d'étapes ne doit pas être null");
        assertFalse(progression.getEtapes().isEmpty(), "La liste d'étapes ne doit être vide");
        assertEquals(6, progression.getEtapes().size());
        assertEquals(1, progression.getIntEtapeActuelle());
    }

    @Test
    void testGetEtapeActuelle() {
        Progression progression = new Progression(etapes, etapeActuelle);
        assertEquals(etape, progression.getEtapeActuelle(), "getEtapeActuelle() doit retourner l'étape à l'index etapeActuelle");
    }

    @Test
    void testGetEtapes() {
        Progression progression = new Progression(etapes, etapeActuelle);
        assertEquals(etapes, progression.getEtapes(), "getEtapes() doit retourner la liste d'étapes correcte");
    }

    @Test
    void testAddEtape(){
        Progression progression = new Progression(etapes, etapeActuelle);
        assertEquals(etapes, progression.getEtapes());
    }

    @Test
    void testValiderDefi(){
        Progression progression = new Progression();
        int i=progression.getIntEtapeActuelle();
        progression.validerDefi(i, Difficulte.FACILE);
        assertTrue(progression.getValidation().get(i).get(Difficulte.FACILE));
        assertTrue(progression.getEtapeActuelle().getDefi(Difficulte.FACILE).isTermine());
    }
    @Test
    void testAvancerEtape() {
        etapes.add(new Etape(30, 30));
        etapes.add(new Etape(20,20));
        Progression progression = new Progression(etapes, etapeActuelle);
        int etapeAnterieure = progression.getIntEtapeActuelle();

        assertFalse(progression.avancerEtape());
        assertEquals(etapeAnterieure, progression.getIntEtapeActuelle());

        for (Defi d : progression.getEtapeActuelle().getDefis()){
            d.defiTermine();
        }
        assertTrue(progression.avancerEtape());
        assertEquals(etapeAnterieure+1, progression.getIntEtapeActuelle());
    }

    @Test
    void testAfficherEtape(){
        Progression progression = new Progression();
        assertEquals(progression.getIntEtapeActuelle(), progression.afficherEtape());
    }

    @Test
    void testAfficherDifficulte() {
        Progression progression = new Progression();
        assertEquals(Difficulte.FACILE.toString(), progression.afficherDifficulte());

        progression.validerDefi(progression.getIntEtapeActuelle(), Difficulte.FACILE);
        assertEquals(Difficulte.NORMAL.toString(), progression.afficherDifficulte());

        progression.validerDefi(progression.getIntEtapeActuelle(), Difficulte.NORMAL);
        progression.validerDefi(progression.getIntEtapeActuelle(), Difficulte.DIFFICILE);
        assertEquals("Aucune difficulté en cours", progression.afficherDifficulte());
    }

    @Test
    void testRecupererEtapeValider(){
        Progression progression = new Progression();
        boolean[] tab = {true, true, true, true, false, true, true, true, true};
        boolean[] result = progression.recupererEtapeValider(tab);
        assertArrayEquals(new boolean[]{true,false,true}, result);
    }
}
import { test, describe, beforeEach } from 'node:test';
import assert from 'node:assert';
import Joueur from './Joueur.ts';

describe('Tests de la classe Joueur - Cas Hugo', () => {
    
    // On nettoie la liste statique avant chaque test
    beforeEach(() => {
        Joueur.joueurs = [];
    });

    test('doit créer Hugo avec 5 PV et 0.5 de vitesse', () => {
        const id = "Huguette_La_Chevre"; 
        const nom = "Hugo";
        const vieMax = 5;
        const vitesse = 0.5;

        const hugo = new Joueur(id, nom, "JeSuisCadre", vieMax, vitesse);

        // Vérification des identités
        assert.strictEqual(hugo.nom, "Hugo");
        assert.strictEqual(hugo.id, id);
        
        // Vérification des stats Vitalité
        assert.strictEqual(hugo.vie, 5);
        assert.strictEqual(hugo.vie_max, 5);
        
        // Vérification de la vitesse (nombre décimal)
        assert.strictEqual(hugo.vitesse, 0.5);
        
        // Vérification des scores initiaux
        assert.strictEqual(hugo.score, 0);
        assert.strictEqual(hugo.ennemisTues, 0);
    });

    test(`Hugo ne doit pas dépasser ses PV Max lors d'un addVie`, () => {
        const hugo = new Joueur("1", "Hugo", "", 5, 0.5);
        
        // On lui retire de la vie
        hugo.vie = 2; 
        
        // On le soigne de beaucoup
        hugo.addVie(10); 
        
        // Il doit être capé à son maximum de 5
        assert.strictEqual(hugo.vie, 5);
    });

    test('Hugo doit mourir quand ses PV tombent à 0', () => {
        const hugo = new Joueur("1", "Hugo", "", 5, 0.5);
        
        assert.strictEqual(hugo.isDead(), false);
        
        hugo.vie = 0;
        assert.strictEqual(hugo.isDead(), true);
    });

    test('Hugo doit être présent dans la liste globale des joueurs', () => {
        const hugo = new Joueur("Biquette_id", "Hugo", "", 5, 0.5);
        
        assert.strictEqual(Joueur.joueurs.length, 1);
        assert.strictEqual(Joueur.joueurs[0].nom, "Hugo");
    });
});
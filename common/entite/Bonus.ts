import { BonusType } from "./BonusType.ts";
import Entite from "./Entite.ts";
import type Joueur from "./Joueur.ts";

export default class Bonus extends Entite {
    typeEffet : string;
    valeur : number;
    

    constructor(nom:string, image_path : string, vie_max : number, vitesse : number, typeEffet : string, valeur : number){
        super(nom, image_path, vie_max, vitesse);
        this.typeEffet = typeEffet;
        this.valeur = valeur;
    }

    appliquerEffet(joueur : Joueur) {
        switch(this.typeEffet) {
            case BonusType.VIE:
                joueur.vie = Math.min(joueur.vie + this.valeur, joueur.vie_max);
                break;
            case BonusType.SCORE:
                joueur.score *= this.valeur;
                break;
            case BonusType.INVINCIBILITE:
                joueur.is_invincible = true;
                setTimeout(() => {
                    joueur.is_invincible = false;
                }, this.valeur);
                break;
            case BonusType.TIR_DOUBLE:
                joueur.nbTir += 1;
                break;
        }
    }
}

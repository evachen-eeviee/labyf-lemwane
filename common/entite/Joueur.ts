import Entite from "./Entite.ts";

export default class Joueur extends Entite {
    public static joueurs: Joueur[] = [];
    
    id: string;
    score : number;
    is_invincible : boolean;
    ennemisTues : number = 0;
    tempsSurvecu : number = 0;
    nbTir : number = 1;

    constructor(id: string, nom:string, image_path : string = "", vie_max : number, vitesse : number){
        super(nom, image_path, vie_max, vitesse);
        this.id = id;
        this.score = 0;
        this.is_invincible = false;

        Joueur.joueurs.push(this);
    }
}

export default class Entite {
    nom : string;
    image_path : string;
    vie : number;
    vie_max : number;
    vitesse : number;
    coef_vitesse : number;
    pos_x : number;
    pos_y : number;

    constructor(nom : string, image_path : string, vie_max : number, vitesse : number) {
        this.nom = nom;
        this.image_path = image_path;
        this.vie_max = vie_max;
        this.vie = vie_max;
        this.vitesse = vitesse;
        this.coef_vitesse = 1.0;
        this.pos_x = 0;
        this.pos_y = 0;
    }

    addVie(montant : number){
        if(montant + this.vie <= this.vie_max){
            this.vie += montant;
        } else {
            this.vie = this.vie_max;
        }
    }

    isDead(){
        return this.vie <= 0;
    }

    setVieMax(montant : number){
        this.vie_max = montant;
        if(this.vie > this.vie_max) {
            this.vie = this.vie_max;
        }
    }
}
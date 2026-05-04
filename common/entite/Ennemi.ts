import Entite from './Entite.ts';
import { ENNEMI_CONFIGS, EnnemiType } from './EnnemiType.ts';


export default class Ennemi extends Entite {
	id: string; 
	score_on_kill: number;
	poids: number;
	pos_x: number;
	pos_y: number;
	type: EnnemiType; // Stocke le type de l'ennemi

	constructor(type: EnnemiType, x: number, y: number) {
		// On récupère les stats correspondant au type passé en paramètre
		const config = ENNEMI_CONFIGS[type];
		super('', config.image_path, config.vie_max, config.vitesse);
		this.id = crypto.randomUUID(); //creer un id unique pour chaque ennemie
		this.type = type;
		this.score_on_kill = config.score_on_kill;
		this.poids = config.poids;
		this.pos_x = x;
		this.pos_y = y
	}

	setY(y: number) {
		this.pos_y = y;
	}

	deplacer() {
		this.pos_x -= this.vitesse;
	}

	estSorti(): boolean {
		return this.pos_x < -100;
	}
}

// Responsable de faire apparaître et disparaître les ennemis.

import Bonus from '../../common/entite/Bonus.ts';
import Ennemi from '../../common/entite/Ennemi.ts';
import { EnnemiType } from '../../common/entite/EnnemiType.ts';
import { BonusType, BONUS_CONFIGS } from '../../common/entite/BonusType.ts';

export default class SpawnManager {
	private ennemis: Ennemi[] = [];
	private ennemis_possible: Ennemi[];
	private ENNEMI_COUNT: number = 10;
	private spawn_interval: number = 350;
	private last_spawn_time: number = 0;
	private last_new_spawn: number = 0;
	private new_spawn_interval: number = 30000;
	private ennemis_spawned: number = 0;
	private canvasWidth: number;
	private canvasHeight: number;
	private bonus: Bonus[] = [];
	private last_bonus_spawn: number = 0;
	private bonus_spawn_interval: number = 30000;
	autoRespawn: boolean = true;

	constructor(canvasWidth: number, canvasHeight: number) {
		this.canvasWidth = canvasWidth;
		this.canvasHeight = canvasHeight;
		this.ennemis_possible = [
			new Ennemi(EnnemiType.METEOR, 0, 0),
			new Ennemi(EnnemiType.COMETE, 0, 0),
			new Ennemi(EnnemiType.ALIEN, 0, 0),
		];
	}

	public setAutoRespawn(value: boolean) {
		this.autoRespawn = value;
	}

	private getRandomEnnemi(): Ennemi {
		let sommePoids = this.ennemis_possible.reduce((s, e) => s + e.poids, 0);
		const tirage = Math.random() * sommePoids;
		let cumul = 0;
		for (const e of this.ennemis_possible) {
			cumul += e.poids;
			if (tirage <= cumul) return e;
		}
		return this.ennemis_possible[
			Math.floor(Math.random() * this.ennemis_possible.length)
		];
	}

	private respawn() {
		const template = this.getRandomEnnemi();
		const nouvel = new Ennemi(
			template.type,
			this.canvasWidth,
			Math.random() * this.canvasHeight
		);
		this.ennemis.push(nouvel);
	}


	public spawnEnnemi(
		type: EnnemiType
	) {
		const ennemi = new Ennemi(
			type,
			this.canvasWidth + 32, // spawn hors écran à droite
			Math.random() * (this.canvasHeight - 64) + 32,
		);
		this.ennemis.push(ennemi);
	}

	private spawnBonus() {
		// Calcul du poids total pour un tirage aléatoire pondéré
		const configs = Object.entries(BONUS_CONFIGS);
		const sommePoids = configs.reduce((s, [_, cfg]) => s + cfg.poids, 0);
		let tirage = Math.random() * sommePoids;

		let typeChoisi: BonusType = BonusType.VIE;
		for (const [type, cfg] of configs) {
			tirage -= cfg.poids;
			if (tirage <= 0) {
				typeChoisi = type as BonusType;
				break;
			}
		}
		const config = BONUS_CONFIGS[typeChoisi];
		const nouveauBonus = new Bonus(
			config.nom,
			'',
			1,
			config.vitesse,
			typeChoisi,
			config.valeur
		);
		nouveauBonus.pos_x = this.canvasWidth;
		nouveauBonus.pos_y = Math.random() * (this.canvasHeight - 64);

		this.bonus.push(nouveauBonus);
	}

	tick(timestamp: number) {
		// Spawn
		if (this.autoRespawn) {
			if (this.ennemis_spawned < this.ENNEMI_COUNT) {
				if (timestamp - this.last_spawn_time >= this.spawn_interval) {
					this.respawn();
					this.ennemis_spawned = this.ennemis.length;
					this.last_spawn_time = timestamp;
				}
			}
			if (this.ENNEMI_COUNT < 30) {
				if (timestamp - this.last_new_spawn >= this.new_spawn_interval) {
					this.ENNEMI_COUNT += 1;
					this.last_new_spawn = timestamp;
				}
			}
		}

		// Déplacement + destruction des sortis
		let nbDetruit = 0;
		this.ennemis = this.ennemis.filter(e => {
			e.deplacer();
			if (e.estSorti()) {
				nbDetruit++;
				return false;
			}
			return true;
		});
		if (this.autoRespawn) {
			for (let i = 0; i < nbDetruit; i++) this.respawn();
			this.ennemis_spawned = this.ennemis.length;
		}

		// Spawn bonus
		if (timestamp - this.last_bonus_spawn >= this.bonus_spawn_interval) {
			let unechancesurdeux = Math.random() < 0.5;
			if (unechancesurdeux) {
				this.spawnBonus();
				this.last_bonus_spawn = timestamp;
			}
		}

		// Déplacement + destruction des sortis
		this.bonus = this.bonus.filter(b => {
			b.pos_x -= b.vitesse;
			return b.pos_x > -64;
		});
	}

	getEnnemis(): Ennemi[] {
		return this.ennemis;
	}

	setEnnemis(ennemis: Ennemi[]) {
		this.ennemis = ennemis;
		this.ennemis_spawned = ennemis.length;
	}

	getBonus(): Bonus[] {
		return this.bonus;
	}

	setBonus(bonus: Bonus[]) {
		this.bonus = bonus;
	}
}

// Détecte les collisions entre tirs/ennemis et joueurs/ennemis.

import Joueur from '../../common/entite/Joueur.ts';
import Ennemi from '../../common/entite/Ennemi.ts';
import type { TirState } from '../../common/GameState.ts';
import type Bonus from '../../common/entite/Bonus.ts';
import Impact from '../../common/entite/Impact.ts';

export default class GestionnaireCollisions {
	static toucheEnnemi(
		tirs: TirState[],
		ennemis: Ennemi[],
		joueurs: Map<string, Joueur>,
		boss?: Ennemi
	): { ennemis: Ennemi[]; tirs: TirState[]; impact: Impact[]; boss?: Ennemi } {
		// un tir touche un ennemi
		const tirsRestants: TirState[] = [...tirs]; // copie du tab tirs pour pas utiliser l'original
		const impacts: Impact[] = [];

		ennemis = ennemis.filter(ennemi => {
			const tirIndex = tirsRestants.findIndex(
				tir =>
					tir.pos_x < ennemi.pos_x + 50 &&
					tir.pos_x + 10 > ennemi.pos_x &&
					tir.pos_y < ennemi.pos_y + 50 &&
					tir.pos_y + 10 > ennemi.pos_y
			);
			if (tirIndex !== -1) {
				ennemi.addVie(-1);
				const tir = tirsRestants[tirIndex];
				tirsRestants.splice(tirIndex, 1);

				impacts.push(new Impact(tir.pos_x, tir.pos_y));

				if (ennemi.vie == 0) {
					const joueur = joueurs.get(tir.owner_id);
					if (joueur) {
						joueur.score += ennemi.score_on_kill;
						joueur.ennemisTues = (joueur.ennemisTues || 0) + 1;
					}
					return false;
				}
			}
			return true;
		});

		if (boss) {
			const bossLocal = boss;

			// console.log('Boss pos:', bossLocal.pos_x, bossLocal.pos_y + ' vie boss:', boss.vie);
			// console.log('Tirs actifs:',	tirsRestants.map(t => `(${t.pos_x}, ${t.pos_y})`));

			const tirIndex = tirsRestants.findIndex(
				tir =>
					tir.pos_x < bossLocal.pos_x + 600 && // tir n'est pas à droite du boss
					tir.pos_x + 10 > bossLocal.pos_x && // tir n'est pas à gauche du boss
					tir.pos_y < bossLocal.pos_y + 600 && // tir n'est pas en dessous du boss
					tir.pos_y + 10 > bossLocal.pos_y // tir n'est pas au dessus du boss
			);
			console.log();
			if (tirIndex !== -1) {
				boss.addVie(-1);
				const tir = tirsRestants[tirIndex];
				tirsRestants.splice(tirIndex, 1);
				impacts.push(new Impact(tir.pos_x, tir.pos_y));
				if (boss.vie === 0) {
					const joueur = joueurs.get(tir.owner_id);
					if (joueur) {
						joueur.score += boss.score_on_kill;
						joueur.ennemisTues = (joueur.ennemisTues || 0) + 1;
					}
					boss = undefined; // boss mort
				}
			}
		}
		return { ennemis, tirs: tirsRestants, impact: impacts, boss };
	}

	static toucheJoueur(
		joueurs: Map<string, Joueur>,
		ennemis: Ennemi[],
		boss?: Ennemi
	): { joueurs: Map<string, Joueur>; ennemis: Ennemi[] } {
		//un ennemi touche un joueur
		const JOUEUR_SIZE = 128;
		const ENNEMI_SIZE = 50;

		joueurs.forEach(joueur => {
			if (joueur.is_invincible) return;
			if (joueur.vie <= 0) return;
			ennemis = ennemis.filter(ennemi => {
				const collision =
					joueur.pos_x - JOUEUR_SIZE / 2 < ennemi.pos_x + ENNEMI_SIZE &&
					joueur.pos_x + JOUEUR_SIZE / 2 > ennemi.pos_x &&
					joueur.pos_y - JOUEUR_SIZE / 2 < ennemi.pos_y + ENNEMI_SIZE &&
					joueur.pos_y + JOUEUR_SIZE / 2 > ennemi.pos_y;
				if (collision) {
					joueur.addVie(-1);
					return false;
				}
				return true;
			});
			// Collision avec le boss
			if (boss && boss.vie > 0) {
				const BOSS_SIZE = 600;
				const collision =
					joueur.pos_x - JOUEUR_SIZE / 2 < boss.pos_x + BOSS_SIZE &&
					joueur.pos_x + JOUEUR_SIZE / 2 > boss.pos_x &&
					joueur.pos_y - JOUEUR_SIZE / 2 < boss.pos_y + BOSS_SIZE &&
					joueur.pos_y + JOUEUR_SIZE / 2 > boss.pos_y;
				if (collision) {
					joueur.addVie(-1);
				}
			}
		});

		return { joueurs, ennemis };
	}

	static ramasserBonus(joueurs: Map<string, Joueur>, bonus: Bonus[]): Bonus[] {
		const BONUS_SIZE = 80;

		joueurs.forEach(joueur => {
			bonus = bonus.filter(b => {
				const collision =
					joueur.pos_x - BONUS_SIZE / 2 < b.pos_x + BONUS_SIZE &&
					joueur.pos_x + BONUS_SIZE / 2 > b.pos_x &&
					joueur.pos_y - BONUS_SIZE / 2 < b.pos_y + BONUS_SIZE &&
					joueur.pos_y + BONUS_SIZE / 2 > b.pos_y;
				if (collision) {
					b.appliquerEffet(joueur);
					return false;
				}
				return true;
			});
		});
		return bonus;
	}
}

import Ennemi from '../../common/entite/Ennemi.ts';
import Joueur from '../../common/entite/Joueur.ts';
import type { BossTirState } from '../../common/GameState.ts';

const BOSS_TARGET_X_RATIO = 2 / 3;
const BOSS_TARGET_Y_RATIO = 0.08;
const BOSS_SHOOT_INTERVAL_MS = 1200; // tir toutes les 1.2s
const BOSS_TIR_VITESSE = 6;

export default class BossManager {
	private canvasWidth: number;
	private canvasHeight: number;
	private targetX: number;
	private targetY: number;
	private boss: Ennemi | null = null;
	private bossTirs: BossTirState[] = [];
	private lastShot: number = 0;
	private entreeTerminee: boolean = false;

	constructor(canvasWidth: number, canvasHeight: number) {
		this.canvasWidth = canvasWidth;
		this.canvasHeight = canvasHeight;
		this.targetX = canvasWidth * BOSS_TARGET_X_RATIO;
		this.targetY = canvasHeight * BOSS_TARGET_Y_RATIO;
	}

	public setBoss(boss: Ennemi) {
		this.boss = boss;
		this.bossTirs = [];
		this.lastShot = 0;
		this.entreeTerminee = false;
	}

	public reset() {
		this.boss = null;
		this.bossTirs = [];
		this.entreeTerminee = false;
	}

	public tick(now: number, joueurs: Map<string, Joueur>) {
		if (!this.boss) return;

		//  Entrée : le boss avance jusqu'à targetX
		if (!this.entreeTerminee) {
            let arriveeX = false;
            let arriveeY = false;

            if (this.boss.pos_x > this.targetX) {
                this.boss.pos_x -= this.boss.vitesse;
                if (this.boss.pos_x <= this.targetX) {
                    this.boss.pos_x = this.targetX;
                    arriveeX = true;
                }
            } else {
                arriveeX = true;
            }

            if (this.boss.pos_y > this.targetY) {
                this.boss.pos_y -= this.boss.vitesse; // même vitesse, ou une vitesse dédiée
                if (this.boss.pos_y <= this.targetY) {
                    this.boss.pos_y = this.targetY;
                    arriveeY = true;
                }
            } else {
                arriveeY = true;
            }

            if (arriveeX && arriveeY) {
                this.entreeTerminee = true;
            }
        }

		// Tirs : uniquement une fois l'entrée terminée
		if (this.entreeTerminee && now - this.lastShot >= BOSS_SHOOT_INTERVAL_MS) {
			this.tirerVersJoueurs(joueurs);
			this.lastShot = now;
		}

		// Déplacement des projectiles existants
		this.bossTirs.forEach(t => {
			t.pos_x += t.vel_x;
			t.pos_y += t.vel_y;
		});

		// Supprimer les tirs hors écran
		this.bossTirs = this.bossTirs.filter(
			t =>
				t.pos_x > -50 &&
				t.pos_x < this.canvasWidth + 50 &&
				t.pos_y > -50 &&
				t.pos_y < this.canvasHeight + 50
		);
	}

	private tirerVersJoueurs(joueurs: Map<string, Joueur>) {
		if (!this.boss) return;
		if (this.boss.vie <= 0) return;

		joueurs.forEach(joueur => {
			if (joueur.vie <= 0) return;

			const dx = joueur.pos_x - this.boss!.pos_x;
			const dy = joueur.pos_y - this.boss!.pos_y;
			const dist = Math.sqrt(dx * dx + dy * dy) || 1;

			this.bossTirs.push({
				id: crypto.randomUUID(),
				pos_x: this.boss!.pos_x,
				pos_y: this.boss!.pos_y,
				vel_x: (dx / dist) * BOSS_TIR_VITESSE,
				vel_y: (dy / dist) * BOSS_TIR_VITESSE,
			});
		});
	}

	public getBoss(): Ennemi | null {
		return this.boss;
	}

	public getBossTirs(): BossTirState[] {
		return this.bossTirs;
	}

	public setBossTirs(tirs: BossTirState[]) {
		this.bossTirs = tirs;
	}

	public isEntreeTerminee(): boolean {
		return this.entreeTerminee;
	}
}

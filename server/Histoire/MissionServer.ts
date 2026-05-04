import type { MissionConfig } from '../../common/histoire/MissionConfig.ts';

import { Server as IOServer } from 'socket.io';
import PartieServeur from '../Partie/PartieServeur.ts';
import BossManager from './BossManager.ts';
import type Ennemi from '../../common/entite/Ennemi.ts';
import Router from '../../client/src/Router/Router.ts';

export const MissionPhase = {
	WAVE: 'WAVE',
	BOSS: 'BOSS',
	VICTORY: 'VICTORY',
};
type MissionPhase = (typeof MissionPhase)[keyof typeof MissionPhase];

export default class MissionServeur extends PartieServeur {
	private config: MissionConfig;
	private phase: MissionPhase = MissionPhase.WAVE;
	private phaseStartTime: number = 0;
	private bossManager: BossManager;

	// Timers de spawn par type d'ennemi
	private lastSpawnByType: Map<string, number> = new Map();

	constructor(
		io: IOServer,
		roomId: string,
		config: MissionConfig,
		canvas_width: number,
		canvas_height: number,
		boss?: Ennemi
	) {
		super(
			io,
			roomId,
			config.nom,
			1,
			'Histoire',
			canvas_width,
			canvas_height,
			boss
		);
		this.config = config;
		this.bossManager = new BossManager(canvas_width, canvas_height);
	}

	override start() {
		this.spawnManager.setAutoRespawn(false);
		this.phase = MissionPhase.WAVE;
		this.phaseStartTime = Date.now();
		this.config.ennemisVague.forEach(e => this.lastSpawnByType.set(e.type, 0));
		super.start();
		console.log(`[Mission ${this.config.id}] Départ — phase WAVE`);
	}

	protected override update() {
		const now = Date.now();

		if (this.phase === MissionPhase.WAVE) {
			this.updateWave(now);
		} else if (this.phase === MissionPhase.BOSS) {
			this.updateBoss(now);
		}

		super.update();
	}

	private updateWave(now: number) {
		this.boss = undefined;
		const elapsed = now - this.phaseStartTime;

		for (const ec of this.config.ennemisVague) {
			const lastSpawn = this.lastSpawnByType.get(ec.type) ?? 0;
			if (now - lastSpawn < ec.spawnRateMs) continue;

			const ennemisActuels = this.spawnManager
				.getEnnemis()
				.filter(e => e.type === ec.type).length;

			if (ennemisActuels < ec.maxSimultanes) {
				this.spawnManager.spawnEnnemi(ec.type);
				this.lastSpawnByType.set(ec.type, now);
			}
		}
		if (elapsed >= this.config.dureeVagueMs) {
			if (this.config.bossType) {
				this.passerEnPhaseBoss(now);
			} else {
				this.remporterVictoire();
			}
		}
	}

	private updateBoss(now: number) {
		this.bossManager.tick(now, this.joueurs);
		this.boss = this.bossManager.getBoss()!;
		// Synchroniser les tirs boss dans le gameState via GestionnaireCollisions
		const { joueurs: joueursMisAJour, bossTirsRestants } =
			this.appliquerCollisionsBossTirs();
		this.joueurs = joueursMisAJour;
		this.bossManager.setBossTirs(bossTirsRestants);

		// Vérifier si le boss est mort
		const boss = this.bossManager.getBoss();
		if (!boss || boss.vie <= 0) {
			// S'assurer que le boss a bien été spawné (évite victoire immédiate)
			if (
				this.bossManager.isEntreeTerminee() ||
				this.phaseStartTime < now - 500
			) {
				this.bossManager.reset();
				this.boss = undefined;
				this.remporterVictoire();
			}
		}
	}

	private appliquerCollisionsBossTirs() {
		const JOUEUR_SIZE = 64;
		const TIR_SIZE = 16;
		let bossTirsRestants = [...this.bossManager.getBossTirs()];

		this.joueurs.forEach(joueur => {
			if (joueur.vie <= 0 || joueur.is_invincible) return;

			bossTirsRestants = bossTirsRestants.filter(tir => {
				const collision =
					tir.pos_x + TIR_SIZE > joueur.pos_x - JOUEUR_SIZE / 2 &&
					tir.pos_x - TIR_SIZE < joueur.pos_x + JOUEUR_SIZE / 2 &&
					tir.pos_y + TIR_SIZE > joueur.pos_y - JOUEUR_SIZE / 2 &&
					tir.pos_y - TIR_SIZE < joueur.pos_y + JOUEUR_SIZE / 2;

				if (collision) {
					joueur.addVie(-1);
					return false; // supprimer le tir
				}
				return true;
			});
		});

		return { joueurs: this.joueurs, bossTirsRestants };
	}

	private passerEnPhaseBoss(now: number) {
		console.log(`[Mission ${this.config.id}] Phase BOSS`);
		this.phase = MissionPhase.BOSS;
		this.phaseStartTime = now;

		// Vider tous les ennemis de vague — plus de nouveaux spawns
		this.spawnManager.setAutoRespawn(false);

		if (this.config.bossType) {
			this.spawnManager.spawnEnnemi(this.config.bossType);

			const bossEnnemi = this.spawnManager
				.getEnnemis()
				.find(e => e.type === this.config.bossType);

			if (bossEnnemi) {
				this.spawnManager.setEnnemis(
					this.spawnManager.getEnnemis().filter(e => e !== bossEnnemi)
				);
				this.bossManager.setBoss(bossEnnemi);
				this.boss = bossEnnemi;
			}
		}
	}

	private remporterVictoire() {
		console.log(`[Mission ${this.config.id}] Victoire !`);
		this.phase = MissionPhase.VICTORY;

		const scoreTotal = Array.from(this.joueurs.values()).reduce(
			(sum, j) => sum + j.score,
			0
		);
		const ennemisTuesTotal = Array.from(this.joueurs.values()).reduce(
			(sum, j) => sum + j.ennemisTues,
			0
		);
		const tempsSurvecu = Math.floor(this.tick / 60);

		this.io.to(this.roomId).emit('gameOver', {
			scoreTotal: scoreTotal,
			tempsTotal: tempsSurvecu,
			ennemisTuesTotal: ennemisTuesTotal,
		});
		this.stop();
	}

	public getPhase(): MissionPhase {
		return this.phase;
	}

	public getMissionId(): number {
		return this.config.id;
	}

	public override getBossState() {
		return {
			boss: this.bossManager.getBoss(),
			bossTirs: this.bossManager.getBossTirs(),
		};
	}
}

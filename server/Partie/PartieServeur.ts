// server/PartieServeur.ts
// Source de vérité du jeu. Tourne une boucle à ~60ms côté serveur.
// Reçoit les inputs des clients, calcule l'état, l'émet à tous.

import Joueur from '../../common/entite/Joueur.ts';
import type {
	BossTirState,
	GameState,
	TirState,
} from '../../common/GameState.ts';
import SpawnManager from './SpawnManager.ts';
import GestionnaireCollisions from './GestionnaireCollisions.ts';
import { Server as IOServer } from 'socket.io';
import type { BonusType } from '../../common/entite/BonusType.ts';
import type Impact from '../../common/entite/Impact.ts';
import type Ennemi from '../../common/entite/Ennemi.ts';

const CANVAS_WIDTH = 1280;
const CANVAS_HEIGHT = 720;
const TICK_MS = 1000 / 60;
const SHOT_INTERVAL_MS = 500;

export default class PartieServeur {
	protected roomId: string;
	protected nom: string;
	protected maxJoueur: number;
	protected joueurs: Map<string, Joueur> = new Map();
	protected inputs: Map<string, Record<string, boolean>> = new Map();
	private inputssouris: Map<string, { x: number; y: number; down: boolean }> =
		new Map();
	protected tirs: TirState[] = [];
	spawnManager: SpawnManager;
	protected io: IOServer;
	protected tick: number = 0;
	protected lastShots: Map<string, number> = new Map();
	protected running: boolean = false;
	intervalId: NodeJS.Timeout | null = null;
	protected impacts: Impact[] = [];
	private tickMort: Map<string, number> = new Map();
	protected canvas_width: number = CANVAS_WIDTH;
	protected canvas_height: number = CANVAS_HEIGHT;
	protected difficulte: string;
	protected acceleration: number = 0.15;
	protected vitesse_max: number = 7;
	protected vitesse_min: number = 1;
	boss?: Ennemi;

	constructor(
		io: IOServer,
		roomId: string,
		nom: string,
		maxJoueur: number,
		difficulte: string,
		canvas_width: number,
		canvas_height: number,
		boss?: Ennemi
	) {
		this.io = io;
		this.roomId = roomId;
		this.nom = nom;
		this.maxJoueur = maxJoueur;
		this.difficulte = difficulte;
		if (canvas_width) this.canvas_width = canvas_width;
		if (canvas_height) this.canvas_height = canvas_height;
		this.boss = boss;

		this.spawnManager = new SpawnManager(this.canvas_width, this.canvas_height);
	}

	addJoueur(socketId: string, joueur: Joueur) {
		if (this.difficulte === 'difficile') {
			joueur.setVieMax(2);
		} else if (this.difficulte === 'moyen') {
			joueur.setVieMax(3);
		} else {
			joueur.setVieMax(5);
		}
		joueur.vie = joueur.vie_max;
		joueur.score = 0;
		joueur.ennemisTues = 0;
		joueur.pos_x = 200;
		joueur.pos_y = this.canvas_height / 2;
		this.joueurs.set(socketId, joueur);
		this.inputs.set(socketId, {});
		this.lastShots.set(socketId, 0);
		this.tickMort.delete(socketId);
		console.log(`Joueur ajouté : ${joueur.nom} (${socketId})`);
	}

	removeJoueur(socketId: string) {
		this.joueurs.delete(socketId);
		this.inputs.delete(socketId);
		this.inputssouris.delete(socketId);
		this.lastShots.delete(socketId);
		this.tickMort.delete(socketId);
		console.log(`Joueur retiré : ${socketId}`);

		if (this.joueurs.size === 0) {
			this.stop();
		}
	}

	stop() {
		if (this.intervalId) {
			clearInterval(this.intervalId);
			this.intervalId = null;
			this.running = false;
			console.log('--- Serveur mis en PAUSE (0 joueurs) ---');

			this.tirs = [];
			this.spawnManager.setEnnemis([]); // Vide les ennemis
			this.tick = 0;
		}
	}

	setInput(
		socketId: string,
		touches: Record<string, boolean>,
		souris: { x: number; y: number; down: boolean }
	) {
		this.inputs.set(socketId, touches);
		this.inputssouris.set(socketId, souris);
	}

	start() {
		if (this.running) return;
		this.reset();
		this.running = true;
		this.intervalId = setInterval(() => this.update(), TICK_MS);
		console.log('Boucle de jeu démarrée');
	}

	public getNbJoueurs(): number {
		return this.joueurs.size;
	}

	public getDifficulte(): string {
		if (this.difficulte) return this.difficulte;
		return 'Moyen';
	}

	public getNom() {
		return this.nom;
	}

	public getMaxNbJoueur() {
		return this.maxJoueur;
	}

	public isRunning(): boolean {
		return this.running;
	}

	protected update() {
		const now = Date.now();
		this.tick++;

		// S'il y a une partie en cours et que tous les joueurs sont morts
		const joueursEnVie = Array.from(this.joueurs.values()).filter(
			j => j.vie > 0
		);
		if (this.joueurs.size > 0 && joueursEnVie.length === 0 && this.running) {
			console.log(
				'Joueurs : ' +
					this.joueurs.size +
					' , En Vie : ' +
					joueursEnVie.length +
					' , Running : ' +
					this.running
			);
			this.terminerPartie();
			return;
		}

		// Déplacer les joueurs selon leurs inputs
		this.joueurs.forEach((joueur, socketId) => {
			if (joueur.vie <= 0) return;
			const mouse = this.inputssouris.get(socketId) || {
				x: 0,
				y: 0,
				down: false,
			};
			const touches = this.inputs.get(socketId) ?? {};

			let mouvement = false;
			if (touches['z'] || touches['arrowup']) {
				joueur.pos_y -= joueur.vitesse;
				mouvement = true;
			}
			if (touches['s'] || touches['arrowdown']) {
				joueur.pos_y += joueur.vitesse;
				mouvement = true;
			}
			if (touches['q'] || touches['arrowleft']) {
				joueur.pos_x -= joueur.vitesse;
				mouvement = true;
			}
			if (touches['d'] || touches['arrowright']) {
				joueur.pos_x += joueur.vitesse;
				mouvement = true;
			}

			if (mouse.down) {
				const dx = mouse.x - joueur.pos_x;
				const dy = mouse.y - joueur.pos_y;
				const distance = Math.sqrt(dx * dx + dy * dy);

				if (distance > 5) {
					const angle = Math.atan2(dy, dx);
					joueur.pos_x += Math.cos(angle) * joueur.vitesse;
					joueur.pos_y += Math.sin(angle) * joueur.vitesse;
					mouvement = true;
				}
			}

			if (mouvement) {
				joueur.vitesse = Math.min(
					joueur.vitesse + this.acceleration,
					this.vitesse_max
				);
			} else {
				joueur.vitesse *= 0.9;
				if (joueur.vitesse < this.vitesse_min) {
					joueur.vitesse = this.vitesse_min;
				}
			}

			joueur.pos_x = Math.max(
				32,
				Math.min(this.canvas_width - 32, joueur.pos_x)
			);
			joueur.pos_y = Math.max(
				32,
				Math.min(this.canvas_height - 32, joueur.pos_y)
			);

			// Tir automatique
			const lastShot = this.lastShots.get(socketId) ?? 0;
			if (now - lastShot >= SHOT_INTERVAL_MS) {
				const ecartTir = 20;
				const offsettir = -((joueur.nbTir - 1) * ecartTir) / 2;
				for (let i = 0; i < joueur.nbTir; i++)
					this.tirs.push({
						id: crypto.randomUUID(),
						pos_x: joueur.pos_x,
						pos_y: joueur.pos_y + offsettir + i * ecartTir,
						owner_id: socketId,
					});
				this.lastShots.set(socketId, now);
			}
		});

		// Déplacer les tirs
		this.tirs.forEach(t => {
			t.pos_x += 10;
		});
		this.tirs = this.tirs.filter(t => t.pos_x < this.canvas_width);

		// Spawn / déplacement des ennemis
		this.spawnManager.tick(now);

		// Collisions
		let ennemisApresHit: Ennemi[];
		let tirsApresHit: TirState[];
		let impacts: Impact[];
	
		if (this.boss) {
			const result = GestionnaireCollisions.toucheEnnemi(
				this.tirs,
				this.spawnManager.getEnnemis(),
				this.joueurs,
				this.boss
			);
			ennemisApresHit = result.ennemis;
			tirsApresHit = result.tirs;
			impacts = result.impact;
			this.boss = result.boss;
		} else {
			const result = GestionnaireCollisions.toucheEnnemi(
				this.tirs,
				this.spawnManager.getEnnemis(),
				this.joueurs
			);
			ennemisApresHit = result.ennemis;
			tirsApresHit = result.tirs;
			impacts = result.impact;
		}

		this.tirs = tirsApresHit;
		this.spawnManager.setEnnemis(ennemisApresHit);
		// Mise à jour des impacts existants et ajout des nouveaux
		this.impacts.forEach(impact => impact.nextStep());
		this.impacts = this.impacts.filter(impact => !impact.isFinished());
		this.impacts.push(...impacts);

		const { joueurs: joueursMisAJour, ennemis: ennemisApresContact } =
			GestionnaireCollisions.toucheJoueur(
				this.joueurs,
				this.spawnManager.getEnnemis(),
				this.boss
			);
		// Enregistrer le tick de mort pour les joueurs qui viennent de mourir
		joueursMisAJour.forEach((joueur, socketId) => {
			if (joueur.vie <= 0 && !this.tickMort.has(socketId)) {
				this.tickMort.set(socketId, this.tick);
			}
		});
		this.joueurs = joueursMisAJour;
		this.spawnManager.setEnnemis(ennemisApresContact);

		const bonusRestants = GestionnaireCollisions.ramasserBonus(
			this.joueurs,
			this.spawnManager.getBonus()
		);
		this.spawnManager.setBonus(bonusRestants);

		// Construire et émettre le GameState
		const gameState: GameState = {
			tick: this.tick,
			players: Array.from(this.joueurs.entries()).map(([id, j]) => ({
				id,
				nom: j.nom,
				image_path: j.image_path,
				pos_x: j.pos_x,
				pos_y: j.pos_y,
				vie: j.vie,
				vie_max: j.vie_max,
				vitesse: j.vitesse,
				coef_vitesse: j.coef_vitesse,
				score: j.score,
				ennemisTues: j.ennemisTues,
				is_invincible: j.is_invincible,
			})),
			ennemis: this.spawnManager.getEnnemis().map(e => ({
				id: e.id,
				type: e.type,
				pos_x: e.pos_x,
				pos_y: e.pos_y,
				vie: e.vie,
				vie_max: e.vie_max,
			})),
			bonus: this.spawnManager.getBonus().map(b => ({
				id: crypto.randomUUID(),
				type: b.typeEffet as BonusType,
				pos_x: b.pos_x,
				pos_y: b.pos_y,
			})),
			tirs: this.tirs,
			impact: this.impacts,
			boss: this.boss,
			bossTirs: [],
		};

		const { boss, bossTirs } = this.getBossState();
		gameState.boss = boss;
		gameState.bossTirs = bossTirs;

		if (this.tick % 60 === 0) {
			// Log toutes les 60 frames pour ne pas flood
			console.log(
				'Pseudo : ' +
					(gameState.players.length > 0 ? gameState.players[0].nom : '')
			);
			console.log('Nombre de joueurs en vie:', gameState.players.length);
			//console.log('Vitesse joueur 1:', this.joueurs.get(gameState.players[0].id)?.vitesse ?? "N/A");
			// console.log('Nombre de bonus actifs :',this.spawnManager.getBonus().length);
		}
		this.io.to(this.roomId).emit('gameState', gameState);
	}

	private terminerPartie() {
		console.log(`Fin de partie pour la room ${this.roomId}`);

		const scoreTotal = Array.from(this.joueurs.values()).reduce(
			(sum, j) => sum + j.score,
			0
		);
		const ennemisTuesTotal = Array.from(this.joueurs.values()).reduce(
			(sum, j) => sum + j.ennemisTues,
			0
		);
		const tempsSurvecu = Math.floor(this.tick / 60); // secondes

		// Temps survécu individuel : tick de mort ou tick final si encore en vie
		this.joueurs.forEach((joueur, socketId) => {
			const mort = this.tickMort.get(socketId) ?? this.tick;
			joueur.tempsSurvecu = Math.floor(mort / 60);
		});

		this.io.to(this.roomId).emit('gameOver', {
			scoreTotal: scoreTotal,
			tempsTotal: tempsSurvecu,
			ennemisTuesTotal: ennemisTuesTotal,
		});

		this.stop();
	}

	public reset() {
		console.log(`--- RESET de la partie ${this.roomId} ---`);
		if (this.intervalId) {
			clearInterval(this.intervalId);
			this.intervalId = null;
		}
		this.running = false;
		this.tirs = [];
		this.impacts = [];
		this.spawnManager.setEnnemis([]);
		this.spawnManager.setBonus([]);
		this.tick = 0;
		this.tickMort.clear();
		this.joueurs.forEach((joueur, socketId) => {
			joueur.vie = joueur.vie_max;
			joueur.score = 0;
			joueur.pos_x = 200;

			joueur.pos_y = this.canvas_height / 2;
			this.lastShots.set(socketId, 0);
			this.inputs.set(socketId, {});
		});
	}

	protected getBossState(): {
		boss: Ennemi | null;
		bossTirs: BossTirState[];
	} {
		return { boss: null, bossTirs: [] };
	}
}

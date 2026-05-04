import type GameView from './view/GameView';
import type { BossTirState, GameState } from '../../common/GameState';
import type MissionView from './view/histoire/MissionView';

export default class Partie {
	view: GameView | MissionView;
	gameState: GameState | null = null;
	isRunning: boolean = false;
	private requestID: number | null = null;

	constructor(view: GameView | MissionView) {
		this.view = view;
	}

	applyState(state: GameState) {
		this.gameState = state;
		// this.gameState.players.length > 0 ? console.log(this.gameState.players[0].nom) : console.log('Aucun joueur');
	}

	resetState() {
		this.gameState = null;
		this.isRunning = false;
	}

	play() {
		console.log('Démarrage de la boucle play()');
		if (this.isRunning) {
			return;
		}

		localStorage.removeItem('tempsSurvecu');
		localStorage.removeItem('scoreFinal');

		this.gameState = null;
		this.isRunning = true;

		const loop = () => {
			if (!this.isRunning) {
				console.log('Boucle stoppée : isRunning est false');
				return;
			}

			if (!this.gameState) {
				this.view.ctx.clearRect(
					0,
					0,
					this.view.canvas.width,
					this.view.canvas.height
				);
				this.requestID = requestAnimationFrame(loop);
				return;
			}

			this.view.ctx.clearRect(
				0,
				0,
				this.view.canvas.width,
				this.view.canvas.height
			);

			if (this.gameState) {
				const localPlayer = this.gameState.players.find(
					p => p.id === this.view.mySocketId
				);

				// Dessiner les ennemis
				for (const ennemi of this.gameState.ennemis) {
					const img = this.view.getEnnemiImage(ennemi.type);
					if (img?.complete) {
						this.view.dessinerEnnemi(img, ennemi.pos_x, ennemi.pos_y);
					}
				}

				// Dessiner les tirs
				for (const tir of this.gameState.tirs) {
					this.view.ctx.fillStyle = 'RED';
					this.view.ctx.fillRect(tir.pos_x, tir.pos_y, 10, 10);
					this.view.ctx.fillStyle = 'WHITE';
				}

				// Dessiner tous les joueurs
				for (const player of this.gameState.players) {
					if (player.vie > 0) this.view.dessinerJoueur(player);
				}

				// Dessiner les bonus
				for (const bonus of this.gameState.bonus) {
					this.view.dessinerBonus(bonus);
				}

				//dessiner les explosions
				for (const impact of this.gameState.impact) {
					this.view.dessinerExplosion(impact);
				}

				//dessiner le boss
				const boss = this.gameState.boss;
				if (boss && boss.vie > 0) {
					this.view.dessinerBoss(boss);
				}

				//dessiner lest tirs du boss
				const tirsBoss: BossTirState[] | undefined = this.gameState.bossTirs;
				if (tirsBoss) {
					for (const tir of tirsBoss) {
						this.view.ctx.fillStyle = 'GREEN';
						this.view.ctx.fillRect(tir.pos_x, tir.pos_y, 10, 10);
						this.view.ctx.fillStyle = 'WHITE';
					}
				}

				if (localPlayer) {
					this.view.updateUI(localPlayer);
				}
			}

			this.requestID = requestAnimationFrame(loop);
		};
		this.requestID = requestAnimationFrame(loop);
	}

	stop() {
		this.isRunning = false;
		if (this.requestID) cancelAnimationFrame(this.requestID);
		this.gameState = null;
		this.view.ctx.clearRect(
			0,
			0,
			this.view.canvas.width,
			this.view.canvas.height
		);
	}
}

/* 
const canvasResizeObserver = new ResizeObserver(() => resampleCanvas());
canvas.ResizeObserver.observe(canvas);

function resampleCanvas() {
  if(canvas.clientWidth === 0 || canvas.clientHeight === 0) return;
  canvas.width = canvas.clientWidth;
  canvas.height = canvas.clientHeight;
}
*/

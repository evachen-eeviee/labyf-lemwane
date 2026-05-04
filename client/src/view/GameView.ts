import { ENNEMI_CONFIGS, EnnemiImageMap } from '../../../common/entite/EnnemiType';
import { BonusImageMap } from '../../../common/entite/BonusType.ts';
import type {
	BonusState,
	GameState,
	PlayerState,
} from '../../../common/GameState';
import type { Space } from '../backgroundCanva/space/Space';
import Partie from '../Partie';
import View from './View';
import { Socket } from 'socket.io-client';
import type Impact from '../../../common/entite/Impact.ts';
import { imgExploTab } from '../../constant/index.ts';
import Router from '../Router/Router.ts';
import type Ennemi from '../../../common/entite/Ennemi.ts';

export default class GameView extends View {
	partie: Partie;
	canvas: HTMLCanvasElement;
	space: Space;
	ctx: CanvasRenderingContext2D;
	socket: Socket;
	mySocketId: string = '';
	touches: Record<string, boolean> = {};
	currentRoomId: string = '';
	canvasResizeObserver: ResizeObserver;
	mouse = { x: 0, y: 0, down: false };

	// Cache des images ennemis & joueurs
	private ennemiImages: Map<string, HTMLImageElement> = new Map();
	private bonusImages: Map<string, HTMLImageElement> = new Map();
	private ExplosionImg: Map<number, HTMLImageElement> = new Map();
	private joueurImages: Map<string, HTMLImageElement> = new Map();

	constructor(element: HTMLElement, space: Space, socket: Socket) {
		super(element);
		this.socket = socket;
		this.partie = new Partie(this);
		this.space = space;

		this.canvas = this.element.querySelector('#game') as HTMLCanvasElement;
		this.ctx = this.canvas.getContext('2d')!;
		this.canvas.width = window.innerWidth;
		this.canvas.height = window.innerHeight;
		this.canvasResizeObserver = new ResizeObserver(() => this.resampleCanvas());
		this.canvasResizeObserver.observe(this.canvas);
		//Précharger bonus
		for (const [type, path] of Object.entries(BonusImageMap)) {
			const img = new Image();
			img.src = path;
			this.bonusImages.set(type, img);
		}
		// Précharger les images des ennemis
		for (const [type, path] of Object.entries(EnnemiImageMap)) {
			const img = new Image();
			img.src = path;
			this.ennemiImages.set(type, img);
		}

		// precharger les images d'explosion
		let i = 0;
		for (const path of imgExploTab) {
			i++;
			const img = new Image();
			img.src = path;
			this.ExplosionImg.set(i, img);
		}

		//Précharger les images des joueurs
		for (let i = 1; i <= 5; i++) {
			const img = new Image();
			const imgPath = `/assets/perso${i}.png`;
			img.src = imgPath;
			this.joueurImages.set(`perso${i}`, img);
		}

		// Écoute du gameState serveur
		this.socket.on('gameState', (state: GameState) => {
			this.partie.applyState(state);
			const moi = state.players.find(p => p.id === this.mySocketId);
			if (moi) {
				localStorage.setItem('ScoreLocal', moi.score.toString());
				localStorage.setItem('ennemisTues', moi.ennemisTues.toString());
			}
		});

		this.socket.on('connect', () => {
			// a la connection a la socket en enregistre l'id uniq pour retrouver le joueur local (pour le multi)
			this.mySocketId = this.socket.id || '';
		});

		this.socket.on(
			'gameOver',
			(data: {
				scoreTotal: number;
				tempsTotal: number;
				ennemisTuesTotal: number;
			}) => {
				// console.log('Reçu GameOver du serveur', data);

				// On stocke les stats globales envoyées par le serveur
				localStorage.setItem('scoreTotal', data.scoreTotal.toString());
				localStorage.setItem('tempsTotal', data.tempsTotal.toString());
				localStorage.setItem(
					'ennemisTuesTotal',
					data.ennemisTuesTotal.toString()
				);

				this.partie.stop();
				Router.navigate('/rejouer');
			}
		);

		window.addEventListener('keydown', e => {
			this.touches[e.key.toLowerCase()] = true;
		});
		window.addEventListener('keyup', e => {
			this.touches[e.key.toLowerCase()] = false;
		});

		window.addEventListener('mousemove', e => {
			const rect = this.canvas.getBoundingClientRect();
			this.mouse.x = e.clientX - rect.left;
			this.mouse.y = e.clientY - rect.top;
		});
		window.addEventListener('mousedown', e => {
			this.mouse.down = true;
		});
		window.addEventListener('mouseup', e => {
			this.mouse.down = false;
		});

		// Envoi des inputs au serveur à ~60fps
		setInterval(() => {
			if (this.mySocketId && this.currentRoomId) {
				this.socket.emit('input', {
					roomId: this.currentRoomId,
					touches: this.touches,
					mouse: this.mouse
				});
			}
		}, 1000 / 60);

		const btnLancerPartie = this.element.querySelector(
			'#contentViewGame .lanceGame'
		) as HTMLButtonElement;

		btnLancerPartie.addEventListener('click', e => {
			e.preventDefault();
			btnLancerPartie.style.display = 'none';
			this.decompte();
		});

		this.socket.on('gameAlreadyRunning', () => {
			btnLancerPartie.style.display = 'none';
			this.partie.play();
		});
	}

	setRoomId(id: string) {
		this.currentRoomId = id;
	}

	decompte() {
		const sequence = [
			'/assets/3-removebg-preview.png',
			'/assets/2-removebg-preview.png',
			'/assets/1-removebg-preview.png',
			'/assets/go-removebg-preview.png',
		];
		let index = 0;
		const afficherSuivant = () => {
			if (index >= sequence.length) {
				this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);
				this.lancerJeu();
				return;
			}
			const img = new Image();
			img.src = sequence[index];
			img.onload = () => {
				this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);
				this.ctx.drawImage(
					img,
					this.canvas.width / 2 - img.width / 2,
					this.canvas.height / 2 - img.height / 2
				);
				index++;
				setTimeout(afficherSuivant, 1000);
			};
		};
		afficherSuivant();
	}

	getEnnemiImage(type: string): HTMLImageElement | undefined {
		return this.ennemiImages.get(type);
	}

	dessinerJoueur(player: PlayerState) {
		const img = this.joueurImages.get(player.image_path);
		if (img?.complete) {
			this.ctx.drawImage(img, player.pos_x - 64, player.pos_y - 64, 128, 128);
		}
		this.ctx.fillStyle = 'white';
		this.ctx.textAlign = 'center';
		this.ctx.fillText(player.nom, player.pos_x, player.pos_y - 70);
	}

	dessinerEnnemi(img: HTMLImageElement, x: number, y: number) {
		this.ctx.drawImage(img, x, y, 50, 50);
	}

	updateUI(player: PlayerState) {
		// Barre de vie
		let html = '';
		for (let i = 0; i < player.vie; i++)
			html += `<img src='/assets/heart.png' alt="coeur">`;
		for (let i = player.vie; i < player.vie_max; i++)
			html += `<img src='/assets/heartVide.png' alt="coeur vide">`;
		this.element.querySelector('.life')!.innerHTML = html;

		// Score
		this.element.querySelector('.score')!.innerHTML =
			`<h1 class="futur">${player.score}</h1>`;
	}

	lancerJeu() {
		this.space.setSpeed(4);
		this.socket.emit('resetGame', { roomId: this.currentRoomId });
		setTimeout(() => {
			// console.log('Lancement effectif de la boucle...');
			this.partie.play();
		}, 50);

		const diff = localStorage.getItem('difficulte') || 'facile';

		this.socket.emit('startGame', {
			difficulte: diff,
			roomId: this.currentRoomId,
		});
	}

	dessinerExplosion(impact: Impact) {
		const img = this.ExplosionImg.get(impact.step);
		if (img?.complete) {
			const scaleX = img.width * 0.3;
			const scaleY = img.height * 0.3;
			this.ctx.drawImage(
				img,
				impact.x - scaleX / 2,
				impact.y - scaleY / 2,
				scaleX,
				scaleY
			);
		}
	}

	dessinerBonus(bonus: BonusState) {
		const img = this.bonusImages.get(bonus.type);
		if (img?.complete) {
			this.ctx.drawImage(img, bonus.pos_x - 32, bonus.pos_y - 32, 64, 64);
		}
	}

	dessinerBoss(boss: Ennemi) {
		const img = new Image();
		img.src = ENNEMI_CONFIGS[boss.type].image_path;
		this.ctx.drawImage(img, boss.pos_x, boss.pos_y, 150, 150);
	}

	reset() {
		this.partie.stop();
		this.partie.resetState();
		this.space.setSpeed(0);

		if (this.mySocketId && this.currentRoomId) {
			this.socket.emit('leaveGame', {
				roomId: this.currentRoomId,
			});
		}
		this.currentRoomId = '';
		this.touches = {};
		this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);
		this.element.querySelector('.life')!.innerHTML = '';
		this.element.querySelector('.score')!.innerHTML =
			'<h1 class="futur">0</h1>';

		const btn = this.element.querySelector('.lanceGame') as HTMLElement;
		if (btn) btn.style.display = 'block';
	}

	resampleCanvas() {
		if (this.canvas.clientWidth === 0 || this.canvas.clientHeight === 0) return;
		this.canvas.width = this.canvas.clientWidth;
		this.canvas.height = this.canvas.clientHeight;
	}

	show() {
		super.show();
		this.canvas.width = window.innerWidth;
		this.canvas.height = window.innerHeight;
		console.log('GameView est maintenant affichée, Canvas prêt.');
	}

	hide() {
		super.hide();
		this.partie.stop();
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

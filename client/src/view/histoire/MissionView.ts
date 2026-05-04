import type { Socket } from 'socket.io-client';
import { BonusImageMap } from '../../../../common/entite/BonusType';
import {
	ENNEMI_CONFIGS,
	EnnemiImageMap,
} from '../../../../common/entite/EnnemiType';
import { imgExploTab } from '../../../constant';
import Partie from '../../Partie';
import View from '../View';
import type {
	BonusState,
	GameState,
	PlayerState,
} from '../../../../common/GameState';
import type Impact from '../../../../common/entite/Impact';
import Router from '../../Router/Router';
import Earth from '../../backgroundCanva/earth/Earth';
import type Ennemi from '../../../../common/entite/Ennemi';
import type { Space } from '../../backgroundCanva/space/Space';
import Iworld from '../../backgroundCanva/InsectWorld/Iworld';

export default class MissionView extends View {
	mission: Partie;
	canvas: HTMLCanvasElement;
	ctx: CanvasRenderingContext2D;
	socket: Socket;
	mySocketId: string = '';
	currentRoomId: string = '';
	touches: Record<string, boolean> = {};
	idMission: number = 1;
	bgCanva: HTMLCanvasElement;
	private space: Space;

	private ennemiImages: Map<string, HTMLImageElement> = new Map();
	private bonusImages: Map<string, HTMLImageElement> = new Map();
	private ExplosionImg: Map<number, HTMLImageElement> = new Map();
	private joueurImages: Map<string, HTMLImageElement> = new Map();

	constructor(
		element: HTMLElement,
		socket: Socket,
		bgcanva: HTMLCanvasElement,
		space: Space
	) {
		super(element);
		this.socket = socket;
		this.mission = new Partie(this);
		this.canvas = this.element.querySelector(
			'#gameHistoire'
		) as HTMLCanvasElement;
		this.ctx = this.canvas.getContext('2d')!;
		this.canvas.width = window.innerWidth;
		this.canvas.height = window.innerHeight;
		this.bgCanva = bgcanva;
		this.space = space;

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
		this.socket.on('connect', () => {
			this.mySocketId = this.socket.id || '';
		});

		//Précharger les images des joueurs
		for (let i = 1; i <= 5; i++) {
			const img = new Image();
			const imgPath = `/assets/perso${i}.png`;
			img.src = imgPath;
			this.joueurImages.set(`perso${i}`, img);
		}

		this.socket.on('gameState', (state: GameState) => {
			this.mission.applyState(state);
			const moi = state.players.find(p => p.id === this.mySocketId);
			if (moi) {
				localStorage.setItem('ScoreLocal', moi.score.toString());
				localStorage.setItem('ennemisTues', moi.ennemisTues.toString());

				if (moi.vie <= 0 && !localStorage.getItem('tempsSurvecuLocal')) {
					const tempsVivant = Math.floor(state.tick / 60);
					localStorage.setItem('tempsSurvecuLocal', tempsVivant.toString());
				}
			}
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

				this.mission.stop();
				this.space.resume();
				Router.navigate('/rejouer');
			}
		);

		window.addEventListener('keydown', e => {
			this.touches[e.key.toLowerCase()] = true;
		});
		window.addEventListener('keyup', e => {
			this.touches[e.key.toLowerCase()] = false;
		});

		// Envoi des inputs au serveur à ~60fps
		setInterval(() => {
			if (this.mySocketId && this.currentRoomId) {
				this.socket.emit('input', {
					roomId: this.currentRoomId,
					touches: this.touches,
				});
			}
		}, 1000 / 60);

		type receivedId = {
			id: number;
		};
		this.socket.on('numberMissiongive', (received: receivedId) => {
			this.idMission = received.id;
			this.setBgCanva();
		});
	}

	setBgCanva() {
		if (this.idMission === 1) {
			this.space.stop();
			new Earth(this.bgCanva);
		} else if (this.idMission === 3 || this.idMission === 4) {
			this.space.stop();
			new Iworld(this.bgCanva);
		}
	}

	setRoomId(id: string) {
		this.currentRoomId = id;
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

	dessinerEnnemi(img: HTMLImageElement, x: number, y: number) {
		this.ctx.drawImage(img, x, y, 70, 70);
	}

	dessinerBoss(boss: Ennemi) {
		const img = new Image();
		img.src = ENNEMI_CONFIGS[boss.type].image_path;
		this.ctx.drawImage(img, boss.pos_x, boss.pos_y, 600, 600);
	}

	reset() {
		this.mission.stop();
		this.mission.resetState();

		if (this.mySocketId && this.currentRoomId) {
			this.socket.emit('leaveGame', {
				roomId: this.currentRoomId,
			});
		}

		// this.currentRoomId = '';
		this.touches = {};
		this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);
		this.element.querySelector('.life')!.innerHTML = '';
		this.element.querySelector('.score')!.innerHTML =
			'<h1 class="futur">0</h1>';

		const btn = this.element.querySelector('.lanceGame') as HTMLElement;
		if (btn) btn.style.display = 'block';
	}
}

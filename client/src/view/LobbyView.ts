import { Socket } from 'socket.io-client';
import View from './View';
import Router from '../Router/Router';
import type GameView from './GameView';
import type { Lobby } from '../../../common/Lobby';
import type MissionView from './histoire/MissionView';

export default class LobbyView extends View {
	private socket: Socket;
	private tableBody: HTMLElement;
	private formCreatGame: HTMLElement;
	private gameView: GameView | MissionView;

	constructor(element: HTMLElement, socket: Socket, gameView: GameView | MissionView) {
		super(element);
		this.socket = socket;
		this.gameView = gameView;
		this.tableBody = this.element.querySelector('#lobby-list')!;
		this.formCreatGame = this.element.querySelector('#formCreatGame')!;

		this.element.querySelector('.btnBack')?.addEventListener('click', e => {
			e.preventDefault();
			/*code pour rejoindre une partie*/
			Router.navigate('/choixJeu');
		});

		this.element.querySelector('.btnTop10')?.addEventListener('click', e => {
			e.preventDefault();
			Router.navigate('/classement');
		});

		// Écouter la mise à jour des salons
		this.socket.on('listLobbies', (lobbies: Lobby[]) => {
			this.renderLobbies(lobbies);
		});

		const resetDifficulte = () => {
			(this.formCreatGame.querySelector('.facile') as HTMLElement).style.backgroundColor = 'rgba(50, 140, 50, 0.33)';
			(this.formCreatGame.querySelector('.moyen') as HTMLElement).style.backgroundColor = 'rgba(255, 165, 0, 0.33)';
			(this.formCreatGame.querySelector('.difficile') as HTMLElement).style.backgroundColor = 'rgba(255, 0, 0, 0.33)';
		};

		this.formCreatGame
			.querySelector('.facile')
			?.addEventListener('click', e => {
				e.preventDefault();
				localStorage.setItem('difficulte', 'facile');
				resetDifficulte();
				(e.currentTarget as HTMLElement).style.backgroundColor = 'rgba(50, 140, 50, 0.7)';
			});

		this.formCreatGame.querySelector('.moyen')?.addEventListener('click', e => {
			e.preventDefault();
			localStorage.setItem('difficulte', 'moyen');
			resetDifficulte();
			(e.currentTarget as HTMLElement).style.backgroundColor = 'rgba(255, 165, 0, 0.7)';
		});

		this.formCreatGame
			.querySelector('.difficile')
			?.addEventListener('click', e => {
				e.preventDefault();
				localStorage.setItem('difficulte', 'difficile');
				resetDifficulte();
				(e.currentTarget as HTMLElement).style.backgroundColor = 'rgba(255, 0, 0, 0.7)';
			});

		this.element.querySelector('.btnCreat')?.addEventListener('click', e => this.handleFormSubmit(e));

		this.socket.on('gameCreated', (roomId: string) => {
			console.log("Succès : Lobby créé sur le serveur avec l'ID :", roomId);
					
			// 1. On donne l'ID à la vue de jeu
			this.gameView.setRoomId(roomId);
					
			// 2. On rejoint la room socket.io
			this.socket.emit('joinRoom', roomId);
					
			// 3. On change de vue
			Router.navigate('/game');
		});
	}

	handleFormSubmit(e: Event) {
		// console.log("on click !!")
		e.preventDefault();
		
		const nom = (this.formCreatGame.querySelector('#nom') as HTMLInputElement)?.value.trim();
		const nbJoueur = (this.formCreatGame.querySelector('[name="nbJoueur"]') as HTMLInputElement)?.value.trim();
    	const diff = localStorage.getItem('difficulte');

		if(this.verifAllChamp(nom, nbJoueur, diff)) {
			this.socket.emit('createGame', {
				nom: nom,
				maxJoueur : Number(nbJoueur),
				difficulte: diff,
				canvas_width: window.innerWidth,
				canvas_height: window.innerHeight
			});
		}
		
	}

	verifAllChamp(nom:string | undefined, nbJoueur: string | undefined, diff: string | null): boolean {
		// console.log("on veriff !!")
		if(!nom || !nbJoueur || !diff) {
			alert('Veuillez remplir tous les champs et séléctionez votre difficulté');
			return false;
		}
		if(nom.length > 20) {
			alert('Le nom de server doit contenir au maximum 20 caractères');
			return false;
		}

		const regexNb = new RegExp('^\\d+$');

		if (!regexNb.test(nbJoueur)) {
			alert('Le nombre de joueurs doit contenir uniquement des chiffres');
			return false;
		}

		return true;
	}

	show() {
		super.show();
		localStorage.removeItem('difficulte');
		this.socket.emit('getLobbies'); // Demander la liste à l'affichage
	}

	renderLobbies(lobbies: Lobby[]) {
		this.tableBody.innerHTML = ''; // On vide la liste actuelle

		if (lobbies.length === 0) {
			this.tableBody.innerHTML =
				'<tr><td colspan="4">Aucune partie trouvée... Crée-en une !</td></tr>';
			return;
		}

		lobbies.forEach(lobby => {
			const row = document.createElement('tr');
			row.innerHTML = `
                <td>${lobby.nom}</td>
                <td>${lobby.joueurs}/${lobby.maxJoueurs}</td>
                <td><span class="badge">${lobby.difficulte}</span></td>
                <td><button class="join-btn" data-id="${lobby.id}">Rejoindre</button></td>
            `;

			row.querySelector('.join-btn')?.addEventListener('click', () => {
				if(lobby.joueurs < lobby.maxJoueurs) {
					this.joinLobby(lobby.id);
				}
			});

			this.tableBody.appendChild(row);
		});
	}

	joinLobby(lobbyId: string) {
		this.gameView.setRoomId(lobbyId);
		this.socket.emit('joinRoom', lobbyId);
		Router.navigate('/game');
	}
}

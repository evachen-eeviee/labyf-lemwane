import { Socket } from 'socket.io-client';
import Router from '../Router/Router';
import View from './View';
import Joueur from '../../../common/entite/Joueur';

export default class ClassementView extends View {
	private socket: Socket;
	private tableBody: HTMLElement;

	constructor(element: HTMLElement, socket: Socket) {
		super(element);
		this.socket = socket;
		this.tableBody = this.element.querySelector('#player-list')!;

		this.element.querySelector('.btnBack')?.addEventListener('click', e => {
			e.preventDefault();
			Router.navigate('/lobby');
		});

		this.socket.on('listBestJoueur', (joueurs: Joueur[]) => {
			this.renderJoueur(joueurs);
		});
	}

	renderJoueur(joueurs: Joueur[]) {
		this.tableBody.innerHTML = ''; // On vide la liste actuelle

		if (joueurs.length === 0) {
			this.tableBody.innerHTML =
				'<tr><td colspan="4">Aucun joueur trouvée...</td></tr>';
			return;
		}

		joueurs.forEach(joueur => {
			const row = document.createElement('tr');
			row.innerHTML = `
                <td>${joueur.nom}</td>
                <td><span class="badge">${joueur.score}</span></td>
                <td>${joueur.ennemisTues}</td>
                
            `;

			this.tableBody.appendChild(row);
		});
	}

	show(): void {
		super.show();
		this.socket.emit('getBestJoueur');
	}

	hide(): void {
		super.hide();
	}
}

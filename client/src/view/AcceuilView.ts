import { Socket } from 'socket.io-client';
import Router from '../Router/Router';
import View from './View';

export default class AccueilView extends View {
	constructor(element: HTMLElement, socket: Socket) {
		super(element);

		this.element
			.querySelector('#contentAccueilForm form')
			?.addEventListener('submit', e => this.handleSubmit(e, socket));

		this.element
			.querySelector('.containerBtn button')
			?.addEventListener('click', e => {
				e.preventDefault();
				Router.navigate('/credits');
			});
	}

	show() {
		super.show();
		this.nettoyerDonneesPartie();
	}

	hide() {
		super.hide();
	}

	private nettoyerDonneesPartie() {
		const clesASupprimer = [
			'scoreLocal',
			'scoreFinal',
			'scoreTotal',
			'tempsSurvecuLocal',
			'tempsTotal',
			'ennemisTues',
		];

		clesASupprimer.forEach(cle => localStorage.removeItem(cle));
		// console.log('Données de la partie précédente nettoyées.');
	}

	handleSubmit(e: Event, socket: Socket) {
		e.preventDefault();

		const form = e.target as HTMLFormElement;
		const data = new FormData(form);

		const pseudo = data.get('pseudo')?.toString().trim();
		const perso = data.get('selectPerso')?.toString();

		if (!pseudo || !perso) {
			alert('Veuillez remplir tous les champs');
			return;
		}

		if (pseudo.length < 3 || pseudo.length > 16) {
			alert('Le pseudo doit contenir entre 3 et 16 caractères');
			return;
		}

		const pseudoPattern = new RegExp('^[a-zA-Z0-9_-]+$');
		if (!pseudoPattern.test(pseudo)) {
			alert('Le pseudo ne peut contenir que des lettres, chiffres, - ou _');
			return;
		}

		let image = '';

		switch (perso) {
			case 'perso1': // Tinoratops
				image = 'perso1';
				break;
			case 'perso2': // Evaraptor
				image = 'perso2';
				break;
			case 'perso3': // Hugosaure
				image = 'perso3';
				break;
			case 'perso4': // Neofritschus
				image = 'perso4';
				break;
			case 'perso5': // Lilirex
				image = 'perso5';
				break;
		}

		socket.emit('basePlayer', {
			nom: pseudo,
			img: image,
			vie: 5,
			vitesse: 5,
		});
		Router.navigate('/choixJeu');
	}
}

import Router from '../Router/Router';
import View from './View';

export default class ChoixJeuView extends View {
	constructor(element : HTMLElement) {
		super(element);

		this.element
            .querySelector('.joinGame')?.addEventListener('click', e => {
                e.preventDefault();
                /*code pour rejoindre une partie*/
                Router.navigate('/lobby');
            });

        this.element
            .querySelector('.history')?.addEventListener('click', e => {
                e.preventDefault();
                /*code pour lancer le mode history*/
                Router.navigate('/choixMission');
            });
	}

	show() {
		super.show();
	}

	hide() {
		super.hide();
	}
}

import Router from '../Router/Router';
import View from './View';

export class CreditView extends View {
	constructor(element: HTMLElement) {
		super(element);
	}
	show(): void {
		super.show();
		const scrollText = this.element.querySelector(
			'#creditContent .wrapper .scroll-text'
		) as HTMLElement;

		scrollText.addEventListener('animationend', () => {
			Router.navigate('/');
		});
	}
	hide(): void {
		super.hide();
	}
}

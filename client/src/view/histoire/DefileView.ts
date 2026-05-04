import Router from '../../Router/Router';
import View from '../View';

export default class DefileView extends View {

	constructor(element: HTMLElement) {
		super(element);
	}
	show(): void {
		super.show();
		const scrollText = this.element.querySelector(
			'.scroll-text'
		) as HTMLElement;

		scrollText.addEventListener('animationend', () => {
			Router.navigate('/mission');
		});
	}
	hide(): void {
		super.hide();
	}
}

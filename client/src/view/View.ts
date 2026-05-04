export default class View {
	/**
	 * Balise HTML associée à la vue
	 */
	element;

	constructor(element: HTMLElement) {
		this.element = element;
	}

	show() {
		this.element.classList.add('active');
	}

	hide() {
		this.element.classList.remove('active');
	}
}

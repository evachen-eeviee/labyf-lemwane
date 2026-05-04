import type View from '../view/View';

export default interface Route {
	path: String;
	view: View;
}

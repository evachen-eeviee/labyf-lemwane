import type Route from './Route';

export default class Router {
	static routes : Array<Route> = [];
	static currentRoute: Route;

	static navigate(path: String) {
		const route = this.routes.find(r => r.path === path);

		if (route) {
			if (this.currentRoute) {
				this.currentRoute.view.hide();
			}

			this.currentRoute = route;
			route.view.show();
		}
	}
}

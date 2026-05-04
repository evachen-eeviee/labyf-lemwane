import { test, describe, beforeEach } from 'node:test';
import assert from 'node:assert';
import Router from './Router.ts';
import type View from '../view/View.ts';

interface MockView extends View {
    showCount: number;
    hideCount: number;
}

const createMockView = (): MockView => {
    return {
        element: {} as HTMLElement,
        showCount: 0,
        hideCount: 0,
        show() { this.showCount++; },
        hide() { this.hideCount++; }
    } as MockView;
};

describe('Tests du Router - Navigation', () => {

    let view1: MockView;
    let view2: MockView;

    beforeEach(() => {
        view1 = createMockView();
        view2 = createMockView();
        Router.routes = [];
        // @ts-ignore 
        Router.currentRoute = undefined;
    });

    test('enregistre et navigue vers la route initiale', () => {
        Router.routes = [{ path: '/', view: view1 }];
        
        Router.navigate('/');

        assert.strictEqual(Router.currentRoute.path, '/');
        assert.strictEqual(view1.showCount, 1);
    });

    test('cache l\'ancienne vue lors d\'un changement de route', () => {
        Router.routes = [
            { path: '/', view: view1 },
            { path: '/game', view: view2 }
        ];

        Router.navigate('/');
        Router.navigate('/game');

        assert.strictEqual(view1.hideCount, 1, 'La vue 1 aurait dû être cachée');
        assert.strictEqual(view2.showCount, 1, 'La vue 2 aurait dû être affichée');
        assert.strictEqual(Router.currentRoute.path, '/game');
    });

    test('ne doit pas changer de route si le path est inexistant', () => {
        Router.routes = [{ path: '/', view: view1 }];

        Router.navigate('/');
        Router.navigate('/page-introuvable');
        assert.strictEqual(Router.currentRoute.path, '/');
        assert.strictEqual(view1.showCount, 1);
    });

    test('doit gérer la navigation répétée sur la même route', () => {
        Router.routes = [{ path: '/', view: view1 }];

        Router.navigate('/');
        Router.navigate('/');
        assert.strictEqual(view1.showCount, 2);
        assert.strictEqual(view1.hideCount, 1);
    });
});
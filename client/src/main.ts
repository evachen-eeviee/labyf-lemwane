import { Space } from './backgroundCanva/space/Space';
import AccueilView from './view/AcceuilView';
import ChoixJeuView from './view/ChoixJeuView';

import GameView from './view/GameView';
import Router from './Router/Router';
import { io } from 'socket.io-client';
import { CreditView } from './view/CreditView';
import LobbyView from './view/LobbyView';
import RejouerView from './view/RejouerView';
import ClassementView from './view/ClassementView';
import ChoixMission from './view/histoire/ChoixMission';
import DefileView from './view/histoire/DefileView';
import MissionView from './view/histoire/MissionView';

const socket = io(window.location.hostname + ':8080');

const canvas: HTMLCanvasElement = document.getElementById(
	'bgCanva'
) as HTMLCanvasElement;
const space = new Space(canvas);
const AccueilVue = new AccueilView(document.querySelector('#Accueil')!, socket);
const choixJeuVue = new ChoixJeuView(document.querySelector('#choixJeu')!);
const gameVue = new GameView(document.querySelector('#vueJeu')!, space, socket);
const creditVue = new CreditView(document.querySelector('#Credit')!);
const lobbyVue = new LobbyView(
	document.querySelector('#Lobby')!,
	socket,
	gameVue
);
const rejouerView = new RejouerView(
	document.querySelector('#Rejouer')!,
	socket,
	gameVue
);
const classementView = new ClassementView(
	document.querySelector('#classement')!,
	socket
);

const defileMission1 = new DefileView(document.querySelector('#mission1')!);
const defileMission2 = new DefileView(document.querySelector('#mission2')!);
const defileMission3 = new DefileView(document.querySelector('#mission3')!);
const defileMission4 = new DefileView(document.querySelector('#mission4')!);
const missionGame = new MissionView(
	document.querySelector('#vueJeuHistoire')!,
	socket,
	canvas,
	space
);
const choixMission = new ChoixMission(
	document.querySelector('#choixMission')!,
	socket,
	missionGame
);

const music = document.getElementById('credits-music') as HTMLAudioElement;
const btnCredits = document.querySelector('.btnStyleAcceuil') as HTMLButtonElement;
const creditsView = document.getElementById('Credit') as HTMLElement;
const scrollText = creditsView?.querySelector('.scroll-text') as HTMLElement;

btnCredits?.addEventListener('click', () => {
    if (music) {
        music.currentTime = 0;
        music.play().catch(err => console.warn("Autoplay bloqué :", err));
    }
});

scrollText?.addEventListener('animationend', () => {
    stopCreditsMusic();
});

function stopCreditsMusic(): void {
    if (music) {
        music.pause();
        music.currentTime = 0;
    }
}

window.addEventListener('beforeunload', () => {
    music?.pause();
});

Router.routes = [
	{ path: '/', view: AccueilVue },
	{ path: '/choixJeu', view: choixJeuVue },
	{ path: '/game', view: gameVue },
	{ path: '/credits', view: creditVue },
	{ path: '/lobby', view: lobbyVue },
	{ path: '/rejouer', view: rejouerView },
	{ path: '/classement', view: classementView },
	{ path: '/choixMission', view: choixMission },
	{ path: '/mission1Defil', view: defileMission1 },
	{ path: '/mission2Defil', view: defileMission2 },
	{ path: '/mission3Defil', view: defileMission3 },
	{ path: '/mission4Defil', view: defileMission4 },
	{ path: '/mission', view: missionGame }
];
Router.navigate('/');

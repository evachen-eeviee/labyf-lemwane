import { missionInfo } from '../../../constant';
import Router from '../../Router/Router';
import View from '../View';
import type { Socket } from 'socket.io-client';
import type MissionView from './MissionView';

export default class ChoixMission extends View {
	missionInfo: { nb: number; path: string; title: string; text: string }[];
	missionSelect: { nb: number; path: string; title: string; text: string };
	titre: HTMLElement;
	text: HTMLElement;
	socket: Socket;
	missionView: MissionView;

	constructor(element: HTMLElement, socket: Socket, missionView: MissionView) {
		super(element);
		this.socket = socket;
		this.missionView = missionView;
		this.missionInfo = missionInfo;
		this.missionSelect = this.missionInfo[0];
		this.titre = this.element.querySelector('#titreMission')!;
		this.text = this.element.querySelector('#textMission')!;

		this.element
			.querySelector('.buttonGoBack')
			?.addEventListener('click', e => {
				e.preventDefault();
				Router.navigate('/choixJeu');
			});

		this.renderMission(this.missionInfo[0]);

		this.element.querySelector('#mission1Btn')?.addEventListener('click', e => {
			e.preventDefault();
			this.missionSelect = this.missionInfo[0];
			this.renderMission(this.missionSelect);
		});

		this.element.querySelector('#mission2Btn')?.addEventListener('click', e => {
			e.preventDefault();
			this.missionSelect = this.missionInfo[1];
			this.renderMission(this.missionSelect);
		});

		this.element.querySelector('#mission3Btn')?.addEventListener('click', e => {
			e.preventDefault();
			this.missionSelect = this.missionInfo[2];
			this.renderMission(this.missionSelect);
		});

		this.element.querySelector('#mission4Btn')?.addEventListener('click', e => {
			e.preventDefault();
			this.missionSelect = this.missionInfo[3];
			this.renderMission(this.missionSelect);
		});

		this.element
			.querySelector('#lancerMission')
			?.addEventListener('click', e => {
				e.preventDefault();

				this.socket.emit('creatMission', {
					number: this.missionSelect.nb,
					canvas_width: window.innerWidth,
					canvas_height: window.innerHeight,
				});
			});

		this.socket.on('missionCreate', (roomId: string) => {
			console.log("Succès : Lobby créé sur le serveur avec l'ID :", roomId);
			this.missionView.setRoomId(roomId);
			this.socket.emit('joinRoom', roomId);
			this.socket.emit('getNumberMission', this.missionSelect.nb);
			Router.navigate(this.missionSelect.path);
		});
	}

	renderMission(missionSelect: { nb: number; title: string; text: string }) {
		this.titre.innerHTML = `${missionSelect.title}`;
		this.text.innerHTML = `${missionSelect.text}`;
	}
}

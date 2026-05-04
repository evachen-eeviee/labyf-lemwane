import type { Socket } from "socket.io-client";
import View from "./View";
import Router from "../Router/Router";
import GameView from "./GameView";
import type MissionView from "./histoire/MissionView";

export default class RejouerView extends View {
    socket: Socket;
    gameView: GameView | MissionView;
    constructor(element: HTMLElement, socket: Socket, gameView: GameView | MissionView) {
        super(element);
        this.socket = socket;
        this.gameView = gameView;
        this.element
            .querySelector('.rejouer')?.addEventListener('click', e => {
                e.preventDefault();
                this.clearLocalStats();
                this.gameView.reset();

                Router.navigate('/choixJeu');
            });

        this.element
            .querySelector('.accueil')?.addEventListener('click', e => {
                e.preventDefault();
                this.clearLocalStats();
                this.gameView.reset();
                Router.navigate('/');
            });

        
    }

    private clearLocalStats() {
        console.log("Nettoyage des stats du joueur");
        const stats = [
            'scoreTotal', 
            'tempsTotal'
        ];
        stats.forEach(key => localStorage.removeItem(key));

        this.socket.emit('clearLocalStats');
    }

    updateStats(scoreL: number, scoreT: number, tempsL: number, tempsT: number, ennemisTues: number, ennemisTuesTotal: number) {
        // Récupération QuerySelector
        const scoreLocalHTML = this.element.querySelector('#final-score');
        const scoreTotalHTML = this.element.querySelector('#total-score');

        const timeLocalHTML = this.element.querySelector('#final-time');
        const timeTotalHTML = this.element.querySelector('#total-time');

        const ennemisTuesLocalHTML = this.element.querySelector('#final-enemies');
        const ennemisTuesTotalHTML = this.element.querySelector('#total-enemies');
        
        // Attribution des valeurs
        if (scoreLocalHTML) scoreLocalHTML.textContent = scoreL.toString() + ` (${Math.round(scoreL/scoreT * 100)}%)`;
        if (scoreTotalHTML) scoreTotalHTML.textContent = scoreT.toString();

        if (timeLocalHTML) timeLocalHTML.textContent = tempsL.toString()  + `s (${Math.round(tempsL/tempsT * 100)}%)`;
        if (timeTotalHTML) timeTotalHTML.textContent = tempsT.toString();

        if (ennemisTuesLocalHTML) ennemisTuesLocalHTML.textContent = ennemisTues.toString() + ` (${Math.round(ennemisTues/ennemisTuesTotal * 100)}%)`;
        if (ennemisTuesTotalHTML) ennemisTuesTotalHTML.textContent = ennemisTuesTotal.toString();
        
        
    }

    show(){
        super.show();

        this.socket.on('scoreLocal', (data: { score: number, ennemisTues: number, tempsSurvecu: number }) => {
            const scoreTotal = localStorage.getItem('scoreTotal') || "0";
            const tempsTotal = localStorage.getItem('tempsTotal') || "0";
            const ennemisTuesTotal = localStorage.getItem('ennemisTuesTotal') ||"0";

            this.updateStats(
                data.score,
                parseInt(scoreTotal),
                data.tempsSurvecu,
                parseInt(tempsTotal),
                data.ennemisTues,
                parseInt(ennemisTuesTotal)
            );
        });

        this.socket.emit('getScoreLocal');
    } 
    hide(){
        super.hide();
        this.socket.off('scoreLocal');
    }
}
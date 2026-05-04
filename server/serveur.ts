import http from 'http';
import dotenv from 'dotenv';

import { Server as IOServer } from 'socket.io';
import LobbyManager from './LobbyManager.ts';
import Joueur from '../common/entite/Joueur.ts';
import {
	MISSIONS,
	type MissionConfig,
} from '../common/histoire/MissionConfig.ts';
import Ennemi from '../common/entite/Ennemi.ts';
/*import type { Lobby } from '../common/Lobby.ts';*/

dotenv.config();
const PORT = process.env.PORT || '8080';

/*const lobbies: Lobby[] = [];*/
const joueurs: Joueur[] = [];

const httpServer = http.createServer((_req, res) => {
	res.statusCode = 200;
	res.setHeader('Content-Type', 'text/plain');
	res.end();
});

const io = new IOServer(httpServer, { cors: { origin: true } });
const lobbyManager = new LobbyManager(io);

io.on('connection', socket => {
	console.log(`Nouvelle connexion du client ${socket.id}`);

	socket.on('getBestJoueur', () => {
		socket.emit('listBestJoueur', getBestJoueur());
	});

	socket.on('getScoreLocal', () => {
		console.log(`--- Appel getScoreLocal ---`);
		const joueur = joueurs.find(j => j.id === socket.id);

		if (joueur) {
			socket.emit('scoreLocal', {
				score: joueur.score,
				ennemisTues: joueur.ennemisTues,
				tempsSurvecu: joueur.tempsSurvecu,
			});
		} else {
			console.log(`--- Erreur appel getScoreLocal (pas de joueur) ---`);
		}
	});

	socket.on('clearLocalStats', () => {
		console.log(`--- Appel clearLocalStats ---`);
		const joueur = joueurs.find(j => j.id === socket.id);

		if (joueur) {
			joueur.score = 0;
			joueur.ennemisTues = 0;
			joueur.tempsSurvecu = 0;
			joueur.nbTir = 1;
		} else {
			console.log(`--- Erreur appel clearLocalStats (pas de joueur) ---`);
		}
	});

	// --- LOGIQUE DES LOBBY ---
	socket.on('getLobbies', () => {
		console.log(`--- Appel getLobbies ---`);
		socket.emit('listLobbies', lobbyManager.getLobbiesList());
	});

	socket.on(
		'createGame',
		({ nom, maxJoueur, difficulte, canvas_width, canvas_height }) => {
			console.log(`--- Appel createGame ---`);

			const roomId = lobbyManager.créerPartie(
				nom,
				maxJoueur,
				difficulte,
				canvas_width,
				canvas_height
			);
			// On renvoie l'ID au créateur pour qu'il sache où aller
			socket.emit('gameCreated', roomId);
		}
	);

	socket.on('joinRoom', roomId => {
		const partie = lobbyManager.getPartie(roomId);
		if (partie) {
			const joueur = joueurs.filter(e => e.id === socket.id);
			if (joueur[0]) {
				partie.addJoueur(socket.id, joueur[0]);
			}

			socket.join(roomId);
			console.log(`Joueur ${socket.id} a rejoint le lobby ${roomId}`);

			if (partie.isRunning()) {
				socket.emit('gameAlreadyRunning');
			}
		}
	});

	// --- Logique des missions ---

	interface MissionCreat {
		number: number;
		canvas_width: number;
		canvas_height: number;
	}

	socket.on('creatMission', (data: MissionCreat) => {
		const mission: MissionConfig = MISSIONS[data.number - 1];
		let boss: Ennemi | undefined;
		if (mission.bossType) {
			boss = new Ennemi(mission.bossType, data.canvas_width * 2/3, data.canvas_height * 0.08);
		}

		const roomId = lobbyManager.créerMission(
			mission,
			data.canvas_width,
			data.canvas_height,
			boss
		);

		socket.emit('missionCreate', roomId);
	});

	socket.on('getNumberMission', (id: number) => {
		console.log('recevied message');
		console.log('id : ' + id);
		socket.emit('numberMissiongive', { id });
	});

	// // --- LOGIQUE EN JEU ---
	socket.on('newPlayer', ({ nom, img, vie, vitesse, roomId }) => {
		console.log(`--- Appel newPlayer ---`);

		const partie = lobbyManager.getPartie(roomId);
		if (partie) {
			const joueur = new Joueur(socket.id, nom, img, vie, vitesse);
			partie.addJoueur(socket.id, joueur);
		}
	});

	socket.on(
		'basePlayer',
		(a: { nom: string; img: string; vie: number; vitesse: number }) => {
			console.log(`--- Appel basePlayer ---`);

			joueurs.push(new Joueur(socket.id, a.nom, a.img, a.vie, a.vitesse));
		}
	);

	socket.on('startGame', data => {
		const partie = lobbyManager.getPartie(data.roomId);
		if (partie) {
			console.log(`Lancement de la partie : ${data.roomId}`);
			partie.start();
		} else {
			console.error(
				'Impossible de lancer la partie : ID introuvable',
				data.roomId
			);
		}
	});

	socket.on('input', ({ roomId, touches, mouse }) => {
		const partie = lobbyManager.getPartie(roomId);
		if (partie) {
			partie.setInput(socket.id, touches, mouse);
		}
	});

	socket.on('disconnect', () => {
		console.log(`Déconnexion : ${socket.id}`);
		for (const partie of lobbyManager['parties'].values()) {
			if (partie['joueurs'].has(socket.id)) {
				partie.removeJoueur(socket.id);
				break;
			}
		}
	});

	socket.on('leaveGame', ({ roomId }) => {
		console.log(`--- Appel leaveGame ---`);

		const partie = lobbyManager.getPartie(roomId);
		if (partie) {
			partie.removeJoueur(socket.id);
		}
		socket.leave(roomId);
	});

	socket.on('resetGame', ({ roomId }) => {
		console.log(`--- Appel resetGame ---`);
		const partie = lobbyManager.getPartie(roomId);
		if (partie && !partie.isRunning()) partie.reset();
	});
});

const getBestJoueur = () => {
	return [...joueurs].sort((a, b) => b.score - a.score).slice(0, 10);
};

// httpServer.listen(PORT, () => {
// 	console.log(`Serveur démarré sur http://localhost:${PORT}/`);
// });

httpServer.listen(parseInt(PORT), '0.0.0.0', () => {
    console.log(`Serveur démarré sur http://0.0.0.0:${PORT}/`);
});
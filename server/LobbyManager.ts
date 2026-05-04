import type { MissionConfig } from '../common/histoire/MissionConfig.ts';
import type { Lobby } from '../common/Lobby.ts';
import MissionServeur from './Histoire/MissionServer.ts';
import PartieServeur from './Partie/PartieServeur.ts';
import { Server as IOServer } from 'socket.io';
import type Ennemi from '../common/entite/Ennemi.ts';

export default class LobbyManager {
	private parties: Map<string, PartieServeur> = new Map();
	private io: IOServer;

	constructor(io: IOServer) {
		this.io = io;
	}

	// Crée une nouvelle partie et la démarre
	public créerPartie(
		nom: string,
		maxJoueur: number,
		difficulte: string,
		canvas_width: number,
		canvas_height: number
	): string {
		const id = crypto.randomUUID();
		const nouvellePartie: PartieServeur = new PartieServeur(
			this.io,
			id,
			nom,
			maxJoueur,
			difficulte,
			canvas_width,
			canvas_height
		);
		this.parties.set(id, nouvellePartie);

		console.log(`Lobby créé : ${id}`);
		return id;
	}

	// Crée une nouvelle mission et la démarre
	public créerMission(
		config: MissionConfig,
		canvas_width: number,
		canvas_height: number,
		boss?: Ennemi
	): string {
		const id = crypto.randomUUID();
		let nouvelleMission: MissionServeur;
		if (boss) {
			nouvelleMission = new MissionServeur(
				this.io,
				id,
				config,
				canvas_width,
				canvas_height,
				boss
			);
		} else {
			nouvelleMission = new MissionServeur(
				this.io,
				id,
				config,
				canvas_width,
				canvas_height
			);
		}

		this.parties.set(id, nouvelleMission);
		nouvelleMission.start();

		console.log(`Lobby créé : ${id}`);
		return id;
	}

	// Récupère la liste pour la vue Lobby du client
	public getLobbiesList(): Lobby[] {
		const tableauLobbies = [];

		for (const [id, partie] of this.parties.entries()) {
			const donneesLobby: Lobby = {
				id: id,
				nom: partie.getNom(), // TODO
				joueurs: partie.getNbJoueurs(),
				maxJoueurs: partie.getMaxNbJoueur(),
				difficulte: partie.getDifficulte(),
			};
			if (
				donneesLobby.joueurs < donneesLobby.maxJoueurs &&
				donneesLobby.joueurs != 0
			) {
				tableauLobbies.push(donneesLobby);
			} else if (donneesLobby.joueurs == 0) {
				// Clear des parties à 0 joueurs
				this.supprimerPartie(donneesLobby.id);
			}
		}

		return tableauLobbies;
	}

	public supprimerPartie(id: string): void {
		const partie = this.parties.get(id);
		if (partie) {
			partie.stop();
			this.parties.delete(id);
			console.log(`Lobby supprimé : ${id}`);
		}
	}

	public getPartie(id: string): PartieServeur | MissionServeur | undefined {
		return this.parties.get(id);
	}
}

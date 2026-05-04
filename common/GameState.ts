// common/GameState.ts

import type { BonusType } from './entite/BonusType.ts';
import type Ennemi from './entite/Ennemi.ts';
import type { EnnemiType } from './entite/EnnemiType.ts';
import type Impact from './entite/Impact.ts';

export interface PlayerState {
	id: string;
	nom: string;
	image_path: string;
	pos_x: number;
	pos_y: number;
	vie: number;
	vie_max: number;
	vitesse: number;
	coef_vitesse: number;
	score: number;
	ennemisTues: number;
	is_invincible: boolean;
}

export interface EnemyState {
	id: string;
	type: EnnemiType;
	pos_x: number;
	pos_y: number;
	vie: number;
	vie_max: number;
}

export interface TirState {
	id: string;
	pos_x: number;
	pos_y: number;
	owner_id: string;
}

export interface BonusState {
	id: string;
	type: BonusType;
	pos_x: number;
	pos_y: number;
}

export type BossTirState = {
	id: string;
	pos_x: number;
	pos_y: number;
	vel_x: number;
	vel_y: number;
};

export interface GameState {
	players: PlayerState[];
	ennemis: EnemyState[];
	bonus: BonusState[];
	tirs: TirState[];
	tick: number;
	impact: Impact[];
	boss?: Ennemi | null;
	bossTirs?: BossTirState[];
}

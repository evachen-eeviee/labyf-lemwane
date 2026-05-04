import { EnnemiType } from '../entite/EnnemiType.ts';

export interface EnnemiVagueConfig {
	type: EnnemiType;
	spawnRateMs: number;
	maxSimultanes: number;
}

export interface MissionConfig {
	id: number;
	nom: string;
	dureeVagueMs: number;
	ennemisVague: EnnemiVagueConfig[];
	bossType?: EnnemiType;
}

export const MISSIONS: MissionConfig[] = [
	{
		id: 1,
		nom: 'La menace dinosaure',
		dureeVagueMs: 120_000,
		ennemisVague: [
			{ type: EnnemiType.MANTIS, spawnRateMs: 1200, maxSimultanes: 6 },
			{ type: EnnemiType.SPRFLY2, spawnRateMs: 1000, maxSimultanes: 8 },
			{ type: EnnemiType.SNAIL, spawnRateMs: 1400, maxSimultanes: 4 },
			{ type: EnnemiType.CATERPILAR, spawnRateMs: 2500, maxSimultanes: 2 },
		],
	},
	{
		id: 2,
		nom: "L'attaques des dinosaures",
		dureeVagueMs: 180_000,
		ennemisVague: [
			{ type: EnnemiType.METEOR, spawnRateMs: 1500, maxSimultanes: 4 },
			{ type: EnnemiType.COMETE, spawnRateMs: 1500, maxSimultanes: 3 },
			{ type: EnnemiType.SPRFLY1, spawnRateMs: 1500, maxSimultanes: 6 },
		],
		bossType: EnnemiType.Sebastion,
	},
	{
		id: 3,
		nom: 'La revanche des dinosaures',
		dureeVagueMs: 180_000,
		ennemisVague: [
			{ type: EnnemiType.SPIDER, spawnRateMs: 1200, maxSimultanes: 6 },
			{ type: EnnemiType.SPRFLY2, spawnRateMs: 1000, maxSimultanes: 8 },
			{ type: EnnemiType.SNAIL, spawnRateMs: 1400, maxSimultanes: 4 },
			{ type: EnnemiType.CATERPILAR, spawnRateMs: 2500, maxSimultanes: 2 },
		],
		bossType: EnnemiType.Sully_Van,
	},
	{
		id: 4,
		nom: 'Un nouveau dinosaure',
		dureeVagueMs: 240_000,
		ennemisVague: [
            { type: EnnemiType.SPIDER, spawnRateMs: 1200, maxSimultanes: 6 },
			{ type: EnnemiType.SPRFLY2, spawnRateMs: 1000, maxSimultanes: 8 },
			{ type: EnnemiType.SNAIL, spawnRateMs: 1400, maxSimultanes: 4 },
			{ type: EnnemiType.CATERPILAR, spawnRateMs: 2500, maxSimultanes: 2 },
            { type: EnnemiType.MANTIS, spawnRateMs: 1200, maxSimultanes: 6 },
            { type: EnnemiType.SPRFLY1, spawnRateMs: 1500, maxSimultanes: 6 },
        ],
		bossType: EnnemiType.FL0R1AN,
	},
];

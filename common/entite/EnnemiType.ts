export const EnnemiType = {
	// Multi
	METEOR: 'METEOR',
	COMETE: 'COMETE',
	ALIEN: 'ALIEN',
	// Histoire
	CATERPILAR: 'CATERPILAR',
	MANTIS: 'MANTIS',
	SNAIL: 'SNAIL',
	SPIDER: 'SPIDER',
	SPRFLY1: 'SPRFLY1',
	SPRFLY2: 'SPRFLY2',
	//Boss
	Sebastion: 'Sebastion',
	Sully_Van: 'Sully-Van',
	FL0R1AN: 'FL0-R1AN',
} as const;

type EnnemiKeys = keyof typeof EnnemiType;
export type EnnemiType = (typeof EnnemiType)[EnnemiKeys];

// Interface qui définit la structure des stats d'un ennemi
interface EnnemiConfig {
	image_path: string;
	vie_max: number;
	vitesse: number;
	score_on_kill: number;
	poids: number;
	isBoss?: boolean;
}

// Dictionnaire qui associe chaque type d'ennemi à ses stats
// Record<EnnemiType, EnnemiConfig> = objet dont les clés sont des EnnemiType et les valeurs des EnnemiConfig
export const ENNEMI_CONFIGS: Record<EnnemiType, EnnemiConfig> = {
	// --- Multi ---
	[EnnemiType.METEOR]: {
		image_path: '/assets/Ennemi/meteor.png',
		vie_max: 1,
		vitesse: 5,
		score_on_kill: 10,
		poids: 10,
	},
	[EnnemiType.COMETE]: {
		image_path: '/assets/Ennemi/comete.png',
		vie_max: 3,
		vitesse: 4,
		score_on_kill: 50,
		poids: 5,
	},
	[EnnemiType.ALIEN]: {
		image_path: '/assets/Ennemi/vaisseauAlien.png',
		vie_max: 4,
		vitesse: 3,
		score_on_kill: 150,
		poids: 1,
	},
	// --- Histoire (vagues) ---
	[EnnemiType.CATERPILAR]: {
		image_path: '/assets/Ennemi/histoire/spr_caterpilar_01_green.png',
		vie_max: 4,
		vitesse: 3,
		score_on_kill: 300,
		poids: 8,
	},
	[EnnemiType.MANTIS]: {
		image_path: '/assets/Ennemi/histoire/spr_mantis_yellow.png',
		vie_max: 2,
		vitesse: 4,
		score_on_kill: 100,
		poids: 4,
	},
	[EnnemiType.SNAIL]: {
		image_path: '/assets/Ennemi/histoire/spr_snail_yellow.png',
		vie_max: 3,
		vitesse: 3,
		score_on_kill: 200,
		poids: 2,
	},
	[EnnemiType.SPIDER]: {
		image_path: '/assets/Ennemi/histoire/spr_spider_purple.png',
		vie_max: 2,
		vitesse: 4,
		score_on_kill: 100,
		poids: 2,
	},
	[EnnemiType.SPRFLY1]: {
		image_path: '/assets/Ennemi/histoire/spr_fly_01_pink.png',
		vie_max: 2,
		vitesse: 2,
		score_on_kill: 200,
		poids: 2,
	},
	[EnnemiType.SPRFLY2]: {
		image_path: '/assets/Ennemi/histoire/spr_fly_02_blue.png',
		vie_max: 1,
		vitesse: 5,
		score_on_kill: 50,
		poids: 2,
	},
	// --- Boss ---
	[EnnemiType.Sebastion]: {
		image_path: '/assets/Ennemi/histoire/spr_butterfly_purple.png',
		vie_max: 100,
		vitesse: 1,
		score_on_kill: 2000,
		poids: 0,
		isBoss: true,
	},
	[EnnemiType.Sully_Van]: {
		image_path: '/assets/Ennemi/histoire/spr_caterpilar_02_green.png',
		vie_max: 400,
		vitesse: 1,
		score_on_kill: 3500,
		poids: 0,
		isBoss: true,
	},
	[EnnemiType.FL0R1AN]: {
		image_path: '/assets/Ennemi/histoire/spr_centipede_red.png',
		vie_max: 700,
		vitesse: 1,
		score_on_kill: 6000,
		poids: 0,
		isBoss: true,
	},
};

export const EnnemiImageMap: Record<EnnemiType, string> = Object.fromEntries(
	Object.entries(ENNEMI_CONFIGS).map(([type, cfg]) => [type, cfg.image_path])
) as Record<EnnemiType, string>;

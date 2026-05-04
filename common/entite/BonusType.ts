export const BonusType = {
    VIE: 'VIE',
    INVINCIBILITE: 'INVINCIBILITE',
    SCORE: 'SCORE',
    TIR_DOUBLE: 'TIR_DOUBLE',
} as const;

type BonusKeys = keyof typeof BonusType;
export type BonusType = typeof BonusType[BonusKeys];

interface BonusConfig {
    nom: string;
    image_path: string;
    vitesse: number;
    valeur: number; 
    poids: number; 
}

export const BONUS_CONFIGS: Record<BonusType, BonusConfig> = {
    [BonusType.VIE]: {
        nom: 'Soin',
        image_path: '/assets/bonusvie.png',
        vitesse: 1,
        valeur: 1,
        poids: 3,
    },
    [BonusType.INVINCIBILITE]: {
        nom: 'Invincibilité',
        image_path: '/assets/bonusinvi.png',
        vitesse: 1,
        valeur: 5000,
        poids: 2,
    },
    [BonusType.SCORE]: {
        nom: 'Score',
        image_path: '/assets/bonusx3.png',
        vitesse: 1,
        valeur: 3,
        poids: 1,
    },
    [BonusType.TIR_DOUBLE]: {
        nom: 'Tir Double',
        image_path: '/assets/bonustir.png',
        vitesse: 1,
        valeur: 5000,
        poids: 4,
    },
};

export const BonusImageMap: Record<BonusType, string> = {
    [BonusType.VIE]: '/assets/bonusvie.png',
    [BonusType.INVINCIBILITE]: '/assets/bonusinvi.png',
    [BonusType.SCORE]: '/assets/bonusx3.png',
    [BonusType.TIR_DOUBLE]: '/assets/bonustir.png',
};
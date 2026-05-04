import test, { beforeEach, describe } from 'node:test';
import assert from 'node:assert';
import MissionServeur, { MissionPhase } from './MissionServer.ts';
import { MISSIONS } from '../../common/histoire/MissionConfig.ts';
import { Server as IOServer } from 'socket.io';

describe('MissionServeur — initialisation', () => {
	let mission: MissionServeur;

	beforeEach(() => {
		const io = new IOServer();
		mission = new MissionServeur(io, 'test_id', MISSIONS[0], 10, 10);
	});

	test('getPhase() retourne WAVE avant start()', () => {
		assert.strictEqual(mission.getPhase(), MissionPhase.WAVE);
	});

	test("getMissionId() retourne l'id de la config", () => {
		assert.strictEqual(mission.getMissionId(), 1);
	});
});

describe('MissionServeur — start()', () => {
	let mission: MissionServeur;

	beforeEach(() => {
		const io = new IOServer();
		mission = new MissionServeur(io, 'test_id', MISSIONS[0], 10, 10);
	});

	test('la mission est en phase WAVE après start()', () => {
		mission.start();
		assert.strictEqual(mission.getPhase(), MissionPhase.WAVE);
	});

	test('autoRespawn est désactivé après start()', () => {
		mission.start();
		const sm = mission.spawnManager;
		assert.strictEqual(sm.autoRespawn, false);
	});

	test('appeler start() deux fois ne lance pas deux boucles', () => {
		mission.start();
		const id1 = mission.intervalId;
		mission.start();
		const id2 = mission.intervalId;
		assert.strictEqual(id1, id2);
	});
});

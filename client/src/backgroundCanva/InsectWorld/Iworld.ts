import { bgIWorld } from '../../../constant';

export default class Iworld {
	canvas: HTMLCanvasElement;
	ctx: CanvasRenderingContext2D;
	canvasResizeObserver: ResizeObserver;
	bgImages: string[] = bgIWorld;
	images: Map<string, HTMLImageElement> = new Map();

	constructor(canvas: HTMLCanvasElement) {
		this.canvas = canvas;
		this.ctx = canvas.getContext('2d')!;

		this.canvas.width = window.innerWidth;
		this.canvas.height = window.innerHeight - 100;

		this.canvasResizeObserver = new ResizeObserver(() => this.resampleCanvas());
		this.canvasResizeObserver.observe(this.canvas);

		this.init();
	}

	async init() {
		await this.preloadImages();
		this.dessineWorld();
	}

	resampleCanvas() {
		if (this.canvas.clientWidth === 0 || this.canvas.clientHeight === 0) return;
		this.canvas.width = this.canvas.clientWidth;
		this.canvas.height = this.canvas.clientHeight;
	}

	async preloadImages() {
		const promises = this.bgImages.map(path => {
			return new Promise<void>(resolve => {
				const img = new Image();
				img.onload = () => resolve();
				img.src = path;
				this.images.set(path, img);
			});
		});
		await Promise.all(promises);
	}

	dessineWorld() {
		this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);
		this.images.forEach((img, path) => {
			if (img.complete) {
				this.ctx.drawImage(img, 0, 0, this.canvas.width, this.canvas.height);
			} else {
				console.log('pas trouver : ' + path);
			}
		});
	}
}

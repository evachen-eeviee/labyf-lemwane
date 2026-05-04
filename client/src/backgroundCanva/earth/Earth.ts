import { bgEarth } from '../../../constant';

export default class Earth {
	canvas: HTMLCanvasElement;
	ctx: CanvasRenderingContext2D;
	canvasResizeObserver: ResizeObserver;
	bgImages: string[] = bgEarth;
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
		await this.preloadImages(); // attend la fin du chargement
		this.dessineEarth();
	}

	resampleCanvas() {
		if (this.canvas.clientWidth === 0 || this.canvas.clientHeight === 0) return;
		this.canvas.width = this.canvas.clientWidth;
		this.canvas.height = this.canvas.clientHeight;

		this.dessineEarth();
	}

	async preloadImages() {
		const promises = this.bgImages.map(path => {
			return new Promise<void>(resolve => {
				const img = new Image();
				img.onload = () => resolve();
				img.onerror = () => resolve();
				img.src = path;
				this.images.set(path, img);
			});
		});
		await Promise.all(promises);
	}

	dessineEarth() {
		this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);
		const imgMontagne = this.images.get('/assets/bg/histoire/montagne.png');
		const imgArbre = this.images.get('/assets/bg/histoire/Forest_Layer1.png');

		if (imgMontagne) {
			this.ctx.drawImage(
				imgMontagne,
				0,
				0,
				this.canvas.width,
				this.canvas.height
			);
		} else {
			console.log('pas trouvé');
		}

		if (imgArbre) {
			this.ctx.drawImage(imgArbre, 0, 0, this.canvas.width, this.canvas.height);
		} else {
			console.log('pas trouvé');
		}
	}
}

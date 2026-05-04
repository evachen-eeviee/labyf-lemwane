import { Astres } from '../../../constant';
import type Astre from './Astre';
import type Star from './Stars';

export class Space {
	canvas: HTMLCanvasElement;
	ctx: CanvasRenderingContext2D;
	canvasResizeObserver: ResizeObserver;
	astreImages: Map<string, HTMLImageElement> = new Map();
	stars: Array<Star> = [];
	STAR_COUNT = 300;
	astres: Array<Astre> = [];
	MAX_OTHER_ASTRE = 5;
	IMG_OTHER_ASTRE = Astres;
	private active: boolean = true;

	constructor(canvas: HTMLCanvasElement) {
		this.canvas = canvas;
		this.ctx = canvas.getContext('2d')!;

		this.canvas.width = window.innerWidth;
		this.canvas.height = window.innerHeight - 100;

		this.canvasResizeObserver = new ResizeObserver(() => this.resampleCanvas());
		this.canvasResizeObserver.observe(this.canvas);
		this.preloadImages();
		this.creatSpace();
		this.animateSpace();
	}

	creatSpace() {
		for (let i = 0; i < this.STAR_COUNT; i++) {
			this.stars.push({
				x: Math.random() * this.canvas.width,
				y: Math.random() * this.canvas.height,
				size: Math.random() * 2,
				speed: Math.random() * 0.5,
			});
		}
		this.spawnAstres();
	}

	preloadImages() {
		for (const path of this.IMG_OTHER_ASTRE) {
			const img = new Image();
			img.src = path;
			this.astreImages.set(path, img);
		}
	}

	getImg() {
		let index = 0;
		const uniqueImgs = [
			'/assets/bg/spr_planet_pink.png',
			'/assets/bg/spr_planet_blue.png',
		];

		let img = '';

		do {
			index = Math.floor(Math.random() * this.IMG_OTHER_ASTRE.length);
			img = this.IMG_OTHER_ASTRE[index];
		} while (
			uniqueImgs.includes(img) &&
			this.astres.filter(a => a.img === img).length >= 1
		);

		return img;
	}

	spawnAstres() {
		let img = '';

		for (let i = 0; i < this.MAX_OTHER_ASTRE; i++) {
			img = this.getImg();
			const randx = Math.random();
			const randy = Math.random();
			this.astres.push({
				x: Math.random() * this.canvas.width,
				y: Math.random() * this.canvas.height,
				randx: randx,
				randy: randy,
				size: Math.random(),
				speed: Math.random() * 0.2,
				img: img,
			});
		}
	}

	respawnAstres() {
		let img = '';
		for (let i = this.astres.length; i < this.MAX_OTHER_ASTRE; i++) {
			img = this.getImg();
			const randx = Math.random();
			const randy = Math.random();
			this.astres.push({
				x: window.innerWidth,
				y: Math.random() * this.canvas.height,
				randx: randx,
				randy: randy,
				size: Math.random(),
				speed: Math.random() * 2,
				img: img,
			});
		}
	}

	animateSpace = () => {

		if (!this.active) return;

		this.ctx.fillStyle = 'black';
		this.ctx.fillRect(0, 0, this.canvas.width, this.canvas.height);

		this.ctx.fillStyle = 'white';

		this.stars.forEach(star => {
			star.x -= star.speed;

			if (star.x < 0) {
				star.x = window.innerWidth;
				star.y = Math.random() * this.canvas.height;
			}

			this.ctx.beginPath();
			this.ctx.arc(star.x, star.y, star.size, 0, Math.PI * 2);
			this.ctx.fill();
		});

		this.astres.forEach(astre => {
			astre.x -= astre.speed;
			const img = this.astreImages.get(astre.img);
			if (img?.complete) {
				this.ctx.drawImage(img, astre.x, astre.y);
			}
		});

		// Retire les astres sortis de l'écran
		this.astres = this.astres.filter(astre => {
			const img = new Image();
			img.src = astre.img;
			return astre.x + img.width >= 0;
		});

		if (this.astres.length < this.MAX_OTHER_ASTRE) {
			this.respawnAstres();
		}

		requestAnimationFrame(this.animateSpace);
	};

	setSpeed(speed: number) {
		this.stars.forEach(star => {
			star.speed = star.speed * speed;
		});
	}

	respawnStars() {
		this.stars = [];
		for (let i = 0; i < this.STAR_COUNT; i++) {
			this.stars.push({
				x: Math.random() * this.canvas.width,
				y: Math.random() * this.canvas.height,
				size: Math.random() * 2,
				speed: Math.random() * 0.5,
			});
		}
	}

	resizeAstres() {
		this.astres.forEach(astre => {
			astre.x = astre.randx * this.canvas.width;
			astre.y = astre.randy * this.canvas.height;
		});
	}

	resampleCanvas() {
		if (this.canvas.clientWidth === 0 || this.canvas.clientHeight === 0) return;
		this.canvas.width = this.canvas.clientWidth;
		this.canvas.height = this.canvas.clientHeight;

		this.respawnStars();
		this.resizeAstres();
	}

	stop() {
        this.active = false;
    }

    resume() {
        if (!this.active) {
            this.active = true;
            this.animateSpace();
        }
    }
}

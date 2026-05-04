export default class Impact {
    x: number;
    y: number;
    step: number;

    constructor(x:number, y: number) {
        this.x = x;
        this.y = y;
        this.step = 1;
    }

    getStep() : number {
        return this.step;
    }

     nextStep(): void {
        this.step++;
    }

    isFinished(): boolean {
        return this.step > 10;
    }
}
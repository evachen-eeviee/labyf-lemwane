import SpaceElement from "./SpaceElement.ts";


export default class Astre extends SpaceElement {
    img : string;
    randx : number;
    randy : number;

    constructor(x : number, y : number, randx : number, randy : number, size : number, speed : number, img : string) {
        super(x, y, size, speed);
        this.randx = randx;
        this.randy = randy;
        this.img = img;
    }
}
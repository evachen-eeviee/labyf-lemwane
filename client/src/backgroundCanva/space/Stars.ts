import SpaceElement from "./SpaceElement";

export default class Star extends SpaceElement {

    constructor(x : number, y : number, size : number, speed : number) {
        super(x, y, size, speed);
    }
}
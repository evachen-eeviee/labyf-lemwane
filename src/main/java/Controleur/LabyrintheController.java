package Controleur;

import modele.labyrinthe.*;
import modele.joueur.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class LabyrintheController {
    private Labyrinthe laby;
    private Progression prog;
    private Rectangle[][] cell;
    public final int VISION = 8;

    public LabyrintheController(Labyrinthe laby){
        this.laby = laby;
    }

    public void deplacerHaut() {
        laby.deplacerJoueur(Direction.HAUT);
    }

    public void deplacerBas() {
        laby.deplacerJoueur(Direction.BAS);
    }

    public void deplacerGauche() {
        laby.deplacerJoueur(Direction.GAUCHE);
    }

    public void deplacerDroite() {
        laby.deplacerJoueur(Direction.DROITE);
    }


    public GridPane creerMiniGrille(Rectangle[][] miniCell, int vision) {
        GridPane miniGrid = new GridPane();
        for (int i = 0; i < vision; i++) {
            for (int j = 0; j < vision; j++) {
                Rectangle r = new Rectangle(20, 20);
                r.setStroke(Color.GRAY);
                r.setFill(Color.DARKGRAY);
                miniCell[i][j] = r;
                miniGrid.add(r, i, j);
            }
        }
        return miniGrid;
    }



    public Color[][] visionZoom(){
        int[] pos = laby.getCoordJoueur();
        int jx = pos[0];
        int jy = pos[1];
        int centre = VISION / 2;

        Color[][] vision = new Color[VISION][VISION];

        for (int cx = -centre; cx < centre; cx++) {
            for (int cy = -centre; cy < centre; cy++) {
                int x = jx + cx;
                int y = jy + cy;
                int gx = cx + centre;
                int gy = cy + centre;
                boolean marqEntree = x == laby.getCoordEntree()[0] && y == laby.getCoordEntree()[1];
                boolean marqSortie = x == laby.getCoordSortie()[0] && y == laby.getCoordSortie()[1];

                if (x < 0 || y < 0 || x >= laby.getLARGEUR() || y >= laby.getHAUTEUR()) {
                    vision[gx][gy] = Color.BLACK; // hors limites
                } else {
                    Case c = laby.getGrille()[x][y];
                    if (x == jx && y == jy) {
                        vision[gx][gy] = Color.GOLD;
                    } else if (c.estMur()) {
                        vision[gx][gy] = Color.BLACK;
                    } else if(marqEntree){
                        vision[gx][gy] = Color.GREEN;
                    } else if(marqSortie){
                        vision[gx][gy] = Color.RED;
                    }else {
                        vision[gx][gy] = Color.WHITE;
                    }
                }
            }
        }

        return vision;
    }

    public void vueRestrainte(){
        int[] pos = laby.getCoordJoueur();
        int jx = pos[0];
        int jy = pos[1];
        int rayon = VISION / 2;

        for (int x = 0; x < cell.length; x++) {
            for (int y = 0; y < cell[0].length; y++) {
                Rectangle rect = cell[x][y];
                if (Math.abs(x - jx) <= rayon && Math.abs(y - jy) <= rayon) {
                    rect.setOpacity(1.0);
                } else {
                    rect.setOpacity(0.015);
                }
            }
        }
    }

}

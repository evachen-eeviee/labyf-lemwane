package Controleur;

import modele.labyrinthe.*;
import javafx.geometry.Insets;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class LabyrintheParfaitController {
    private LabyrintheParfait laby;
    public final int VISION = 8;

    public LabyrintheParfaitController(LabyrintheParfait laby) {
        this.laby = laby;
    }

    // Déplacements avec vérification des murs
    public void deplacerHaut() {
        deplacer(Direction.HAUT);
    }

    public void deplacerBas() {
        deplacer(Direction.BAS);
    }

    public void deplacerGauche() {
        deplacer(Direction.GAUCHE);
    }

    public void deplacerDroite() {
        deplacer(Direction.DROITE);
    }

    public GridPane creerMiniGrille(StackPane[][] miniCell, int vision, int tailleMiniCase) {
        GridPane miniGrid = new GridPane();
        for (int i = 0; i < vision; i++) {
            for (int j = 0; j < vision; j++) {
                StackPane sp = new StackPane();
                sp.setPrefSize(tailleMiniCase, tailleMiniCase);

                // Bordure fine pour représenter le mur
                sp.setBorder(new Border(new BorderStroke(Color.BROWN, BorderStrokeStyle.SOLID, CornerRadii.EMPTY,
                        new BorderWidths(1))));

                // Fond initial de la case
                Region fond = new Region();
                fond.setPrefSize(tailleMiniCase, tailleMiniCase);
                fond.setBackground(new Background(new BackgroundFill(Color.DARKGRAY, CornerRadii.EMPTY, Insets.EMPTY)));

                sp.getChildren().add(fond);
                miniCell[i][j] = sp;
                miniGrid.add(sp, i, j);
            }
        }
        return miniGrid;
    }



    public Color[][] visionZoom() {
        Color[][] vision = new Color[VISION][VISION];
        int[] pos = laby.getCoordJoueur();
        int jx = pos[0];
        int jy = pos[1];

        for (int i = 0; i < VISION; i++) {
            for (int j = 0; j < VISION; j++) {
                int x = jx - VISION/2 + i;
                int y = jy - VISION/2 + j;

                if (x < 0 || y < 0 || x >= laby.getLARGEUR() || y >= laby.getHAUTEUR()) {
                    vision[i][j] = Color.BLACK;
                } else {
                    Case c = laby.getGrille()[x][y];
                    if (x == jx && y == jy) vision[i][j] = Color.GOLD;
                    else if (c.estMur()) vision[i][j] = Color.BLACK;
                    else if (x == laby.getCoordEntree()[0] && y == laby.getCoordEntree()[1])
                        vision[i][j] = Color.GREEN;
                    else if (x == laby.getCoordSortie()[0] && y == laby.getCoordSortie()[1])
                        vision[i][j] = Color.RED;
                    else vision[i][j] = Color.WHITE;
                }
            }
        }
        return vision;
    }



    private void deplacer(Direction dir) {
        int[] pos = laby.getCoordJoueur();
        int x = pos[0];
        int y = pos[1];
        int nx = x;
        int ny = y;

        switch (dir) {
            case HAUT -> ny = y - 1;
            case BAS -> ny = y + 1;
            case GAUCHE -> nx = x - 1;
            case DROITE -> nx = x + 1;
        }

        // Vérifie les limites
        if (nx < 0 || ny < 0 || nx >= laby.getLARGEUR() || ny >= laby.getHAUTEUR()) return;

        // Vérifie le mur entre cases
        if (murEntreCases(x, y, nx, ny)) return;

        // Déplace le joueur
        laby.setCoordJoueur(new int[]{nx, ny});

        // Notifie la vue pour mettre à jour l'affichage
        laby.notifierObservateurs();
    }

    // Vérifie s'il y a un mur entre deux cases (horizontal ou vertical)
    private boolean murEntreCases(int x1, int y1, int x2, int y2) {
        if (x1 == x2) { // Déplacement vertical
            int minY = Math.min(y1, y2);
            return laby.murHorizontaux[minY][x1];
        } else if (y1 == y2) { // Déplacement horizontal
            int minX = Math.min(x1, x2);
            return laby.mursVerticaux[minX][y1];
        }
        return true; // Déplacement diagonal impossible
    }
}

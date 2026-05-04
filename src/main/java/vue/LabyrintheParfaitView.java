package vue;

import Controleur.LabyrintheParfaitController;
import Controleur.ProgressionController;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import modele.joueur.Joueur;
import modele.joueur.Sauvegarde;
import modele.labyrinthe.*;
import modele.partie.PartieProgression;
import vue.utils.ThemeManager;

public class LabyrintheParfaitView implements Observateur {

    private LabyrintheParfait laby;
    private LabyrintheParfaitController controller;
    private Joueur joueur;
    private int numEtape;
    private Difficulte diff;
    private ProgressionController progController;
    private Stage stage;
    private boolean[][] decouvert;
    private StackPane[][] casesPane;
    private Image imgJoueur;
    private Image imgChemin;
    private ImageView joueurView;
    private int ancienX, ancienY;
    private int taille_case = 0;
    private PartieProgression partie;

    private final int VISION = 8;
    private GridPane miniGrid;
    private StackPane[][] miniCell = new StackPane[VISION][VISION];

    private int cpt = 0;
    private int cptMur = 0;
    private Label compteur;
    private Label labelCptMur;
    private int vision5;

    public LabyrintheParfaitView(Joueur joueur, int numEtape, Difficulte diff, ProgressionController progController) {
        this.joueur = joueur;
        this.numEtape = numEtape;
        this.diff = diff;
        this.progController = progController;
    }

    public void afficher(Stage stage) {
        this.stage = stage;

        chargerImages();

        initialiserLabyrinthe();

        creerGrille();

        BorderPane bp = new BorderPane();
        bp.setLeft(creerBoutons());
        bp.setTop(creerTopBar());
        bp.setCenter(creerCenterBox());
        bp.setBottom(creerPanneauBas());

        Region background = creerBackground();
        StackPane root = new StackPane(background, bp);

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/vueCss/style.css").toExternalForm());

        stage.setScene(scene);
        stage.setTitle("Labyrinthe Parfait Progression");
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
        stage.show();
        stage.centerOnScreen();
        bp.requestFocus();

        gererClavier(bp);
    }

    private void chargerImages() {
        imgJoueur = new Image(getClass().getResourceAsStream("/images/joueur.jpg"));
        imgChemin = new Image(getClass().getResourceAsStream("/images/chemin.png"));
    }

    private void initialiserLabyrinthe() {
        int largeur = (numEtape == 4) ? 8 : 25;
        int hauteur = (numEtape == 4) ? 6 : 25;
        laby = GenererLabyrinthe.genererParfait(largeur, hauteur, 0.0);
        laby.construireGrilleDepuisMurs();
        laby.ajouterObservateur(this);

        controller = new LabyrintheParfaitController(laby);
        partie = new PartieProgression(laby, joueur);
        decouvert = new boolean[laby.getLARGEUR()][laby.getHAUTEUR()];

        int[] entree = laby.getCoordEntree();
        laby.setCoordJoueur(entree[0], entree[1]);
        int[] posJoueur = laby.getCoordJoueur();
        ancienX = posJoueur[0];
        ancienY = posJoueur[1];

        double screenWidth = javafx.stage.Screen.getPrimary().getBounds().getWidth();
        double screenHeight = javafx.stage.Screen.getPrimary().getBounds().getHeight();
        taille_case = (int) Math.min(screenWidth * 0.6 / laby.getLARGEUR(), screenHeight * 0.7 / laby.getHAUTEUR());
    }

    private void creerGrille() {
        GridPane grid = new GridPane();
        casesPane = new StackPane[laby.getLARGEUR()][laby.getHAUTEUR()];

        for (int x = 0; x < laby.getLARGEUR(); x++) {
            for (int y = 0; y < laby.getHAUTEUR(); y++) {
                casesPane[x][y] = creerCase(x, y);
                grid.add(casesPane[x][y], x, y);
            }
        }

        joueurView = new ImageView(imgJoueur);
        joueurView.setFitWidth(taille_case - 4);
        joueurView.setFitHeight(taille_case - 4);
        casesPane[ancienX][ancienY].getChildren().add(joueurView);
    }

    private StackPane creerCase(int x, int y) {
        StackPane sp = new StackPane();
        sp.setPrefSize(taille_case, taille_case);

        boolean murHaut = (y == 0) || laby.murHorizontaux[y - 1][x];
        boolean murBas = (y == laby.getHAUTEUR() - 1) || laby.murHorizontaux[y][x];
        boolean murGauche = (x == 0) || laby.mursVerticaux[x - 1][y];
        boolean murDroite = (x == laby.getLARGEUR() - 1) || laby.mursVerticaux[x][y];

        sp.setBorder(new Border(new BorderStroke(Color.BROWN, BorderStrokeStyle.SOLID, CornerRadii.EMPTY,
                new BorderWidths(murHaut ? 2 : 0, murDroite ? 2 : 0, murBas ? 2 : 0, murGauche ? 2 : 0))));

        ImageView chemin = new ImageView(imgChemin);
        chemin.setFitWidth(taille_case);
        chemin.setFitHeight(taille_case);
        chemin.setOpacity(0.5);
        sp.getChildren().add(chemin);

        Region overlay = new Region();
        overlay.setPrefSize(taille_case, taille_case);

        if (x == laby.getCoordEntree()[0] && y == laby.getCoordEntree()[1])
            overlay.setBackground(new Background(new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, Insets.EMPTY)));
        else if (x == laby.getCoordSortie()[0] && y == laby.getCoordSortie()[1])
            overlay.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));

        sp.getChildren().add(overlay);
        return sp;
    }

    private VBox creerBoutons() {
        Button haut = new Button("\u2191");
        Button bas = new Button("\u2193");
        Button gauche = new Button("\u2190");
        Button droite = new Button("\u2192");

        for (Button b : new Button[]{haut, bas, gauche, droite}) {
            b.setFocusTraversable(false);
            b.setMinSize(40, 40);
            b.getStyleClass().add("button-direction");
        }

        haut.setOnAction(e -> controller.deplacerHaut());
        bas.setOnAction(e -> controller.deplacerBas());
        gauche.setOnAction(e -> controller.deplacerGauche());
        droite.setOnAction(e -> controller.deplacerDroite());

        GridPane grid = new GridPane();
        grid.add(haut, 1, 0);
        grid.add(gauche, 0, 1);
        grid.add(droite, 2, 1);
        grid.add(bas, 1, 2);
        grid.setHgap(5);
        grid.setVgap(5);
        grid.setAlignment(Pos.CENTER);

        VBox panneau = new VBox(grid);
        panneau.setPadding(new Insets(10));
        panneau.setAlignment(Pos.CENTER_LEFT);
        return panneau;
    }

    private VBox creerTopBar() {
        Button retour = new Button("Retour");
        retour.getStyleClass().add("button-principal");
        retour.setOnAction(e -> new DefiView(progController, joueur).afficher(stage, numEtape));

        VBox topBar = new VBox(retour);
        topBar.setPadding(new Insets(10));
        topBar.setAlignment(Pos.TOP_RIGHT);
        return topBar;
    }

    private VBox creerPanneauBas() {
        int nbDeplacements = CheminMinimum.plusCourtCheminParfait(laby).size() - 2;
        Label cheminMin = new Label("Labyrinthe possible en " + nbDeplacements + " mouvements.");
        compteur = new Label("Nombre de coups : " + cpt);
        labelCptMur = new Label("");

        VBox panneauBas = new VBox(5, cheminMin, compteur, labelCptMur);
        panneauBas.setPadding(new Insets(10));
        panneauBas.setAlignment(Pos.BOTTOM_CENTER);
        return panneauBas;
    }

    private HBox creerCenterBox() {
        HBox centerBox = new HBox(30, casesPaneToGrid());
        centerBox.setAlignment(Pos.CENTER);

        if (numEtape == 6) {
            int tailleMiniCase = 20;
            miniGrid = controller.creerMiniGrille(miniCell, VISION, tailleMiniCase);
            majMiniCarte();
            centerBox.getChildren().add(miniGrid);
        }

        return centerBox;
    }

    private GridPane casesPaneToGrid() {
        GridPane grid = new GridPane();
        for (int x = 0; x < laby.getLARGEUR(); x++)
            for (int y = 0; y < laby.getHAUTEUR(); y++)
                grid.add(casesPane[x][y], x, y);
        grid.setAlignment(Pos.CENTER);
        return grid;
    }

    private Region creerBackground() {
        Region background = new Region();
        background.setId(ThemeManager.getCurrentBackground());
        background.prefWidthProperty().bind(stage.widthProperty());
        background.prefHeightProperty().bind(stage.heightProperty());
        return background;
    }

    private void gererClavier(BorderPane bp) {
        bp.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case UP -> controller.deplacerHaut();
                case DOWN -> controller.deplacerBas();
                case LEFT -> controller.deplacerGauche();
                case RIGHT -> controller.deplacerDroite();
            }
        });
    }

    private void vueRestrainte() {
        int[] pos = laby.getCoordJoueur();
        int jx = pos[0];
        int jy = pos[1];

        switch (diff) {
            case FACILE -> vision5 = 8;
            case NORMAL -> vision5 = 6;
            case DIFFICILE -> vision5 = 3;
            default -> vision5 = 5;
        }
        int rayon = vision5 / 2;

        for (int x = 0; x < casesPane.length; x++) {
            for (int y = 0; y < casesPane[0].length; y++) {
                StackPane pane = casesPane[x][y];
                boolean dansVision = Math.abs(x - jx) <= rayon && Math.abs(y - jy) <= rayon;
                pane.setOpacity(dansVision ? 1.0 : 0);
            }
        }
    }

private void majMiniCarte() {
    int[] pos = laby.getCoordJoueur();
    int jx = pos[0];
    int jy = pos[1];
    int centre = VISION / 2;

    for (int i = 0; i < VISION; i++) {
        for (int j = 0; j < VISION; j++) {
            int x = jx - centre + i;  // coordonnée réelle dans le labyrinthe
            int y = jy - centre + j;

            StackPane sp = miniCell[i][j];
            Region fond = (Region) sp.getChildren().get(0);

            // Cas hors limites : fond noir et pas de bordure
            if (x < 0 || y < 0 || x >= laby.getLARGEUR() || y >= laby.getHAUTEUR()) {
                fond.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
                sp.setBorder(Border.EMPTY);
                continue;
            }

            // Fond de la case
            if (x == jx && y == jy) {
                fond.setBackground(new Background(new BackgroundFill(Color.GOLD, CornerRadii.EMPTY, Insets.EMPTY)));
            } else if (x == laby.getCoordEntree()[0] && y == laby.getCoordEntree()[1]) {
                fond.setBackground(new Background(new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, Insets.EMPTY)));
            } else if (x == laby.getCoordSortie()[0] && y == laby.getCoordSortie()[1]) {
                fond.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
            } else {
                fond.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
            }

            // Bordures pour représenter les murs fins
            boolean murHaut = (y == 0) || laby.murHorizontaux[y - 1][x];
            boolean murBas = (y == laby.getHAUTEUR() - 1) || laby.murHorizontaux[y][x];
            boolean murGauche = (x == 0) || laby.mursVerticaux[x - 1][y];
            boolean murDroite = (x == laby.getLARGEUR() - 1) || laby.mursVerticaux[x][y];

            sp.setBorder(new Border(new BorderStroke(Color.BROWN, BorderStrokeStyle.SOLID, CornerRadii.EMPTY,
                    new BorderWidths(murHaut ? 1 : 0, murDroite ? 1 : 0, murBas ? 1 : 0, murGauche ? 1 : 0))));
        }
    }
}

    private void majVueGlobal() {
        int[] pos = laby.getCoordJoueur();
            int jx = pos[0];
            int jy = pos[1];

            switch (diff) {
                case FACILE -> vision5 = 8;
                case NORMAL -> vision5 = 6;
                case DIFFICILE -> vision5 = 3;
                default -> vision5 = 5;
            }
            int rayon = vision5 / 2;

        for (int x = 0; x < laby.getLARGEUR(); x++) {
            for (int y = 0; y < laby.getHAUTEUR(); y++) {

                boolean dansVision =
                    Math.abs(x - jx) <= rayon &&
                    Math.abs(y - jy) <= rayon;

                if (dansVision) {
                    decouvert[x][y] = true;
                }
            }
        }

        // Affichage
        for (int x = 0; x < laby.getLARGEUR(); x++) {
            for (int y = 0; y < laby.getHAUTEUR(); y++) {

                StackPane pane = casesPane[x][y];

                if (decouvert[x][y]) {
                    pane.setOpacity(1);
                }

                boolean dansVision =
                    Math.abs(x - jx) <= rayon &&
                    Math.abs(y - jy) <= rayon;

                if (dansVision) {
                    // dans vision = 100%
                    pane.setOpacity(1.0);
                }
                else if (!decouvert[x][y]) {
                    // jamais vu = très sombre
                    pane.setOpacity(0);
                }
            }
        }
    }


    @Override
    public void mettreAJour(Observable o) {
        if (!(o instanceof LabyrintheParfait)) return;
        Platform.runLater(() -> {
            int[] pos = laby.getCoordJoueur();
            int x = pos[0], y = pos[1];

            if (numEtape == 5) {
                vueRestrainte();
            }

            boolean aFrappeMur = (x == ancienX && y == ancienY);
            if (!aFrappeMur) {
                ImageView cheminView = new ImageView(imgChemin);
                cheminView.setFitWidth(taille_case);
                cheminView.setFitHeight(taille_case);
                casesPane[ancienX][ancienY].getChildren().add(cheminView);

                cpt++;
            } else {
                cptMur++;
            }
            if(numEtape == 6){
                majMiniCarte();
                majVueGlobal();
            }else{
                casesPane[ancienX][ancienY].getChildren().remove(joueurView);
                casesPane[x][y].getChildren().add(joueurView);
                ancienX = x;
                ancienY = y;
            }

            compteur.setText("Nombre de coups : " + cpt);
            labelCptMur.setText(texteCptMur(cptMur));

            if (partie.terminerPartie(laby)) {
                laby.bloquerMouvement();
                joueur.getProgression().validerDefi(numEtape - 1, diff);
                Sauvegarde.majJoueurSer(joueur);
                compteur.setText("Défi terminé !");
                new FinLabyrinthe().afficherFin(new Stage(), stage, progController, joueur, numEtape);
            }
        });
    }

    private String texteCptMur(int cptMur) {
        if (cptMur >= 1005) return "T'es un détraqué toi, t'es quand même resté un total de 33,60 secondes à foncer dans un mur";
        if (cptMur >= 100) return "Bon tu sais quoi? J'abandonne, débrouilles-toi";
        if (cptMur >= 60) return "J'suis pas sûr, mais je crois c'est toujours un mur que tu tapes";
        if (cptMur >= 40) return "Après tout... pourquoi pas hein?";
        if (cptMur >= 20) return "Marron = Mur, Herbe = Chemin, Vert = Entrée, Rouge = Sortie, Joueur = toi";
        if (cptMur >= 10) return "Hum, la couleur marron sont des murs au cas où";
        if (cptMur >= 2) return "Aïe!";
        return "";
    }
}

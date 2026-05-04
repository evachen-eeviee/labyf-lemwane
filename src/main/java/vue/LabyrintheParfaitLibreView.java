package vue;

import Controleur.LabyrintheParfaitController;
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
import javafx.stage.Stage;
import modele.labyrinthe.*;
import modele.partie.Partie;
import vue.utils.ThemeManager;

public class LabyrintheParfaitLibreView implements Observateur {

    private boolean deplacementEnCours = false;
    private LabyrintheParfait laby;
    private LabyrintheParfaitController controller;
    private int ancienX, ancienY;
    private Label compteur;
    private Label labelCptMur;
    private int cpt = 0;
    private int cptMur = 0;
    private Partie partie;
    private Stage stage;
    private Image imgJoueur;
    private Image imgChemin;
    private ImageView joueurView;
    private StackPane[][] casesPane;
    private int taille_case = 40;
    private double distanceMin;
    private int nbDeplacements;

    public LabyrintheParfaitLibreView(int largeur, int hauteur, double distanceMin) {
        this.laby = GenererLabyrinthe.genererParfait(largeur, hauteur, distanceMin);
        this.distanceMin = distanceMin;
    }

    public void afficher(Stage stage) {
        this.stage = stage;
        initialiserLabyrinthe();

        BorderPane bp = new BorderPane();
        bp.setCenter(creerGrille());
        bp.setLeft(creerBoutonsDeplacement());
        bp.setTop(creerTopBar());
        bp.setBottom(creerPanneauBas());

        Region background = creerBackground();
        StackPane root = new StackPane(background, bp);

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/vueCss/style.css").toExternalForm());

        stage.setScene(scene);
        stage.setTitle("Labyrinthe Parfait");
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
        stage.show();
        stage.centerOnScreen();
        bp.requestFocus();

        gererClavier(bp);
    }

    private void initialiserLabyrinthe() {
        nbDeplacements = CheminMinimum.plusCourtCheminParfait(laby).size() - 2;
        while (nbDeplacements < distanceMin) {
            laby = GenererLabyrinthe.genererParfait(laby.getLARGEUR(), laby.getHAUTEUR(), distanceMin);
            nbDeplacements = CheminMinimum.plusCourtCheminParfait(laby).size() - 2;
        }

        imgJoueur = new Image(getClass().getResourceAsStream("/images/joueur.jpg"));
        imgChemin = new Image(getClass().getResourceAsStream("/images/chemin.png"));

        laby.ajouterObservateur(this);
        controller = new LabyrintheParfaitController(laby);
        partie = new Partie(laby);

        int[] entree = laby.getCoordEntree();
        laby.setCoordJoueur(entree[0], entree[1]);
        int[] posJoueur = laby.getCoordJoueur();
        ancienX = posJoueur[0];
        ancienY = posJoueur[1];

        double screenWidth = javafx.stage.Screen.getPrimary().getBounds().getWidth();
        double screenHeight = javafx.stage.Screen.getPrimary().getBounds().getHeight();
        taille_case = (int) Math.min(screenWidth * 0.6 / laby.getLARGEUR(),
                screenHeight * 0.7 / laby.getHAUTEUR());
    }

    private GridPane creerGrille() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        casesPane = new StackPane[laby.getLARGEUR()][laby.getHAUTEUR()];

        for (int x = 0; x < laby.getLARGEUR(); x++) {
            for (int y = 0; y < laby.getHAUTEUR(); y++) {
                casesPane[x][y] = creerCase(x, y);
                grid.add(casesPane[x][y], x, y);
            }
        }

        // Joueur
        joueurView = new ImageView(imgJoueur);
        joueurView.setFitWidth(taille_case - 4);
        joueurView.setFitHeight(taille_case - 4);
        casesPane[ancienX][ancienY].getChildren().add(joueurView);

        return grid;
    }

    private StackPane creerCase(int x, int y) {
        StackPane spCase = new StackPane();
        spCase.setPrefSize(taille_case, taille_case);
        spCase.setAlignment(Pos.CENTER);

        double epaisseur = 0.8;
        boolean murHaut = (y == 0) || laby.murHorizontaux[y - 1][x];
        boolean murBas = (y == laby.getHAUTEUR() - 1) || laby.murHorizontaux[y][x];
        boolean murGauche = (x == 0) || laby.mursVerticaux[x - 1][y];
        boolean murDroite = (x == laby.getLARGEUR() - 1) || laby.mursVerticaux[x][y];
        spCase.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY,
                new BorderWidths(murHaut ? epaisseur : 0,
                        murDroite ? epaisseur : 0,
                        murBas ? epaisseur : 0,
                        murGauche ? epaisseur : 0))));

        ImageView cheminView = new ImageView(imgChemin);
        cheminView.setFitWidth(taille_case);
        cheminView.setFitHeight(taille_case);
        cheminView.setOpacity(0.5);
        spCase.getChildren().add(cheminView);

        Region overlay = new Region();
        overlay.setPrefSize(taille_case, taille_case);

        if (x == laby.getCoordEntree()[0] && y == laby.getCoordEntree()[1]) {
            overlay.setBackground(new Background(new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, Insets.EMPTY)));
        } else if (x == laby.getCoordSortie()[0] && y == laby.getCoordSortie()[1]) {
            overlay.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
        }

        spCase.getChildren().add(overlay);
        return spCase;
    }

    private VBox creerBoutonsDeplacement() {
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
        panneau.setAlignment(Pos.CENTER);
        return panneau;
    }

    private VBox creerTopBar() {
        Button reset = new Button("Reset");
        Button accueil = new Button("Accueil");
        for (Button b : new Button[]{reset, accueil}) b.getStyleClass().add("button-principal");

        reset.setOnAction(e -> resetLabyrinthe(stage));
        accueil.setOnAction(e -> new HomeView().start(stage));

        VBox topBar = new VBox(10, reset, accueil);
        topBar.setPadding(new Insets(10));
        topBar.setAlignment(Pos.TOP_RIGHT);
        return topBar;
    }

    private VBox creerPanneauBas() {
        nbDeplacements = CheminMinimum.plusCourtCheminParfait(laby).size() - 2;
        Label cheminMin = new Label("Labyrinthe possible en " + nbDeplacements + " mouvements");
        compteur = new Label("Nombre de coups : " + cpt);
        labelCptMur = new Label("");

        VBox panneauBas = new VBox(5, cheminMin, compteur, labelCptMur);
        panneauBas.setPadding(new Insets(10));
        panneauBas.setAlignment(Pos.BOTTOM_CENTER);
        return panneauBas;
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

    private void resetLabyrinthe(Stage stage) {
        cpt = 0;
        laby = GenererLabyrinthe.genererParfait(laby.getLARGEUR(), laby.getHAUTEUR(), 0);
        afficher(stage);
    }

    @Override
    public void mettreAJour(Observable o) {
        if (!(o instanceof LabyrintheParfait)) return;

        Platform.runLater(() -> {
            deplacementEnCours = false;
            int[] pos = laby.getCoordJoueur();
            int x = pos[0];
            int y = pos[1];

            boolean aFrappeMur = (x == ancienX && y == ancienY);
            if (!aFrappeMur) {
                StackPane ancienneCase = casesPane[ancienX][ancienY];
                ImageView cheminView = new ImageView(imgChemin);
                cheminView.setFitWidth(taille_case);
                cheminView.setFitHeight(taille_case);
                // Ajout derrière les autres enfants
                ancienneCase.getChildren().add(0, cheminView);
                cpt++;
            } else {
                cptMur++;
            }

            casesPane[ancienX][ancienY].getChildren().remove(joueurView);
            casesPane[x][y].getChildren().add(joueurView);
            ancienX = x;
            ancienY = y;

            compteur.setText("Nombre de coups : " + cpt);
            labelCptMur.setText(texteCptMur(cptMur));

            if (partie.terminerPartie(laby)) {
                new FinLabyrinthe().afficherFinLibre(new Stage(), stage);
            }
        });
    }

    private String texteCptMur(int cptMur) {
        if (cptMur >= 1005) return "T'es un détraqué toi, t'es quand même resté un total de 33,60 secondes à foncer sans un mur";
        if (cptMur >= 100) return "Bon tu sais quoi? J'abandonne, débrouilles-toi";
        if (cptMur >= 60) return "J'suis pas sûr, mais je crois c'est toujours un mur que tu tapes";
        if (cptMur >= 40) return "Après tout... pourquoi pas hein?";
        if (cptMur >= 20) return "Brique = Mur, Herbe = Chemin, Vert = Entrée, Rouge = Sortie, Joueur = toi";
        if (cptMur >= 10) return "Hum, les briques marrons sont des murs au cas où";
        if (cptMur >= 2) return "Aïe!";
        return "";
    }
}

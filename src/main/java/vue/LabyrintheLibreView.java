package vue;

import Controleur.LabyrintheController;
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
import modele.labyrinthe.*;
import modele.partie.*;
import vue.utils.ThemeManager;

import java.util.List;

public class LabyrintheLibreView implements Observateur {

    private boolean labyParfait;
    private boolean deplacementEnCours = false;
    private Labyrinthe laby;
    private LabyrintheController controller;
    private int ancienX, ancienY;
    private Rectangle[][] cell;
    private Label compteur;
    private Label labelCptMur;
    private int cpt = 0;
    private int cptMur = 0;
    private Partie partie;
    private Stage stage;
    private Image imgJoueur;
    private Image imgMur;
    private Image imgChemin;
    private ImageView joueurView;
    private StackPane[][] casesPane;
    private int taille_case = 0;

    public LabyrintheLibreView(int largeur, int hauteur, double pourcentageMurs) {
        this.laby = GenererLabyrinthe.genererAlea(largeur, hauteur, pourcentageMurs);
    }

    public void afficher(Stage stage) {
        this.stage = stage;

        chargerImages();
        initialiserGrilleEtPartie();
        calculerTailleCase();

        Parcours parcours = new Parcours(laby);
        List<Case> courtChemin = parcours.plusCourtChemin();
        GridPane grid = construireCases();

        ajouterJoueur();

        // Boutons de déplacement
        GridPane boutons = creerBoutonsDeplacement();
        VBox topBar = creerTopBar(stage);
        VBox panneauGauche = new VBox(10, boutons);
        panneauGauche.setPadding(new Insets(10));
        panneauGauche.setAlignment(Pos.CENTER);

        VBox panneauBas = creerPanneauMouvements(courtChemin);

        Region background = creerBackground();

        BorderPane bp = new BorderPane();
        bp.setLeft(panneauGauche);
        bp.setCenter(grid);
        bp.setBottom(panneauBas);
        bp.setTop(topBar);
        bp.setBackground(Background.EMPTY);

        StackPane sp = new StackPane(background, bp);

        Scene scene = new Scene(sp);
        scene.getStylesheets().add(getClass().getResource("/vueCss/style.css").toExternalForm());

        stage.setScene(scene);
        stage.setTitle("Labyrinthe Libre");
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
        stage.show();
        stage.centerOnScreen();
        bp.requestFocus();

        // Gestion clavier
        gererClavier(bp);
    }

    private void chargerImages() {
        imgJoueur = new Image(getClass().getResourceAsStream("/images/joueur.jpg"));
        imgMur = new Image(getClass().getResourceAsStream("/images/mur.jpg"));
        imgChemin = new Image(getClass().getResourceAsStream("/images/chemin.png"));
    }

    private void initialiserGrilleEtPartie() {
        laby.ajouterObservateur(this);
        controller = new LabyrintheController(laby);

        int largeur = laby.getLARGEUR();
        int hauteur = laby.getHAUTEUR();

        cell = new Rectangle[largeur][hauteur];
        casesPane = new StackPane[largeur][hauteur];

        partie = new Partie(laby);

        int[] entree = laby.getCoordEntree();
        laby.setCoordJoueur(entree[0], entree[1] + 1);
        int[] posJoueur = laby.getCoordJoueur();
        this.ancienX = posJoueur[0];
        this.ancienY = posJoueur[1];
    }

    private void calculerTailleCase() {
        double screenWidth = javafx.stage.Screen.getPrimary().getBounds().getWidth();
        double screenHeight = javafx.stage.Screen.getPrimary().getBounds().getHeight();
        this.taille_case = (int) Math.min(screenWidth * 0.6 / laby.getLARGEUR(),
                screenHeight * 0.7 / laby.getHAUTEUR());
    }

    private GridPane construireCases() {
        int largeur = laby.getLARGEUR();
        int hauteur = laby.getHAUTEUR();
        GridPane grid = new GridPane();

        for (int x = 0; x < largeur; x++) {
            for (int y = 0; y < hauteur; y++) {
                StackPane spCase = new StackPane();
                Case c = laby.getGrille()[x][y];
                Rectangle rect = new Rectangle(taille_case, taille_case);

                if (c.estMur()) {
                    ImageView murView = new ImageView(imgMur);
                    murView.setFitWidth(taille_case);
                    murView.setFitHeight(taille_case);
                    spCase.getChildren().add(murView);
                } else {
                    ImageView cheminView = new ImageView(imgChemin);
                    cheminView.setOpacity(0.5);
                    cheminView.setFitWidth(taille_case);
                    cheminView.setFitHeight(taille_case);
                    spCase.getChildren().add(cheminView);

                    rect.setStroke(Color.GRAY);
                    rect.setFill(Color.TRANSPARENT);
                    spCase.getChildren().add(rect);
                    cell[x][y] = rect;
                }

                if (x == laby.getCoordEntree()[0] && y == laby.getCoordEntree()[1])
                    rect.setFill(Color.GREEN);

                if (x == laby.getCoordSortie()[0] && y == laby.getCoordSortie()[1])
                    rect.setFill(Color.RED);

                casesPane[x][y] = spCase;
                grid.add(spCase, x, y);
            }
        }
        grid.setAlignment(Pos.CENTER);
        return grid;
    }

    private void ajouterJoueur() {
        joueurView = new ImageView(imgJoueur);
        joueurView.setFitWidth(taille_case);
        joueurView.setFitHeight(taille_case);
        casesPane[ancienX][ancienY].getChildren().add(joueurView);
    }

    private GridPane creerBoutonsDeplacement() {

        Button haut = new Button("\u2191");
        Button bas = new Button("\u2193");
        Button gauche = new Button("\u2190");
        Button droite = new Button("\u2192");

        // Styles génériques
        for (Button b : new Button[]{haut, bas, gauche, droite}) {
            b.setFocusTraversable(false);
            b.setMinSize(40, 40);
            b.getStyleClass().add("button-direction");
        }

        // Actions
        haut.setOnAction(e -> controller.deplacerHaut());
        bas.setOnAction(e -> controller.deplacerBas());
        gauche.setOnAction(e -> controller.deplacerGauche());
        droite.setOnAction(e -> controller.deplacerDroite());

        // Placement dans une grille
        GridPane boutons = new GridPane();
        boutons.add(haut, 1, 0);
        boutons.add(gauche, 0, 1);
        boutons.add(droite, 2, 1);
        boutons.add(bas, 1, 2);
        boutons.setHgap(2);
        boutons.setVgap(2);
        boutons.setAlignment(Pos.CENTER);

        return boutons;
    }
    private VBox creerTopBar(Stage stage) {

        Button reset = new Button("Reset");
        reset.getStyleClass().add("button-principal");
        reset.setOnAction(e -> resetLabyrinthe(stage));

        Button accueil = new Button("Accueil");
        accueil.getStyleClass().add("button-principal");
        accueil.setOnAction(e -> new HomeView().start(stage));

        VBox topBar = new VBox(10, reset, accueil);
        topBar.setPadding(new Insets(10));
        topBar.setAlignment(Pos.TOP_RIGHT);

        return topBar;
    }

    private VBox creerPanneauMouvements(List<Case> courtChemin) {
        Label CheminMin = new Label("Labyrinthe possible en " + (courtChemin.size() - 2) + " mouvements.");
        compteur = new Label("Nombre de coups : " + cpt);
        labelCptMur = new Label("");

        VBox panneauBas = new VBox(5, CheminMin, compteur, labelCptMur);
        panneauBas.setPadding(new Insets(10));
        panneauBas.setAlignment(Pos.BOTTOM_CENTER);

        return panneauBas;
    }

    private Region creerBackground() {
        Region region = new Region();
        region.setId(ThemeManager.getCurrentBackground());
        region.prefWidthProperty().bind(stage.widthProperty());
        region.prefHeightProperty().bind(stage.heightProperty());
        return region;
    }

    private void gererClavier(BorderPane bp) {
        bp.setOnKeyPressed(event -> {
            if (deplacementEnCours) return;
            deplacementEnCours = true;
            switch (event.getCode()) {
                case UP -> controller.deplacerHaut();
                case DOWN -> controller.deplacerBas();
                case LEFT -> controller.deplacerGauche();
                case RIGHT -> controller.deplacerDroite();
                default -> {}
            }
        });
    }

    private void resetLabyrinthe(Stage stage) {
        cpt = 0;
        cptMur = 0;
        this.laby = GenererLabyrinthe.genererAlea(laby.getLARGEUR(), laby.getHAUTEUR(),0.3);
        afficher(stage);
    }


    @Override
    public void mettreAJour(Observable o) {
        if (!(o instanceof Labyrinthe)) return;

        Platform.runLater(() -> {
            deplacementEnCours = false;
            int[] pos = laby.getCoordJoueur();
            int x = pos[0];
            int y = pos[1];

            boolean aFrappeMur = (x == ancienX && y == ancienY);
            if (!aFrappeMur) {
                Rectangle rect = cell[ancienX][ancienY];
                if (rect != null) {
                    ImageView cheminView = new ImageView(imgChemin);
                    cheminView.setFitWidth(taille_case);
                    cheminView.setFitHeight(taille_case);
                    casesPane[ancienX][ancienY].getChildren().add(cheminView);
                }
                cpt++;
            } else {
                cptMur++;
            }

            // Déplace le joueur
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
        if (cptMur >= 20) return "Brique = Mur, Herbe = Chemin, Vert = Entrée, Rouge = Sortie, Champignon = toi, joueur";
        if (cptMur >= 10) return "Hum, les briques marrons sont des murs au cas où";
        if (cptMur >= 2) return "Aïe!";
        return "";
    }
}

package vue;

import Controleur.LabyrintheController;
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

import java.util.List;

public class LabyrintheView implements Observateur{
    private Labyrinthe laby;
    private LabyrintheController controller;
    private int ancienX, ancienY;
    private boolean[][] decouvert;
    private Rectangle[][] cell;
    private Joueur joueur;
    private int cpt = 0;
    GridPane miniGrid;
    private Label compteur;
    private PartieProgression partie;
    private final int VISION = 8;
    private int vision5;
    private Rectangle[][] miniCell;
    private int numEtape;
    private Difficulte diff;
    private int cptMur=0;
    private Label labelCptMur;
    private Controleur.ProgressionController progController;
    private Stage stage;
    private Image imgJoueur;
    private Image imgMur;
    private Image imgChemin;
    private ImageView joueurView;
    private StackPane[][] casesPane;
    private int taille_case = 0;

    public LabyrintheView(Joueur joueur, int numEtape, Difficulte diff, ProgressionController progController){
        this.joueur = joueur;
        this.numEtape = numEtape;
        this.diff = diff;
        this.progController = progController;
        this.miniCell = new Rectangle[VISION][VISION];
    }

    public void afficherLaby(Stage stage) {
        this.stage = stage;

        chargerImages();
        initialiserLabyrinthe();
        calculerTailleCase();

        GridPane grid = construireGrille();
        ajouterJoueur();
        initialiserMiniGrid();

        GridPane boutons = creerBoutonsDeplacement();
        VBox infos = creerInfosJoueur();
        VBox panneauGauche = new VBox(10, infos, boutons);
        panneauGauche.setPadding(new Insets(10));
        panneauGauche.setAlignment(Pos.CENTER);

        VBox panneauBas = creerPanneauMouvements(new Parcours(laby).plusCourtChemin());
        Region background = creerBackground();

        BorderPane bp = new BorderPane();
        bp.setLeft(panneauGauche);
        bp.setCenter(grid);
        if(miniGrid != null) bp.setRight(miniGrid);
        bp.setBottom(panneauBas);
        bp.setTop(creerBoutonRetour());
        bp.setBackground(Background.EMPTY);

        StackPane sp = new StackPane(background,bp);
        Scene scene = new Scene(sp);

        scene.getStylesheets().add(getClass().getResource("/vueCss/style.css").toExternalForm());

        stage.setScene(scene);
        stage.setTitle("Labyrinthe Progression");
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
        stage.show();
        stage.centerOnScreen();
        bp.requestFocus();

        gererClavier(bp);
    }

    private void chargerImages() {
        imgJoueur = new Image(getClass().getResourceAsStream("/images/joueur.jpg"));
        imgMur = new Image(getClass().getResourceAsStream("/images/mur.jpg"));
        imgChemin = new Image(getClass().getResourceAsStream("/images/chemin.png"));
    }

    private void initialiserLabyrinthe() {
        laby = genererLabyrintheSelonEtape();
        controller = new LabyrintheController(laby);
        decouvert = new boolean[laby.getLARGEUR()][laby.getHAUTEUR()];
        cell = new Rectangle[laby.getLARGEUR()][laby.getHAUTEUR()];
        casesPane = new StackPane[laby.getLARGEUR()][laby.getHAUTEUR()];
        partie = new PartieProgression(laby, joueur);
        laby.ajouterObservateur(this);

        int[] entree = laby.getCoordEntree();
        laby.setCoordJoueur(entree[0], entree[1] + 1);
        int[] pos = laby.getCoordJoueur();
        ancienX = pos[0];
        ancienY = pos[1];
    }

    private Labyrinthe genererLabyrintheSelonEtape() {
        if(numEtape == 1) return GenererLabyrinthe.genererAlea(30, 30, diff.getPourcentage());
        if(numEtape == 4) return GenererLabyrinthe.genererAlea(8, 6, VISION);
        if(numEtape == 5 || numEtape == 6) return GenererLabyrinthe.genererAlea(25, 25, VISION);

        double pourc = diff.getPourcentage();
        if(pourc == 0.3) return GenererLabyrinthe.genererAlea(40, 40, pourc);
        if(pourc == 0.4) return GenererLabyrinthe.genererAlea(50, 50, pourc);
        return GenererLabyrinthe.genererAlea(55, 55, pourc);
    }

    private void calculerTailleCase() {
        double screenWidth = javafx.stage.Screen.getPrimary().getBounds().getWidth();
        double screenHeight = javafx.stage.Screen.getPrimary().getBounds().getHeight();
        taille_case = (int) Math.min(screenWidth * 0.6 / laby.getLARGEUR(),
                screenHeight * 0.7 / laby.getHAUTEUR());
    }

    private GridPane construireGrille() {
        int largeur = laby.getLARGEUR();
        int hauteur = laby.getHAUTEUR();
        GridPane grid = new GridPane();

        for (int x = 0; x < largeur; x++) {
            for (int y = 0; y < hauteur; y++) {
                StackPane spCase = new StackPane();
                Case c = laby.getGrille()[x][y];

                Rectangle rect = new Rectangle(taille_case, taille_case);
                rect.setStroke(Color.GRAY);
                rect.setFill(Color.TRANSPARENT);
                cell[x][y] = rect;

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
                }

                spCase.getChildren().add(rect);

                if (x == laby.getCoordEntree()[0] && y == laby.getCoordEntree()[1]) {
                    rect.setFill(Color.GREEN);
                }
                if (x == laby.getCoordSortie()[0] && y == laby.getCoordSortie()[1]) {
                    rect.setFill(Color.RED);
                }

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

        for(Button b : new Button[]{haut, bas, gauche, droite}) {
            b.setFocusTraversable(false);
            b.setMinSize(40,40);
            b.getStyleClass().add("button-direction");
        }

        haut.setOnAction(e -> controller.deplacerHaut());
        bas.setOnAction(e -> controller.deplacerBas());
        gauche.setOnAction(e -> controller.deplacerGauche());
        droite.setOnAction(e -> controller.deplacerDroite());

        GridPane boutons = new GridPane();
        boutons.add(haut,1,0);
        boutons.add(gauche,0,1);
        boutons.add(droite,2,1);
        boutons.add(bas,1,2);
        boutons.setHgap(2);
        boutons.setVgap(2);
        boutons.setAlignment(Pos.CENTER);
        return boutons;
    }

    private VBox creerInfosJoueur() {
        Label nom = new Label("Joueur : " + joueur.getNom());
        Label etape = new Label("Etape : " + joueur.getProgression().getIntEtapeActuelle());
        Label defi = new Label("Defi : ");
        VBox infos = new VBox(5, nom, etape, defi);
        infos.setPadding(new Insets(10));
        infos.setAlignment(Pos.TOP_CENTER);
        return infos;
    }

    private VBox creerPanneauMouvements(List<Case> courtChemin) {
        Label cheminMin = new Label("Labyrinthe possible en " + (courtChemin.size()-2) + " mouvements");
        compteur = new Label("Nombre de coups : " + cpt);
        labelCptMur = new Label(".");
        VBox panneau = new VBox(5, cheminMin, compteur, labelCptMur);
        panneau.setPadding(new Insets(10));
        panneau.setAlignment(Pos.BOTTOM_CENTER);
        return panneau;
    }

    private void initialiserMiniGrid() {
        if(numEtape == 3 || numEtape == 6) {
            miniGrid = controller.creerMiniGrille(miniCell, VISION);
        }
    }

    private Region creerBackground() {
        Region region = new Region();
        region.setId(ThemeManager.getCurrentBackground());
        region.prefWidthProperty().bind(stage.widthProperty());
        region.prefHeightProperty().bind(stage.heightProperty());
        return region;
    }

    private HBox creerBoutonRetour() {
        Button retour = new Button("Retour");
        retour.getStyleClass().add("button-principal");
        retour.setOnAction(e -> new DefiView(progController, joueur).afficher(stage, numEtape));

        HBox container = new HBox(retour);
        container.setAlignment(Pos.TOP_RIGHT);   // positionne le bouton en haut à droite
        container.setPadding(new Insets(10));    // petit padding pour pas coller au bord
        return container;
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

    private void majMiniCarte() {
        Color[][] vision = controller.visionZoom();
        for (int i = 0; i < VISION; i++) {
            for (int j = 0; j < VISION; j++) {
                miniCell[i][j].setFill(vision[i][j]);
            }
        }
    }

    public void vueRestrainte(){
        int[] pos = laby.getCoordJoueur();
        int jx = pos[0];
        int jy = pos[1];

        switch (diff) {
        case FACILE -> this.vision5 = 8;
        case NORMAL -> this.vision5 = 6;
        case DIFFICILE -> this.vision5 = 3;
        default -> this.vision5 = 5;
        }
        int rayon = vision5 / 2;

        for (int x = 0; x < cell.length; x++) {
            for (int y = 0; y < cell[0].length; y++) {
                StackPane pane = casesPane[x][y];
                boolean dansVision = Math.abs(x - jx) <= rayon && Math.abs(y - jy) <= rayon;
                if (dansVision) {
                    pane.setOpacity(1.0);
                } else {
                    pane.setOpacity(0.15);  // beaucoup plus visible
                }

            }
        }
    }

    private void majVueGlobal() {
        int[] pos = laby.getCoordJoueur();
        int jx = pos[0];
        int jy = pos[1];

        int rayon;
        switch (diff) {
            case FACILE -> rayon = 7;
            case NORMAL -> rayon = 4;
            case DIFFICILE -> rayon = 3;
            default -> rayon = 5;
        }

        // Marquer la zone visible autour du joueur
        for (int x = Math.max(0, jx - rayon); x <= Math.min(laby.getLARGEUR() - 1, jx + rayon); x++) {
            for (int y = Math.max(0, jy - rayon); y <= Math.min(laby.getHAUTEUR() - 1, jy + rayon); y++) {
                decouvert[x][y] = true;
            }
        }

        // Mettre à jour l'affichage sur la grande grille
        for (int x = 0; x < laby.getLARGEUR(); x++) {
            for (int y = 0; y < laby.getHAUTEUR(); y++) {
                StackPane pane = casesPane[x][y];
                if (pane == null) continue; // murs ou images ignorées
                if (!decouvert[x][y]) {
                    pane.setOpacity(0.03);                  // presque invisible
                } else {
                    pane.setOpacity(1.0);                   // visible
                    // Réaffiche sortie correctement
                    if (x == laby.getCoordSortie()[0] && y == laby.getCoordSortie()[1]) {
                    }
                    // Réaffiche entrée
                    if (x == laby.getCoordEntree()[0] && y == laby.getCoordEntree()[1]) {
                    }
                }
            }
        }
    }


    @Override
    public void mettreAJour(Observable o) {
        if (!(o instanceof Labyrinthe)) return;

        int[] pos = laby.getCoordJoueur();
        int x = pos[0];
        int y = pos[1];

        Platform.runLater(() -> {
            // --- Étape 5 : vue restreinte ---
            if (numEtape == 5) {
                vueRestrainte();
                casesPane[ancienX][ancienY].getChildren().remove(joueurView);
                casesPane[x][y].getChildren().add(joueurView);
                ancienX = x;
                ancienY = y;
            }
            // --- Étapes 3 et 6 : mini-carte uniquement ---
            else if (numEtape == 6) {
                majMiniCarte();
                majVueGlobal();   // applique le masque sur la grande grille
            }
            else if (numEtape == 3) {
                majMiniCarte();
            }

            // --- Autres étapes : vue normale avec joueur qui bouge ---
            else {
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

                // Déplacement joueur uniquement si étape normale
                casesPane[ancienX][ancienY].getChildren().remove(joueurView);
                casesPane[x][y].getChildren().add(joueurView);
                ancienX = x;
                ancienY = y;

                compteur.setText("Nombre de coups : " + cpt);
                labelCptMur.setText(texteCptMur(cptMur));
            }
        });

        // Fin de partie
        if (partie.terminerPartie(laby, joueur.getProgression(), diff)) {
            laby.bloquerMouvement();
            joueur.getProgression().validerDefi(numEtape - 1, diff);
            Sauvegarde.majJoueurSer(joueur);
            compteur.setText("Défi terminé !");
            if (partie.terminerPartie(laby)) {
                new FinLabyrinthe().afficherFin(new Stage(), stage, progController, joueur, numEtape);
            }
        }
    }

    private String texteCptMur(int cptMur) {
        if (cptMur >= 1005) return "T'es un détraqué toi, t'es quand même resté un total de 33,60 secondes à foncer sans un mur";
        if (cptMur >= 100) return "Bon tu sais quoi? J'abandonne, débrouilles-toi";
        if (cptMur >= 60) return "J'suis pas sûr, mais je crois c'est toujours un mur que tu tapes";
        if (cptMur >= 40) return "Après tout... pourquoi pas hein?";
        if (cptMur >= 20) return "Briques = Mur, Herbe = Chemin, Vert = Entrée, Rouge = Sortie, Champignon = toi, joueur";
        if (cptMur >= 10) return "Hum, les briques sont des murs au cas où";
        if (cptMur >= 2) return "Aïe!";
        return "";
    }
}
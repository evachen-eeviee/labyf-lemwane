package vue;

import Controleur.ProgressionController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import modele.joueur.Joueur;
import vue.utils.ThemeManager;

public class ProgressionView {

    private static ProgressionController controller;
    private static Joueur joueur;

    public ProgressionView(ProgressionController controller, Joueur joueur) {
        this.controller = controller;
        this.joueur = joueur;
    }
    public void affiherProg(Stage stage){
        Label titre = new Label("Votre progression");
        titre.getStyleClass().add("label-section");

        VBox etapesBox = new VBox(10);
        etapesBox.setAlignment(Pos.CENTER);
        etapesBox.getChildren().add(titre);

        boolean[] etapesValidees = controller.getEtapesValidees();
        int nombreEtapes = 6; // si tu veux rendre dynamique : joueur.getProgression().getNombreEtapes()

        for (int i = 0; i < nombreEtapes; i++) {
            int numEtape = i + 1;
            Button btnEtape = creerBoutonEtape(numEtape, i < etapesValidees.length && etapesValidees[i],
                    joueur.getProgression().estEtapeDisponible(numEtape), stage);
            etapesBox.getChildren().add(btnEtape);
        }

        Button accueil = new Button("Accueil");
        accueil.getStyleClass().add("button-principal");
        accueil.setOnAction(e -> new HomeView().start(stage));
        VBox vbAccueil = new VBox(accueil);
        vbAccueil.setAlignment(Pos.CENTER);
        vbAccueil.setPadding(new Insets(20));

        Region background = new Region();
        background.setId(ThemeManager.getCurrentBackground());
        background.prefWidthProperty().bind(stage.widthProperty());
        background.prefHeightProperty().bind(stage.heightProperty());

        BorderPane bp = new BorderPane();
        bp.setCenter(etapesBox);
        bp.setBottom(vbAccueil);
        bp.setBackground(Background.EMPTY);

        StackPane root = new StackPane(background, bp);
        Scene scene = new Scene(root, 600, 600);
        scene.getStylesheets().add(getClass().getResource("/vueCss/style.css").toExternalForm());

        stage.setScene(scene);
        stage.setTitle("Progression");
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
        stage.show();
    }

    private Button creerBoutonEtape(int numEtape, boolean validee, boolean dispo, Stage stage) {
        Button btn = new Button("Etape " + numEtape);
        btn.setMinWidth(200);
        btn.setMinHeight(60);

        if (validee) {
            btn.getStyleClass().add("button-vert");
        } else if (!dispo) {
            btn.getStyleClass().add("button-rouge");
            btn.setDisable(true);
            btn.setOpacity(0.5);
        } else {
            btn.getStyleClass().add("button-rouge");
        }

        btn.setOnAction(e -> new DefiView(controller, joueur).afficher(stage, numEtape));
        return btn;
    }
}

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
import modele.labyrinthe.Difficulte;
import vue.utils.ThemeManager;

public class DefiView {

    private ProgressionController progcontroller;
    private Joueur joueur;

    public DefiView(ProgressionController controller, Joueur joueur) {
        this.progcontroller = controller;
        this.joueur = joueur;
    }

    public void afficher(Stage stage, int numEtape) {

        Button retour = new Button("Retour");
        retour.getStyleClass().add("button-principal");
        retour.setOnAction(e -> new ProgressionView(progcontroller,joueur).affiherProg(stage));

        VBox vb = new VBox(20);
        vb.setAlignment(Pos.CENTER);

        Label titre = new Label("Défis de l'étape " + numEtape);
        titre.getStyleClass().add("label-section");
        vb.getChildren().add(titre);

        // Pour chaque difficulté, créer un bouton
        for (Difficulte diff : Difficulte.values()) {
            boolean termine = progcontroller.getValidationDefi(numEtape - 1, diff);
            Button btnDefi = new Button(diff.toString() + (termine ? " ✅" : ""));
            btnDefi.setMinWidth(200);
            btnDefi.setMinHeight(50);
            if (termine) btnDefi.getStyleClass().add("button-vert");
            else btnDefi.getStyleClass().add("button-rouge");

            // Action sur clic
            btnDefi.setOnAction(e -> {
                if(numEtape < 4) new LabyrintheView(joueur, numEtape, diff,progcontroller).afficherLaby(stage);
                else new LabyrintheParfaitView(joueur, numEtape, diff,progcontroller).afficher(stage);
            });

            vb.getChildren().add(btnDefi);
        }

        VBox vbRetour = new VBox(retour);
        vbRetour.setAlignment(Pos.CENTER);
        vbRetour.setPadding(new Insets(20, 40, 40, 20));

        Region region = new Region();
        region.setId(ThemeManager.getCurrentBackground());
        region.prefWidthProperty().bind(stage.widthProperty());
        region.prefHeightProperty().bind(stage.heightProperty());


        BorderPane bp = new BorderPane();
        bp.setCenter(vb);
        bp.setBottom(vbRetour);
        bp.setBackground(Background.EMPTY);

        StackPane sp = new StackPane();
        sp.getChildren().addAll(region,bp);

        Scene scene = new Scene(sp, 600, 400);

        scene.getStylesheets().add(getClass().getResource("/vueCss/style.css").toExternalForm());


        stage.setScene(scene);
        stage.setTitle("Défis de l'étape " + numEtape);
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
        stage.show();
    }
}

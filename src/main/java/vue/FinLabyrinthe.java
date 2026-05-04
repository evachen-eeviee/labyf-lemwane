package vue;

import Controleur.ProgressionController;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import modele.joueur.Joueur;

public class FinLabyrinthe {

    public void afficherFin(Stage stage,Stage ancienneStage,ProgressionController progController, Joueur joueur, int numEtape) {

        Label finJeu = new Label("Bravo !");
        finJeu.getStyleClass().add("label-fin");

        Button passer = new Button("Passer");
        passer.getStyleClass().add("button-principal");

        passer.setOnAction(e -> {
            stage.close();
            new DefiView(progController,joueur).afficher(ancienneStage,numEtape);
        });

        VBox contenu = new VBox(40,finJeu,passer);
        contenu.setAlignment(Pos.CENTER);

        StackPane sp = new StackPane(contenu);
        sp.setId("overlay");

        Scene scene = new Scene(sp,500,300, Color.TRANSPARENT);

        scene.getStylesheets().add(getClass().getResource("/vueCss/style.css").toExternalForm());

        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(scene);
        stage.setTitle("");
        stage.show();
    }

    public void afficherFinLibre(Stage stage, Stage ancienneStage) {

        Label finJeu = new Label("Bravo !");
        finJeu.getStyleClass().add("label-fin");

        Button passer = new Button("Passer");
        passer.getStyleClass().add("button-principal");

        passer.setOnAction(e -> {
            stage.close();
            new ChoixLibre().start(ancienneStage);
        });

        VBox contenu = new VBox(40,finJeu,passer);
        contenu.setAlignment(Pos.CENTER);

        StackPane sp = new StackPane(contenu);
        sp.setId("overlay");

        Scene scene = new Scene(sp,500,300, Color.TRANSPARENT);

        scene.getStylesheets().add(getClass().getResource("/vueCss/style.css").toExternalForm());

        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(scene);
        stage.setTitle("");
        stage.show();
    }
}
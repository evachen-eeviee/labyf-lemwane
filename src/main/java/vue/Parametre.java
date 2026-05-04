package vue;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import vue.utils.ThemeManager;


public class Parametre {

    public void afficher(Stage stage) {

        // === Choix du background ===
        ComboBox<String> choixBackground = new ComboBox<>();
        choixBackground.getItems().addAll(
                "Fond bleu",
                "Fond rouge",
                "Fond vert",
                "Fond violet",
                "Fond jaune",
                "Fond sunrise",
                "Fond gamer",
                "Fond nature",
                "Fond ambiance chaude"
        );
        choixBackground.getStyleClass().add("combo-theme");
        choixBackground.setPromptText("Choisir un fond");


        // === Boutons ===
        Button retour = new Button("Retour");
        retour.getStyleClass().add("button-principal");

        // Partie du haut ou du centre (au choix)
        VBox centre = new VBox(20, choixBackground);
        centre.setAlignment(Pos.CENTER);

        HBox bas = new HBox(retour);
        bas.setAlignment(Pos.BOTTOM_RIGHT);
        bas.setPadding(new Insets(20));

        // === Region servant de background ===
        Region region = new Region();
        region.setId(ThemeManager.getCurrentBackground());
        region.prefWidthProperty().bind(stage.widthProperty());
        region.prefHeightProperty().bind(stage.heightProperty());

        // === Changement de background au choix ===
        choixBackground.setOnAction(e -> {
            switch (choixBackground.getValue()) {
                case "Fond bleu":
                    region.setId("background-bleu");
                    ThemeManager.setCurrentBackground("background-bleu");
                    break;
                case "Fond rouge":
                    region.setId("background-rouge");
                    ThemeManager.setCurrentBackground("background-rouge");
                    break;
                case "Fond vert":
                    region.setId("background-vert");
                    ThemeManager.setCurrentBackground("background-vert");
                    break;
                case "Fond violet":
                    region.setId("background-violet");
                    ThemeManager.setCurrentBackground("background-violet");
                    break;
                case "Fond jaune":
                    region.setId("background-jaune");
                    ThemeManager.setCurrentBackground("background-jaune");
                    break;
                case "Fond sunrise":
                    region.setId("background-sunrise");
                    ThemeManager.setCurrentBackground("background-sunrise");
                    break;
                case "Fond gamer":
                    region.setId("background-gamer");
                    ThemeManager.setCurrentBackground("background-gamer");
                    break;
                case "Fond nature":
                    region.setId("background-nature");
                    ThemeManager.setCurrentBackground("background-nature");
                    break;
                case "Fond ambiance chaude":
                    region.setId("background-chaude");
                    ThemeManager.setCurrentBackground("background-chaude");
                    break;
            }
        });


        retour.setOnAction(e -> new HomeView().start(stage));

        BorderPane bp = new BorderPane();
        bp.setCenter(centre);
        bp.setBottom(bas);

        StackPane sp = new StackPane(region, bp);

        Scene scene = new Scene(sp, 600, 400);
        scene.getStylesheets().add(
                getClass().getResource("/vueCss/style.css").toExternalForm()
        );

        stage.setScene(scene);
        stage.setTitle("Paramètres");
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
        stage.show();
    }
}

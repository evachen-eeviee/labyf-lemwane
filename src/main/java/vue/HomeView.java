package vue;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import vue.utils.ThemeManager;


public class HomeView extends Application {

    @Override
    public void start(Stage stage) {
        // Titre du jeu
        Label nomJeu = new Label("LABYFÉLEMWANE");
        nomJeu.getStyleClass().add("label-titre");

        // Boutons
        Button chargerProfil = new Button("Lancer une partie Progression");
        Button partieLibre = new Button("Lancer une partie libre");
        Button parametre = new Button("Paramètre");

        chargerProfil.getStyleClass().add("button-principal");
        partieLibre.getStyleClass().add("button-principal");
        parametre.getStyleClass().add("button-principal");

        // Actions
        partieLibre.setOnAction(e -> new ChoixLibre().start(stage));
        chargerProfil.setOnAction(e -> new ChoixProfil().start(stage));
        parametre.setOnAction(e -> new Parametre().afficher(stage));

        VBox boutons = new VBox(20, chargerProfil, partieLibre, parametre);
        boutons.setAlignment(Pos.CENTER);
        boutons.setPadding(new Insets(50));

        // Dégradé en fond
        Region region = new Region();
        region.setId(ThemeManager.getCurrentBackground());
        region.prefWidthProperty().bind(stage.widthProperty());
        region.prefHeightProperty().bind(stage.heightProperty());

        // Image
        Image image = new Image(getClass().getResourceAsStream("/images/Labyfelemwane.PNG"));
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.fitWidthProperty().bind(stage.widthProperty());
        imageView.fitHeightProperty().bind(stage.heightProperty());

        // BorderPane avec titre et boutons
        BorderPane bp = new BorderPane();
        bp.setTop(nomJeu);
        BorderPane.setAlignment(nomJeu, Pos.CENTER);
        bp.setBottom(boutons);
        bp.setBackground(Background.EMPTY);

        // StackPane principal
        StackPane sp = new StackPane();
        sp.getChildren().addAll(region, imageView, bp);

        // Scène
        Scene scene = new Scene(sp, 800, 600);

        // Chargement du CSS
        scene.getStylesheets().add(getClass().getResource("/vueCss/style.css").toExternalForm());

        stage.setScene(scene);
        stage.setTitle("Accueil");
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
        stage.show();
    }
}

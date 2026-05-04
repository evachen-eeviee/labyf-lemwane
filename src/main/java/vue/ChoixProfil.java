package vue;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import vue.utils.ThemeManager;

public class ChoixProfil extends Application {

    @Override
    public void start(Stage stage) {


        // === Boutons ===
        Button annuler = new Button("Annuler");
        annuler.getStyleClass().add("button-principal");

        Button chargerProfil = new Button("Charger Profil");
        chargerProfil.getStyleClass().add("button-principal");

        Button creerProfil = new Button("Créer Profil");
        creerProfil.getStyleClass().add("button-principal");

        creerProfil.setOnAction(e -> new CreerJoueur().start(stage));
        chargerProfil.setOnAction(e -> new ChargerProfil().start(stage));

        annuler.setOnAction(e -> new HomeView().start(stage));

        VBox boutons = new VBox(20, creerProfil, chargerProfil);
        boutons.setAlignment(Pos.CENTER);
        boutons.setPadding(new Insets(50));

        HBox aBoutons = new HBox(annuler);
        aBoutons.setAlignment(Pos.BOTTOM_RIGHT);
        aBoutons.setPadding(new Insets(20));

        Region region = new Region();
        region.setId(ThemeManager.getCurrentBackground());
        region.prefWidthProperty().bind(stage.widthProperty());
        region.prefHeightProperty().bind(stage.heightProperty());

        BorderPane bp = new BorderPane();
        bp.setCenter(boutons);
        bp.setBottom(aBoutons);

        StackPane sp = new StackPane(region, bp);

        Scene scene = new Scene(sp, 600, 400);
        scene.getStylesheets().add(getClass().getResource("/vueCss/style.css").toExternalForm());

        stage.setScene(scene);
        stage.setTitle("Choix Profil");
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
        stage.show();
    }
}

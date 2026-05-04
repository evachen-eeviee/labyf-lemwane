package vue;

import Controleur.ProgressionController;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import modele.joueur.Joueur;
import modele.joueur.Sauvegarde;
import vue.utils.ThemeManager;

public class CreerJoueur extends Application {
    public void start(Stage stage) {

        Label chargeProfil = new Label("Creer votre profil");
        chargeProfil.getStyleClass().add("titre-section");

        Label labelNom = new Label("Nom du joueur");
        labelNom.getStyleClass().add("label-section");

        Label labelMdp = new Label("Mot de passe");
        labelMdp.getStyleClass().add("label-section");

        TextField champNom = new TextField();
        champNom.getStyleClass().add("text-field");
        champNom.setPromptText("Entrez votre nom");

        TextField champMdp = new TextField();
        champMdp.getStyleClass().add("text-field");
        champMdp.setPromptText("Entrez votre mot de passe");

        HBox ligneNom = new HBox(10, labelNom, champNom);
        ligneNom.setAlignment(Pos.CENTER);

        HBox ligneMdp = new HBox(10, labelMdp, champMdp);
        ligneMdp.setAlignment(Pos.CENTER);

        VBox contenu = new VBox(20, chargeProfil, ligneNom, ligneMdp);
        contenu.setAlignment(Pos.CENTER);

        Button annuler = new Button("Annuler");
        annuler.getStyleClass().add("button-principal");

        Button valider = new Button("Valider");
        valider.getStyleClass().add("button-principal");

        valider.setOnAction(e -> {
            String nom = champNom.getText();
            String mdp = champMdp.getText();
            Joueur j = new Joueur(nom,mdp);
            Sauvegarde.majJoueurSer(j);
            ProgressionController progController = new ProgressionController(j.getProgression());
            new ProgressionView(progController,j).affiherProg(stage);
        });

        annuler.setOnAction(e ->new HomeView().start(stage));

        HBox boutons = new HBox(10, valider, annuler);
        boutons.setAlignment(Pos.BOTTOM_RIGHT);
        boutons.setPadding(new Insets(20));

        Region region = new Region();
        region.setId(ThemeManager.getCurrentBackground());
        region.prefWidthProperty().bind(stage.widthProperty());
        region.prefHeightProperty().bind(stage.heightProperty());

        BorderPane bp = new BorderPane();
        bp.setCenter(contenu);
        bp.setBottom(boutons);
        BorderPane.setAlignment(boutons, Pos.BOTTOM_RIGHT);
        bp.setBackground(Background.EMPTY);

        StackPane sp = new StackPane();
        sp.getChildren().addAll(region,bp);

        Scene scene = new Scene(sp, 600, 400);
        scene.getStylesheets().add(getClass().getResource("/vueCss/style.css").toExternalForm());


        stage.setScene(scene);
        stage.setTitle("Creer Joueur");
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
        stage.show();
    }
}

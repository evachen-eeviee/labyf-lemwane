package vue;

import Controleur.ChargerProfilController;
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

import vue.utils.ThemeManager;

public class ChargerProfil extends Application {

    private Stage stage;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        Scene scene = construireScene();
        stage.setScene(scene);
        stage.setTitle("Charger le profil");
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
        stage.show();
    }

    private Scene construireScene() {
        TextField champNom = new TextField();
        TextField champMdp = new TextField();

        VBox contenu = creerFormulaire(champNom, champMdp);
        HBox boutons = creerBoutons(champNom, champMdp);

        Region background = creerBackground();

        BorderPane bp = new BorderPane();
        bp.setCenter(contenu);
        bp.setBottom(boutons);
        BorderPane.setAlignment(boutons, Pos.BOTTOM_RIGHT);
        bp.setBackground(Background.EMPTY);

        StackPane root = new StackPane(background, bp);
        Scene scene = new Scene(root, 600, 400);
        scene.getStylesheets().add(getClass().getResource("/vueCss/style.css").toExternalForm());
        return scene;
    }

    private VBox creerFormulaire(TextField champNom, TextField champMdp) {
        Label titre = new Label("Charger votre profil");
        titre.getStyleClass().add("titre-section");

        Label labelNom = new Label("Nom du joueur");
        labelNom.getStyleClass().add("label-section");

        champNom.setPromptText("Entrez votre nom");
        champNom.getStyleClass().add("text-field");

        Label labelMdp = new Label("Mot de passe");
        labelMdp.getStyleClass().add("label-section");

        champMdp.setPromptText("Entrez votre mot de passe");
        champMdp.getStyleClass().add("text-field");

        HBox ligneNom = new HBox(10, labelNom, champNom);
        ligneNom.setAlignment(Pos.CENTER);

        HBox ligneMdp = new HBox(10, labelMdp, champMdp);
        ligneMdp.setAlignment(Pos.CENTER);

        VBox formulaire = new VBox(20, titre, ligneNom, ligneMdp);
        formulaire.setAlignment(Pos.CENTER);
        formulaire.setPadding(new Insets(20));

        return formulaire;
    }

    private HBox creerBoutons(TextField champNom, TextField champMdp) {
        Button valider = new Button("Valider");
        Button annuler = new Button("Annuler");

        for (Button b : new Button[]{valider, annuler}) {
            b.getStyleClass().add("button-principal");
            b.setMinWidth(100);
        }
        valider.setOnAction(e -> {
            String nom = champNom.getText().trim();
            String mdp = champMdp.getText().trim();
            ChargerProfilController controller = new ChargerProfilController();
            Joueur joueur = controller.chargerProfil(nom, mdp);

            if (joueur != null) {
                ProgressionController progController = new ProgressionController(joueur.getProgression());
                new ProgressionView(progController, joueur).affiherProg(stage);
            } else {
                System.out.println("Nom ou mot de passe incorrect.");
            }
        });

        annuler.setOnAction(e -> new HomeView().start(stage));

        HBox boutons = new HBox(10, valider, annuler);
        boutons.setAlignment(Pos.BOTTOM_RIGHT);
        boutons.setPadding(new Insets(20));

        return boutons;
    }

    private Region creerBackground() {
        Region background = new Region();
        background.setId(ThemeManager.getCurrentBackground());
        background.prefWidthProperty().bind(stage.widthProperty());
        background.prefHeightProperty().bind(stage.heightProperty());
        return background;
    }
}

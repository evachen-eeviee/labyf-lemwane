package vue;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import vue.utils.ThemeManager;

public class ChoixLibre extends Application {

    private TextField champLargeur;
    private TextField champHauteur;
    private TextField champPourcentage;
    private TextField champMinimum;
    private Label labelTaille;

    private LabyrintheLibreView lbl;
    private LabyrintheParfaitLibreView lpv;
    @Override
    public void start(Stage stage){
        RadioButton parfait = new RadioButton("Parfait");
        RadioButton aleatoire = new RadioButton("Aléatoire");
        parfait.getStyleClass().add("label-section");
        aleatoire.getStyleClass().add("label-section");

        ToggleGroup toggleGroup = new ToggleGroup();
        parfait.setToggleGroup(toggleGroup);
        aleatoire.setToggleGroup(toggleGroup);

        HBox choixType = new HBox(20, parfait, aleatoire);
        choixType.setAlignment(Pos.CENTER);

        // Formulaire
        GridPane formulaire = creerFormulaire();

        // Boutons
        HBox boutons = creerBoutons(stage, parfait, aleatoire);

        VBox contenu = new VBox(20, choixType, formulaire);
        contenu.setAlignment(Pos.CENTER);

        // Background
        Region region = new Region();
        region.setId(ThemeManager.getCurrentBackground());
        region.prefWidthProperty().bind(stage.widthProperty());
        region.prefHeightProperty().bind(stage.heightProperty());

        BorderPane bp = new BorderPane();
        bp.setCenter(contenu);
        bp.setBottom(boutons);
        BorderPane.setAlignment(boutons, Pos.BOTTOM_RIGHT);
        bp.setBackground(Background.EMPTY);

        StackPane sp = new StackPane(region, bp);

        Scene scene = new Scene(sp, 600, 400);
        scene.getStylesheets().add(getClass().getResource("/vueCss/style.css").toExternalForm());

        stage.setScene(scene);
        stage.setTitle("Choix Libre");
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
        stage.show();
    }

    private GridPane creerFormulaire() {
        champHauteur = new TextField();
        champLargeur = new TextField();
        champPourcentage = new TextField();
        champMinimum = new TextField();
        labelTaille = new Label("");

        // Ajout du style CSS aux champs
        champHauteur.getStyleClass().add("text-field");
        champLargeur.getStyleClass().add("text-field");
        champPourcentage.getStyleClass().add("text-field");
        champMinimum.getStyleClass().add("text-field");
        labelTaille.getStyleClass().add("label-erreur-petit");

        // Création des labels stylés
        Label labelHauteur = new Label("Hauteur du labyrinthe (10-70)");
        labelHauteur.getStyleClass().add("label-section");

        Label labelLargeur = new Label("Largeur du labyrinthe (10-70)");
        labelLargeur.getStyleClass().add("label-section");

        Label labelPourcentage = new Label("Pourcentage de Murs (Laby Aléatoire)");
        labelPourcentage.getStyleClass().add("label-section");

        Label labelMinimum = new Label("Chemin minimum (Laby Parfait)");
        labelMinimum.getStyleClass().add("label-section");

        // Grille
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        grid.add(labelHauteur, 0, 0);
        grid.add(champHauteur, 1, 0);

        grid.add(labelLargeur, 0, 1);
        grid.add(champLargeur, 1, 1);

        grid.add(labelPourcentage, 0, 2);
        grid.add(champPourcentage, 1, 2);

        grid.add(labelMinimum, 0, 3);
        grid.add(champMinimum, 1, 3);

        grid.add(labelTaille, 0, 4, 2, 1); // span 2 colonnes

        return grid;
    }

    private HBox creerBoutons(Stage stage, RadioButton parfait, RadioButton aleatoire) {
        Button valider = new Button("Valider");
        valider.getStyleClass().add("button-principal");
        valider.setOnAction(e -> validerFormulaire(stage, parfait.isSelected()));

        Button annuler = new Button("Annuler");
        annuler.getStyleClass().add("button-principal");
        annuler.setOnAction(e -> new HomeView().start(stage));

        HBox boutons = new HBox(10, valider, annuler);
        boutons.setAlignment(Pos.CENTER_RIGHT);
        boutons.setPadding(new Insets(20, 40, 40, 20));
        return boutons;
    }

    private void validerFormulaire(Stage stage, boolean labyParfait) {
        try {
            int hauteur = Integer.parseInt(champHauteur.getText());
            int largeur = Integer.parseInt(champLargeur.getText());
            if (hauteur < 10 || hauteur > 70 || largeur < 10 || largeur > 70) {
                labelTaille.setText("Mais t'as cru c'était open bar ? Il y a des limites chef");
                return;
            }

            if (labyParfait) {
                int distance = Integer.parseInt(champMinimum.getText());
                lpv = new LabyrintheParfaitLibreView(largeur, hauteur, distance);
                lpv.afficher(stage);
            } else {
                double pourcentage = Integer.parseInt(champPourcentage.getText()) * 0.01;
                lbl = new LabyrintheLibreView(largeur, hauteur, pourcentage);
                lbl.afficher(stage);
            }
        } catch (NumberFormatException e) {
            labelTaille.setText("Merci de saisir des nombres valides !");
        }
    }

}

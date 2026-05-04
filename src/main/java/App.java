

import javafx.application.Application;
import javafx.stage.Stage;
import vue.HomeView;
import vue.LabyrintheLibreView;
import modele.joueur.*;
import vue.LabyrintheView;

public class App extends Application{
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage)throws Exception{
        Joueur joueur = new Joueur("chèvre","beeeeh");
        new HomeView().start(stage);
    }
}

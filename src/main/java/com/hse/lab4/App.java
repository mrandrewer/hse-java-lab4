package com.hse.lab4;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("primary"), 900, 650);
        stage.setScene(scene);
        stage.setMinWidth(800);
        stage.setMinHeight(620);
        stage.show();
    }

    static void showModal(String fxml, String title) throws IOException {
        Stage dialog = new Stage();
        if (scene != null && scene.getWindow() != null) {
            dialog.initOwner(scene.getWindow());
            dialog.initModality(Modality.WINDOW_MODAL);
        }
        dialog.setTitle(title);
        dialog.setScene(new Scene(loadFXML(fxml), 600, 500));
        dialog.show();
        dialog.toFront();
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}
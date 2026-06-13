package com.hse.lab4;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;

import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("primary"), 900, 650);
        applyTheme(scene);
        stage.getIcons().add(loadAppIcon());
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
        dialog.getIcons().add(loadAppIcon());
        dialog.setTitle(title);
        Scene modalScene = new Scene(loadFXML(fxml), 600, 500);
        applyTheme(modalScene);
        dialog.setScene(modalScene);
        dialog.show();
        dialog.toFront();
    }

    private static Image loadAppIcon() {
        try (InputStream stream = App.class.getResourceAsStream("logo.png")) {
            return new Image(stream);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load app icon", e);
        }
    }

    private static void applyTheme(Scene scene) {
        var stylesheet = App.class.getResource("millionaire-theme.css");
        if (scene != null && stylesheet != null) {
            scene.getStylesheets().add(stylesheet.toExternalForm());
        }
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}
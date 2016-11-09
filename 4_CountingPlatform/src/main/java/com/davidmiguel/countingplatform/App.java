package com.davidmiguel.countingplatform;

import com.davidmiguel.countingplatform.controller.GuiController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Start devplatform.
 */
public class App extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/gui.fxml"));
        final Parent root = loader.load();
        final Scene scene = new Scene(root);
        primaryStage.setTitle("CountingPlatform");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.centerOnScreen();

        GuiController controller = loader.getController();
        controller.setPrimaryStage(primaryStage);
        primaryStage.show();
    }
}
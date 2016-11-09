package com.davidmiguel.countingplatform.utils;

import javafx.scene.image.Image;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;

public class FileUtils {

    public static File selectDir(Stage primaryStage, String title) {
        return selectDir(primaryStage, title, System.getProperty("user.home"));
    }

    public static File selectDir(Stage primaryStage, String title, String path) {
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle(title);
        dirChooser.setInitialDirectory(new File(path));
        return dirChooser.showDialog(primaryStage);
    }

    public static Image loadImage(File f) {
        return new Image(f.toURI().toString());
    }

    public static void writeFile(int[] beeNumbers) {

    }
}

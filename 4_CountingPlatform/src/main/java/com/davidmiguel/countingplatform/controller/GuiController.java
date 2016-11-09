package com.davidmiguel.countingplatform.controller;

import com.davidmiguel.countingplatform.recorder.BeesRecorder;
import com.davidmiguel.countingplatform.utils.FileUtils;
import com.davidmiguel.countingplatform.utils.Point;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.File;
import java.util.Optional;

public class GuiController {

    private static final double OPACITY = 0.7;

    @FXML
    private ImageView image;
    @FXML
    private Canvas canvas;
    @FXML
    private Label numBees;
    @FXML
    private Label numFrame;

    private Stage primaryStage;
    private GraphicsContext gc;
    private BeesRecorder br;
    private File inputDir;

    @FXML
    private void initialize() {
        br = new BeesRecorder(this);
        // Configure canvas
        gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.CHARTREUSE);
        gc.setGlobalAlpha(OPACITY);
    }

    @FXML
    private void handleOpen() {
        File f = inputDir != null ?
                FileUtils.selectDir(primaryStage, "Select frames folder", inputDir.getPath()) :
                FileUtils.selectDir(primaryStage, "Select frames folder");
        if (f != null) {
            inputDir = f;
            setTile(inputDir.getPath());
            br.openDir(inputDir);
            handleMouse();
        }
    }

    @FXML
    private void handleSave() {
        if(br.saveData()){
            dialog(AlertType.INFORMATION,
                    "Data saved!", "The data have been saved successfully as numBees.txt.");
        } else {
            dialog(AlertType.ERROR,
                    "Error", "An error has occurred while saving the data.");
        }
    }

    @FXML
    private void handleQuit() {
        System.exit(0);
    }

    @FXML
    private void handleNextFrame() {
        br.nextFrame();
    }

    @FXML
    private void handlePreviousFrame() {
        br.previousFrame();
    }

    @FXML
    private void handleReset() {
        br.resetFrame();
    }

    private void handleMouse() {
        canvas.setOnMouseClicked(event -> {
            Point point = new Point((int) event.getX(), (int) event.getY());
            br.addBee(point);
        });
    }

    @FXML
    private void handleGoToFrame() {
        TextInputDialog dialog = new TextInputDialog("1");
        dialog.setTitle("Go to frame...");
        dialog.setHeaderText(null);
        dialog.setContentText("Frame:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(frame -> br.goToFrame(Integer.parseInt(frame)));
    }

    @FXML
    private void handleAbout() {
        dialog(AlertType.INFORMATION,
                "About", "Counting Platform\nAuthor: David Miguel\nWebsite: http://davidmiguel.com/");
    }

    public void setImage(Image img) {
        image.setImage(img);
    }

    public void setNumBees(int numBees, int numFrame) {
        this.numBees.setText(Integer.toString(numBees));
        this.numFrame.setText(Integer.toString(numFrame));
    }

    public void clearCanvas() {
        gc.setGlobalAlpha(1);
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setGlobalAlpha(OPACITY);
    }

    public void drawPoint(Point point) {
        canvas.getGraphicsContext2D().fillOval(point.getX() - 10, point.getY() - 10, 20, 20);
    }

    public boolean warnOverwrite() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Overwrite numBees.txt?");
        alert.setHeaderText(null);
        alert.setContentText("numBees.txt already exists.\nDo you want overwrite it?");
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    private void setTile(String title) {
        primaryStage.setTitle("CountingPlatform - [" + title + "]");
    }

    private void dialog(AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

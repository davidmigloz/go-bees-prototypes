package com.davidmiguel.devplatform.controller;

import com.davidmiguel.devplatform.video.VideoPlayer;
import com.davidmiguel.devplatform.utils.ImageUtils;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.opencv.core.Mat;

import java.io.File;
import java.util.Stack;

public class GuiController {
    @FXML
    private ImageView orgininalIV;
    @FXML
    private TextField inputFile;
    @FXML
    private Button selInputBtn;
    @FXML
    private Button playBtn;
    @FXML
    private Button restartBtn;
    @FXML
    private Label fps;

    private File input;
    private VideoPlayer player;
    private Runnable task;
    private Timeline tl;
    private boolean playing;
    private Stack<Long> previousFramesDuration;

    @FXML
    private void initialize() {
        // Instantiate player
        player = new VideoPlayer();
        // Set userDir as default
        inputFile.setText(System.getProperty("user.home").replace("\\", "/"));
        // Setup rendering loop
        setupTask();
        setupRenderingLoop();
        previousFramesDuration = new Stack<>();
        playing = false;
    }

    /**
     * Select input file.
     */
    @FXML
    private void handleSelectInput() {
        File f = input != null ? selectFile(true, "Select input", input.getParent()) :
                selectFile(true, "Select input");
        if (f != null) {
            input = f;
            inputFile.setText(input.toString().replace("\\", "/"));
        }
        player.open(input.getPath());
    }

    /**
     * Handle play button.
     */
    @FXML
    private void handlePlay() {
        if(input != null) {
            if (playing) {
                playing = false;
                playBtn.setText("Play");
                tl.pause();
            } else {
                playing = true;
                playBtn.setText("Pause");
                tl.play();
            }
        }
    }

    @FXML
    private void handleRestart() {
        if(input != null) {
            tl.pause();
            player.open(input.getPath());
            tl.play();
        }
    }

    private void setupTask() {
        task = () -> {
            setImgOrgininalIV(player.nextFrame());
            // Update fps info
            handleMetrics();
        };
    }

    /**
     * Setup rendering loop (20hz).
     */
    private void setupRenderingLoop() {
        tl = new Timeline();
        tl.setCycleCount(Animation.INDEFINITE);
        KeyFrame frame = new KeyFrame(Duration.millis(50), event ->{
            // Run task
            task.run();
        });

        tl.getKeyFrames().add(frame);
    }

    private void setImgOrgininalIV(Mat matrix) {
        Image img = ImageUtils.matToImage(matrix);
        Platform.runLater(() ->
                orgininalIV.setImage(img));

    }

    /**
     * Compute the average FPS on 60 samples.
     */
    private void handleMetrics() {
        previousFramesDuration.push(System.currentTimeMillis());
        if(previousFramesDuration.size() == 60) {
            // Calculate average of last 60 samples
            long sum = 0;
            for(int i = 0; i < 30; i++) {
                sum += previousFramesDuration.pop() - previousFramesDuration.pop();
            }
            fps.setText(Long.toString(Math.round(1 / ((sum / 30.0) / 1000.0))));
        }
    }

    /**
     * Open a FileChooser to select a file.
     *
     * @param open true: open file / false: save file
     * @param title title of the FileChooser
     * @param path path to open
     * @return selected file
     */
    private File selectFile(boolean open, String title, String path) {
        Stage primaryStage = (Stage) inputFile.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("AVI videos (*.avi)", "*.avi"));
        fileChooser.setInitialDirectory(new File(path));
        fileChooser.setTitle(title);
        return open ? fileChooser.showOpenDialog(primaryStage) : fileChooser.showSaveDialog(primaryStage);
    }

    /**
     * Open a FileChooser to select a file in the default path (user.home).
     */
    private File selectFile(boolean open, String title) {
        return selectFile(open, title, System.getProperty("user.home"));
    }
}

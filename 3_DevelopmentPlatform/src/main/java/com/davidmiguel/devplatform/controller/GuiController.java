package com.davidmiguel.devplatform.controller;

import com.davidmiguel.devplatform.utils.ImageUtils;
import com.davidmiguel.devplatform.video.io.VideoPlayer;
import com.davidmiguel.devplatform.video.processors.VideoProcessor;
import com.davidmiguel.devplatform.video.processors.bgsub.MOG2Subtractor;
import com.davidmiguel.devplatform.video.processors.postbgsub.Postprocessing;
import com.davidmiguel.devplatform.video.processors.prebgsub.Preprocessing;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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
    private TextField inputFile;
    @FXML
    private Button selInputBtn;
    @FXML
    private ImageView playBtn;
    @FXML
    private Button restartBtn;
    @FXML
    private Label fps;
    @FXML
    private ImageView orgininalIV;
    @FXML
    private TabPane stepsTabPane;
    // Preprocessing
    @FXML
    private Tab preprocessingTab;
    @FXML
    private ImageView preprocessingIV;
    @FXML
    private Label tPreprocessing;
    @FXML
    private CheckBox equalizeHist;
    @FXML
    private CheckBox gaussianBlur;
    @FXML
    private Slider blurKernelSize;
    @FXML
    private TextField nBlur;
    // Background subtraction
    @FXML
    private Tab bsTab;
    @FXML
    private ImageView bsIV;
    @FXML
    private Label tBS;
    @FXML
    private RadioButton detectShadows;
    @FXML
    private TextField shadowsThreshold;
    @FXML
    private TextField history;
    @FXML
    private TextField backgroundRatio;
    @FXML
    private TextField varThreshold;
    @FXML
    private TextField varInit;
    // Postprocesing
    @FXML
    private Tab postprocessingTab;
    @FXML
    private ImageView postprocessingIV;
    @FXML
    private Label tPostprocessing;
    @FXML
    private CheckBox dilateImg;
    @FXML
    private CheckBox erodeImg;
    @FXML
    private Slider dilateKernelSize;
    @FXML
    private Slider erodeKernelSize;
    @FXML
    private TextField nDilate;
    @FXML
    private TextField nErode;


    // Video file
    private File input;
    // Rendering loop
    private Timeline tl;
    private boolean playing;
    private Stack<Long> previousFramesDuration;
    private Runnable task;
    // Video player
    private VideoPlayer player;
    // Preprocessing
    private VideoProcessor preprocessing;
    // Background subtraction
    private VideoProcessor bg;
    // Postprocesing
    private VideoProcessor postprocessing;

    @FXML
    private void initialize() {
        // Set userDir as default
        inputFile.setText(System.getProperty("user.home").replace("\\", "/"));
        // Setup rendering loop
        setupTask();
        setupRenderingLoop();
        previousFramesDuration = new Stack<>();
        playing = false;
        // Instantiate player
        player = new VideoPlayer();
        // Preprocessing
        preprocessing = new Preprocessing();
        // Background subtraction
        bg = new MOG2Subtractor();
        // Postprocesing
        postprocessing = new Postprocessing();
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
        if (input != null) {
            if (playing) {
                playing = false;
                playBtn.setImage(new Image("/img/play.png"));
                tl.pause();
            } else {
                playing = true;
                playBtn.setImage(new Image("/img/pause.png"));
                tl.play();
            }
        }
    }

    /**
     * Handle restart button.
     */
    @FXML
    private void handleRestart() {
        if (input != null) {
            tl.pause();
            player.open(input.getPath());
            tl.play();
        }
    }

    /**
     * Handle next frame button.
     */
    @FXML
    private void handleNextFrame() {
        if (!playing) {
            task.run();
        }
    }

    /**
     * Handle save options button.
     */
    @FXML
    private void handleSave() {
        if (preprocessingTab.isSelected()) {
            Preprocessing prp = (Preprocessing) preprocessing;
            prp.activeEqualizeHist(equalizeHist.isSelected());
            prp.activeGaussianBlur(gaussianBlur.isSelected(),
                    (int) blurKernelSize.getValue(), Integer.parseInt(nBlur.getText()));
        } else if (bsTab.isSelected()) {
            MOG2Subtractor mog = (MOG2Subtractor) bg;
            mog.setDetectShadows(detectShadows.isSelected(), Double.parseDouble(shadowsThreshold.getText()));
            mog.setConfig(Integer.parseInt(history.getText()), Double.parseDouble(backgroundRatio.getText()),
                    Double.parseDouble(varThreshold.getText()), Double.parseDouble(varInit.getText()));
        } else if (postprocessingTab.isSelected()) {
            Postprocessing pop = (Postprocessing) postprocessing;
            pop.activeDilate(dilateImg.isSelected(),
                    (int) dilateKernelSize.getValue(), Integer.parseInt(nDilate.getText()));
            pop.activeErode(erodeImg.isSelected(),
                    (int) erodeKernelSize.getValue(), Integer.parseInt(nErode.getText()));
        }
        if (!playing) {
            task.run();
        }
    }

    /**
     * Define the task to execute in the rendering loop.
     */
    private void setupTask() {
        task = () -> {
            Mat frame = player.nextFrame();
            long t0 = System.currentTimeMillis();
            Mat frame1 = preprocessing.process(frame);
            long t1 = System.currentTimeMillis();
            Mat frame2 = bg.process(frame1);
            long t2 = System.currentTimeMillis();
            Mat frame3 = postprocessing.process(frame2);
            long t3 = System.currentTimeMillis();

            setImgIV(orgininalIV, frame);
            //if (preprocessingTab.isSelected()) {
            setImgIV(preprocessingIV, frame1);
            //} else if (bsTab.isSelected()) {
            setImgIV(bsIV, frame2);
            //} else if (postprocessingTab.isSelected()) {
            setImgIV(postprocessingIV, frame3);
            //}

            // Update metrics
            updateTimes(t1 - t0, t2 - t1, t3 - t2);
            updateFps();
        };
    }

    /**
     * Setup rendering loop (20hz).
     */
    private void setupRenderingLoop() {
        tl = new Timeline();
        tl.setCycleCount(Animation.INDEFINITE);
        KeyFrame frame = new KeyFrame(Duration.millis(67), event -> {
            // Run task
            task.run();
        });

        tl.getKeyFrames().add(frame);
    }

    /**
     * Set an image (in mat format) in an ImageView.
     */
    private void setImgIV(ImageView iv, Mat matrix) {
        Image img = ImageUtils.matToImage(matrix);
        Platform.runLater(() -> iv.setImage(img));
        matrix.release();
    }

    /**
     * Update the execution times.
     */
    private void updateTimes(long preprocessing, long bs, long postprocessing) {
        Platform.runLater(() -> {
            tPreprocessing.setText(Long.toString(preprocessing));
            tBS.setText(Long.toString(bs));
            tPostprocessing.setText(Long.toString(postprocessing));
        });
    }

    /**
     * Compute the average FPS on 60 samples.
     */
    private void updateFps() {
        previousFramesDuration.push(System.currentTimeMillis());
        if (previousFramesDuration.size() == 60) {
            // Calculate average of last 60 samples
            long sum = 0;
            for (int i = 0; i < 30; i++) {
                sum += previousFramesDuration.pop() - previousFramesDuration.pop();
            }
            long finalSum = sum;
            Platform.runLater(() -> fps.setText(Long.toString(Math.round(1 / ((finalSum / 30.0) / 1000.0)))));
        }
    }

    /**
     * Open a FileChooser to select a file.
     *
     * @param open  true: open file / false: save file
     * @param title title of the FileChooser
     * @param path  path to open
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

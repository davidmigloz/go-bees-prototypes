package com.davidmiguel.devplatform;

import com.davidmiguel.devplatform.filters.Filter;
import com.davidmiguel.devplatform.filters.bgsub.AverageBackground;
import com.davidmiguel.devplatform.filters.bgsub.FrameDifferencing;
import com.davidmiguel.devplatform.filters.bgsub.KNNSubtractor;
import com.davidmiguel.devplatform.filters.bgsub.MOG2Subtractor;
import com.davidmiguel.devplatform.utils.ImageProcessor;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import javax.swing.*;
import java.awt.*;

public class App {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private JFrame frame;
    private JLabel imageLabel;

    private JFrame playbackFrame;
    private JLabel playbackLabel;

    private Mat backgroundImage = new Mat();
    private Mat currentImage = new Mat();
    private Mat foregroundImage = new Mat();

    private void initGUI() {
        frame = new JFrame("Background Subtraction");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        imageLabel = new JLabel();
        frame.add(imageLabel);
        frame.setVisible(true);

        playbackFrame = new JFrame("Video Player");
        playbackFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        playbackFrame.setSize(400, 400);
        playbackLabel = new JLabel();
        playbackFrame.add(playbackLabel);
        playbackFrame.setVisible(true);
    }

    private void runMainLoop() throws InterruptedException {
        ImageProcessor imageProcessor = new ImageProcessor();
        Image tempImage;

        Filter filter = null;
        VideoCapture capture = new VideoCapture("src/main/resources/videos/hive.avi");

        if (capture.isOpened()) {

            capture.read(backgroundImage);

//            filter = new FrameDifferencing(1280, 720);
//            filter = new AverageBackground();
            filter = new MOG2Subtractor();
//            filter = new KNNSubtractor();

            while (true) {
                capture.read(currentImage);
                if (!currentImage.empty()) {

                    foregroundImage = filter.process(currentImage);

                    tempImage = imageProcessor.toBufferedImage(foregroundImage);
                    ImageIcon imageIcon = new ImageIcon(tempImage, "Video Player");
                    imageLabel.setIcon(imageIcon);
                    frame.pack();  //this will resize the window to fit the image

                    Image playbackImage = imageProcessor.toBufferedImage(currentImage);
                    ImageIcon playbackImageIcon = new ImageIcon(playbackImage, "Video Player");
                    playbackLabel.setIcon(playbackImageIcon);
                    playbackFrame.pack();

                    Thread.sleep(70);
                } else {
                    // Looping video
                    capture = new VideoCapture("src/main/resources/videos/hive.avi");
                }
            }
        } else {
            System.out.println("Couldn't open video file.");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        App app = new App();
        app.initGUI();
        app.runMainLoop();
    }
}
package com.davidmiguel.devplatform.video.processors.bgsub;

import com.davidmiguel.devplatform.video.processors.VideoProcessor;
import org.opencv.core.Mat;
import org.opencv.video.BackgroundSubtractorKNN;
import org.opencv.video.Video;

/**
 * Implments BackgroundSubtractorKNN.
 */
public class KNNSubtractor implements VideoProcessor {

    private final static double LEARNING_RATE = 0.01;

    private BackgroundSubtractorKNN knn;
    private Mat foreground;

    public KNNSubtractor() {
        knn = Video.createBackgroundSubtractorKNN();
        knn.setDetectShadows(false);
        foreground = new Mat();
    }

    public Mat process(Mat inputImage) {
        knn.apply(inputImage, foreground, LEARNING_RATE);
        return foreground;
    }
}

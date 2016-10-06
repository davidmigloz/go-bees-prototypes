package com.davidmiguel.devplatform.filters.bgsub;

import com.davidmiguel.devplatform.filters.Filter;
import org.opencv.core.Mat;
import org.opencv.video.BackgroundSubtractorKNN;
import org.opencv.video.Video;

/**
 * Implments BackgroundSubtractorMOG2.
 */
public class KNNSubtractor implements Filter {

    private final static double LEARNING_RATE = 0.01;

    private BackgroundSubtractorKNN knn;
    private Mat foreground;

    public KNNSubtractor() {
        knn = Video.createBackgroundSubtractorKNN();
        foreground = new Mat();
    }

    public Mat process(Mat inputImage) {
        knn.apply(inputImage, foreground, LEARNING_RATE);
        return foreground;
    }
}

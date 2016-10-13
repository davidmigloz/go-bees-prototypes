package com.davidmiguel.backgroundsub.utils.bgsubtractors;

import com.davidmiguel.backgroundsub.utils.VideoProcessor;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.BackgroundSubtractorMOG2;
import org.opencv.video.Video;

/**
 * Implments BackgroundSubtractorMOG2.
 */

public class MOG2Subtractor implements VideoProcessor {

    private final static double LEARNING_RATE = 0.01;

    private BackgroundSubtractorMOG2 mog;
    private Mat foreground = new Mat();

    public MOG2Subtractor() {
        mog = Video.createBackgroundSubtractorMOG2();
    }

    public Mat process(Mat inputImage) {
        mog.apply(inputImage, foreground, LEARNING_RATE);
        // Erode result
        Mat structuringElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
        Imgproc.erode(foreground, foreground, structuringElement);
        return foreground;
    }
}

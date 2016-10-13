package com.davidmiguel.devplatform.video.processors.bgsub;

import com.davidmiguel.devplatform.video.processors.VideoProcessor;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

/**
 * Implements averaging background subtractor.
 */
public class AverageBackground implements VideoProcessor {

    private Mat accumulatedBackground = new Mat();     // Accumulated bg in 32-bit floating point
    private Mat accumulatedBackground8U = new Mat();   // Accumulated bg in 8-bit unsigned
    private Mat difference = new Mat();                // Result image from absdiff
    private final static double LEARNING_RATE = 0.1;   // Learning reate
    private final static int THRESHOLD = 30;           // Threshold

    @Override
    public Mat process(Mat inputImage) {

        // Initialize accumulatedBackground
        if (accumulatedBackground.empty()) {
            inputImage.convertTo(accumulatedBackground, CvType.CV_32F);
        }
        // accumulatedBackground to 8U to run absdiff
        accumulatedBackground.convertTo(accumulatedBackground8U, CvType.CV_8U);

        // Compute difference between image and background
        Core.absdiff(accumulatedBackground8U, inputImage, difference);

        // Apply THRESHOLD to the difference
        Mat foregroundThresh = new Mat();
        Imgproc.threshold(difference, foregroundThresh, THRESHOLD, 255, Imgproc.THRESH_BINARY_INV);

        // Update accumulatedBackground
        Imgproc.accumulateWeighted(inputImage, accumulatedBackground, LEARNING_RATE);

        return negative(foregroundThresh);
    }

    private Mat negative(Mat foregroundThresh) {
        Mat result = new Mat();
        Mat white = foregroundThresh.clone();
        white.setTo(new Scalar(255.0));
        Core.subtract(white, foregroundThresh, result);
        return result;
    }
}
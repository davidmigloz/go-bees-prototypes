package com.davidmiguel.devplatform.video.processors.bgsub;

import com.davidmiguel.devplatform.video.processors.VideoProcessor;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.BackgroundSubtractorMOG2;
import org.opencv.video.Video;

/**
 * Background substraction using BackgroundSubtractorMOG2.
 */
public class MOG2Subtractor implements VideoProcessor {

    private BackgroundSubtractorMOG2 mog;
    private Mat foreground = new Mat();

    public MOG2Subtractor() {
        reset();
    }

    public Mat process(Mat inputImage) {
        if (!inputImage.empty()) {
            mog.apply(inputImage, foreground);

            Mat structuringElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
            for (int i = 0; i < 1; i++) {
                Imgproc.erode(foreground, foreground, structuringElement);
            }
        }
        return foreground;
    }

    public void setDetectShadows(boolean value, double threshold) {
        mog.setDetectShadows(value);
        mog.setShadowThreshold(threshold);
    }

    public void setConfig(int history, double bgRatio, double varThreshold, double varInit) {
        mog.setHistory(history);
        mog.setBackgroundRatio(bgRatio);
        mog.setVarThreshold(varThreshold);
        mog.setVarInit(varInit);
        mog.setVarMax(varInit * 5);
    }

    public void reset() {
        mog = Video.createBackgroundSubtractorMOG2();
        mog.setShadowValue(0); // Shadows as background
        mog.setDetectShadows(false);
        setConfig(50, 0.04, 40, 15);
        setDetectShadows(true, 0.7);
    }
}
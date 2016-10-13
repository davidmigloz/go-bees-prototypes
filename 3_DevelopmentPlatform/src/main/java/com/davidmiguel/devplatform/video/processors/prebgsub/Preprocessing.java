package com.davidmiguel.devplatform.video.processors.prebgsub;

import com.davidmiguel.devplatform.video.processors.VideoProcessor;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

/**
 * Prepare the image to apply background subtraction:
 * 1º Convert to grayscale.
 * 2º Histogram equalization.
 * 3º Gaussian blur.
 */
public class Preprocessing implements VideoProcessor {
    private boolean equalizeHist;
    private boolean gaussianBlur;
    private Mat grayImg;
    private int kernelSize;
    private int nGaussianBlur;

    public Preprocessing() {
        grayImg = new Mat();
        equalizeHist = true;
        gaussianBlur = true;
        kernelSize = 3;
        nGaussianBlur = 2;
    }

    @Override
    public Mat process(Mat inputImage) {
        // Convert image
        Imgproc.cvtColor(inputImage, grayImg, Imgproc.COLOR_BGR2GRAY);
        // Equalize histogram
        if (equalizeHist) {
            Imgproc.equalizeHist(grayImg, grayImg);
        }
        // Gaussian blur
        if (gaussianBlur) {
            for (int i = 0; i < nGaussianBlur; i++) {
                Imgproc.GaussianBlur(grayImg, grayImg, new Size(kernelSize, kernelSize), 0);
            }
        }
        return grayImg;
    }

    public void activeEqualizeHist(boolean equalizeHist) {
        this.equalizeHist = equalizeHist;
    }

    public void activeGaussianBlur(boolean blur, int kernel, int repetitions) {
        gaussianBlur = blur;
        kernelSize = kernel;
        nGaussianBlur = repetitions;
    }
}
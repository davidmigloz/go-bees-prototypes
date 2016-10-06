package com.davidmiguel.backgroundsub.utils;

import org.opencv.core.Mat;

/**
 * It doesn't process the image.
 */
public class NoneFilter implements VideoProcessor {

    @Override
    public Mat process(Mat inputImage) {
        return inputImage;
    }
}
package com.davidmiguel.devplatform.filters;

import org.opencv.core.Mat;

/**
 * It doesn't process the image.
 */
public class NoneFilter implements Filter {

    @Override
    public Mat process(Mat inputImage) {
        return inputImage;
    }
}
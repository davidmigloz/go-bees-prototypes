package com.davidmiguel.devplatform.filters;

import org.opencv.core.Mat;

public interface Filter {

    Mat process(Mat inputImage);

}
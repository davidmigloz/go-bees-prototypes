package com.davidmiguel.backgroundsub.utils;

import org.opencv.core.Mat;

public interface VideoProcessor {

    Mat process(Mat inputImage);

}
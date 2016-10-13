package com.davidmiguel.devplatform.video.processors;

import org.opencv.core.Mat;

public interface VideoProcessor {

    Mat process(Mat inputImage);

}
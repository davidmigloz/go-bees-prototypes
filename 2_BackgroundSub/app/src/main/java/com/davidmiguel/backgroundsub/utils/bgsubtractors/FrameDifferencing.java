package com.davidmiguel.backgroundsub.utils.bgsubtractors;

import com.davidmiguel.backgroundsub.utils.VideoProcessor;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

/**
 * Implements frame differencing background subtractor.
 */
public class FrameDifferencing implements VideoProcessor {
    private Mat previous;
    private Mat difference;

    public FrameDifferencing() {
        difference = new Mat();
    }

    @Override
    public Mat process(Mat inputFrame) {
        if(previous == null) {
            previous = new Mat();
            inputFrame.copyTo(previous);
        }
        Core.absdiff(inputFrame, previous, difference);
        inputFrame.copyTo(previous);
        return difference;
    }
}

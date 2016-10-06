package com.davidmiguel.devplatform.filters.bgsub;

import com.davidmiguel.devplatform.filters.Filter;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

/**
 * Implements frame differencing background subtractor.
 */

public class FrameDifferencing implements Filter {
    private Mat previous;
    private Mat difference;

    public FrameDifferencing(int width, int height) {
        previous = new Mat(height, width, CvType.CV_8UC1, new Scalar(0));
        difference = new Mat(height, width, CvType.CV_8UC1, new Scalar(0));
    }

    @Override
    public Mat process(Mat inputFrame) {
        Core.absdiff(inputFrame, previous, difference);
        inputFrame.copyTo(previous);
        return difference;
    }
}

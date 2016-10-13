package com.davidmiguel.devplatform.video.processors.postbgsub;

import com.davidmiguel.devplatform.video.processors.VideoProcessor;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class Postprocessing implements VideoProcessor {

    private Mat tmp;
    private boolean erodeImg;
    private boolean dilateImg;
    private int dilateKernelSize;
    private int erodeKernelSize;
    private int nDilate;
    private int nErode;

    public Postprocessing() {
        tmp = new Mat();
        erodeImg = true;
        dilateImg = true;
        dilateKernelSize = 3;
        erodeKernelSize = 4;
        nDilate = 3;
        nErode = 2;
    }

    @Override
    public Mat process(Mat inputImage) {
        inputImage.copyTo(tmp);
        if (dilateImg) {
            for (int i = 0; i < nDilate; i++) {
                dilate(tmp, dilateKernelSize, Imgproc.CV_SHAPE_ELLIPSE);
            }
        }
        if (erodeImg) {
            for (int i = 0; i < nErode; i++) {
                erode(tmp, erodeKernelSize, Imgproc.CV_SHAPE_ELLIPSE);
            }
        }
        return tmp;
    }

    private void erode(Mat input, int elementSize, int elementShape) {
        Mat element = getKernelFromShape(elementSize, elementShape);
        Imgproc.erode(input, input, element);
    }

    private void dilate(Mat input, int elementSize, int elementShape) {
        Mat element = getKernelFromShape(elementSize, elementShape);
        Imgproc.dilate(input, input, element);
    }

    private Mat getKernelFromShape(int elementSize, int elementShape) {
        return Imgproc.getStructuringElement(elementShape,
                new Size(elementSize * 2 + 1, elementSize * 2 + 1), new Point(elementSize, elementSize));
    }

    public void activeDilate(boolean dilate, int kernelSize, int repetitions) {
        dilateImg = dilate;
        dilateKernelSize = kernelSize;
        nDilate = repetitions;
    }

    public void activeErode(boolean erode, int kernelSize, int repetitions) {
        erodeImg = erode;
        erodeKernelSize = kernelSize;
        nErode = repetitions;
    }
}

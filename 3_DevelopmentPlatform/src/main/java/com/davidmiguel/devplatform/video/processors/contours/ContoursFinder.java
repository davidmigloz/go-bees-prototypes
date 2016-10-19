package com.davidmiguel.devplatform.video.processors.contours;

import com.davidmiguel.devplatform.video.processors.VideoProcessor;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Contours are connected curves in an image or boundaries of connected components in an image.
 * This class find and mark them.
 */
public class ContoursFinder implements VideoProcessor {

    private final Logger logger = LoggerFactory.getLogger(getClass().getName());
    private final static Scalar RED = new Scalar(0, 0, 255);
    private final static Scalar GREEN = new Scalar(0, 255, 0);

    private Mat input;
    private List<MatOfPoint> contourList;
    private Mat hierarchy;

    private double minArea;
    private double maxArea;
    private int numBees;

    public ContoursFinder() {
        input = new Mat();
        contourList = new ArrayList<>();
        hierarchy = new Mat();
        minArea = 15;
        maxArea = 600;
        numBees = 0;
    }

    @Override
    public Mat process(Mat inputImage) {
        // Copy mat (findContours modify the mat)
        inputImage.copyTo(input);
        // Finding outer contours
        contourList.clear();
        Imgproc.findContours(input, contourList, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        // Filter bees
        Mat contours = new Mat(input.rows(),input.cols(), CvType.CV_8UC3);
        double area;
        Scalar color;
        numBees = 0;
        for (int i = 0; i < contourList.size(); i++) {
            area = Imgproc.contourArea(contourList.get(i));
            logger.debug("Area{}: {}", i, area);
            if (area > minArea && area < maxArea) {
                color = GREEN;
                numBees++;
            } else {
                color = RED;
            }
            // Draw contour
            Imgproc.drawContours(contours, contourList, i, color, -1);
        }
        logger.debug("Bees: {}/{} \n-------", numBees, contourList.size());
        return contours;
    }

    public void setAreaLimits(int min, int max) {
        minArea = min;
        maxArea = max;
    }

    public int getNumBees() {
        return numBees;
    }
}
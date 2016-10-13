package com.davidmiguel.devplatform.video.io;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

/**
 * Open and read an avi video file.
 */
public class VideoPlayer {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private String file;
    private VideoCapture videoCapture;
    private Mat currentFrame;

    public VideoPlayer() {
        currentFrame = new Mat();
    }

    /**
     * Open an avi video file.
     *
     * @param file path to the video file
     * @return true if success
     */
    public boolean open(String file) {
        this.file = file;
        if (videoCapture != null) {
            videoCapture.release();
        }
        videoCapture = new VideoCapture(file);
        return videoCapture.isOpened();
    }

    /**
     * Read next frame and return it.
     * @return read frame
     */
    public Mat nextFrame() {
        videoCapture.read(currentFrame);
        if (currentFrame.empty()) {
            // Looping video
            videoCapture.open(file);
            videoCapture.read(currentFrame);
        }
        return currentFrame;
    }
}

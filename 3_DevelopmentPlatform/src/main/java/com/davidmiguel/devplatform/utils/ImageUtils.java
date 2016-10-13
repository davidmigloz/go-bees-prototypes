package com.davidmiguel.devplatform.utils;

import javafx.scene.image.Image;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;

@SuppressWarnings("unused")
public class ImageUtils {
    /**
     * Convert matrix into BufferedImage.
     *
     * @param matrix OpenCV mat
     * @return BufferedImage
     */
    public static BufferedImage matToBufferedImage(Mat matrix) {
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (matrix.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = matrix.channels() * matrix.cols() * matrix.rows();
        byte[] buffer = new byte[bufferSize];
        matrix.get(0, 0, buffer);
        BufferedImage image = new BufferedImage(matrix.cols(), matrix.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(buffer, 0, targetPixels, 0, buffer.length);
        return image;
    }

    /**
     * Convert matrix into JavaFX image.
     *
     * @param matrix OpenCV mat
     * @return JavaFX image
     */
    public static Image matToImage(Mat matrix) {
        if (matrix == null || matrix.empty()) {
            return null;
        }
        MatOfByte buffer = new MatOfByte();
        Imgcodecs.imencode(".png", matrix, buffer);
        return new Image(new ByteArrayInputStream(buffer.toArray()));
    }
}
package com.davidmiguel.countingplatform.utils;

import java.io.File;
import java.io.FileFilter;

/**
 * Filter for image files (.jpg and .png).
 */
public class ImageFileFilter implements FileFilter {

    private static final String JPG = ".jpg";
    private static final String PNG = ".png";

    @Override
    public boolean accept(File pathname) {
        String fileName = pathname.getName().toLowerCase();
        return fileName.endsWith(JPG) || fileName.endsWith(PNG);
    }
}

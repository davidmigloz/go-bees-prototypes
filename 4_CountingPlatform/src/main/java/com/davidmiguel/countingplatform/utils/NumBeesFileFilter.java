package com.davidmiguel.countingplatform.utils;


import java.io.File;
import java.io.FileFilter;

/**
 * Filter for numBees.txt file.
 */
public class NumBeesFileFilter implements FileFilter {

    private static final String NUM_BEES_FILE = "numBees.txt";

    @Override
    public boolean accept(File pathname) {
        String fileName = pathname.getName();
        return fileName.equals(NUM_BEES_FILE);
    }
}

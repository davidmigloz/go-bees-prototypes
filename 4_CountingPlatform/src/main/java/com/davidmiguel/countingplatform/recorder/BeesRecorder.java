package com.davidmiguel.countingplatform.recorder;

import com.davidmiguel.countingplatform.controller.GuiController;
import com.davidmiguel.countingplatform.utils.FileUtils;
import com.davidmiguel.countingplatform.utils.ImageFileFilter;
import com.davidmiguel.countingplatform.utils.NumBeesFileFilter;
import com.davidmiguel.countingplatform.utils.Point;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BeesRecorder {

    private static final String NUM_BEES_FILE = "numBees.txt";

    private GuiController controller;
    private File dir;
    private File[] framesFiles;
    private int[] numBees;
    private List<Set<Point>> beesPoints;
    private int actualFrame;

    public BeesRecorder(GuiController controller) {
        this.controller = controller;
    }

    public void openDir(File dir) {
        if(dir == null || !dir.exists() || !dir.isDirectory() )  {
            return;
        }
        this.dir = dir;
        // Count number of frames
        framesFiles = dir.listFiles(new ImageFileFilter());
        if(framesFiles == null || framesFiles.length == 0) {
            return;
        }
        // Create and initialize (with 0s) array of bees
        numBees = IntStream.generate(() -> 0).limit(framesFiles.length).toArray();
        // Create and initialize array of points
        beesPoints = new ArrayList<>(framesFiles.length);
        for (int i = 0; i < framesFiles.length; i++) {
            beesPoints.add(i, new HashSet<>());
        }
        // If numBees.txt exists -> load data
        File[] files = dir.listFiles(new NumBeesFileFilter());
        if(files != null && files.length > 0) {
            loadData(files[0]);
        }
        // Initialize counters
        actualFrame = 0;
        // Load first frame
        setFrame(actualFrame);
    }

    public void nextFrame() {
        if(dir != null &&  actualFrame + 1 < framesFiles.length) {
            actualFrame++;
            setFrame(actualFrame);
        }
    }

    public void previousFrame() {
        if(dir != null && actualFrame  > 0) {
            actualFrame--;
            setFrame(actualFrame);
        }
    }

    public void goToFrame(int index) {
        if(dir == null || index <= 0 || index > framesFiles.length){
            return;
        }
        actualFrame = index - 1;
        setFrame(actualFrame);
    }

    public void resetFrame() {
        if(dir != null) {
            // Reset number of bees and points
            numBees[actualFrame] = 0;
            beesPoints.get(actualFrame).clear();
            // Set number of bees and number of frame
            controller.setNumBees(numBees[actualFrame], actualFrame + 1);
            // Clear canvas
            controller.clearCanvas();
        }
    }

    public void addBee(Point point) {
        // Save point
        beesPoints.get(actualFrame).add(point);
        // Draw point
        controller.drawPoint(point);
        // Increment num bees
        numBees[actualFrame]++;
        // Set number of bees and number of frame
        controller.setNumBees(numBees[actualFrame], actualFrame + 1);
    }

    public boolean saveData() {
        return dir != null && writeData(dir);
    }

    private void setFrame(int index){
        // Set image
        controller.setImage(FileUtils.loadImage(framesFiles[index]));
        // Set number of bees and number of frame
        controller.setNumBees(numBees[index], index + 1);
        // Draw points if exist
        drawPoints();
    }

    private void drawPoints() {
        // Clear canvas
        controller.clearCanvas();
        // Draw points
        for (Point p : beesPoints.get(actualFrame)) {
            controller.drawPoint(p);
        }
    }

    /**
     * Load data from numBees.txt.
     * @param file numBees.txt.
     */
    private void loadData(File file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            int i = 0;
            for (String line; i < numBees.length && (line = br.readLine()) != null; i++) {
                // Get number of bees in the frame
                numBees[i] = Integer.parseInt(line);
            }
        } catch ( IOException e) {
            e.printStackTrace();
        }
    }

    private boolean writeData(File dir) {
        List<Integer> numbers = IntStream.of(numBees).boxed().collect(Collectors.toList());
        List<String> lines = numbers.stream().map(Object::toString).collect(Collectors.toList());
        Path file = Paths.get(dir.getPath() + "/" + NUM_BEES_FILE);
        if(file.toFile().exists()) {
            if(!controller.warnOverwrite()){
                return false;
            }
        }
        try {
            Files.write(file, lines, Charset.forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}

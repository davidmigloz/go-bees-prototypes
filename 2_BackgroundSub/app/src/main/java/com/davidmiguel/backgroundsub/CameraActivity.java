package com.davidmiguel.backgroundsub;

import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.hardware.Camera.CameraInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.Window;
import android.view.WindowManager;

import com.davidmiguel.backgroundsub.utils.NoneFilter;
import com.davidmiguel.backgroundsub.utils.VideoProcessor;
import com.davidmiguel.backgroundsub.utils.bgsubtractors.AverageBackground;
import com.davidmiguel.backgroundsub.utils.bgsubtractors.FrameDifferencing;
import com.davidmiguel.backgroundsub.utils.bgsubtractors.KNNSubtractor;
import com.davidmiguel.backgroundsub.utils.bgsubtractors.MOG2Subtractor;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;

import java.util.List;
import java.util.Locale;


@SuppressWarnings("deprecation")
public class CameraActivity extends AppCompatActivity
        implements CvCameraViewListener2 {

    private static final String TAG = CameraActivity.class.getSimpleName();
    // Key for storing the index of the active camera
    private static final String STATE_CAMERA_INDEX = "cameraIndex";
    // Key for storing the index of the active image size
    private static final String STATE_IMAGE_SIZE_INDEX = "imageSizeIndex";
    // Key for storing the background subtraction algorithm
    private static final String STATE_BR_ALG_INDEX = "bgAlgIndex";
    // An ID for items in the image size submenu
    private static final int MENU_GROUP_ID_SIZE = 2;
    // Index of the active camera
    private int cameraIndex;
    // Index of the active image size
    private int imageSizeIndex;
    // Index of the background subtraction algorithm
    private int bgAlgIndex;
    // Whether the active camera is front-facing. If so, the camera view is mirrored
    private boolean isCameraFrontFacing;
    // The number of cameras on the device
    private int numCameras;
    // The camera view
    private CameraBridgeViewBase cameraView;
    // The image sizes supported by the active camera
    private List<Size> supportedImageSizes;
    // Whether an asynchronous menu action is in progress. If so, menu interaction is disabled
    private boolean isMenuLocked;
    // To apply background subtraction
    private VideoProcessor videoProcessor;
    // OpenCV loader callback
    private BaseLoaderCallback loaderCallback =
            new BaseLoaderCallback(this) {
                @Override
                public void onManagerConnected(final int status) {
                    switch (status) {
                        case LoaderCallbackInterface.SUCCESS:
                            Log.d(TAG, "OpenCV loaded successfully");
                            cameraView.enableView();
                            break;
                        default:
                            super.onManagerConnected(status);
                            break;
                    }
                }
            };

    /**
     * Sets up the camera view and initializes data about the cameras. It also reads any previous
     * data about the active camera that may have been written by onSaveInstanceState.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        // Don't switch off screen
        final Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // Load state info if exists
        if (savedInstanceState != null) {
            cameraIndex = savedInstanceState.getInt(STATE_CAMERA_INDEX, 0);
            imageSizeIndex = savedInstanceState.getInt(STATE_IMAGE_SIZE_INDEX, 0);
            bgAlgIndex = savedInstanceState.getInt(STATE_BR_ALG_INDEX, 0);
        } else {
            cameraIndex = 0;
            imageSizeIndex = 0;
        }
        // Get camera info
        final Camera camera;
        CameraInfo cameraInfo = new CameraInfo();
        Camera.getCameraInfo(cameraIndex, cameraInfo);
        isCameraFrontFacing = (cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT);
        numCameras = Camera.getNumberOfCameras();
        camera = Camera.open(cameraIndex);
        final Camera.Parameters parameters = camera.getParameters();
        camera.release();
        supportedImageSizes = parameters.getSupportedPreviewSizes();
        final Size size = supportedImageSizes.get(imageSizeIndex);
        // Configure view
        cameraView = (JavaCameraView) findViewById(R.id.camera_view);
        cameraView.setCameraIndex(cameraIndex);
        cameraView.setMaxFrameSize(size.width, size.height);
        cameraView.setCvCameraViewListener(this);
    }

    /**
     * Disable camera when the activity goes into background.
     */
    @Override
    public void onPause() {
        if (cameraView != null) {
            cameraView.disableView();
        }
        super.onPause();
    }

    /**
     * Initialize the OpenCV library when the activity comes into foreground.
     * (The camera view is enabled once the library is successfully initialized).
     * The menu interaction is reenabled.
     */
    @Override
    public void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, loaderCallback);
        isMenuLocked = false;
    }

    /**
     * Disable camera when the activity finishes.
     */
    @Override
    public void onDestroy() {
        if (cameraView != null) {
            cameraView.disableView();
        }
        super.onDestroy();
    }

    /**
     * Saves the current camera index and the image size index.
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the current camera index
        savedInstanceState.putInt(STATE_CAMERA_INDEX, cameraIndex);
        // Save the current image size index
        savedInstanceState.putInt(STATE_IMAGE_SIZE_INDEX, imageSizeIndex);
        // Save the current bg algorithm
        savedInstanceState.putInt(STATE_BR_ALG_INDEX, bgAlgIndex);
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * Load menu from its resource file. Then, if the device has only one camera, the Next Cam menu
     * item is removed. If the active camera supports more than one image size, a set of menu
     * options for all the supported sizes is created.
     */
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.activity_camera, menu);
        if (numCameras < 2) {
            // Remove the option to switch cameras, since there is only 1
            menu.removeItem(R.id.menu_next_camera);
        }
        int numSupportedImageSizes = supportedImageSizes.size();
        if (numSupportedImageSizes > 1) {
            final SubMenu sizeSubMenu = menu.addSubMenu(R.string.menu_image_size);
            for (int i = 0; i < numSupportedImageSizes; i++) {
                final Size size = supportedImageSizes.get(i);
                sizeSubMenu.add(MENU_GROUP_ID_SIZE, i, Menu.NONE,
                        String.format(Locale.getDefault(), "%dx%d", size.width, size.height));
            }
        }
        return true;
    }

    /**
     * Handle any image size menu item by recreating the activity with the specified image size.
     * Similarly, it handles the Next Cam menu item by cycling to the next camera index and then
     * recreating the activity. (Image size index and camera index is saved in onSaveInstanceState
     * and restored in onCreate, where it is used to construct the camera view). In either case,
     * it blocks any further handling of menu options until the current handling is complete
     * (for example, until onResume).
     */
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (isMenuLocked) {
            return true;
        }
        if (item.getGroupId() == MENU_GROUP_ID_SIZE) {
            imageSizeIndex = item.getItemId();
            recreate();
            return true;
        } else if (item.getGroupId() == R.id.bg_group) {
            bgAlgIndex = item.getItemId();
            recreate();
            return true;
        }
        switch (item.getItemId()) {
            case R.id.menu_next_camera:
                isMenuLocked = true;
                // With another camera index, recreate the activity
                cameraIndex++;
                if (cameraIndex == numCameras) {
                    cameraIndex = 0;
                }
                imageSizeIndex = 0;
                recreate();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        switch (bgAlgIndex) {
            case R.id.bg_fd:
                videoProcessor = new FrameDifferencing(width, height);
                break;
            case R.id.bg_avg:
                videoProcessor = new AverageBackground();
                break;
            case R.id.bg_mog2:
                videoProcessor = new MOG2Subtractor();
                break;
            case R.id.bg_knn:
                videoProcessor = new KNNSubtractor();
                break;
            case R.id.bg_none:
                videoProcessor = new NoneFilter();
                break;
            default:
                videoProcessor = new NoneFilter();
        }
    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        final Mat currentImage = inputFrame.gray();
        if (isCameraFrontFacing) {
            // Mirror (horizontally flip) the preview
            Core.flip(currentImage, currentImage, 1);
        }
        // Background subtraction
        return videoProcessor.process(currentImage);
    }
}

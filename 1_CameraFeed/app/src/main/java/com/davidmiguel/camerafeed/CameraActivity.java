package com.davidmiguel.camerafeed;

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

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
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
    // An ID for items in the image size submenu
    private static final int MENU_GROUP_ID_SIZE = 2;
    // Index of the active camera
    private int mCameraIndex;
    // Index of the active image size
    private int mImageSizeIndex;
    // Whether the active camera is front-facing. If so, the camera view is mirrored
    private boolean mIsCameraFrontFacing;
    // The number of cameras on the device
    private int mNumCameras;
    // The camera view
    private CameraBridgeViewBase mCameraView;
    // The image sizes supported by the active camera
    private List<Size> mSupportedImageSizes;
    // Whether an asynchronous menu action is in progress. If so, menu interaction is disabled
    private boolean mIsMenuLocked;
    // OpenCV loader callback
    private BaseLoaderCallback mLoaderCallback =
            new BaseLoaderCallback(this) {
                @Override
                public void onManagerConnected(final int status) {
                    switch (status) {
                        case LoaderCallbackInterface.SUCCESS:
                            Log.d(TAG, "OpenCV loaded successfully");
                            mCameraView.enableView();
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
            mCameraIndex = savedInstanceState.getInt(STATE_CAMERA_INDEX, 0);
            mImageSizeIndex = savedInstanceState.getInt(STATE_IMAGE_SIZE_INDEX, 0);
        } else {
            mCameraIndex = 0;
            mImageSizeIndex = 0;
        }
        // Get camera info
        final Camera camera;
        CameraInfo cameraInfo = new CameraInfo();
        Camera.getCameraInfo(mCameraIndex, cameraInfo);
        mIsCameraFrontFacing = (cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT);
        mNumCameras = Camera.getNumberOfCameras();
        camera = Camera.open(mCameraIndex);
        final Camera.Parameters parameters = camera.getParameters();
        camera.release();
        mSupportedImageSizes = parameters.getSupportedPreviewSizes();
        final Size size = mSupportedImageSizes.get(mImageSizeIndex);
        // Configure view
        mCameraView = (JavaCameraView) findViewById(R.id.camera_view);
        mCameraView.setCameraIndex(mCameraIndex);
        mCameraView.setMaxFrameSize(size.width, size.height);
        mCameraView.setCvCameraViewListener(this);
    }

    /**
     * Disable camera when the activity goes into background.
     */
    @Override
    public void onPause() {
        if (mCameraView != null) {
            mCameraView.disableView();
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
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallback);
        mIsMenuLocked = false;
    }

    /**
     * Disable camera when the activity finishes.
     */
    @Override
    public void onDestroy() {
        if (mCameraView != null) {
            mCameraView.disableView();
        }
        super.onDestroy();
    }

    /**
     * Saves the current camera index and the image size index.
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the current camera index
        savedInstanceState.putInt(STATE_CAMERA_INDEX, mCameraIndex);
        // Save the current image size index
        savedInstanceState.putInt(STATE_IMAGE_SIZE_INDEX, mImageSizeIndex);
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
        if (mNumCameras < 2) {
            // Remove the option to switch cameras, since there is only 1
            menu.removeItem(R.id.menu_next_camera);
        }
        int numSupportedImageSizes = mSupportedImageSizes.size();
        if (numSupportedImageSizes > 1) {
            final SubMenu sizeSubMenu = menu.addSubMenu(R.string.menu_image_size);
            for (int i = 0; i < numSupportedImageSizes; i++) {
                final Size size = mSupportedImageSizes.get(i);
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
        if (mIsMenuLocked) {
            return true;
        }
        if (item.getGroupId() == MENU_GROUP_ID_SIZE) {
            mImageSizeIndex = item.getItemId();
            recreate();
            return true;
        }
        switch (item.getItemId()) {
            case R.id.menu_next_camera:
                mIsMenuLocked = true;
                // With another camera index, recreate the activity
                mCameraIndex++;
                if (mCameraIndex == mNumCameras) {
                    mCameraIndex = 0;
                }
                mImageSizeIndex = 0;
                recreate();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        final Mat rgba = inputFrame.rgba();
        if (mIsCameraFrontFacing) {
            // Mirror (horizontally flip) the preview
            Core.flip(rgba, rgba, 1);
        }
        return rgba;
    }
}

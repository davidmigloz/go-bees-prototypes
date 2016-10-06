# GoBees prototypes

Tests and prototypes for [GoBees](https://github.com/davidmigloz/go-bees) project.

#### 0_HelloWorld
Hello world app to test OpenCV in Android.

#### 1_CameraFeed
App that reads the camera feed and shows it on the screen. It allows to select the desired camera and change the image size.

#### 2_BackgroundSub
App based on 1_CameraFeed that implements four subtraction algorithms:
- Frame differencing
- Average background model
- BackgroundSubtractorMOG2
- KNNSubtractor

#### 3_DevelopmentPlatform
Java app to speed up the development of the GoBees's computer vision algoritms.
The main advantage is that we can read a video from a file, instead of reading always the camera feed (this cannot be done in OpenCV4Android 3.1.0).
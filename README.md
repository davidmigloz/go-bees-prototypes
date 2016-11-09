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
Java app to speed up the development of the GoBees's computer vision algorithms. It allows to easily parameterize all the steps of the algorithm.

#### 3_CountingPlatform
Java app to manually count bees from a dataset of frames. It saves the results in a `numbees.txt` file that later can be used to test the bee counter algorithm.
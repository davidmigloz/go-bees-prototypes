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

> [developmentplatform.v1.jar](https://github.com/davidmigloz/go-bees-prototypes/releases/download/v1/developmentplatform.v1.jar)

![DevelopmentPlatform](http://go-bees.readthedocs.io/es/develop/_images/devplatform1.png)

#### 4_CountingPlatform
Java app to manually count bees from a dataset of frames. It saves the results in a `numbees.txt` file that later can be used to test the bee counter algorithm.

> [countingplatform.v1.jar](https://github.com/davidmigloz/go-bees-prototypes/releases/download/v1/countingplatform.v1.jar)

![CountingPlatform](http://go-bees.readthedocs.io/es/develop/_images/counting_platform1.png)

#### 5_AndroidCameraOpenCV
Android Camera implementation that retrieves the frames in OpenCV `Mat` format.

#### 6_OpenWeatherMap
Simple Android app that gets weather data from OpenWeatherMap.org API.

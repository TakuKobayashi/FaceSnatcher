#include <iostream>
#include <opencv2/opencv.hpp>
#include <opencv2/core/core.hpp>
#include <opencv2/contrib/detection_based_tracker.hpp>

using namespace cv;

class Detector {
public:
    Detector(int pixels[]);
    virtual ~Detector();
    void faceDetect();
private:
    Mat mSrcImg;
};

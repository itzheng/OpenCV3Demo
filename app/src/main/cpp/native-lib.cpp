#include <jni.h>
#include <string>
#include "opencv2/opencv.hpp"

using namespace cv;
//using namespace std;

extern "C" JNIEXPORT jstring JNICALL
Java_org_itzheng_opencv3_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

/**
 * openCv 方法 将图片转成灰色
 * @param srcPath 原始图片路径
 * @param descPath 转换后保存路径
 */
void openCvToGray(char *srcPath, char *descPath) {
    Mat src = imread(srcPath);//读取图片，要提前将图片放在程序路径下
    cvtColor(src, src, CV_BGR2GRAY);//转为灰度图
    imwrite(descPath, src);//保存结果图片
}

/**
 * 传入图片地址，将图片变成灰色
 */
extern "C"
JNIEXPORT void JNICALL
Java_org_itzheng_opencv3_MainActivity_openCVToGray(JNIEnv *env, jobject thiz, jstring jSrcPath,
                                                   jstring jDescPath) {
    char *srcPath = const_cast<char *>(env->GetStringUTFChars(jSrcPath, NULL));
    char *descPath = const_cast<char *>(env->GetStringUTFChars(jDescPath, NULL));
    openCvToGray(srcPath, descPath);
}
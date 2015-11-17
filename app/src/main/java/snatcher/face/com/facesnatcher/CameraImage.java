package snatcher.face.com.facesnatcher;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class CameraImage {
  private HashMap<String, ArrayList<Rect>> mDetectedParts;
  private Bitmap mSrcImage;
  private Bitmap mGrayscaleImage;
  private boolean isUploaded;
  private int width;
  private int height;
  private int degree;

  public CameraImage(int w, int h, int deg){
    mDetectedParts = new HashMap<String, ArrayList<Rect>>();
    mSrcImage = null;
    degree = deg;
    width = w;
    height = h;
  }

  public void setSrcImage(int[] pixels){
    if(mSrcImage != null){
      mSrcImage.recycle();
      mSrcImage = null;
    }
    Bitmap image = Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888);
    mSrcImage = ApplicationHelper.bitmapRotate(image, degree);
    //mSrcImage = ApplicationHelper.bitmapRotate(image, 0);
    pixels = null;
    //image.recycle();
    //image = null;
  }

  public void setGrayscaleImage(int[] pixels){
    if(mGrayscaleImage != null){
      mGrayscaleImage.recycle();
      mGrayscaleImage = null;
    }
    Bitmap image = Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888);
    mGrayscaleImage = ApplicationHelper.bitmapRotate(image, degree);
    //mGrayscaleImage = ApplicationHelper.bitmapRotate(image, 0);
    pixels = null;
    //image.recycle();
    //image = null;
  }

  public void clipRectImageAndUpload(Context context, ArrayList<Rect> rectList){
    if(isUploaded) return;
    isUploaded = true;
    for(Rect rect : rectList) {
      int x = Math.max(rect.left, 0);
      int y = Math.max(rect.top, 0);
      int right = Math.min(rect.right, getWidth());
      int bottom = Math.min(rect.bottom, getHeight());
      Bitmap clipped = Bitmap.createBitmap(mSrcImage, x, y, right - x, bottom - y);
      UploadActivity.uploadFace(context, clipped);
    }
  }

  public void setDetectedPart(String key, ArrayList<Rect> results){
    mDetectedParts.put(key, results);
  }

  public int getWidth(){
    return width;
  }

  public int getHeight(){
    return height;
  }

  public int getDegree(){
    return degree;
  }

  public Bitmap getSrcImage(){
    return mSrcImage;
  }

  public Bitmap getGrayscaleImage(){
    return mGrayscaleImage;
  }

  public HashMap<String, ArrayList<Rect>> getDetectedParts(){
    return mDetectedParts;
  }

  public void release(){
    if(mSrcImage != null){
      mSrcImage.recycle();
      mSrcImage = null;
    }
    if(mGrayscaleImage != null){
      mGrayscaleImage.recycle();
      mGrayscaleImage = null;
    }
    mDetectedParts.clear();
  }
}

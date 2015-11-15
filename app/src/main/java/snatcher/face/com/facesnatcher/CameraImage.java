package snatcher.face.com.facesnatcher;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class CameraImage {
  private HashMap<String, ArrayList<Rect>> mDetectedParts;
  private Bitmap mSrcImage;
  private Bitmap mGrayscaleImage;
  public int width;
  public int height;
  public int degree;

  public CameraImage(int w, int h){
    mDetectedParts = new HashMap<String, ArrayList<Rect>>();
    mSrcImage = null;
    width = w;
    height = h;
  }

  public void setSrcImage(int[] pixels){
    Log.d(Config.DEBUG_KEY, "w:" + width + " h:" + height + " l:" + pixels.length);
    if(mSrcImage != null){
      mSrcImage.recycle();
      mSrcImage = null;
    }
    mSrcImage = Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888);
    pixels = null;
  }

  public void setGrayscaleImage(int[] pixels){
    Log.d(Config.DEBUG_KEY, "w:" + width + " h:" + height + " l:" + pixels.length);
    if(mGrayscaleImage != null){
      mGrayscaleImage.recycle();
      mGrayscaleImage = null;
    }
    mGrayscaleImage = Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888);
    pixels = null;
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

  public Bitmap getSrcImage(){
    return mSrcImage;
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

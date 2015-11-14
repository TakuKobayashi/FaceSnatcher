package snatcher.face.com.facesnatcher;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class CameraImage {
  private HashMap<String, ArrayList<Rect>> mDetectedParts;
  private Bitmap mSrcImage;

  public CameraImage(){
    mDetectedParts = new HashMap<String, ArrayList<Rect>>();
    mSrcImage = null;
  }

  public void setSrcImage(int width, int height, int[] pixels){
    Log.d(Config.DEBUG_KEY, "w:" + width + " h:" + height + " l:" + pixels.length);
    mSrcImage = Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888);
    pixels = null;
  }

  public void setDetectedPart(String key, ArrayList<Rect> results){
    mDetectedParts.put(key, results);
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
    mDetectedParts.clear();
  }
}

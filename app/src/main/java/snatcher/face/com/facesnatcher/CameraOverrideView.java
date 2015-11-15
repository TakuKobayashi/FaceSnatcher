package snatcher.face.com.facesnatcher;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CameraOverrideView extends ImageView {
  private HashMap<String, ArrayList<Rect>> mCaptured;
  private Paint mPaint;

  public CameraOverrideView(Context context, AttributeSet attrs) {
    super(context, attrs);
    setup();
  }

  private void setup(){
    mCaptured = new HashMap<String, ArrayList<Rect>>();
    mPaint = new Paint();
  }

  protected void onDraw(Canvas c) {
    super.onDraw(c);
    for(Map.Entry<String, ArrayList<Rect>> kr : mCaptured.entrySet()){
      for(Rect r : kr.getValue()){
        //c.drawRect(r, mPaint);
      }
    }
    this.invalidate();
  }

  public void putDetectedRect(String key, ArrayList<Rect> rectList){
    mCaptured.put(key, rectList);
    this.invalidate();
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    this.invalidate();
  }
}
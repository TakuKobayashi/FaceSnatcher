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

import java.util.HashMap;
import java.util.Map;

public class CameraOverrideView extends ImageView {
  private HashMap<String, RectF> mCaptured;
  private Paint mPaint;

  public CameraOverrideView(Context context, AttributeSet attrs) {
    super(context, attrs);
    setup();
  }

  private void setup(){
    mCaptured = new HashMap<String,RectF>();
    mCaptured.put("hogehoge", new RectF(100,100,200,200));
    mPaint = new Paint();
    mPaint.setColor(Color.RED);
    mPaint.setStyle(Paint.Style.STROKE);
  }

  protected void onDraw(Canvas c) {
    super.onDraw(c);
    for(Map.Entry<String, RectF> kr : mCaptured.entrySet()){
      c.drawRect(kr.getValue(), mPaint);
    }
    this.invalidate();
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    this.invalidate();
  }
}
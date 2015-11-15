package snatcher.face.com.facesnatcher;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CameraOverrideEffectView extends ImageView {
  private Paint mPaint;
  private int alpha = 0;
  private Bitmap effect = null;
  private int counter = 0;

  public CameraOverrideEffectView(Context context, AttributeSet attrs) {
    super(context, attrs);
    mPaint = new Paint();
  }

  protected void onDraw(Canvas c) {
    super.onDraw(c);
    if(effect != null){
      counter++;
      Log.d(Config.DEBUG_KEY, "a:" + alpha);
      mPaint.setAlpha(alpha);
      c.drawBitmap(effect, null, new Rect(0, 0, c.getWidth(), c.getHeight()), mPaint);
      counter++;
      if(counter > 500){
        alpha--;
      }
      if(alpha >= 0){
        release();
      }
    }
    this.invalidate();
  }

  public void setEffect(Bitmap image){
    if(effect != null) return;
    effect = image;
    alpha = 256;
    this.invalidate();
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    this.invalidate();
  }

  public void release(){
    if(effect != null){
      effect.recycle();
      effect = null;
    }
    counter = 0;
  }
}
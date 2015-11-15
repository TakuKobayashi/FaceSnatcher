package snatcher.face.com.facesnatcher;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class CameraActivity extends Activity {
    static {
        if (!OpenCVLoader.initDebug()) {
            Log.d(Config.DEBUG_KEY, "Filed OpenCVLoader.initDebug()");
        }
    }

    private Camera mCamera;
    private SurfaceTexture mTexture;
    private CameraOverrideView mCameraOverrideView;
    private CascadeClassifier mCascadeClassifier;
    private CameraImage mCameraImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera);
        mCameraOverrideView = (CameraOverrideView) findViewById(R.id.camera_override_view);
        mCascadeClassifier = new CascadeClassifier(copyAndGetPath("lbpcascade_frontalface.xml", R.raw.lbpcascade_frontalface));
        setupPreview();
    }

    //OpenCVによる検出処理
    private ArrayList<Rect> doDetection(CascadeClassifier cascade, Bitmap image){
        Mat grayScale = new Mat();
        Utils.bitmapToMat(image, grayScale);
        //顔検出cascade.xmlの読み込み
        MatOfRect matRect = new MatOfRect();
        List FacesRectList = matRect.toList();
        ArrayList<Rect> RectList = new ArrayList<Rect>();

        cascade.detectMultiScale(grayScale, matRect);
        List<Rect> DetectedRectList = matRect.toList();
        for (int i=0; i < DetectedRectList.size(); i++){
            Rect rct = DetectedRectList.get(i);
            RectList.add(new Rect(rct.x,rct.y,rct.x + rct.width,rct.y+rct.height));
        }
        return RectList;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ApplicationHelper.releaseImageView(mCameraOverrideView);
        mCameraImage.release();
        if(mTexture != null) {
            mTexture.release();
            mTexture = null;
        }
    }

    private void setupPreview(){
        TextureView preview = (TextureView) findViewById(R.id.camera_preview);
        preview.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                Log.d(Config.DEBUG_KEY, "available width:" + width + " height:" + height + " time:" + surface.getTimestamp());
                mTexture = surface;
                setupCamera();
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                //Log.d(Config.DEBUG_KEY, "changed width:" + width + " height:" + height + " time:" + surface.getTimestamp());
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                Log.d(Config.DEBUG_KEY, "destroyed time:" + surface.getTimestamp());
                releaseCamera();
                return false;
            }

            //毎フレーム呼ばれる
            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
                //getTimeStampは更新されたナノ秒単位の値を取得
                //Log.d(Config.DEBUG_KEY, "updated time:" + surface.getTimestamp());
            }
        });
    }

    private void setupCamera(){
        try {
            mCamera = Camera.open(1); // attempt to get a Camera instance
            mCamera.setPreviewTexture(mTexture);
            Camera.Parameters params = mCamera.getParameters();
            mCameraImage = new CameraImage(params.getPreviewSize().width, params.getPreviewSize().height);
            //今回はフロントカメラのみなのでCameraIdは0のみ使う
            mCamera.setDisplayOrientation(ApplicationHelper.getCameraDisplayOrientation(this, 1));
            mCamera.setPreviewCallback(mPreviewCallback);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }

    Camera.PreviewCallback mPreviewCallback = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            Log.d(Config.DEBUG_KEY, "length:" + data.length + " width:" + camera.getParameters().getPreviewSize().width + " height:" + camera.getParameters().getPreviewSize().height);
            NativeHelper.decodeYUV420SP(data, mCameraImage);
            //Bitmap image = Bitmap.createBitmap(rgb, camera.getParameters().getPreviewSize().width, camera.getParameters().getPreviewSize().height, Bitmap.Config.ARGB_8888);
            ApplicationHelper.releaseImageView(mCameraOverrideView);
            Log.d(Config.DEBUG_KEY, " " + doDetection(mCascadeClassifier, mCameraImage.getGrayscaleImage()));
            mCameraOverrideView.setImageBitmap(mCameraImage.getSrcImage());
        }
    };

    private void releaseCamera(){
        if (mCamera != null){
            mCamera.cancelAutoFocus();
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        };
    }

    private String copyAndGetPath(String name, int id) {
        try {
            InputStream is = this.getResources().openRawResource(id);
            File cascadeDir = this.getDir("cascade", Context.MODE_PRIVATE);
            File cascadeFile = new File(cascadeDir, name);
            FileOutputStream os = new FileOutputStream(cascadeFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();

            return cascadeFile.getAbsolutePath();
        } catch (Exception e) {
            return "";
        }
    }
}

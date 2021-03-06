package snatcher.face.com.facesnatcher;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
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
    private CameraOverrideEffectView mEffectView;
    private CameraImage mCameraImage;
    private boolean isOpened = false;
    private boolean isTap = false;
    private FaceDetector mFaceDetector;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.camera);
        mCameraOverrideView = (CameraOverrideView) findViewById(R.id.camera_override_view);
        mEffectView = (CameraOverrideEffectView) findViewById(R.id.camera_override_effect_view);
        mCascadeClassifier = new CascadeClassifier(copyAndGetPath("haarcascade_frontalface_default.xml", R.raw.haarcascade_frontalface_default));
        //mCascadeClassifier = new CascadeClassifier(copyAndGetPath("haarcascade_frontalface_default.xml", R.raw.haarcascade_frontalface_default));
        setupPreview();
        Button button = (Button) findViewById(R.id.nextPageButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CameraActivity.this, FaceListActivity.class);
                startActivity(intent);
                finish();
            }
        });

        FaceDetector.Builder builder = new FaceDetector.Builder(this);
        // 動画か静止画か。動画ならtrue
        builder.setTrackingEnabled(true);
        builder.setLandmarkType(FaceDetector.ALL_LANDMARKS);
        builder.setMode(FaceDetector.ACCURATE_MODE);
        builder.setClassificationType(FaceDetector.ALL_CLASSIFICATIONS);

        mFaceDetector = builder.build();
    }

    //OpenCVによる検出処理
    private ArrayList<Rect> doDetection(CascadeClassifier cascade, Bitmap image){
        Mat grayScale = new Mat();
        Utils.bitmapToMat(image, grayScale);
        //顔検出cascade.xmlの読み込み
        MatOfRect matRect = new MatOfRect();
        ArrayList<Rect> RectList = new ArrayList<Rect>();

        cascade.detectMultiScale(grayScale, matRect);
        List<org.opencv.core.Rect> DetectedRectList = matRect.toList();
        for (int i=0; i < DetectedRectList.size(); i++){
            org.opencv.core.Rect rct = DetectedRectList.get(i);
            int x = Math.max(0, rct.x - mCameraImage.getSrcImage().getWidth() / 100);
            int y = Math.max(0, rct.y - mCameraImage.getSrcImage().getHeight() / 100);
            int right = Math.min(rct.x + rct.width + mCameraImage.getSrcImage().getWidth() / 50, mCameraImage.getSrcImage().getWidth());
            int bottom = Math.min(rct.y+rct.height + mCameraImage.getSrcImage().getHeight() / 50, mCameraImage.getSrcImage().getHeight());
            RectList.add(new Rect(x,y,right,bottom));
        }
        return RectList;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mTexture != null){
            setupCamera();
        }
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
        mEffectView.release();
        if(mTexture != null) {
            mTexture.release();
            mTexture = null;
        }
        mFaceDetector.release();
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
        if(isOpened) return;
        isOpened = true;
        try {
            mCamera = Camera.open(1); // attempt to get a Camera instance
            mCamera.setPreviewTexture(mTexture);
            Camera.Parameters params = mCamera.getParameters();
            int ori = ApplicationHelper.getCameraDisplayOrientation(this, 1);
            Camera.Size size = ApplicationHelper.getFitNearlySize(params.getSupportedPreviewSizes(), 500, ori);
            mCameraImage = new CameraImage(size.width, size.height, ori);
            params.setPreviewSize(size.width, size.height);
            mCamera.setParameters(params);
            //今回はフロントカメラのみなのでCameraIdは0のみ使う
            mCamera.setDisplayOrientation(mCameraImage.getDegree());
            mCamera.setPreviewCallback(mPreviewCallback);
            mCamera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            isTap = true;
        }else if(event.getAction() == MotionEvent.ACTION_UP){
            isTap = false;
        }
        return true;
    }

    Camera.PreviewCallback mPreviewCallback = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            Log.d(Config.DEBUG_KEY, "length:" + data.length + " width:" + camera.getParameters().getPreviewSize().width + " height:" + camera.getParameters().getPreviewSize().height);
            NativeHelper.decodeYUV420SP(data, mCameraImage);
            //Bitmap image = Bitmap.createBitmap(rgb, camera.getParameters().getPreviewSize().width, camera.getParameters().getPreviewSize().height, Bitmap.Config.ARGB_8888);
            ApplicationHelper.releaseImageView(mCameraOverrideView);
            ArrayList<Rect> recList = new ArrayList<Rect>();
            //ArrayList<Rect> recList = doDetection(mCascadeClassifier, mCameraImage.getGrayscaleImage());
            //Log.d(Config.DEBUG_KEY, " " + recList);
            Frame.Builder builder = new Frame.Builder();
            builder.setBitmap(mCameraImage.getSrcImage());

            SparseArray<Face> faces = mFaceDetector.detect(builder.build());
            for(int i = 0;i < faces.size();++i){
                Face face = faces.get(i);
                Log.d(Config.DEBUG_KEY, "角度 Y:" + face.getEulerY() + " Z:" + face.getEulerZ());
                Log.d(Config.DEBUG_KEY, "Position:" + face.getPosition() + " width:" + face.getWidth() + " height:" + face.getHeight());
                Log.d(Config.DEBUG_KEY, "leftEye:" + face.getIsLeftEyeOpenProbability() + " rightEye:" + face.getIsRightEyeOpenProbability() + " smiling:" + face.getIsSmilingProbability());
                for(Landmark l : face.getLandmarks()){
                    Log.d(Config.DEBUG_KEY, "type:" + l.getType() + " position:" + l.getPosition());
                }
            }

            mCameraOverrideView.putDetectedRect(mCameraImage.getSrcImage(), "lbpcascade_frontalface", recList);
            if(!recList.isEmpty() && isTap) {
                Bitmap subbmp = mCameraImage.getSrcImage().copy(Bitmap.Config.ARGB_8888, true);
                int width = subbmp.getWidth();
                int height = subbmp.getHeight();
                int[] pixels = new int[width * height];
                subbmp.getPixels(pixels, 0, width, 0, 0, width, height);
                pixels = NativeHelper.negative(pixels, width, height);
                subbmp.setPixels(pixels, 0, width, 0, 0, width, height);
                mEffectView.setEffect(subbmp);
                //mCameraImage.clipRectImageAndUpload(CameraActivity.this, recList);
            }
            //mCameraOverrideView.putDetectedRect("haarcascade_frontalface_default", recList);
        }
    };

    private void releaseCamera(){
        isOpened = false;
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

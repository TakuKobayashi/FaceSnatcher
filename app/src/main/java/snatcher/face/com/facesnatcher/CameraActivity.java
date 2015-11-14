package snatcher.face.com.facesnatcher;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;

import java.io.IOException;

public class CameraActivity extends Activity {

    private Camera mCamera;
    private SurfaceTexture mTexture;
    private CameraOverrideView mCameraOverrideView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera);
        mCameraOverrideView = (CameraOverrideView) findViewById(R.id.camera_override_view);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupPreview();
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
        if(mTexture != null) {
            mTexture.release();
            mTexture = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
                Log.d(Config.DEBUG_KEY, "changed width:" + width + " height:" + height + " time:" + surface.getTimestamp());
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
                Log.d(Config.DEBUG_KEY, "updated time:" + surface.getTimestamp());
            }
        });
    }

    private void setupCamera(){
        try {
            mCamera = Camera.open(); // attempt to get a Camera instance
            mCamera.setPreviewTexture(mTexture);
            //今回はフロントカメラのみなのでCameraIdは0のみ使う
            mCamera.setDisplayOrientation(ApplicationHelper.getCameraDisplayOrientation(this, 0));
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }


    private void releaseCamera(){
        if (mCamera != null){
            mCamera.cancelAutoFocus();
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        };
    }
}

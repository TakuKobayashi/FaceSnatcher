package snatcher.face.com.facesnatcher;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends Activity {
    private static int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        requestPermission();
    }

    private void requestPermission(){
        if(Build.VERSION.SDK_INT >= 23) {
            ArrayList<String> permissions = ApplicationHelper.getSettingPermissions(this);
            boolean isRequestPermission = false;
            for(String permission : permissions){
                if(!ApplicationHelper.hasSelfPermission(this, permission)){
                    isRequestPermission = true;
                    break;
                }
            }
            if(isRequestPermission) {
                requestPermissions(permissions.toArray(new String[0]), REQUEST_CODE);
            }
        }
    }

    @OnClick(R.id.button)
    public void onClickButton(View v) {
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.button2)
    public void onClickButton2(View v) {
//        Intent intent = new Intent(this, SoundActivity.class);
//        startActivity(intent);
    }

    @OnClick(R.id.btn_upload_image)
    public void onClickUploadImage(View v){
        Intent intent = new Intent(this, UploadActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.btn_s3_sample)
    public void onClickS3Sample(View v){
        Intent intent = new Intent(this, com.mysampleapp.SplashActivity.class);
        startActivity(intent);
    }
}

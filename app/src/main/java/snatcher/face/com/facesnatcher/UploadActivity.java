package snatcher.face.com.facesnatcher;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.OnClick;

public final class UploadActivity extends Activity {
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_upload_start)
    public void onClickUploadStart(View v){
        Toast.makeText(this, "onClickUploadStart", Toast.LENGTH_LONG).show();
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.female_face);
     }
}

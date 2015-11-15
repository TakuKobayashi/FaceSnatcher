package snatcher.face.com.facesnatcher;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.kii.cloud.storage.Kii;
import com.kii.cloud.storage.KiiObject;
import com.kii.cloud.storage.KiiUser;
import com.kii.cloud.storage.exception.app.AppException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.OnClick;

public final class UploadActivity extends Activity {

    private Context mContext;

    static void uploadFace(Context context, Bitmap bitmap){
        upload(context, bitmap);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        ButterKnife.bind(this);
        mContext = this;
    }

    @OnClick(R.id.btn_upload_start)
    public void onClickUploadStart(View v) {
        Toast.makeText(this, "onClickUploadStart", Toast.LENGTH_LONG).show();
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.female_face);
        upload(this, bitmap);
    }

    private static void upload(final Context context, final Bitmap bitmap) {
        final String path = saveBitmapToCache(context, bitmap);
        final File file = new File(path);
//        Toast.makeText(this, path, Toast.LENGTH_SHORT).show();

        new Thread(new Runnable() { //新規スレッドを作成
            public void run() {
                // Use the access token to sign-in again
                try {
                    KiiUser.loginWithToken(AppConstants.ACCESS_TOKEN);
                } catch (IOException e) {
                    // Sign-in failed for some reasons
                    // Please check IOExecption to see what went wrong...
                    Log.e("TAG", "IOException");
                    return;
                } catch (AppException e) {
                    // Sign-in failed for some reasons
                    // Please check AppException to see what went wrong...
                    Log.e("TAG", "AppException");
                    return;
                }

                // Create an object in an application-scope bucket.
                KiiObject object = Kii.bucket("tutorial").object();

// Set key-value pairs
                object.set("score", System.currentTimeMillis());
                object.set("mode", "easy");
                object.set("premiumUser", false);


// Save the object
                try {
                    object.save();
                } catch (IOException e) {
                    // Handle error
                } catch (AppException e) {
                    // Handle error
                }

// Start uploading
                try {
                    object.uploadBody(file, "image/bmp");
                } catch (IOException e) {
                    // Handle error
                } catch (AppException e) {
                    // Handle error
                }
                Log.e("TAG", "Upload COMPLETE!!!!");
            }
        }).start();
    }

    public static String saveBitmapToCache(final Context context, Bitmap mBitmap) {
        try {
            // sdcardフォルダを指定
            File root = context.getCacheDir();

            // 日付でファイル名を作成　
            Date mDate = new Date();
            SimpleDateFormat fileName = new SimpleDateFormat("yyyyMMdd_HHmmss");

            // 保存処理開始
            FileOutputStream fos = null;
            final String path = root + "/" + fileName.format(mDate) + ".jpg";

            fos = new FileOutputStream(new File(path));

            // jpegで保存
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

            // 保存処理終了
            fos.close();

            return path;
        } catch (Exception e) {
            Log.e("Error", "" + e.toString());
        }

        return null;
    }

}

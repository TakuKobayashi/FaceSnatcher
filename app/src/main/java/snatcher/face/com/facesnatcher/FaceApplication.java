package snatcher.face.com.facesnatcher;

import android.app.Application;

import com.kii.cloud.storage.Kii;

public class FaceApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Kii.initialize(AppConstants.APP_ID, AppConstants.APP_KEY,
                AppConstants.APP_SITE);


    }
}

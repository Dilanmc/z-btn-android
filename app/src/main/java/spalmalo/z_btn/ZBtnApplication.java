package spalmalo.z_btn;

import android.app.Application;

import io.realm.Realm;


public class ZBtnApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
    }
}

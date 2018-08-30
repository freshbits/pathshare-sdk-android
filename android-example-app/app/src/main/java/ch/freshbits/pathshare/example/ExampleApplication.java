package ch.freshbits.pathshare.example;

import android.app.Application;

import ch.freshbits.pathshare.sdk.Pathshare;
import ch.freshbits.pathshare.sdk.location.TrackingMode;

public class ExampleApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Pathshare.initialize(this, getString(R.string.pathshare_account_token), TrackingMode.SMART);
    }
}

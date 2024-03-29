package inc.osbay.android.tutorroom;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.facebook.drawee.backends.pipeline.Fresco;

import inc.osbay.android.tutorroom.sdk.TRSDK;
import inc.osbay.android.tutorroom.sdk.listener.StatusListener;
import inc.osbay.android.tutorroom.utils.WSMessageClient;

public class TRApplication extends Application {
    private static final String TAG = TRApplication.class.getSimpleName();
    private static TRApplication sInstance;
    private WSMessageClient mWSMessageClient;

    public static TRApplication getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        // Initialize TutorMandarin SDK
        TRSDK.initialize(getApplicationContext(), new StatusListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "TutorRoom sdk initialized.");
            }

            @Override
            public void onError(int errorCode) {
                Log.e(TAG, "TutorRoom sdk initialize error.");
            }
        });


        mWSMessageClient = new WSMessageClient(getApplicationContext());
        mWSMessageClient.doBindService();

        // Setup handler for crashing or uncaught exceptions.
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable e) {
                handleUncaughtException(thread, e);
            }
        });

        // Initialize Fresco SDK
        Fresco.initialize(getApplicationContext());
    }

    public void handleUncaughtException(Thread thread, Throwable e) {
        e.printStackTrace(); // not all Android versions will print the stack trace automatically

        Intent intent = new Intent();
        intent.setAction("inc.osbay.android.tutorroom.SEND_LOG");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // required when starting from Application
        startActivity(intent);

        System.exit(1); // kill off the crashed app
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        mWSMessageClient.doUnbindService();
    }

    public WSMessageClient getWSMessageClient() {
        return mWSMessageClient;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}

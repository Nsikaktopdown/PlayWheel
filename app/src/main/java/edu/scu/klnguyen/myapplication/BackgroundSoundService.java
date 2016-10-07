package edu.scu.klnguyen.myapplication;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;

public class BackgroundSoundService extends Service {
    private static final String TAG = null;
    private static final int VOL = 100;

    MediaPlayer player;
    String fileName;
    boolean looping;
    int vol = VOL;

    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle extras = intent.getExtras();

        if (extras != null) {
            fileName = extras.getString("fileName");    // get sound file name from calling activity
            looping = extras.getBoolean("looping");     // get looping from calling activity
        }

        int resID=getResources().getIdentifier(fileName, "raw", getPackageName());

        player=MediaPlayer.create(this,resID);

        if ( looping)
            player.setLooping(true);    // Set looping
        else
            player.setLooping(false);    // Set looping

        player.setVolume(vol, vol);
        player.start();
        return 1;
    }

    public IBinder onUnBind(Intent arg0) {
        // TO DO Auto-generated method
        return null;
    }

    @Override
    public void onDestroy() {
        player.stop();
        player.reset();
        player.release();
    }
}

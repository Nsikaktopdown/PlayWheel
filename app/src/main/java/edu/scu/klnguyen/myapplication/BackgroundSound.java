package edu.scu.klnguyen.myapplication;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.AsyncTask;

/**
 * Created by kim long on 5/31/2016.
 */
public class BackgroundSound extends AsyncTask<String, Void, Void> {
    private Context context;

    public BackgroundSound (Context c){
        context = c;
    }

    @Override
    protected Void doInBackground(String... params) {
        String fileName = params[0];

        int resID = context.getResources().getIdentifier(fileName, "raw", context.getPackageName());
        MediaPlayer player = MediaPlayer.create(context, resID);

        player.setLooping(true); // Set looping
        player.setVolume(1.0f, 1.0f);
        player.start();

        return null;
    }

}

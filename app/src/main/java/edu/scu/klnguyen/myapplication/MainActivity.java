package edu.scu.klnguyen.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends OverFlowMenuActivity{
    private static String MUSIC_FILE = "love";
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private int level = 0;                      // playing level : 1 - midium, 2 - hard

    private String musicFile = MUSIC_FILE;       // background music file
    private Intent musicIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createActionBar("Play Wheel");          // add actionBar on top
    }

    private void playMusic() {
        musicIntent = new Intent                      // start playing wheel spinning sound in the background
                (this, BackgroundSoundService.class);
        musicIntent.putExtra("fileName", musicFile);         // pass sound file name
        musicIntent.putExtra("looping", true);               // set looping to tree
        startService(musicIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        playMusic();                            // play background music
    }

    @Override
    protected void onPause() {
        stopService(musicIntent);   // stop background music
        super.onPause();
    }

    // listener to 'exit' button
    // exit app, clase all activities
    public void exitApp(View v) {
        onBackPressed();
        //finishAffinity();
    }

    // listener for 'play' botton
    public void startPlay(View v) {
        setLevel();                             // set user chosen playing level
        Intent intent = new Intent              // start the next activity
                (getApplicationContext(), SpinActivity2.class);

        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        //finish(); // finish activity

    }

    // save user chosen playing level to sharePreferences
    public void setLevel() {
        radioGroup = (RadioGroup) findViewById(R.id.group);
        int selectedId = radioGroup.getCheckedRadioButtonId();
        radioButton = (RadioButton)findViewById(selectedId);

        String levelStr = radioButton.getText().toString();     // read level from selected button

        if ( levelStr.equals("Moderate")) {                     // assign 1 to "moderate"
            level = 1;
        }
        if ( levelStr.equals("Hard")) {                         // assign 2 to "hard"
            level = 2;
        }

        SharedPreferences sharedPreferences =
                getSharedPreferences("sharedPreferences",
                Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("level", level);                          // save level to sharePreferences
        editor.commit();
    }
}

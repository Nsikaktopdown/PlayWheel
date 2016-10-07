package edu.scu.klnguyen.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SpinActivity extends OverFlowMenuActivity implements  Runnable{
    private static int HOLDING = 1000;                  // 1 second

    private TextView spinButton;                        // 'spin' button
    private ImageView ivWheel;                          // view to hold the wheel
    private List<Bitmap> bitmaps = new ArrayList<>();   // list of wheel images to show
    private long startTime, currentTime,totaltime;      // time variables
    private float tickTimeSeconds = .05F;
    private int count=0,duration =30, size;
    private static final String TAG ="## My Info ##";

    private String category = "";                   // puzzle category
    private String puzzle = "";                     // puzzle

    private String audioFile = "spinning";          // wheel spinning sound file
    private Intent musicIntent;

    private Thread t;                               // threed

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spin_wheel);

        createActionBar("Spin the Wheel");          // add actionBar on top

        // add all wheel images to one array for easier access
        bitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.wheel_10));
        bitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.wheel_11));
        bitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.wheel_12));
        bitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.wheel_13));
        bitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.wheel_14));
        bitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.wheel_15));
        bitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.wheel_16));
        bitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.wheel_17));
        size= bitmaps.size();

        ivWheel = (ImageView)findViewById(R.id.ivWheel);
        ivWheel.setImageBitmap(bitmaps.get(0));
        spinButton = (TextView)findViewById(R.id.btnSpin);

        // listener for 'spin' botton
        spinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count %= size;
                // Words will be displayed between 30 and 30+words.length times.
                // This also determines which word is the last word displayed.
                duration = ((int) ((Math.random() * size) + 30)) * 2;
                Log.i(TAG, "duration= " + duration);
                t = new Thread(SpinActivity.this);
                t.start();                                  // start  thread to spin the wheel
            }
        });
    }

    //@Override
    //protected void onPause() {
    //    stopService(musicIntent);   // stop background music
    //    super.onPause();
    //}

    // listener to 'exit' button
    // exit app, clase all activities
    public void exit_app(View v) {
        //t.stop();
        //t.interrupt();
        finishAffinity();
    }

    // this thread starts when 'spin' button clicks
    public void run(){
        spin();                     // spin the wheel
        setCategory();              // save the category

        try {
            readPuzzle();           // read a random puzzle from input file
        } catch (IOException e) {
            e.printStackTrace();
        }

        setPuzzle();                // save puzzle in sharePreferences

        long time1 =System.currentTimeMillis();         // this lock just keep the screen on for 1 second
        while(true) {                                   // before moving to the next screen, enough time
            long time2 = System.currentTimeMillis();    // for user to read the category when the wheel
            long interval = time2 - time1;              // stop spinning

            if ( interval > HOLDING) {
                break;
            }
        }

        Intent intent = new Intent                  // start the next activity
                (getApplicationContext(), ChooseLettersActivity.class);
        startActivity(intent);
    }

    // spin the wheel
    public void spin() {
        startTime = System.currentTimeMillis();          // get current time

        musicIntent = new Intent                      // start playing wheel spinning sound in the background
                (this, BackgroundSoundService.class);
        musicIntent.putExtra("fileName", audioFile);         // pass sound file name
        musicIntent.putExtra("looping", true);               // set looping to true
        startService(musicIntent);

        do {
            currentTime = System.currentTimeMillis();   // current time
            totaltime = (currentTime - startTime);      // passed time

            // When the totalTime exceeds tickTimeSeconds
            // display the word.  Setting the text must
            // be done on the UI thread.
            if (totaltime > tickTimeSeconds * 1000) {           // if passed time over interval
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {                         //  display new wheel image on the UI
                        ivWheel.setImageBitmap(bitmaps.get(count % size));
                    }
                });
                count++;                                        // update images count
                totaltime = 0;                                  // update passed time
                startTime = System.currentTimeMillis();         // get current time
            }
        } while(count != duration);                     // spinning ends

        stopService(musicIntent);                       // stop background sound
    }

    // save category to a sharedpreference
    public void setCategory() {
        int index = duration % size;

        switch (index) {
            case 0:
                category = "Phrase";
                break;
            case 2:
                category = "Thing";
                break;
            case 4:
                category = "Person";
                break;
            case 6:
                category = "Things";
                break;
        }

        SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferences",
                Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("category", category);
        editor.commit();
    }

    // save the puzzle to a sharedpreference
    public void setPuzzle() {
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferences",
                Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("puzzle", puzzle);
        editor.commit();
    }

    // generate a random int from 0 (included) to n (excluded)
    private int generateRandomNumber(int n) {
        Random random = new Random();
        return random.nextInt(n);
    }

    // read a puzzle from an input file
    private void readPuzzle () throws IOException {
        String str="";
        int randomeNumber = generateRandomNumber(10);       // generate a random number 1 - 10

        InputStream is = this.getAssets().open(category);   // open input file
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is));

        if (is!=null) {
            for ( int i = 0 ; i <= randomeNumber ; i++ ) {  // read a random number of lines
                str = reader.readLine();
            }
        }

        puzzle = str;                                       // set the puzzle
        reader.close();
        is.close();
    }
}

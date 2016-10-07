package edu.scu.klnguyen.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.support.v4.content.res.ResourcesCompat;
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

public class SpinActivity2 extends OverFlowMenuActivity {
    private static int SHORT = 20;              // 0.03 sec interval
    private static int LONG = 1000;             // 1 sec interval

    private static int MAX = 10;
    private static String TAG = "KL";

    private TextView spinButton;                        // 'spin' button
    private ImageView ivWheel;                          // view to hold the wheel
    private List<Bitmap> bitmaps = new ArrayList<>();   // list of wheel images to show
    private int size;                                   // size of images array
    private int count;                                  // count the number of time bitmap has changed
    private int numberOftimes;                          // number of times bitmap gets changed
    private MyAsyncTask mMyAsyncTask;

    private String category = "";                   // puzzle category
    private String puzzle = "";                     // puzzle

    private String audioFile = "spinning";          // wheel spinning sound file
    private Intent musicIntent;


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
        spinButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        spin();
                    }
                }
        );

        musicIntent = new Intent                         // start playing wheel spinning sound in the background
                (this, BackgroundSoundService.class);
        musicIntent.putExtra("fileName", audioFile);    // pass sound file name
        musicIntent.putExtra("looping", true);          // set looping to true

        mMyAsyncTask = new MyAsyncTask();
    }

    @Override
    protected void onPause() {
        stopService(musicIntent);   // stop background music
        mMyAsyncTask.cancel(true);
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        //finish(); // finish activity
    }

    // listener to 'exit' button
    // exit app, clase all activities
    public void exit_app(View v) {
        onBackPressed();
        //finishAffinity();
    }

    // doing needed works after wheel stops spinning
    public void afterWheelStop(int numberOfTimes){
        stopService(musicIntent);                       // stop background sound
        Log.i(TAG,count + " - stopping music");

        setCategory(numberOfTimes);  // save the category

        try {
            readPuzzle();           // read a random puzzle from input file
        } catch (IOException e) {
            e.printStackTrace();
        }

        setPuzzle();                // save puzzle in sharePreferences

        mMyAsyncTask = new MyAsyncTask();  // initial an asyncTask
        mMyAsyncTask.execute();
    }

    // spin the wheel
    public void spin() {
        startService(musicIntent);

        numberOftimes = generateRandomNumber(MAX);  // generate a random number 0 - 10
        numberOftimes = (numberOftimes + 20) * 2;       // make sure have even number and at least 40
        Log.i(TAG,"numberOftimes =  " + numberOftimes);
        count = 0;
        mMyAsyncTask.execute();
    }

    // save category to a sharedpreference
    public void setCategory(int numberOfTimes) {
        int index = numberOfTimes % size;

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


    ////////////////////////////////////////////////////////////////////////////////////////
    private class MyAsyncTask extends AsyncTask<Void, Void, Void> {
        int interval = SHORT;

        @Override
        protected Void doInBackground(Void... params) {     // sleep from 0.8 second
            if ( count > numberOftimes  ) {
                interval = LONG;
            }

            try {
                Thread.sleep(interval);
            }

            catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onCancelled() {
        }

        @Override
        protected void onProgressUpdate(Void... p) {
        }

        @Override
        protected void onPostExecute(Void result) {
            count++;

            if ( count < numberOftimes ) {
                Log.i(TAG, count + " - adding image");
                int index = count % size;
                ivWheel.setImageBitmap(bitmaps.get(index));

                mMyAsyncTask = new MyAsyncTask();  // initial an asyncTask
                mMyAsyncTask.execute();
            }

            else if (count == numberOftimes  ) {
                int index = count % size;
                ivWheel.setImageBitmap(bitmaps.get(index));

                Log.i(TAG,count + " - adding img + doing admin works");
                afterWheelStop(numberOftimes);
            }

            else {
                Log.i(TAG,count + " - going to next activity");
                Intent intent = new Intent                  // start the next activity
                        (getApplicationContext(), ChooseLettersActivity.class);

                //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                //intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

                startActivity(intent);
            }


        }
    }

}

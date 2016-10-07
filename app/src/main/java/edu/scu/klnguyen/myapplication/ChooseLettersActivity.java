package edu.scu.klnguyen.myapplication;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseLettersActivity extends OverFlowMenuActivity {
    private static final int VOWEL_FR_DIALOGBOX = 1;
    private static final int CONSONANT_FR_DIALOGBOX = 2;
    private static final int VOWEL_FR_MAIN_VIEW = 3;
    private static final int CONSONANT_FR_MAIN_VIEW = 4;

    private static final int ONE_SEC = 1000;
    private static final int ONE_FIVE = 1500;


    private String category;            // puzzle category
    private String puzzle;              //puzzle
    private int level;                  // playing level

    private TextView categoryTextview;
    private TextView[] puzzleChars;
    private TextView[] choosenCosonants;
    private TextView choosenVowel;

    private Dialog letterDialog;        // dialogBox with consonants chooser
    private Dialog vowelDialog;         // dialogBox with vowel chooser
    private GridLayout consonantGrid;

    private String musicFile = "waiting";       // background music file
    private Intent musicIntent;                 // intent to play music

    private MyAsyncTask mMyAsyncTask;           // asyncTask to hold the screen for 1 second

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_letters);

        createActionBar("Choose Letters");      // add actionBar on top

        readSharedPreferences();                // read sharePreferences for category, puzzle and level

        displayCategory();                      // display category on UI

        displayPuzzle();                        // display puzzle on UI

        displaySelectMessage();                 // display prompt

        displayLetters();                       // display letter boxes for user to choose
    }

    private void playMusic() {
        musicIntent = new Intent                      // start playing music in the background
                (this, BackgroundSoundService.class);
        musicIntent.putExtra("fileName", musicFile);
        musicIntent.putExtra("looping", true);
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

    // listener to 1st consonant box click
    public void choose_1st_consonant(View v) {
        int letterNumber = 1;
        displayLetterDialog(letterNumber);
    }

    // listener to 2nd consonant box click
    public void choose_2nd_consonant(View v) {
        int letterNumber = 2;
        displayLetterDialog(letterNumber);
    }

    // listener to 3th consonant box click
    public void choose_3th_consonant(View v) {
        int letterNumber = 3;
        displayLetterDialog(letterNumber);
    }

    // listener to 4th consonant box click
    public void choose_4th_consonant(View v) {
        if ( level == 1) {                      // only execute if playing level is moderate
            int letterNumber = 4;
            displayLetterDialog(letterNumber);
        }
    }

    // display letter chooser in form of dialog box
    private void displayLetterDialog(int letterNumber) {
        letterDialog = new Dialog(ChooseLettersActivity.this);
        letterDialog.setTitle("Select a consonant");

        // inflate custom layout
        letterDialog.setContentView(R.layout.consonant_dialog);

        consonantGrid = (GridLayout) letterDialog.findViewById(R.id.consonantsGrid);
        consonantGrid.setTag(letterNumber);

        letterDialog.show();
    }

    // listener when a consonant from diaglogBox is clicked
    public void choose_letter(View v) {
        TextView aView = (TextView)v;           // get the clicked consonant from dialogBox
        aView.setBackgroundColor(0xffc71585);   // change the view background color
        aView.setTextColor(0xffffffff);
        mMyAsyncTask = new MyAsyncTask(CONSONANT_FR_DIALOGBOX, ONE_SEC);  // initial an asyncTask
        mMyAsyncTask.execute(v);                // execute asyncTask to hold the screen for 1 second
    }

    // this is called from asyncTask onPostExecute()
    // To show the consonant on the main UI after user has chosen from diaglog box
    public void showLetter1(View v) {
        TextView aView = (TextView)v;
        String text = aView.getText().toString();               // read letter from dialog
        int letterNumber = (Integer) consonantGrid.getTag();

        letterDialog.dismiss();                              // close dialogBox
        resetLetters(letterNumber, text);                   // show letter in the main UI
    }

    // this is called after a consonant got chosen
    // It set the TextView on main UI to match the chosen consonant
    public void showLetter(View v) {
        TextView aView = (TextView)v;
        String text = aView.getText().toString();               // read letter from dialog
        int letterNumber = (Integer) consonantGrid.getTag();

        letterDialog.dismiss();                                 // close dialogBox/chooser

        TextView aView2 = choosenCosonants[letterNumber-1];     // get the consonant TextView/Box in main UI
        aView2.setText(text);                                   // set to match the chosen letter
        aView2.setBackgroundColor(0xfff79b2e);                  // change its background color
        aView2.setTextColor(0xffffffff);

        mMyAsyncTask = new MyAsyncTask(CONSONANT_FR_MAIN_VIEW, ONE_FIVE);  // initial an asyncTask
        mMyAsyncTask.execute(aView2);
    }

    // this is called after a consonant got hightlined
    // it sets the  consonant TextView/Box back to its white background
    public void changeConsonantBackground(View v) {
        TextView aView = (TextView)v;
        aView.setBackgroundColor(0xffffffff);                  // change its background color
        aView.setTextColor(0xfff79b2e);
    }

    // reset the main UI to show the consonant after user chose
    private void resetLetters(int letterNumber, String text) {
        TextView aView = choosenCosonants[letterNumber-1];
        aView.setText(text);
    }

    // this shows the letter boxes when view first loaded.
    // if playing level = 1 (moderate), 4 cosonant boxes and one vowel box show
    // if playing level = 2 (hard), 3 consonant boxes and one vowel box show
    private void displayLetters() {
        fillChosenConsonantsArray();

        if (level == 1) {
            choosenCosonants[3].setBackgroundResource(R.drawable.border_yellow);
            choosenCosonants[3].setTextColor(0xfff79b2e);
            choosenCosonants[3].setText("F");
        }

        choosenVowel = (TextView) findViewById(R.id.con5);
    }

    // listener of the vowel TextView/box
    // it opens an vowel chooser for user to choose from
    public void choose_vowel1(View v) {
        vowelDialog = new Dialog(ChooseLettersActivity.this);   // chooser in a dialogBox
        vowelDialog.setTitle("Select a vowel");

        vowelDialog.setContentView(R.layout.vowel_dialog);      // inflate a custom  view

        vowelDialog.show();                                     // show the dialog/chooser
    }

    // listener when a vowel in the chooser/dialogBox is clicked
    public void choose_vowel(View v) {
        TextView aView = (TextView)v;           // get the clicked vowel from dialogBox/chooser
        aView.setBackgroundColor(0xffc71585);   // change the view background color
        aView.setTextColor(0xffffffff);
        mMyAsyncTask = new MyAsyncTask(VOWEL_FR_DIALOGBOX, ONE_SEC);  // initial an asyncTask
        mMyAsyncTask.execute(v);               // execute asyncTask to hold the screen for 0.8 second
    }

    // this is called from asyncTask onPostExecute()
    // it reads the clicked vowel from the chooser/dialogBox
    // then set the vowel TextView/Box in the main UI
    public void showVowel(View v) {
        TextView aView = (TextView)v;
        String text = aView.getText().toString();           // get chosen vowel from dialogBox/chooser

        vowelDialog.dismiss();                              // close dialogBox/chooser

        choosenVowel.setText(text);                         // set main UI to match the chosen vowel
        choosenVowel.setBackgroundColor(0xfff79b2e);               // change its background color
        choosenVowel.setTextColor(0xffffffff);

        mMyAsyncTask = new MyAsyncTask(VOWEL_FR_MAIN_VIEW, ONE_FIVE);  // initial an asyncTask
        mMyAsyncTask.execute(choosenVowel);
    }

    // this is called from asyncTask onPostExecute()
    // it reads the clicked vowel from the chooser/dialogBox
    // then set the vowel TextView/Box in the main UI
    public void showVowel2(View v) {
        TextView aView = (TextView)v;
        String text = aView.getText().toString();
        choosenVowel.setText(text);
        vowelDialog.dismiss();
    }


    // put all letter TextView/Box in one array
    // for easier access
    private void fillChosenConsonantsArray() {
        choosenCosonants = new TextView[4];
        choosenCosonants[0] = (TextView) findViewById(R.id.con0);
        choosenCosonants[1] = (TextView) findViewById(R.id.con1);
        choosenCosonants[2] = (TextView) findViewById(R.id.con2);
        choosenCosonants[3] = (TextView) findViewById(R.id.con3);
    }

    // listener for 'submit' button
    // it's called after user chose all the letters
    public void submit_letters(View v) {
        String s = "";       // this string concatenate all chose letters
        int n = 3;          // number of consonants

        if (level == 1)     // 4 consonant if playing level is moderate
            n = 4;

        for (int i = 0 ; i < n ; i++) {         // loop thru each consonant TextView/box
            s += choosenCosonants[i].getText(); // get the consonant and concatenate to the string
        }

        s += choosenVowel.getText();            // concatenate the chosen vowel to the sting

        SharedPreferences sharedPreferences =
                getSharedPreferences("sharedPreferences",
                Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("choosenLetters", s);  // shave chosen letters in sharePreferences
        editor.commit();

        //stopService(musicIntent);           // stop background music before moving to next activiy

        Intent intent = new Intent          // start next activity
                (getApplicationContext(), SolvePuzzleActivity.class);

        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

        startActivity(intent);
    }

    // display prompty for user to select letters
    private void displaySelectMessage() {
        TextView textView3 = (TextView)findViewById(R.id.tv3);
        String numberOfconsonants = "";

        if (level == 1) {                   // for level = 1 (moderate)
            numberOfconsonants = "four";
        }

        else if (level == 2) {              // for level = 2 (hard)
            numberOfconsonants = "three";
        }

        String selectingMsg = "Select " + numberOfconsonants + " consonants and one vowel";
        textView3.setText(selectingMsg);
    }

    // display puzzle in view
    private void displayPuzzle() {
        fillPuzzleTextViewArray();              // pull all TextView needed to show puzzle in 1 array
        String[] words = puzzle.split(" ");     // split puzzle into word
        String givenChars = "RSTLNE";           // given characters
        int i = 0;                              // index of current TextView
        int spotLeft = 12;                      // this counts the number of empty spots left in line so far

        for ( String w : words) {               // loop thru each word
            char[] a = w.toCharArray();
            if ( a.length > spotLeft) {         // if a word is too long for the current row
                i = 12;                         // go to the 1st TextView of the 2nd row
                spotLeft = 12;                  // update empty spots
            }
            for (char c : a) {                          // loop thru each char in word
                if ( givenChars.indexOf(c) != -1) {     // if char is one of given letter
                    puzzleChars[i].setText(c+"");       // show the char
                    puzzleChars[i].setBackgroundColor(0xffc71585);
                    puzzleChars[i].setTextColor(0xffffffff);
                }
                else {
                    puzzleChars[i].setText("");         // if not a given char, no show
                    puzzleChars[i].setBackgroundColor(0xffc71585);
                    puzzleChars[i].setTextColor(0xffffffff);
                }
                i++;                        // move to the next TextView
                spotLeft--;                 // update empty spot in the row
            }
            i++;                            // leave one space between 2 words
            spotLeft--;                     // update empty spot in the row
        }
    }

    // set all TextView needed to show puzzel in one array
    // for easier access
    private void fillPuzzleTextViewArray() {
        puzzleChars = new TextView[24];
        puzzleChars[0]  = (TextView)findViewById(R.id.puz0);
        puzzleChars[1]  = (TextView)findViewById(R.id.puz1);
        puzzleChars[2]  = (TextView)findViewById(R.id.puz2);
        puzzleChars[3]  = (TextView)findViewById(R.id.puz3);
        puzzleChars[4]  = (TextView)findViewById(R.id.puz4);
        puzzleChars[5]  = (TextView)findViewById(R.id.puz5);
        puzzleChars[6]  = (TextView)findViewById(R.id.puz6);
        puzzleChars[7]  = (TextView)findViewById(R.id.puz7);
        puzzleChars[8]  = (TextView)findViewById(R.id.puz8);
        puzzleChars[9]  = (TextView)findViewById(R.id.puz9);
        puzzleChars[10]  = (TextView)findViewById(R.id.puz10);
        puzzleChars[11]  = (TextView)findViewById(R.id.puz11);
        puzzleChars[12]  = (TextView)findViewById(R.id.puz12);
        puzzleChars[13]  = (TextView)findViewById(R.id.puz13);
        puzzleChars[14]  = (TextView)findViewById(R.id.puz14);
        puzzleChars[15]  = (TextView)findViewById(R.id.puz15);
        puzzleChars[16]  = (TextView)findViewById(R.id.puz16);
        puzzleChars[17]  = (TextView)findViewById(R.id.puz17);
        puzzleChars[18]  = (TextView)findViewById(R.id.puz18);
        puzzleChars[19]  = (TextView)findViewById(R.id.puz19);
        puzzleChars[20]  = (TextView)findViewById(R.id.puz20);
        puzzleChars[21]  = (TextView)findViewById(R.id.puz21);
        puzzleChars[22]  = (TextView)findViewById(R.id.puz22);
        puzzleChars[23]  = (TextView)findViewById(R.id.puz23);
    }

    // display category in view
    private void displayCategory() {
        categoryTextview = (TextView)findViewById(R.id.tv1);
        categoryTextview.setText(category);
    }

    // read level, category, puzzle from sharedPreferences
    private void readSharedPreferences() {
        SharedPreferences settings = getSharedPreferences("sharedPreferences",
                Context.MODE_PRIVATE);

        category = settings.getString("category", "");
        puzzle = settings.getString("puzzle", "");
        level = settings.getInt("level", 1);
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    private class MyAsyncTask extends AsyncTask<View, Integer, View> {
        int letter;                  // letter from the view
        int sec;

        public MyAsyncTask(int letter, int sec) {
            this.letter = letter;
            this.sec = sec;
        }
        @Override
        protected View doInBackground(View... params) {     // sleep from 0.8 second
                try {
                    Thread.sleep(sec);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            return params[0];
        }

        @Override
        protected void onPreExecute() {
            //textView.append("onPreExecute() is called\n");
        }

        @Override
        protected void onCancelled() {
            //textView.append("onCancelled() is called\n");
        }

        @Override
        protected void onProgressUpdate(Integer... p) {
            //Log.i("jsun", "Current progress is " + p[0]);
            //progressBar.setProgress(p[0]);
        }

        @Override
        protected void onPostExecute(View v) {
            if ( letter == VOWEL_FR_DIALOGBOX ) {         // if it's a vowel from dialogBox/chooser
                showVowel(v);
            }

            else if ( letter == CONSONANT_FR_DIALOGBOX ) {     // if it's a consonant from dialogBox/chooser
                showLetter(v);      // call showLetter()
            }

            else if ( letter == CONSONANT_FR_MAIN_VIEW ||
                        letter == VOWEL_FR_MAIN_VIEW ) {        // if it's a consonant or vowel from the main UI
                TextView aView = (TextView)v;
                Drawable bg = ResourcesCompat.getDrawable(getResources(), R.drawable.border_yellow, null);
                aView.setBackground(bg);                        // change its background back to white color
                aView.setTextColor(0xfff79b2e);
            }
        }
    }
}


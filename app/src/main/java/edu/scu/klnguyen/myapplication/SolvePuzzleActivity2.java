package edu.scu.klnguyen.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class SolvePuzzleActivity2 extends OverFlowMenuActivity {
    private static int TIME_TO_SOLVE = 16;
    private static String EDITABLE = "1";

    private String category;                // puzzle category
    private String puzzle;                  // puzzle
    private boolean win = true;             // user wins (true) or loses (false)

    private String choosenLetters;          // user choosen letter
    private String answer;                  // user answer
    private int timeLeft = TIME_TO_SOLVE;   // time left to solve the puzzle

    private TextView categoryTextview;
    private TextView[] puzzleChars;
    private TextView timer;

    private String musicFile = "tick";      // background sound file
    private Intent musicIntent;             // background sound intent


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solve_puzzle);

        createActionBar("Solve the Puzzle");    // add actionBar on top
        readSharedPreferences();                // read sharePreferences for category, puzzle, user choosen letter
        displayCategory();                      // display category on  UI
        displayPuzzle();                        // display puzzle on  UI
        displayTimeLeft();                      // display timer
    }

    // save user's win/loss to  sharedPreferences
    public void saveWin() {

        SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferences",
                Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("win", win);
        editor.commit();
    }

    // save user answer to  sharedPreferences
    public void setUserAnswer() {
        EditText editText = (EditText) findViewById(R.id.tv5);
        answer = editText.getText().toString();

        SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferences",
                Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("answer", answer);
        editor.commit();
    }

    // display count-down timer
    private void displayTimeLeft() {
        timer = (TextView) findViewById(R.id.tv3);

        musicIntent = new Intent                      // start playing sound in the background
                (this, BackgroundSoundService.class);
        musicIntent.putExtra("fileName", musicFile);
        musicIntent.putExtra("looping", true);
        startService(musicIntent);

        new CountDownTimer(16000, 1000) {

            public void onTick(long millisUntilFinished) {  // update timer every 1 second
                timeLeft--;
                timer.setText(""+ timeLeft);
            }

            public void onFinish() {                        // when count down is over
                timeLeft--;
                timer.setText("" + timeLeft);
                checkAnswer();                              // check user's answer against puzzle
                saveWin();                                  // save user's win/loss state
                stopService(musicIntent);                   // stop background sound

                Intent intent = new Intent                  // start next activity
                        (getApplicationContext(), GameOverActivity.class);
                startActivity(intent);
            }
        }.start();
    }

    // display puzzle in view
    private void displayPuzzle() {
        fillPuzzleTextViewArray();                          // put all TextView needed to show puzzle in one array
        String[] words = puzzle.split(" ");                 // split puzzle into words
        String setChars = "RSTLNE" + choosenLetters;       // given characters + user chosen char
        int i = 0;                                  // index of a TextView in array
        int spotLeft = 12;                          // number of spots left in current row

        for ( String w : words) {                   // loop thre each words of puzzle
            char[] a = w.toCharArray();
            if ( a.length > spotLeft) {             // if word is too long
                i = 12;                             // go to 1st TextView of 2nd row
                spotLeft = 12;                      // update number of spot left for the row
            }
            for (char c : a) {                      // loop thru each char of word
                if ( setChars.indexOf(c) != -1) {   // if char is a given char and a user choosen char
                    puzzleChars[i].setText(c + ""); // display it
                    puzzleChars[i].setBackgroundColor(0xffff5a09);
                    puzzleChars[i].setTextColor(0xffffffff);
                }
                else {
                    puzzleChars[i].setText("");     // if not, no display
                    puzzleChars[i].setBackgroundColor(0xffff5a09);
                    puzzleChars[i].setTextColor(0xffffffff);
                    puzzleChars[i].setTag(EDITABLE);    // set editable so user to type in letter
                }
                i++;            // move to the next TextView
                spotLeft--;     // update number of spot left in the row
            }
            i++;                // leave one space between 2 words
            spotLeft--;         // update number of spots left in the row
        }
    }

    // listener to letter TextView
    public void set_editable(View v) {
        TextView aView = (TextView)v;
        String viewTag = (String) aView.getTag();
        if (  viewTag.equals(EDITABLE ) ){
            aView.setCursorVisible(true);
            aView.setFocusableInTouchMode(true);
            aView.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
            aView.requestFocus();
        }
    }

    // check if user anwser matches the puzzle
    private void checkAnswer() {
        String[] words = puzzle.split(" ");         // split puzzle into words

        int i = 0;                                  // index of a TextView in array
        int spotLeft = 12;                          // number of spots left in current row
        boolean misMatch = false;                   // flag to check if a mis-match found

        for ( String w : words) {                   // loop thru each words of puzzle
            char[] a = w.toCharArray();
            if ( a.length > spotLeft) {             // if word is too long
                i = 12;                             // go to 1st TextView of 2nd row
                spotLeft = 12;                      // update number of spot left for the row
            }
            for (char c : a) {                      // loop thru each char of word
                char letter = puzzleChars[i].getText().toString().charAt(0);    // get the letter from view
                if ( letter != c) {             // if chosen letter not match puzzle char
                    win = false;                // update win
                    misMatch = true;            // update misMatch
                    break;                      // out of loop
                }
                i++;            // move to the next TextView
                spotLeft--;     // update number of spot left in the row
            }

            if ( misMatch ) {   // if a mismatch found, break out of loop
                break;
            }
            i++;                // leave one space between 2 words
            spotLeft--;         // update number of spots left in the row
        }
    }

    // put all TextView needed to show the puzzle in on array
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


    // display category in UI
    private void displayCategory() {
        categoryTextview = (TextView)findViewById(R.id.tv1);
        categoryTextview.setText(category);
    }


    // read category, puzzle and letters that user chose from sharedPreferences
    private void readSharedPreferences() {
        SharedPreferences settings = getSharedPreferences("sharedPreferences",
                Context.MODE_PRIVATE);

        category = settings.getString("category", "");
        puzzle = settings.getString("puzzle", "");
        choosenLetters = settings.getString("choosenLetters", "");
    }
}


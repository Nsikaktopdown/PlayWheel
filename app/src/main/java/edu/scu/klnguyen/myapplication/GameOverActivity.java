package edu.scu.klnguyen.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class GameOverActivity extends OverFlowMenuActivity {

    private static final String WIN_SOUND = "drum"; // background music file name when user wins
    private static final String LOSE_SOUND = "no";  // background music file name when user loses

    private String category;        // category
    private String puzzle;          // puzzle
    private String answer;          // user's answer

    private TextView categoryTextview;
    private TextView[] puzzleChars;

    private String musicFile;           // background music file
    private int musicVol;
    private Intent musicIntent;         // background music intent

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        createActionBar("Game Over");               // add actionBar on top
        readSharedPreferences();                    // read sharePreferences for category, puzzle and user answer
        displayCategory();                          // display category on UI
        displayPuzzle();                            // display puzzle on UI
        displayEndingMsg();                         // display win/lose message
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

    // display win/lose message
    private void displayEndingMsg() {
        TextView aView = (TextView) findViewById(R.id.tv2);
        String text;

        if ( isCorrectAnswer()) {                   // if correct answer
            text = "You win. Congratulations!";
            musicVol = 180;
            musicFile = WIN_SOUND;
        }
        else {                                      // if wrong answer
            text = "Your answer: \"" + answer + "\" is not correct.";
            musicVol = 100;
            musicFile = LOSE_SOUND;
        }

        aView.setText(text);
        musicIntent = new Intent                      // start playing sound in the background
                (this, BackgroundSoundService.class);
        musicIntent.putExtra("fileName", musicFile);
        musicIntent.putExtra("looping", false);
        startService(musicIntent);
    }

    // check if answer is correct
    private boolean isCorrectAnswer() {
        String[] puzzleWords = puzzle.split("\\s+");    // split puzzle into words
        String[] answerWords = answer.split("\\s+");    // split answer into words

        int size = puzzleWords.length;

        if ( size != answerWords.length)                // return if length different
            return false;

        for (int i = 0 ; i < size ; i++) {              // compare each pair of words
            if (! puzzleWords[i].equals(answerWords[i]) )
                return false;
        }
        return true;
    }

    // display puzzle in view
    private void displayPuzzle() {
        fillPuzzleTextViewArray();          // put all TextView needed to show the puzzle in one array
        String[] words = puzzle.split(" "); // split the puzzle into words
        int i = 0;                          // index of current TextView
        int spotLeft = 12;                  // number of spots left in the row

        for ( String w : words) {           // loop thru each word
            char[] a = w.toCharArray();
            if ( a.length > spotLeft) {     // if word is too long
                i = 12;                     // go to the 1st TextView in the 2nd row
                spotLeft = 12;              // update number of spots left in the row
            }
            for (char c : a) {              // loop thru each char in word
                    puzzleChars[i].setText(c+"");   // display the char
                    puzzleChars[i].setBackgroundColor(0xff8bc53f);
                    puzzleChars[i].setTextColor(0xffffffff);

                i++;                        // move to the next TextView
                spotLeft--;                 // update number of spots left in the row
            }
            i++;                    // leave one space between 2 words
            spotLeft--;             // update number of spots left in the row
        }
    }

    // put all TextView needed to display the puzzle in one array
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

    // listener for 'play more' button
    public void play_more(View v) {
        stopService(musicIntent);                           // stop background music
        Intent intent = new Intent                          // start new activity/play
                (getApplicationContext(), MainActivity.class);

        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

        startActivity(intent);
    }

    // read category, puzzle and letters that user chose from sharedPreferences
    private void readSharedPreferences() {
        SharedPreferences settings = getSharedPreferences("sharedPreferences",
                Context.MODE_PRIVATE);

        category = settings.getString("category", "");
        puzzle = settings.getString("puzzle", "");
        answer = settings.getString("answer", "");
        answer = answer.toUpperCase().trim();
    }
}

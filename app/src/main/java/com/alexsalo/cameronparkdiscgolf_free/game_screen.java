package com.alexsalo.cameronparkdiscgolf_free;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class game_screen extends ActionBarActivity {
    public static final String RESULT_FILENAME = "disc_golf_stats.csv";
    public static final String RESULT_DIR = "/cameron_disc_golf";

    public static int NOGAME = -99;
    public static int N_HOLES = 14;
    private static int N_FREE_SAVES = 10;

    public int ScreenWidth;

    LinearLayout lt_holes;
    LinearLayout lt_holes_scores;

    TextView[] tv_holes;
    TextView[] tv_holes_scores;

    TextView tv_cur_hole_score;
    TextView tv_score;

    TextView tv_cur_hole_best;
    TextView tv_cur_hole_average;
    TextView tv_cur_hole_recent_average;

    TextView tv_cur_hole_course_best;
    TextView tv_cur_hole_course_average;
    TextView tv_cur_hole_course_recent_average;

    TextView graph;
    static int img_holes[]={R.drawable.hole01,R.drawable.hole02,R.drawable.hole03,R.drawable.hole04
            ,R.drawable.hole05,R.drawable.hole06,R.drawable.hole07,R.drawable.hole08,R.drawable.hole09
            ,R.drawable.hole10,R.drawable.hole11,R.drawable.hole12,R.drawable.hole13,R.drawable.hole12};
    ImageView bg_image;

    int cur_hole;
    int[] cur_hole_scores = new int[N_HOLES];

    ArrayList<ArrayList<Integer>> history_scores = new ArrayList<ArrayList<Integer>>();

    private static final Map<Integer, String> par_names = new HashMap<Integer, String>();
    static
    {
        par_names.put(-2, "Eagle");
        par_names.put(-1, "Birdie");
        par_names.put(0, "Par");
        par_names.put(1, "Boogie");
    }

    private int getScreenWidth(){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    private void findAllViews(){
        graph = (TextView) findViewById(R.id.graph);
        graph.setVisibility(View.INVISIBLE);

        bg_image = (ImageView) findViewById(R.id.iv_cur_hole_image);
        tv_cur_hole_score = (TextView) findViewById(R.id.tv_current_hole_score);
        tv_score = (TextView) findViewById(R.id.tv_current_score);

        tv_cur_hole_best = (TextView) findViewById(R.id.tv_cur_hole_best);
        tv_cur_hole_average = (TextView) findViewById(R.id.tv_cur_hole_average);
        tv_cur_hole_recent_average = (TextView) findViewById(R.id.tv_cur_hole_recent_average);

        tv_cur_hole_course_best = (TextView) findViewById(R.id.tv_cur_hole_course_best);
        tv_cur_hole_course_average = (TextView) findViewById(R.id.tv_cur_hole_course_average);
        tv_cur_hole_course_recent_average = (TextView) findViewById(R.id.tv_cur_hole_recent_average);

        lt_holes = (LinearLayout) findViewById(R.id.lt_holes);
        lt_holes_scores = (LinearLayout) findViewById(R.id.lt_holes_scores);
        tv_holes = new TextView[N_HOLES];
        tv_holes_scores = new TextView[N_HOLES];
    }

    private void initGame(){
        for (int i = 0; i < N_HOLES; i++){
            cur_hole_scores[i] = NOGAME;
            tv_holes_scores[i].setText("");
        }
        readHistoryScores();
        cur_hole = 0;
        updateNewlySelectedScore();
    }

    View.OnTouchListener hole_scores_touch_listener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (history_scores.size() > 0) {
                if (graph.getVisibility() == View.INVISIBLE) {
                    graph.setVisibility(View.VISIBLE);
                    showStatisticForHole((int)v.getTag());
                } else
                    graph.setVisibility(View.INVISIBLE);
            }
            return false;
        }
    };

    View.OnTouchListener holes_on_touch_listener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            tv_holes[cur_hole].setBackgroundColor(Color.parseColor("#CC000000"));
            cur_hole = Integer.parseInt(((TextView) v).getText().toString()) - 1;
            updateNewlySelectedScore();
            return false;
        }
    };

    private void generateScoresViews(){
        for (int i = 0; i < tv_holes.length; i++){
            tv_holes_scores[i] = new TextView(this);
            tv_holes_scores[i].setTag(i);
            tv_holes_scores[i].setBackgroundColor(Color.parseColor("#CC000000"));
            tv_holes_scores[i].setGravity(17);
            tv_holes_scores[i].setWidth(ScreenWidth / N_HOLES);
            tv_holes_scores[i].setTextColor(Color.WHITE);
            tv_holes_scores[i].setOnTouchListener(hole_scores_touch_listener);

            tv_holes[i] = new TextView(this);
            tv_holes[i].setPadding(5, 5, 0, 5);
            tv_holes[i].setBackgroundColor(Color.parseColor("#CC000000"));
            tv_holes[i].setWidth(ScreenWidth / N_HOLES);
            tv_holes[i].setGravity(17);
            tv_holes[i].setText(String.valueOf(i + 1));
            tv_holes[i].setTextColor(Color.WHITE);
            tv_holes[i].setOnTouchListener(holes_on_touch_listener);

            lt_holes.addView(tv_holes[i]);
            lt_holes_scores.addView(tv_holes_scores[i]);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_screen);

        ScreenWidth = getScreenWidth();
        findAllViews();
        generateScoresViews();
        initGame();

        bg_image.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
            @Override
            public void onSwipeLeft() {
                if (cur_hole < N_HOLES){
                    if (cur_hole_scores[cur_hole] == NOGAME){
                        cur_hole_scores[cur_hole] = 0;
                    }

                    String msg = par_names.get(cur_hole_scores[cur_hole]);
                    if (msg != null)
                        Toast.makeText(game_screen.this, msg, Toast.LENGTH_SHORT).show();

                    updateScores();

                    tv_holes[cur_hole].setBackgroundColor(Color.parseColor("#CC000000"));
                    cur_hole++;
                    if (cur_hole == N_HOLES){
                        saveResults(null);
                    }else{
                        updateNewlySelectedScore();
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game_screen, menu);
        return true;
    }

    @Override
    public void onBackPressed()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to reset the game?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        initGame();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            initGame();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void decrease_hole_score(View view){
        if (cur_hole_scores[cur_hole] == NOGAME){
            cur_hole_scores[cur_hole] = 0;
        }
        if (cur_hole_scores[cur_hole] >= -1) {
            cur_hole_scores[cur_hole]--;
            tv_cur_hole_score.setText(String.valueOf(cur_hole_scores[cur_hole]));
        }
    }

    public void increase_hole_score(View view){
        if (cur_hole_scores[cur_hole] == NOGAME){
            cur_hole_scores[cur_hole] = 0;
        }
        if (cur_hole_scores[cur_hole] < 10) {
            cur_hole_scores[cur_hole]++;
            tv_cur_hole_score.setText(String.valueOf(cur_hole_scores[cur_hole]));
        }
    }

    int getTotalScore(){
        int res = 0;
        for (int i = 0; i < cur_hole_scores.length; i++){
            if (cur_hole_scores[i] != NOGAME){
                res += cur_hole_scores[i];
            }
        }
        return res;
    }

    private void updateScores(){
        //display played hole score
        tv_holes_scores[cur_hole].setText(String.valueOf(cur_hole_scores[cur_hole]));
        //update total score text
        tv_score.setText(String.valueOf(getTotalScore()));
        //reset cur hole score to 0
        tv_cur_hole_score.setText("0");
    }

    private void updateNewlySelectedScore(){
        //Change bk color of selected hole
        tv_holes[cur_hole].setBackgroundColor(Color.parseColor("#CC33b5e5"));
        //Change hole pic
        bg_image.setImageDrawable(getDrawable(img_holes[cur_hole]));
        if (cur_hole_scores[cur_hole] != NOGAME){
            tv_cur_hole_score.setText(String.valueOf(cur_hole_scores[cur_hole]));
        }else{
            tv_cur_hole_score.setText("0");
        }

        if (history_scores.size() > 0) {
            tv_cur_hole_best.setText(String.valueOf(Stats.getBestScore(history_scores, cur_hole)));
            tv_cur_hole_average.setText(String.format("%.2f", Stats.getAvgScore(history_scores, cur_hole)));
            tv_cur_hole_recent_average.setText(String.format("%.2f", Stats.getRecentAvgScore(history_scores, cur_hole)));

            tv_cur_hole_course_best.setText(String.valueOf(Stats.getBest(history_scores, cur_hole)));
            tv_cur_hole_course_average.setText(String.format("%.2f", Stats.getAvg(history_scores, cur_hole)));
            tv_cur_hole_course_recent_average.setText(String.format("%.2f", Stats.getRecentAvg(history_scores, cur_hole)));
        }

        if (graph.getVisibility() == View.VISIBLE){
            showStatisticForHole(cur_hole);
        }
    }

    private void showStatisticForHole(int hole){
        Map<Integer, Integer> stat = Stats.getStatsDistr(history_scores, hole);
        String graphText = "Statistics for hole " + String.valueOf(hole + 1) + ":\n";
        ArrayList<Integer> keys = new ArrayList<Integer>(stat.keySet());
        Collections.sort(keys);
        for (int key : keys) {
            String statStr = String.valueOf(key);
            if (key > 0)
                statStr = " +" + statStr + "  ";
            if (par_names.get(key) != null)
                statStr = par_names.get(key);
            graphText +=  statStr + ": " + String.valueOf(stat.get(key)) + "\n";
        }
        graphText = graphText.substring(0, graphText.length()-1); //delete last \n
        graph.setText(graphText);
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable(){
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    private void readHistoryScores(){
        if (isExternalStorageWritable()){
            File root = android.os.Environment.getExternalStorageDirectory();

            // See http://stackoverflow.com/questions/3551821/android-write-to-sd-card-folder
            File dir = new File (root.getAbsolutePath() + RESULT_DIR);
            dir.mkdirs();
            File file = new File(dir, RESULT_FILENAME);

            try {
                Scanner sc = new Scanner(file);

                while (sc.hasNext()){
                    String line = sc.nextLine();
                    if (line.length() > 3) { //not \n
                        String[] values = line.split(",");
                        Integer[] int_values = new Integer[values.length];
                        for (int i = 0; i < values.length; i++) {
                            int_values[i] = Integer.parseInt(values[i]);
                        }
                        history_scores.add(new ArrayList<Integer>(Arrays.asList(int_values)));
                    }
                }
                Toast.makeText(game_screen.this, "Your previous results are loaded", Toast.LENGTH_SHORT).show();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }else{
            Toast.makeText(game_screen.this, "Can't open results - no external drive", Toast.LENGTH_SHORT).show();
        }
    }

    public void saveResults(View view){
        if (isExternalStorageWritable()){
            File root = android.os.Environment.getExternalStorageDirectory();
            // See http://stackoverflow.com/questions/3551821/android-write-to-sd-card-folder
            File dir = new File (root.getAbsolutePath() + RESULT_DIR);
            dir.mkdirs();
            File file = new File(dir, RESULT_FILENAME);

            if (history_scores.size() < N_FREE_SAVES) {
                try {
                    FileOutputStream f = new FileOutputStream(file, true);
                    PrintWriter pw = new PrintWriter(f);
                    for (int i = 0; i < cur_hole_scores.length; i++) {
                        pw.print(cur_hole_scores[i]);
                        if (i != cur_hole_scores.length - 1) {
                            pw.print(",");
                        }
                    }
                    pw.println();
                    pw.flush();
                    pw.close();
                    f.close();
                    Toast.makeText(game_screen.this, "Your result has been saved.", Toast.LENGTH_SHORT).show();
                    initGame();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Log.i("i", "******* File not found. Did you add a WRITE_EXTERNAL_STORAGE permission to the   manifest?");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                AlertDialog alertDialog = new AlertDialog.Builder(game_screen.this).create();
                alertDialog.setTitle("Get a full version");
                alertDialog.setMessage("You are using a free version which can't save more than"
                        + String.valueOf(N_FREE_SAVES) +
                        "games. Please get the full version for the unlimited history storage");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }

        }else{
            Toast.makeText(game_screen.this, "Can't save results - no external drive", Toast.LENGTH_SHORT).show();
        }
    }
}

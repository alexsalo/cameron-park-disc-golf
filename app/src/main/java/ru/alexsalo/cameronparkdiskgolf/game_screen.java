package ru.alexsalo.cameronparkdiskgolf;

import android.graphics.Color;
import android.graphics.Point;
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

import ru.alexsalo.camperonparkdiskgolf.R;


public class game_screen extends ActionBarActivity {
    public int ScreenWidth;
    public static int N_holes = 18;
    LinearLayout lt_holes;
    TextView[] tv_holes;
    ImageView bg_image;
    TextView tv_cur_hole_score;
    TextView tv_score;
    int cur_hole = 0;
    int[] cur_hole_scores = new int[N_holes];
    int cur_score = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_screen);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        ScreenWidth = size.x;

        for (int i=0; i<cur_hole_scores.length; i++){
            cur_hole_scores[i] = 0;
        }

        lt_holes = (LinearLayout) findViewById(R.id.lt_holes);
        tv_holes = new TextView[N_holes];
        for (int i = 0; i < tv_holes.length; i++){
            tv_holes[i] = new TextView(this);
            tv_holes[i].setPadding(5,5,0,5);
            tv_holes[i].setBackgroundColor(Color.parseColor("#CC000000"));
            tv_holes[i].setWidth(ScreenWidth / N_holes);
            tv_holes[i].setGravity(17);
            tv_holes[i].setText(String.valueOf(i + 1));
            tv_holes[i].setTextColor(Color.WHITE);
            tv_holes[i].setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    //Reset current hole background
                    tv_holes[cur_hole].setBackgroundColor(Color.parseColor("#CC000000"));

                    cur_hole = Integer.parseInt(((TextView) v).getText().toString()) - 1;
                    Log.d("debug", String.valueOf(cur_hole));
                    updateScores();
                    return false;
                }
            });
            lt_holes.addView(tv_holes[i]);
        }


        bg_image = (ImageView) findViewById(R.id.iv_cur_hole_image);
        tv_cur_hole_score = (TextView) findViewById(R.id.tv_current_hole_score);
        tv_score = (TextView) findViewById(R.id.tv_current_score);

        bg_image.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
            @Override
            public void onSwipeLeft() {
                if (cur_hole < N_holes - 1){
                    String msg = "";
                    switch (cur_hole_scores[cur_hole]){
                        case -2:
                            msg = "Eagle";
                            break;
                        case -1:
                            msg = "Birdie";
                            break;
                        case 0:
                            msg = "Par";
                            break;
                    }
                    if (msg != "")
                        Toast.makeText(game_screen.this, msg, Toast.LENGTH_SHORT).show();

                    //Update total score
                    cur_score += cur_hole_scores[cur_hole];

                    //Reset current hole background
                    tv_holes[cur_hole].setBackgroundColor(Color.parseColor("#CC000000"));
                    cur_hole++;
                    updateScores();
                }
            }

            @Override
            public void onSwipeRight() {
                if (cur_hole > 0){
                    //Update total score
                    cur_score += cur_hole_scores[cur_hole];

                    //Reset current hole background
                    tv_holes[cur_hole].setBackgroundColor(Color.parseColor("#CC000000"));
                    cur_hole--;
                    updateScores();
                }
            }
        });

        updateScores();
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void decrease_hole_score(View view){
        if (cur_hole_scores[cur_hole] >= -1) {
            cur_hole_scores[cur_hole]--;
            updateScores();
        }
    }

    public void increase_hole_score(View view){
        if (cur_hole_scores[cur_hole] < 10) {
            cur_hole_scores[cur_hole]++;
            updateScores();
        }
    }

    private void updateScores(){
        tv_cur_hole_score.setText(String.valueOf(cur_hole_scores[cur_hole]));
        tv_score.setText(String.valueOf(cur_score));

        //Change bk color of selected hole
        tv_holes[cur_hole].setBackgroundColor(Color.parseColor("#CC33b5e5"));
    }
}

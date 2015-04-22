package ru.alexsalo.cameronparkdiskgolf;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import ru.alexsalo.camperonparkdiskgolf.R;


public class game_screen extends ActionBarActivity {
    public static int N_holes = 18;
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

        for (int i=0; i<cur_hole_scores.length; i++){
            cur_hole_scores[i] = 0;
        }

        bg_image = (ImageView) findViewById(R.id.iv_cur_hole_image);
        tv_cur_hole_score = (TextView) findViewById(R.id.tv_current_hole_score);
        tv_score = (TextView) findViewById(R.id.tv_current_score);

        bg_image.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
            @Override
            public void onSwipeLeft() {
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
                Toast.makeText(game_screen.this, msg, Toast.LENGTH_SHORT).show();
                if (cur_hole < N_holes - 1){
                    cur_hole++;
                    updateScores();
                }
            }

            @Override
            public void onSwipeRight() {
                if (cur_hole > 0){
                    cur_hole--;
                    updateScores();
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
            cur_score--;
            updateScores();
        }
    }

    public void increase_hole_score(View view){
        if (cur_hole_scores[cur_hole] < 10) {
            cur_hole_scores[cur_hole]++;
            cur_score++;
            updateScores();
        }
    }

    private void updateScores(){
        tv_cur_hole_score.setText(String.valueOf(cur_hole_scores[cur_hole]));
        tv_score.setText(String.valueOf(cur_score));
    }
}

package com.example.blurryface.tappy;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import static com.example.blurryface.tappy.MainActivity.bestDistance;

public class GameOverActivity extends Activity {
    TextView distance,bestDistancePlayed;
    float high;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);
        distance = findViewById(R.id.yourScoreText);
        bestDistancePlayed = findViewById(R.id.yourHighScoretext);
        Bundle extras = getIntent().getExtras();
        sharedPreferences = getSharedPreferences("distance",MODE_PRIVATE);
        if(extras!=null){
            String distanceCovered= getIntent().getStringExtra("distance");
            distance.setText(distanceCovered);
        }
        high = sharedPreferences.getFloat("bestDistance",bestDistance);
        bestDistancePlayed.setText(String.valueOf(high));
    }
    public void onReplay(View view){
        //replay the game
        Intent intent = new Intent(GameOverActivity.this,GameActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
    public void onQuit(View view){
        System.exit(0);
    }
}

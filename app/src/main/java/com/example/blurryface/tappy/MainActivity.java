package com.example.blurryface.tappy;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {
    public static float bestDistance;
    SharedPreferences sharedPreferences;
    float defaultDistance=0;
    TextView highScoreText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        highScoreText = findViewById(R.id.highScoreText);
        sharedPreferences = getSharedPreferences("distance",MODE_PRIVATE);
        bestDistance= sharedPreferences.getFloat("bestDistance",defaultDistance);
        highScoreText.setText(String.valueOf(bestDistance));
    }
    //start the game
    public void onPlay(View view){
        Intent intent = new Intent(MainActivity.this,GameActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}

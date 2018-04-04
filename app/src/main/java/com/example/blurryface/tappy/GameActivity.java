package com.example.blurryface.tappy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;

public class GameActivity extends Activity {
    private TDView gameView;
    //variable to hold/display number of pixel
    Display display;
    Point size;
    Intent gameIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //initialise the display
        display = getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);
        gameIntent = getIntent();
        gameView = new TDView(this,size.x,size.y,gameIntent);
        //make the gameview our view
        setContentView(gameView);

    }
    @Override
    protected void onPause() {
        super.onPause();
        //call pause method
        gameView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //call resume method
        gameView.resume();
    }
}

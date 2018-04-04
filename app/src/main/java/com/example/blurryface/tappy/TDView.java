package com.example.blurryface.tappy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

import static com.example.blurryface.tappy.MainActivity.bestDistance;

/**
 * Created by BlurryFace on 3/5/2018.
 */
//class for our view
public class TDView extends SurfaceView implements Runnable{
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private Context context;
    //holds the variable  of the distance covered
    private float distanceCovered;
    //variable to check whether the game has ended
    private boolean gameEnded;
    //holds the variable for the levels
    private int level;
    private int screenX,screenY;
    //lives
    int shieldStrength;
    //space dust object
    public ArrayList<SpaceDust> dustList = new ArrayList<>();
    //playership object
    private PlayerShip playerShip;
    //enemyship object
    private EnemyShip enemyShip;
    //canvas
    private Canvas canvas;
    //paint
    private Paint paint;
    //surfaceHolder
    private SurfaceHolder holder;
    //declare our thread
    Thread gameThread=null;
    // volatile ensures changes to the variable on all threads
    volatile boolean playing;
    //adding sound
    private SoundPool soundPool;
    int start= -1,bump= -1,win= -1,destroyed= -1;
    public TDView(Context context,int x,int y,Intent gameIntent) {
        super(context);
        this.context = context;
        screenX = x;
        screenY = y;
        //initialise the spacedust objects
        int numSpecs = 40;
        for (int i=0;i<numSpecs;i++){
            SpaceDust spaceDust = new SpaceDust(x,y);
            dustList.add(spaceDust);
        }

        //initialise the playership
        playerShip = new PlayerShip(context,x,y);

        Bundle extras = gameIntent.getExtras();
        Log.e("Extras",String.valueOf(extras));
        if(extras!=null){
            String previousDistance = gameIntent.getStringExtra("initialDistance");
            String boughtShield = gameIntent.getStringExtra("extrashield");
            String currentLevel = gameIntent.getStringExtra("currentLevel");
            distanceCovered = Float.parseFloat(previousDistance);
            shieldStrength = Integer.parseInt(boughtShield);
            level = Integer.parseInt(currentLevel);

        }else {
            //initialise the Lives
            //player has 3 lives
            shieldStrength = playerShip.getShipShieldStrength();
            distanceCovered = 0;
            level = 1;
        }
        //initiate the enemy ship
        enemyShip = new EnemyShip(context,x,y,level);
        //initialise the drawing objects
        paint = new Paint();
        holder = getHolder();



        gameEnded=false;
        sharedPreferences= context.getSharedPreferences("distance",Context.MODE_PRIVATE);
        //initialise the soundpool
        soundPool = new SoundPool.Builder().setMaxStreams(10).build();
        start = soundPool.load(context,R.raw.start,0);
        bump = soundPool.load(context,R.raw.bump,0);
        win = soundPool.load(context,R.raw.win,0);
        destroyed = soundPool.load(context,R.raw.destroyed,0);
        //play the start sound
        soundPool.play(win,1,1,0,0,1);
        //if player has made any payments make the necessary adjustments


    }


    @Override
    public void run() {
        while (playing) {
            update();
            draw();
            controlFPS();
        }
    }
    //method to update game Objects
    public void update(){
        playerShip.update();
        enemyShip.update(playerShip.getSpeed());
        for (SpaceDust spaceDust:dustList){
            spaceDust.update(playerShip.getSpeed());
        }

        //check for collision between the player ship and the enemy ship
        if(Rect.intersects(playerShip.getHitBox(),enemyShip.getHitBox())){
            //play hit sound
            soundPool.play(bump,1,1,0,0,1);
            //reduce the lives then respawn the enemy ship
            if(level==1){
                shieldStrength--;
            }
            if(level==2){
                shieldStrength-=2;
            }
            if(level==3){
                shieldStrength-=3;
            }
            if(shieldStrength<1){
                if(distanceCovered>bestDistance){
                    //initialise the editor
                    editor=sharedPreferences.edit();
                    bestDistance=distanceCovered;
                    editor.putFloat("bestDistance",bestDistance/1000);
                    editor.apply();
                }
                //play destroyed sound
                soundPool.play(destroyed,1,1,0,0,1);
                //give the player a chance to continue with the game
                Intent intent = new Intent(context,PaymentActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("distance",String.valueOf(distanceCovered));
                intent.putExtra("currentLevel",String.valueOf(level));
                context.startActivity(intent);
            }
            enemyShip.setX(-350);

        }
        if(!gameEnded){
            //update the distance covered
            distanceCovered +=playerShip.getSpeed();
        }
        //if player has successfully reached we respawn the player and our enemies change and become more vigorous
        if(playerShip.getX()>screenX-playerShip.getPlayerBitmap().getWidth()){
            level++;
            Log.e("level",String.valueOf(level));
            //if you reached the final level
            if(level>3){
                //game ends
                gameEnded = true;
                if(distanceCovered>bestDistance){
                    //initialise the editor
                    editor=sharedPreferences.edit();
                    bestDistance=distanceCovered;
                    editor.putFloat("bestDistance",bestDistance);
                    editor.apply();
                }
                //play the win sound
                soundPool.play(win,1,1,0,0,1);
                Log.e("Game Over",String.valueOf(level));
                //game over page
                gameOverPage();
            }
            else {
                //add some strength to the ship in accordance with the level
                shieldStrength+=level;
                //reset our player and change the ships according to the level
                playerShip.setX(50);
                playerShip.setY(50);
                //change the enemyShips
                enemyShip = new EnemyShip(context, screenX, screenY, level);
            }
        }
    }
    //method to draw game objects
    public void draw(){
        //check that the surface holder is valid
        //if not valid
        if(!holder.getSurface().isValid()){
            return;
        }
        //if valid
        //lock the canvas
        canvas = holder.lockCanvas();
        canvas.drawColor(Color.argb(255,0,0,0));
        //draw player ship
        canvas.drawBitmap(playerShip.getPlayerBitmap(),playerShip.getX(),playerShip.getY(),paint);
        //draw enemy ship
        canvas.drawBitmap(enemyShip.getEnemyBitmap(),enemyShip.getX(),enemyShip.getY(),paint);
        //unlock canvas and draw the scene
        //white specs of dust
        paint.setColor(Color.argb(255,255,255,255));
        for (SpaceDust dust: dustList){
            canvas.drawPoint(dust.getX(),dust.getY(),paint);
        }
        drawHUD();
        holder.unlockCanvasAndPost(canvas);
    }
    //method to control the frequency we update our game objects
    public void controlFPS(){
        try {
            gameThread.sleep(17);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.e("fps",e.getMessage());
        }

    }
    //if the game is interrupted or user exists the app
    public void pause(){
        playing=false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    //after an interruption or an exit and user gets back to the game
    public void resume(){
        playing=true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            //user is pressing the screen
            case MotionEvent.ACTION_DOWN:
                playerShip.startBoosting();
                break;
            //user is not touching the screen
            case MotionEvent.ACTION_UP:
                playerShip.stopBoosting();
                break;
        }
        return true;
    }
    //draws the stats of the game like distance, shield strength and others
    public void drawHUD(){
        //draw the hud
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setColor(Color.argb(255,255,255,255));
        paint.setTextSize(30);
        canvas.drawText("Distance:"+distanceCovered/1000+" KM",300,25,paint);
        canvas.drawText("Shield "+shieldStrength,600,25,paint);
        canvas.drawText("Level "+level,10,25,paint);
    }
    public void gameOverPage(){
        Intent intent = new Intent(context,GameOverActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("distance",String.valueOf(distanceCovered/1000));
        context.startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            System.exit(0);
        }
        return false;
    }
}

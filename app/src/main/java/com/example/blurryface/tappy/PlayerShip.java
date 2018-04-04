package com.example.blurryface.tappy;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

/**
 * Created by BlurryFace on 3/5/2018.
 */

public class PlayerShip {
    private int shipShieldStrength;
    //hitbox
    private Rect hitBox;
    //instance variables
    private final int GRAVITY =-12;
    //stop the ship from leaving the screen in the y axis
    private int maxY,minY;
    private final int MIN_SPEED=1,MAX_SPEED=20;
    //check if player is touching the screen
    private boolean boosting;
    //image of the playerShip
    private Bitmap playerBitmap;
    //speed at which the ship travels with
    private int speed;
    //position of the ship on the phone
    private int x,y;
    //constructor to initialise the variables
    public PlayerShip(Context context,int screenX,int screenY){
        x=50;
        y=50;
        speed=1;
        playerBitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.ship);
        boosting = false;
        maxY = screenY- playerBitmap.getHeight();
        minY = 0;
        hitBox = new Rect(x,y,playerBitmap.getWidth(),playerBitmap.getHeight());
        shipShieldStrength = 3;

    }
    //to keep the ship moving foward
    public void update(){
        if(boosting){
            //speed up
            speed+=2;
            x++;
        }else {
            //slow down
            speed-=5;
            x++;
        }
        //constraint to topspeed
        if(speed>MAX_SPEED){
            speed = MAX_SPEED;
        }
        //constraint to min speed
        if(speed<MIN_SPEED){
            speed = MIN_SPEED;
        }
        //move the ship up and down
        //move the ship up in accordance with
        y -=(speed+GRAVITY);
        //make sure the ship is in the screen
        if(y>maxY){
            y = maxY;
        }
        if(y<minY){
            y=minY;
        }
        hitBox.left = x;
        hitBox.right =x+ playerBitmap.getWidth();
        hitBox.top = y;
        //test check for changes
        hitBox.bottom = y+playerBitmap.getHeight();
    }

    public Bitmap getPlayerBitmap() {
        return playerBitmap;
    }

    public int getSpeed() {
        return speed;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    public void startBoosting(){
        boosting = true;
    }
    public void stopBoosting(){
        boosting=false;
    }

    public Rect getHitBox() {
        return hitBox;
    }

    public int getShipShieldStrength() {
        return shipShieldStrength;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }
}

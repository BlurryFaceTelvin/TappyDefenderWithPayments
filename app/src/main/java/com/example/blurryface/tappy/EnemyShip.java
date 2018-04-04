package com.example.blurryface.tappy;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import java.util.Random;

/**
 * Created by BlurryFace on 3/5/2018.
 */

public class EnemyShip{
    //hitBox
    private Rect hitBox;
    //variable for the image of the enemy ship
    private Bitmap enemyBitmap;
    //position of the enemy ship on the phone
    private int x,y;
    //speed of the enemy ship
    private int speed = 1;
    //stop the ship from leaving the screen in the y axis
    private int maxY,minY;
    //stop the ship from leaving the screen in the x axis
    private int maxX,minX;

    public EnemyShip(Context context,int screenX,int screenY,int level) {
        switch (level) {
            case 1:
                enemyBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.enemy);
                break;
            case 2:
                enemyBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.enemy2);
                break;
            case 3:
                enemyBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.enemy3);
                break;
        }
        maxY = screenY - enemyBitmap.getHeight();
        minY = 0;
        maxX = screenX;
        minX = 0;
        Random generator = new Random();
        //10 to 16
        speed = generator.nextInt(6)+10;
        x=screenX;
        y = generator.nextInt(maxY);
        hitBox = new Rect(x,y,enemyBitmap.getWidth(),enemyBitmap.getHeight());
    }

    public Bitmap getEnemyBitmap() {
        return enemyBitmap;
    }

    public void update(int playerSpeed){
        //moves with the speed of the player
        x-=playerSpeed;
        x-=speed;
        //when the enemy gets to the end of the screen we respawn
        if(x<minX-enemyBitmap.getWidth()){
            //generate another enemy ship
            Random generator = new Random();
            speed = generator.nextInt(10)+10;
            x=maxX;
            y = generator.nextInt(maxY);
        }
        hitBox.left = x;
        hitBox.right =x+ enemyBitmap.getWidth();
        hitBox.top = y;
        //test check for changes
        hitBox.bottom = y+enemyBitmap.getHeight();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Rect getHitBox() {
        return hitBox;
    }

    public void setX(int x) {
        this.x = x;
    }

}

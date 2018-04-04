package com.example.blurryface.tappy;

import java.util.Random;

/**
 * Created by BlurryFace on 3/5/2018.
 */

public class SpaceDust {
    //position of the spacedust
    int x,y;
    private int speed;
    //detect dust leaving the screen
    private int maxX,maxY,minX,minY;
    //constructor
    public SpaceDust(int screenX,int screenY){
        maxY = screenY;
        maxX = screenX;
        minX =0;
        minY=0;
        //random generator
        //set speed between 0 and 9
        Random random = new Random();
        speed = random.nextInt(10);
        //set the random starting coordinates
        x=random.nextInt(maxX);
        y=random.nextInt(maxY);

    }
    public void update(int playerSpeed){
        //speeds up when the player speeds up
        x-= playerSpeed;
        x-=speed;
        //respawn the dust when it gets to the end of the screen
        if(x<minX){
            x=maxX;
            //generate another dust but with speed ranging from 0 to 14
            Random random = new Random();
            y = random.nextInt(maxY);
            speed = random.nextInt(15);
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}

package com.example.m.gameengine.crusher;

import android.graphics.Bitmap;

import com.example.m.gameengine.Game;
import com.example.m.gameengine.Screen;

/**
 * Created by M on 01.06.2016.
 */
public class MainMenuScreen extends Screen
{
    Bitmap mainmenu;
    Bitmap play;
    float passedTime = 0;

    public MainMenuScreen(Game game)
    {
        super(game);
        mainmenu = game.loadBitmap("starfield.png");
        play = game.loadBitmap("insertcoin.png");
    }

    @Override
    public void update(float deltaTime)
    {
        if(game.isTouchDown(0))
        {
            game.setScreen(new GameScreen(game));
            return;
        }
        game.drawBitmap(mainmenu, 0, 0);

        passedTime = passedTime + deltaTime;
        if((passedTime - (int) passedTime) > 0.5f)
        {
            game.drawBitmap(play, 160 - (play.getWidth()/2), 360);
        }
    }

    @Override
    public void pause()
    {

    }

    @Override
    public void resume()
    {

    }

    @Override
    public void dispose()
    {

    }
}

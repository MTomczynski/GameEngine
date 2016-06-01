package com.example.m.gameengine.crusher;

import android.view.KeyEvent;
import android.view.View;

import com.example.m.gameengine.Game;
import com.example.m.gameengine.Screen;

/**
 * Created by M
 */
public class Crusher extends Game
{
    @Override
    public Screen createStartScreen()
    {
        return new MainMenuScreen(this);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event)
    {
        return false;
    }
}

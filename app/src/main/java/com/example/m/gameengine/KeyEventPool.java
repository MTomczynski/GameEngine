package com.example.m.gameengine;

/**
 * Created by M
 */
public class KeyEventPool extends Pool<KeyEvent>
{
    protected KeyEvent newItem()
    {
        return new KeyEvent();
    }
}

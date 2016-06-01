package com.example.m.gameengine;

/**
 * Created by M
 */
public class KeyEvent
{
    public enum KeyEventType
    {
        Down,
        Up
    }

    public KeyEventType type;
    public int keyCode;
    public char character;
}

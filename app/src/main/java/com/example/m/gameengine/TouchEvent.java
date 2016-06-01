package com.example.m.gameengine;

/**
 * Created by M on 01.06.2016.
 */
public class TouchEvent
{
    public enum TouchEventType
    {
        Down,
        Up,
        Dragged
    }

    public TouchEventType type;
    public int x;
    public int y;
    public int pointer;
}

package com.example.m.gameengine;

/**
 * Created by M
 */
public class TouchEventPool extends Pool<TouchEvent>
{
    protected TouchEvent newItem()
    {
        return new TouchEvent();
    }
}

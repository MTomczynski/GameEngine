package com.example.m.gameengine;

/**
 * Created by M
 */
public interface TouchHandler
{
    boolean isTouchDown(int pointer);
    int getTouchX(int pointer);
    int getTouchY(int pointer);
}

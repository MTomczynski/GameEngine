package com.example.m.gameengine.crusher;

/**
 * Created by M
 */
public class Asteroid
{
    public static float WIDTH = 43;
    public static float HEIGHT = 42;

    float x;
    float y;

    float vy = 190;

    public Asteroid(float x, float y)
    {
        this.x = x;
        this.y = y;
    }
}

package com.example.m.gameengine.crusher;

/**
 * Created by M
 */
public class EnemySpaceship
{
    public static float WIDTH = 45;
    public static float HEIGHT = 25;

    //screen position
    public float x;
    public float y;

    public float vx = 90;

    public EnemySpaceship(float x, float y)
    {
        this.x = x;
        this.y = y;
    }
}

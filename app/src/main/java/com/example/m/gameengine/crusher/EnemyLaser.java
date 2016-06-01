package com.example.m.gameengine.crusher;

/**
 * Created by M
 */
public class EnemyLaser
{
    public static float WIDTH = 10;
    public static float HEIGHT = 30;

    float x;
    float y;

    float vy = 155;

    public EnemyLaser(float x, float y)
    {
        this.x = x;
        this.y = y;
    }
}

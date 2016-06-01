package com.example.m.gameengine.crusher;

import android.graphics.Bitmap;

import com.example.m.gameengine.Game;

/**
 * Created by M on 01.06.2016.
 */
public class WorldRenderer
{
    Game game;
    World world;
    Bitmap spaceshipImage;
    Bitmap asteroidImage;
    Bitmap laserShootImage;
    Bitmap enemySpaceshipImage;
    Bitmap enemyLaserShootImage;

    public WorldRenderer(Game game, World world)
    {
        this.game = game;
        this.world = world;
        this.spaceshipImage = game.loadBitmap("spaceshipGreen45.png");
        this.asteroidImage = game.loadBitmap("asteroid.png");
        this.laserShootImage = game.loadBitmap("laserYellow.png");
        this.enemySpaceshipImage = game.loadBitmap("enemySpaceshipRed45.png");
        this.enemyLaserShootImage = game.loadBitmap("laserRed.png");
    }

    public void render()
    {
        //if I want moving background cut it here with a drawbitmap cutting method and do it on variables
        //game.drawbitmap(background, 0 , 0, (int)

        game.drawBitmap(spaceshipImage, (int) world.spaceship.x, (int) world.spaceship.y);
        for(int i = 0; i < world.lasers.size(); i++)
        {
            Laser laser = world.lasers.get(i);
            game.drawBitmap(laserShootImage, (int) laser.x, (int) laser.y);
        }
        for(int i = 0; i < world.enemyLasers.size(); i++)
        {
            EnemyLaser enemyLaser = world.enemyLasers.get(i);
            game.drawBitmap(enemyLaserShootImage, (int) enemyLaser.x, (int) enemyLaser.y);
        }
        for(int i = 0; i < world.asteroids.size(); i++)
        {
            Asteroid asteroid = world.asteroids.get(i);
            game.drawBitmap(asteroidImage, (int) asteroid.x, (int) asteroid.y);
        }
        for(int i = 0; i < world.enemySpaceships.size(); i++)
        {
            EnemySpaceship enemySpaceship = world.enemySpaceships.get(i);
            game.drawBitmap(enemySpaceshipImage, (int) enemySpaceship.x, (int) enemySpaceship.y);
        }
    }
}

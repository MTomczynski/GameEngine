package com.example.m.gameengine.crusher;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by M
 */
public class World
{
    public static float MIN_X = 0;
    public static float MAX_X = 319;
    public static float MIN_Y = 32;
    public static float MAX_Y = 470;
    boolean gameOver = false;
    float passedTime = 0;
    float passedTimeStage = 0;
    float passedTime2 = 0;
    float passedTime3 =0;
    int points = 0;

    Spaceship spaceship = new Spaceship();
    List<EnemySpaceship> enemySpaceships = new ArrayList<>();
    List<Asteroid> asteroids = new ArrayList<>();
    List<Laser> lasers = new ArrayList<>();
    List<EnemyLaser> enemyLasers = new ArrayList<>();
    CollisionListener listener;

    Stage stage = Stage.Stage_1;

    enum Stage
    {
        Stage_1,
        Stage_2,
        Stage_3,
    }

    public World(CollisionListener listener)
    {
        generateAsteroids();
        laserShoot();
        this.listener = listener;
    }

    //for touch handling in world add List<TouchEvent> touchList to the update,
    //public void update(float deltaTime, float accelX, List<TouchEvent> touchList)
    public void update(float deltaTime, float accelX)
    {
        passedTimeStage = passedTimeStage + deltaTime;
        passedTime3 = passedTime3 + deltaTime;
        if(passedTime3 > 40) stage = Stage.Stage_2;
        if(stage == Stage.Stage_1)
        {
            stage1(deltaTime);
        }
        if(stage == Stage.Stage_2)
        {
            passedTime3 = 0;
            stage2(deltaTime);
        }
        if(collideLaserEnemySpaceship())
        {
            stage = Stage.Stage_1;
            points = points + 50;
        }


        if(collideSpaceshipAsteroid() || collideEnemyLaserSpaceship())
        {
            gameOver = true;
            return;
        }

        //asteroid movement
        for(int i = 0; i < asteroids.size(); i++ )
        {
            Asteroid asteroid = asteroids.get(i);
            asteroid.y = asteroid.y + asteroid.vy * deltaTime;
            if(asteroid.y > 480) asteroids.remove(i);
        }

        //laser movement
        for(int i = 0; i < lasers.size(); i++)
        {
            Laser laser = lasers.get(i);
            laser.y = laser.y - laser.vy * deltaTime;
            if(laser.y < - 30 )lasers.remove(i);
        }
        //Enemy laser movement
        for(int i = 0; i < enemyLasers.size(); i++)
        {
            EnemyLaser enemyLaser = enemyLasers.get(i);
            enemyLaser.y = enemyLaser.y + enemyLaser.vy * deltaTime;
            if(enemyLaser.y > 510 )enemyLasers.remove(i);
        }


        spaceship.x = spaceship.x - accelX * 50 * deltaTime;
        if(spaceship.x < MIN_X) spaceship.x = MIN_X;
        if(spaceship.x > MAX_X - Spaceship.WIDTH) spaceship.x = MAX_X - Spaceship.WIDTH;

        collideLaserAsteroid();
    }

    public void stage1(float deltaTime)
    {
        passedTime = passedTime + deltaTime;
        passedTime2 = passedTime2 + deltaTime;
        if ((passedTime - (int) passedTime) > 0.9f)
        {

            if (passedTime2 > 20f)
            {
                if ((passedTime - (int) passedTime) > 0.85f)
                {
                    generateAsteroids();
                }
                if(passedTime2 > 300)
                {
                    if((passedTime - (int) passedTime) > 0.85f)
                    {
                        generateAsteroids();
                    }
                }
            }
            generateAsteroids();
            passedTime = 0;
            points = points + 1;
        }
    }

    public void stage2(float deltaTime)
    {
        if(enemySpaceships.size() == 0) enemySpaceships.add(new EnemySpaceship( -1, - 50 ));
        for(int q = 0; q < enemySpaceships.size(); q++)
        {
            EnemySpaceship enemySpaceship = enemySpaceships.get(q);
            enemySpaceship.y = enemySpaceship.y * 50 * deltaTime;
            enemySpaceship.x = enemySpaceship.x + enemySpaceship.vx * deltaTime;
            if (enemySpaceship.x < MIN_X)
            {
                enemySpaceship.vx = -enemySpaceship.vx;
                enemySpaceship.x = MIN_X;
            }
            if ((enemySpaceship.x + EnemySpaceship.WIDTH) > MAX_X)
            {
                enemySpaceship.vx = -enemySpaceship.vx;
                enemySpaceship.x = MAX_X - EnemySpaceship.WIDTH;
            }
            passedTime = passedTime + deltaTime;
            if (passedTime > 1)
            {
                passedTime = 0;
                enemyLasers.add(new EnemyLaser(enemySpaceship.x + 22, 20));
            }
        }
    }

    public boolean collideSpaceshipAsteroid()
    {
        for(int i = 0; i < asteroids.size(); i++ )
        {
            Asteroid asteroid = asteroids.get(i);
            if(collideRects(asteroid.x, asteroid.y, Asteroid.WIDTH, Asteroid.HEIGHT,
                    spaceship.x, spaceship.y, Spaceship.WIDTH, Spaceship.HEIGHT))
            {
                listener.collisionSpaceship();
                return true;
            }
        }
        return false;
    }

    public void laserShoot()
    {
        lasers.add(new Laser(spaceship.x + 21, 400));
    }

    public void collideLaserAsteroid()
    {
        for(int i = 0; i < asteroids.size(); i++)
        {
            Asteroid asteroid = asteroids.get(i);
            for(int q = 0; q < lasers.size(); q++)
            {
                Laser laser = lasers.get(q);
                if (collideRects(asteroid.x, asteroid.y, Asteroid.WIDTH, Asteroid.HEIGHT,
                        laser.x, laser.y, Laser.WIDTH, Laser.HEIGHT))
                {
                    points = points + 10;
                    asteroids.remove(i);
                    lasers.remove(q);
                    listener.collisionLaser();
                    break;
                }
            }
        }
    }

    public boolean collideEnemyLaserSpaceship()
    {
        for(int i = 0; i < enemyLasers.size(); i++)
        {
            EnemyLaser enemyLaser = enemyLasers.get(i);
            if (collideRects(spaceship.x, spaceship.y, Spaceship.WIDTH, Spaceship.HEIGHT,
                    enemyLaser.x, enemyLaser.y, EnemyLaser.WIDTH, EnemyLaser.HEIGHT))
            {
                listener.collisionSpaceship();
                return true;
            }
        }
        return false;
    }

    public boolean collideLaserEnemySpaceship()
    {
        for(int i = 0; i < lasers.size(); i++)
        {
            Laser laser = lasers.get(i);
            for(int q = 0; q < enemySpaceships.size(); q++)
            {
                EnemySpaceship enemySpaceship = enemySpaceships.get(q);
                if (collideRects(enemySpaceship.x, enemySpaceship.y, EnemySpaceship.WIDTH,
                        EnemySpaceship.HEIGHT, laser.x, laser.y, Laser.WIDTH, Laser.HEIGHT))
                {
                    points = points + 50;
                    lasers.remove(i);
                    enemySpaceships.remove(q);
                    listener.collisionEnemySpaceship();
                    return true;
                }
            }
        }
        return false;
    }

    //Method used for detecting colisions
    public boolean collideRects(float x, float y, float width, float height,
                                float x2, float y2, float width2, float height2)
    {
        if(x < x2 + width2 && x + width > x2 &&
                y < y2 + height2 && y + height > y2)
        {
            return true;
        }
        return false;
    }

    private void generateAsteroids()
    {
        Random random = new Random();
        int n = random.nextInt(300)+1 ;
        int e = random.nextInt(200)+40;
        e = -e;
        asteroids.add(new Asteroid(n, e));
    }
}

package com.example.m.gameengine.crusher;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;

import com.example.m.gameengine.Game;
import com.example.m.gameengine.Screen;
import com.example.m.gameengine.Sound;
import com.example.m.gameengine.TouchEvent;

import java.util.List;

/**
 * Created by M
 */
public class GameScreen extends Screen
{
    Bitmap background;
    Bitmap resume;
    Bitmap gameOver;
    Typeface font;
    Sound crushSound;
    float passedTime;
    float passedTime2;

    World world;
    WorldRenderer renderer;
    State state = State.Running;

    enum State
    {
        Paused,
        Running,
        GameOver
    }

    public GameScreen(Game game)
    {
        super(game);
        background = game.loadBitmap("starfield.png");
        resume = game.loadBitmap("resume.png");
        gameOver = game.loadBitmap("gameover.png");
        font = game.loadFont("font.ttf");
        crushSound = game.loadSound("blocksplosion.wav");
        world = new World(new CollisionListener()
        {
            @Override
            public void collisionSpaceship()
            {
                crushSound.play(1);
            }

            @Override
            public void collisionLaser()
            {
                crushSound.play(1);
            }

            @Override
            public void collisionEnemySpaceship()
            {
                crushSound.play(1);
            }
        });
        renderer = new WorldRenderer(game, world);
    }

    @Override
    public void update(float deltaTime)
    {
        if(state == State.Paused && game.getTouchEvents().size() >0)
        {
            state = State.Running;
        }

        if(world.gameOver) state = State.GameOver;
        if(state == State.GameOver)
        {
            List<TouchEvent> events = game.getTouchEvents();
            for(int i = 0; i < events.size(); i++)
            {
                if(events.get(i).type == TouchEvent.TouchEventType.Up)
                {
                    game.setScreen(new MainMenuScreen(game));
                    return;
                }
            }
        }

        //position of the pause button on the game screen, upper right corner in this case
        if(state == State.Running && game.getTouchY(0) < 32 && game.getTouchX(0) > (320-32))
        {
            state = State.Paused;
            return;
        }

        game.drawBitmap(background, 0, 0);

        if(state == State.Paused)
        {
            //position of the resume button while paused screen
            game.drawBitmap(resume, 160 - resume.getWidth()/2, 240 - resume.getHeight()/2);
        }
        if(state == State.GameOver)
        {
            game.drawBitmap(gameOver, 160 - gameOver.getWidth()/2, 300);
        }

        if(state == State.Running)
        {
            world.update(deltaTime, game.getAccelerometer()[0]);
        }

        passedTime = passedTime + deltaTime;
        List<TouchEvent> events = game.getTouchEvents();
        for(int i = 0; i < events.size(); i++)
        {
            if(events.get(i).type == TouchEvent.TouchEventType.Down && passedTime > 5)
            {
                world.laserShoot();
                passedTime = 0;
            }
        }

        game.drawText(font, Integer.toString(world.points), 25, 450, Color.RED, 12);
        if(passedTime < 6)
        {
            game.drawText(font, Integer.toString((int) passedTime), 300, 450, Color.RED, 12 );
        }
        passedTime2 = passedTime2 + deltaTime;
        if(passedTime > 6)
        {
            if((passedTime2 - (int) passedTime2) > 0.5f)
            {
                game.drawText(font, "Laser ready!", 230, 450, Color.RED, 10);
            }

        }
        renderer.render();
    }

    @Override
    public void pause()
    {
        if(state == State.Running) state = State.Paused;
        if(state == State.Paused)
        {
            //position of the resume button while paused screen
            game.drawBitmap(resume, 160 - resume.getWidth()/2, 240 - resume.getHeight()/2);
        }
    }

    @Override
    public void resume()
    {

    }

    @Override
    public void dispose()
    {

    }
}

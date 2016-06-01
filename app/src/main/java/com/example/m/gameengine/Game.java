package com.example.m.gameengine;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by M
 */
public abstract class Game extends Activity implements Runnable, View.OnKeyListener, SensorEventListener
{
    private Thread mainLoopThread;
    private State state = State.Paused;
    private List<State> stateChanges = new ArrayList<>();

    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private Screen screen;
    private Canvas canvas = null;

    private Bitmap offscreenSurface;

    private boolean pressedKeys[] = new boolean[256];
    private TouchHandler touchHandler;
    private TouchEventPool touchEventPool = new TouchEventPool();
    private List<TouchEvent> touchEvents = new ArrayList<>();
    private List<TouchEvent> touchEventBuffer = new ArrayList<>();

    private KeyEventPool keyEventPool = new KeyEventPool();
    private List<KeyEvent> keyEvents = new ArrayList<>();
    private List<KeyEvent> keyEventBuffer = new ArrayList<>();

    SensorManager manager;
    private float[] accelerometer = new float[3];

    private SoundPool soundPool;

    //how many times run method call the update, check how fast the CPU is
    private int framesPerSecond = 0;
    Paint paint = new Paint();

    //******above are all variables


    //below all methods, above the variables
    public abstract Screen createStartScreen();
    public void setScreen(Screen screen)
    {
        if(this.screen != null) screen.dispose();
        this.screen = screen;
    }


    public void onCreate(Bundle instanceBundle) //Waking up method
    {
        super.onCreate(instanceBundle);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //some window methods, for screen etc
        surfaceView = new SurfaceView(this); //for drawing, canvas etc
        setContentView(surfaceView); //I want to use surface view (THIS class) for drawing
        surfaceHolder = surfaceView.getHolder(); //give me a handle to a surface

        //surface view: focus on screen, attach on key listener,
        surfaceView.setFocusableInTouchMode(true);
        surfaceView.requestFocus();
        surfaceView.setOnKeyListener(this);
        touchHandler = new MultiTouchHandler(surfaceView, touchEventBuffer, touchEventPool);

        manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if(manager.getSensorList(Sensor.TYPE_ACCELEROMETER).size() != 0)
        {
            Sensor accelerometer = manager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
            manager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        }

        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        this.soundPool = new SoundPool(20 , AudioManager.STREAM_MUSIC, 0);
        screen = createStartScreen();
    }

    //check variable UP; We are checking CPU
    public int getFramerate()
    {
        return framesPerSecond;
    }

    @Override
    public void run()
    {

        long lastTime = System.nanoTime();

        while(true) //forever while, this is mainloop thread independent
        {
            if(screen != null)
            {

            }

            synchronized (stateChanges)
            {

                for (int i = 0; i < stateChanges.size(); i++)
                {
                    state = stateChanges.get(i);
                    if (state == state.Disposed)
                    {
                        if (screen != null)
                        {
                            screen.dispose();
                        }
                        Log.d("Game", "State is changed to disposed");
                        return; //its quiting the RUN!! method, so its stopping the mainloopthread, run is void this is why its just return without values

                    }
                    if (state == State.Paused)
                    {
                        if (screen != null)
                        {
                            screen.pause();
                        }
                        Log.d("Game", "State is changed to paused");
                        return;

                    }
                    if (state == State.Resumed)
                    {
                        if (screen != null)
                        {
                            screen.resume();
                            Log.d("Game", "State is changed to resumed");
                        }
                        state = State.Running;
                    }
                }
                stateChanges.clear();
                if (state == State.Running)
                {
                    if (!surfaceHolder.getSurface().isValid()) {continue;}
                    Canvas canvas = surfaceHolder.lockCanvas();

                    fillEvent();
                    if(surfaceView.getWidth() > surfaceView.getHeight())
                    {
                        setOffScreenSurface(480, 320);
                    }
                    else
                    {
                        setOffScreenSurface(320, 480);
                    }
                    long currTime = System.nanoTime();
                    if (screen != null) screen.update((currTime - lastTime)/1000000000.0f);
                    lastTime = currTime;
                    freeEvents();

                    //make one universal screen to properly scale everything
                    src.left = 0;
                    src.top = 0;
                    src.right = offscreenSurface.getWidth();
                    src.bottom = offscreenSurface.getHeight();

                    //fit the screen size that the graphics can change due to pixels density
                    dst.left = 0;
                    dst.top = 0;
                    dst.right = surfaceView.getWidth();
                    dst.bottom = surfaceView.getHeight();

                    canvas.drawBitmap(offscreenSurface, src, dst, null);

                    surfaceHolder.unlockCanvasAndPost(canvas);
                    canvas = null;
                }
            }//end of synchronized
        }//end of while loop
    }//end of run method

    private void fillEvent()
    {
        synchronized (keyEventBuffer)
        {
            for(int i = 0; i < keyEventBuffer.size(); i++)
            {
                keyEvents.add(keyEventBuffer.get(i));
            }
            keyEventBuffer.clear();
        }
        synchronized (touchEventBuffer)
        {
            for(int i = 0; i <touchEventBuffer.size(); i++)
            {
                touchEvents.add(touchEventBuffer.get(i));
            }
            touchEventBuffer.clear();
        }
    }

    public void freeEvents()
    {
        synchronized(keyEvents)
        {
            for(int i = 0; i < keyEvents.size(); i++)
            {
                keyEventPool.free(keyEvents.get(i));
            }
            keyEvents.clear();
        }
        synchronized (touchEvents)
        {
            for( int i = 0; i < touchEvents.size(); i ++)
            {
                touchEventPool.free(touchEvents.get(i));
            }
            touchEvents.clear();
        }
    }

    public Bitmap loadBitmap(String fileName)
    {
        InputStream in = null;
        Bitmap bitmap = null;
        try
        {
            in = getAssets().open(fileName);
            bitmap = BitmapFactory.decodeStream(in);
            if(bitmap == null)
            {
                throw new RuntimeException("Shit there was no graphics in file: " + fileName);
            }
            return bitmap;
        }
        catch (IOException e)
        {
            throw new RuntimeException("Shit I could not open file: " + fileName);
        }
        finally
        {
            if(in != null)
                try
                {
                    in.close();
                }
                catch(IOException e) {}
        }
    }

    public Music loadMusic(String fileName)
    {
        try
        {
            AssetFileDescriptor assetFileDescriptor = getAssets().openFd(fileName);
            return new Music(assetFileDescriptor);
        }
        catch (IOException e)
        {
            throw new RuntimeException("Could not load music file: " + fileName);
        }
    }

    public Sound loadSound(String fileName)
    {
        try
        {
            AssetFileDescriptor assetFileDescriptor = getAssets().openFd(fileName);
            int soundId = soundPool.load(assetFileDescriptor, 1);
            return new Sound(soundPool,soundId);
        }
        catch (IOException e)
        {
            throw new RuntimeException("Could not load sound file " + fileName);
        }
    }

    public void clearFramebuffer(int color)
    {
        if(canvas != null) canvas.drawColor(color);
    }
    public int getFramebufferWidth()
    {
        return offscreenSurface.getWidth();
    }
    public int getFramebufferHeight()
    {
        return offscreenSurface.getHeight();
    }
    public void drawBitmap(Bitmap bitmap, int    x, int y)
    {
        if(canvas != null) canvas.drawBitmap(bitmap, x, y, null);
    }

    //we use it for drawing one part from a bitmap instead full bit map, save memory
    Rect src = new Rect();
    Rect dst = new Rect();
    public void drawBitmap(Bitmap bitmap, int x, int y, int srcX, int srcY,
                           int srcWidth, int srcHeight)
    {
        if(canvas == null) return;
        src.left = srcX;
        src.top = srcY;
        src.right = srcX + srcWidth;
        src.bottom = srcY + srcHeight;

        //rectangles!
        dst.left = x;
        dst.top = y;
        dst.right = x + srcWidth;
        dst.bottom = y + srcHeight;

        canvas.drawBitmap(bitmap, src, dst, null);
    }

    public void setOffScreenSurface(int width, int height)
    {
        //bitmap method, saving resources, save some time on resources
        if (offscreenSurface != null) offscreenSurface.recycle();
        //im saying, make a bitmap object, createBitmap is static method, 565 = 16 bits for colors
        offscreenSurface = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        canvas = new Canvas(offscreenSurface);
    }

//    public boolean onKey(View v, int keyCode, KeyEvent event)
//    {
//        if(event.getAction() == KeyEvent.ACTION_DOWN)
//        {
//            pressedKeys[keyCode] = true;
//            synchronized (keyEventBuffer)
//            {
//                KeyEvent keyEvent = keyEventPool.obtain();
//                keyEvent.type = KeyEvent.KeyEventType.Down;
//                keyEvent.keyCode = keyCode;
//                keyEventBuffer.add(keyEvent);
//            }
//        }
//        if(event.getAction() == KeyEvent.ACTION_UP)
//        {
//            pressedKeys[keyCode] = false;
//            synchronized (keyEventBuffer)
//            {
//                KeyEvent keyEvent = keyEventPool.obtain();
//                keyEvent.type = KeyEvent.KeyEventType.Up;
//                keyEvent.keyCode = keyCode;
//                keyEvent.character = (char) event.getUnicodeChar();
//                keyEventBuffer.add(keyEvent);
//            }
//        }
//        return false;
//    }

    public boolean isKeyPressed(int keyCode)
    {

        return pressedKeys[keyCode];
    }


    public boolean isTouchDown(int pointer)
    {
        return touchHandler.isTouchDown(pointer);
    }
    public int getTouchX(int pointer)
    {
        int offscreenX = (int) ((touchHandler.getTouchX(pointer)
                / (float) surfaceView.getWidth())
                * offscreenSurface.getWidth());
        return offscreenX;
    }
    public int getTouchY(int pointer)
    {
        int offscreenY = (int) ((touchHandler.getTouchY(pointer)
                / (float) surfaceView.getHeight())
                * offscreenSurface.getHeight());
        return offscreenY;
    }

    public List<KeyEvent> getKeyEvents()
    {
        return keyEvents;
    }

    public List<TouchEvent> getTouchEvents()
    {
        return touchEvents;
    }

    /*
    public List<KeyEvent> getKeyEvents() {return null;}
    public List<TouchEvent> getTouchEvent() {return null;}
    */
    public float[] getAccelerometer()
    {
        return accelerometer;
    }


    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {

    }

    public void onSensorChanged(SensorEvent event)
    {
        System.arraycopy(event.values, 0, accelerometer, 0, 3);
    }

    public Typeface loadFont(String fileName) //this will needed for import the fonts, for write the points out
    {
        Typeface font = Typeface.createFromAsset(getAssets(), fileName);
        if(font == null)
        {
            throw new RuntimeException("Could not load the bloody fonts: " + fileName);
        }
        return font;
    }

    public void drawText (Typeface font, String text, int x, int y, int color, int size)
    {
        paint.setTypeface(font);
        paint.setTextSize(size);
        paint.setColor(color);
        canvas.drawText(text, x, y + size, paint);
    }

    public void onPause()
    {
        super.onPause();
        synchronized(stateChanges)
        {
            if(isFinishing())
            {
                stateChanges.add(stateChanges.size(), State.Disposed);
                manager.unregisterListener(this);
            }
            else
            {
                stateChanges.add(stateChanges.size(), State.Paused);
            }
        }
        try
        {
            mainLoopThread.join();
        }
        catch(InterruptedException e)
        {

        }
        if(isFinishing())
        {
            soundPool.release();
        }
    }
    public void onResume() //
    {
        super.onResume();
        mainLoopThread = new Thread(this);
        mainLoopThread.start();
        synchronized (stateChanges)
        {
            stateChanges.add(stateChanges.size(), State.Resumed);
        }
    }
}

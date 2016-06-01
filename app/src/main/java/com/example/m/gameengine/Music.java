package com.example.m.gameengine;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;

import java.io.IOException;

/**
 * Created by M on 01.06.2016.
 */
public class Music implements MediaPlayer.OnCompletionListener
{
    private MediaPlayer mediaPlayer; //media player doing the audio playback
    private boolean isPrepared = false; //is the mediaplayer ready?

    public Music(AssetFileDescriptor assetDescriptor)
    {
        mediaPlayer = new MediaPlayer();
        try
        {
            mediaPlayer.setDataSource(assetDescriptor.getFileDescriptor(),
                    assetDescriptor.getStartOffset(),
                    assetDescriptor.getLength());
            mediaPlayer.prepare();
            isPrepared = true;
            mediaPlayer.setOnCompletionListener(this);
        }
        catch(Exception e)
        {
            throw new RuntimeException("Could not load music ");
        }
    }

    public void dispose()
    {
        if(mediaPlayer.isPlaying()) mediaPlayer.stop();
        mediaPlayer.release();
    }


    //VVVVVVVVVVVVV asking the music object what is he doing, playing/looping
    // its to syncrhonized everything that we wont start to play two tracks at the same time
    public boolean isLooping()
    {
        return mediaPlayer.isLooping();
    }

    public boolean isPlaying()
    {
        return mediaPlayer.isPlaying();
    }

    public boolean isStopped()
    {
        return !isPrepared;
    }

    public void pause()
    {
        if(mediaPlayer.isPlaying()) mediaPlayer.pause();
    }

    public void play()
    {
        if(mediaPlayer.isPlaying()) return;
        try
        {
            synchronized (this)
            {
                if(!isPrepared) mediaPlayer.prepare();
                mediaPlayer.start();
            }

        }
        catch (IllegalStateException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void setLooping(boolean isLooping)
    {
        mediaPlayer.setLooping(isLooping());
    }

    public void setVolume(float volume)
    {
        mediaPlayer.setVolume(volume, volume);
    }

    public void stop()
    {
        synchronized (this)
        {
            if(!isPrepared) return;
            mediaPlayer.stop();
            isPrepared = false;
        }
    }

    public void onCompletion(MediaPlayer arg0)
    {
        synchronized(this)
        {
            isPrepared = false;
        }
    }

}

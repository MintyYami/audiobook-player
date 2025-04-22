package com.example.cw_3.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.cw_3.R;
import com.example.cw_3.object.AudiobookPlayer;
import com.example.cw_3.util;

public class AudioService extends Service {
    private static final String TAG = "COMP3018";
    private static final String CHANNEL_ID = "PlayerChannel";
    private static final int NOTIFICATION_ID = 1;
    private boolean isPlaying = false;
    private int progress = 0;
    private String progresString = "0";
    private boolean cancelPlaying = false;
    private final IBinder binder = new LocalBinder();
    private PlayerCallback callback;
    private AudiobookPlayer audioPlayer;
    private Float speed;
    private String audioFilepath;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "[Service] onCreate: Player Service");

        audioPlayer = new AudiobookPlayer();

        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int start) {
        Log.d(TAG, "[Service] onStartCommand: Player Service");

        Log.d(TAG, "[Service] onStartCommand: Get audio values");
        // either get the value from intent or get default value
        speed = intent.getFloatExtra(
                "speed",
                Float.parseFloat(getResources().getString(R.string.speedDefault))
        );
        // get audiobook file path
        if(intent.hasExtra("audioFilepath")) {
            audioFilepath = intent.getStringExtra("audioFilepath");
        } else {
            audioFilepath = "";
        }
        // get audio start
        progress = intent.getIntExtra("audioProgress", 0);

        if(isPlaying) {
            // stop old audio and play new one
            audioPlayer.stop();
            audioPlayer.load(audioFilepath, speed);
            audioPlayer.skipTo(progress);

            return START_NOT_STICKY;
        }
        // play audio
        audioPlayer.load(audioFilepath, speed);
        audioPlayer.skipTo(progress);

        Notification notification = new Notification.Builder(this, CHANNEL_ID)
                .setContentTitle("Audiobook PLayer")
                .setContentText("Currently playing an audio book from the AudioBook Player.")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .build();
        Log.d(TAG, "[Service] onStartCommand: Start ForeGround Called");
        startForeground(NOTIFICATION_ID, notification);

        // Audio running task
        new Thread(() -> {
            try {
                isPlaying = true;

                Log.d(TAG, "[Service] Audio is running, isPlaying  is true");
                while (!cancelPlaying) {
                    Thread.sleep(1000);
                    if ((audioPlayer != null) && (audioPlayer.getState() != AudiobookPlayer.AudiobookPlayerState.STOPPED)) {
                        progress = audioPlayer.getProgress();
                    }
                    Log.d(TAG, "[Service] onStartCommand: Playing progress " + String.valueOf(progress));

                    if (callback != null) {
                        callback.onPlayProgress(progress);
                    }
                }
                Log.d(TAG, "[Service] onStartCommand: Audiobook completed");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Log.d(TAG, "[Service] onStartCommand: StopSelf() and audioPlayer.stop() called");
            audioPlayer.stop();
            stopSelf();
            cancelPlaying = false;
            isPlaying = false;
        }).start();

        return START_NOT_STICKY;
    }

    private void createNotificationChannel() {
        Log.d(TAG, "[Service] createNotificationChannel: called in service");
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Audiobook Player";
            String description = "The Audiobook Player is now playing...";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            Log.d(TAG, "[Service] createNotificationChannel: completed");
        }
    }

    public interface PlayerCallback {
        void onPlayProgress(int progress);
    }

    public void setCallback(PlayerCallback callback) {
        this.callback = callback;
    }

    public void setCancelPlay(boolean value) {
        this.cancelPlaying = value;
    }

    public AudiobookPlayer getAudioPlayer() {
        return audioPlayer;
    }

    public boolean getIsPlaying() {
        return isPlaying;
    }

    public class LocalBinder extends Binder {
        public AudioService getService() {
            return AudioService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void setAudioSpeed(float speed) {
        Log.d(TAG, "[AudioService] setAudioSpeed: " + speed);
        this.speed = speed;
        audioPlayer.setPlaybackSpeed(speed);
    }
}
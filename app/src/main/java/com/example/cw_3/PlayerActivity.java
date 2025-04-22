package com.example.cw_3;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.cw_3.databinding.ActivityPlayerBinding;
import com.example.cw_3.model.PlayerViewModel;
import com.example.cw_3.object.Audio;
import com.example.cw_3.service.AudioService;

public class PlayerActivity extends AppCompatActivity {
    private static final String TAG = "COMP3018";
    private PlayerViewModel viewModel;
    private AudioService audioService;
    private boolean isBound = false;
    private TextView buttonBack, buttonPrev, buttonStart, buttonPause, buttonNext, buttonStop, buttonBM, audioProgress;
    private ProgressBar progressBar;
    private int progressStart, progressBookmark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_player);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // initialise model for live data
        viewModel = new ViewModelProvider(this).get(PlayerViewModel.class);

        // initialise livedata values
        Intent intent = getIntent();
        if (getIntent() != null) {
            initialiseValues(intent);
        } else {
            initialiseValues(new Intent());
        }

        // livedata binding
        ActivityPlayerBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_player);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        // set livedata observer
        dataObservers(this);

        // initialise click listener for previous button
        onClickStart();
        onClickStopped();
        onClickPrev();
        onClickNext();
        onClickPaused();
        onClickBack();
        onClickBookmark();
    }

    private void initialiseValues(Intent intent) {
        // name
        if(intent.hasExtra("audioID")) {
            int audioID = Integer.parseInt(intent.getStringExtra("audioID"));
            viewModel.getAudioID().setValue(audioID);
            Audio audio = util.getAudio(getResources(), audioID);
            viewModel.getAudioName().setValue(audio.getAudioName());
            // TODO store audio, load, etc.
        }
        progressStart = intent.getIntExtra("progress", 0);
        // speed
        String speedString;
        if (intent.hasExtra("speed")) {
            // get speed from intent & set as speed
            speedString = intent.getStringExtra("speed");
        } else {
            // set speed to default
            speedString = getResources().getString(R.string.speedDefault);
        }
        viewModel.getAudioSpeed().setValue(Float.parseFloat(speedString));
        // theme
        String themeString;
        if (intent.hasExtra("themeMode")) {
            // get background colour from intent & set as BGColour
            themeString = intent.getStringExtra("themeMode");
        } else {
            themeString = getResources().getString(R.string.themeModeDefault);
        }
        viewModel.getThemeMode().setValue(Integer.parseInt(themeString));
    }

    private void dataObservers(AppCompatActivity activity) {
        viewModel.getThemeMode().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                // get theme colours
                int[] colours = util.getThemeColours(activity, integer);

                // change background colours
                findViewById(R.id.main).setBackgroundColor(colours[1]);
                ((TextView)findViewById(R.id.textTitlePlayer)).setTextColor(colours[0]);

                // get views
                buttonBack = findViewById(R.id.buttonBack);
                buttonBack.setBackgroundColor(colours[0]);
                buttonBack.setBackgroundTintList(ColorStateList.valueOf(colours[0]));
                buttonBack.setTextColor(colours[2]);
                findViewById(R.id.scroll).setBackgroundColor(colours[0]);
                ((TextView)findViewById(R.id.textViewAudioTitle)).setTextColor(colours[2]);
                ((TextView)findViewById(R.id.textViewSpeed)).setTextColor(colours[2]);
                buttonPrev = findViewById(R.id.buttonPrev);
                buttonPrev.setBackgroundColor(colours[2]);
                buttonPrev.setTextColor(colours[0]);
                buttonStart = findViewById(R.id.buttonStart);
                buttonStart.setBackgroundColor(colours[2]);
                buttonStart.setTextColor(colours[0]);
                buttonPause = findViewById(R.id.buttonPause);
                buttonPause.setBackgroundColor(colours[2]);
                buttonPause.setTextColor(colours[0]);
                buttonNext = findViewById(R.id.buttonNext);
                buttonNext.setBackgroundColor(colours[2]);
                buttonNext.setTextColor(colours[0]);
                buttonStop = findViewById(R.id.buttonStop);
                buttonStop.setBackgroundColor(colours[2]);
                buttonStop.setTextColor(colours[0]);
                ((TextView)findViewById(R.id.textViewProgress)).setTextColor(colours[2]);
                buttonBM = findViewById(R.id.buttonBookmark);
                buttonBM.setTextColor(colours[0]);
                buttonBM.setBackgroundColor(colours[2]);
            }
        });
        viewModel.getAudioID().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                // get new audio
                Audio new_audio = util.getAudio(getResources(), integer);
                // change audio name and progress
                viewModel.getAudioName().setValue(new_audio.getAudioName());
            }
        });
        // TODO CHANGE TO CHANGE AT MAIN ACTIVITY
        viewModel.getAudioSpeed().observe(this, new Observer<Float>() {
            @Override
            public void onChanged(Float aFloat) {
                // change speed in service if service is happening
                if(isBound && audioService != null) {
                    audioService.setAudioSpeed(aFloat);
                }
            }
        });
    }


    // BUTTON INITIALISING


    private void onClickStart() {
        buttonStart = findViewById(R.id.buttonStart);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isBound && audioService != null) {
                    // play audio player
                    audioService.getAudioPlayer().play();
                    Log.d(TAG, "[PlayerActivity] onClick: START PLAYING AGAIN");
                }
            }
        });
    }
    private void onClickPaused() {
        buttonPause = findViewById(R.id.buttonPause);
        buttonPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isBound && audioService != null) {
                    // pause audio player
                    audioService.getAudioPlayer().pause();
                    Log.d(TAG, "[PlayerActivity] onClickPaused: PAUSED FROM PLAYING");
                }
            }
        });
    }
    private void onClickStopped() {
        buttonStop = findViewById(R.id.buttonStop);
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "[Activity] onClick: stopService Intent Launched");
                Log.d(TAG, "onClick: Changing the value of cancelPlay in the service");
                audioService.setCancelPlay(true);

                // stop audio player
                audioService.getAudioPlayer().stop();

                stopIntentService();

                // go back to main activity
                Intent intent = new Intent();
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }
    private void onClickPrev(){
        buttonPrev = findViewById(R.id.buttonPrev);
        buttonPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] audioList = util.getAudioNames(getResources());
                int length = audioList.length-1;
                if(viewModel.getAudioID().getValue() == 0) {
                    viewModel.getAudioID().setValue(length);
                } else {
                    viewModel.getAudioID().setValue(viewModel.getAudioID().getValue()-1);
                }

                // load previous audio
                if (isBound && audioService != null) {
                    int newAudioID = viewModel.getAudioID().getValue();
                    String audioFilepath = util.getAudio(getResources(), newAudioID).getAudioPath();
                    float speed = viewModel.getAudioSpeed().getValue();

                    audioService.getAudioPlayer().stop();
                    audioService.getAudioPlayer().load(audioFilepath, speed);
                }
            }
        });
    }
    private void onClickNext(){
        buttonNext = findViewById(R.id.buttonNext);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int new_id = util.getAudioID(viewModel.getAudioName().getValue(), getResources());
                viewModel.getAudioID().setValue(new_id);

                String[] audioList = util.getAudioNames(getResources());
                int length = audioList.length-1;
                if(viewModel.getAudioID().getValue() == length) {
                    viewModel.getAudioID().setValue(0);
                } else {
                    viewModel.getAudioID().setValue(viewModel.getAudioID().getValue()+1);
                }

                // load next audio
                if (isBound && audioService != null) {
                    int newAudioID = viewModel.getAudioID().getValue();
                    String audioFilepath = util.getAudio(getResources(), newAudioID).getAudioPath();
                    float speed = viewModel.getAudioSpeed().getValue();

                    audioService.getAudioPlayer().stop();
                    audioService.getAudioPlayer().load(audioFilepath, speed);
                    audioService.getAudioPlayer().play();
                }
            }
        });
    }
    private void onClickBack() {
        buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }

    private void onClickBookmark() {
        buttonBM = findViewById(R.id.buttonBookmark);
        buttonBM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "[Activity] onClick: stopService Intent Launched");
                Log.d(TAG, "onClick: Changing the value of cancelPlay in the service");
                audioService.setCancelPlay(true);

                // stop audio player
                audioService.getAudioPlayer().stop();

                stopIntentService();

                // go back to main activity
                Intent intent = new Intent();
                intent.putExtra("audioID", viewModel.getAudioID().getValue());
                Log.d(TAG, "onClick: Audio progress " + String.valueOf(audioService.getAudioPlayer().getProgress()));
                Log.d(TAG, "onClick: Audio text progress" + audioProgress.getText());
                intent.putExtra("audioProgress", progressBookmark);

                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }


    // FOREGROUND SERVICING


    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "[Activity] onServiceConnected: Connection successful");
            AudioService.LocalBinder binder = (AudioService.LocalBinder) service;
            audioService = binder.getService();
            Log.d(TAG, "[Activity] onServiceConnected: isBind set to true");
            isBound = true;

            audioService.setCallback(progress -> {
                runOnUiThread(() -> {
                    audioProgress = findViewById(R.id.textViewProgress);
                    audioProgress.setText(util.getFormatProg(progress));
                    progressBookmark = progress;
                });
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

    @Override
    protected void onStart() {
        Log.d(TAG, "[Activity] onStart: Called");
        super.onStart();
        startIntentService();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "[Activity] onStop: Called");
        super.onStop();
        if(isBound) {
            unbindService(connection);
            isBound = false;
        }
    }

    private void startIntentService() {
        Intent intent = new Intent(PlayerActivity.this, AudioService.class);

        intent.putExtra("speed", viewModel.getAudioSpeed().getValue());
        String audioFilepath = util.getAudio(getResources(), viewModel.getAudioID().getValue()).getAudioPath();
        intent.putExtra("audioFilepath", audioFilepath);
        intent.putExtra("audioProgress", progressStart);

        startService(intent);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    private void stopIntentService() {
        Intent intent = new Intent(PlayerActivity.this, AudioService.class);
        stopService(intent);
        if(isBound) {
            unbindService(connection);
            isBound = false;
        }
    }
}
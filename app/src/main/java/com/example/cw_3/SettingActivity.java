package com.example.cw_3;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.cw_3.model.SettingViewModel;

public class SettingActivity extends AppCompatActivity {
    private static final String TAG = "COMP3018";
    private SettingViewModel viewModel;
    InputMethodManager imm;
    private EditText speedText;
    private  SeekBar speedBar;
    private TextView viewSave;
    private RadioGroup themeRadio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setting);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // initialise views
        speedText = findViewById(R.id.editTextNumberSpeed);
        speedBar = findViewById(R.id.seekBarSpeed);
        viewSave = findViewById(R.id.textViewSave);
        themeRadio = findViewById(R.id.radioGroupTheme);

        // initialise model for live data
        viewModel = new ViewModelProvider(this).get(SettingViewModel.class);
        // initialise livedata values
        Intent intent = getIntent();
        if (getIntent() != null) {
            initialiseValues(intent);
        } else {
            initialiseValues(new Intent());
        }

        // set livedata observer
        dataObservers(this);

        // initialise focus listener for speed change & update speed + seek bar
        imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        onUnfocusSpeed();
        // initialise change listener for speed seek bar & update speed + seek bar
        onSeekBarChange();
        // initialise change listener for theme radio group
        onRadioChange();
        // initialise click listener for save button
        onClickSave();
    }

    private void initialiseValues(Intent intent) {
        // speed
        String speedString;
        if (intent.hasExtra("speed")) {
            // get speed from intent & set as speed
            speedString = intent.getStringExtra("speed");
        } else {
            // set speed to default
            speedString = getResources().getString(R.string.speedDefault);
        }
        viewModel.setSpeed(Float.parseFloat(speedString));
        speedText.setText(speedString);

        // theme mode
        String themeString;
        if (intent.hasExtra("themeMode")) {
            // get background colour from intent & set as BGColour
            themeString = intent.getStringExtra("themeMode");
        } else {
            themeString = getResources().getString(R.string.themeModeDefault);
        }
        viewModel.getThemeMode().setValue(Integer.parseInt(themeString));
        // set theme colours
        int[] colours = util.getThemeColours(this, Integer.parseInt(themeString));
        viewModel.setColourTheme(colours[0], colours[1], colours[2]);
        ((RadioButton)themeRadio.getChildAt(Integer.parseInt(themeString))).setChecked(true);
    }

    private void dataObservers(AppCompatActivity activity) {
        // speed progress observer
        viewModel.getSpeedProg().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                speedBar.setProgress(integer);
            }
        });
        viewModel.getThemeMode().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                // get theme colours
                int[] colours = util.getThemeColours(activity, integer);
                // set colours to live data
                viewModel.setColourTheme(colours[0], colours[1], colours[2]);

                // change background colours
                findViewById(R.id.main).setBackgroundColor(colours[1]);
                ((TextView)findViewById(R.id.textTitleSetting)).setTextColor(colours[0]);
                viewSave.setBackgroundColor(colours[0]);
                viewSave.setBackgroundTintList(ColorStateList.valueOf(colours[0]));
                viewSave.setTextColor(colours[2]);
                findViewById(R.id.scrollSetting).setBackgroundColor(colours[0]);
                ((TextView)findViewById(R.id.textViewAudioTitle)).setTextColor(colours[2]);
                ((TextView)findViewById(R.id.textViewSpeed)).setTextColor(colours[2]);
                speedText.setTextColor(colours[2]);
                ((TextView)findViewById(R.id.textViewBGTitle)).setTextColor(colours[2]);
                ((RadioButton)findViewById(R.id.radioButton1)).setTextColor(colours[2]);
                ((RadioButton)findViewById(R.id.radioButton1)).setButtonTintList(ColorStateList.valueOf(colours[2]));
                ((RadioButton)findViewById(R.id.radioButton2)).setTextColor(colours[2]);
                ((RadioButton)findViewById(R.id.radioButton2)).setButtonTintList(ColorStateList.valueOf(colours[2]));
                ((RadioButton)findViewById(R.id.radioButton3)).setTextColor(colours[2]);
                ((RadioButton)findViewById(R.id.radioButton3)).setButtonTintList(ColorStateList.valueOf(colours[2]));
            }
        });
    }

    private void onUnfocusSpeed() {
        speedText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    // hide keyboard
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    // update speed to model, then update text edit again
                    viewModel.setSpeed(((EditText) v).getText().toString());
                    ((EditText) v).setText(String.valueOf(viewModel.getSpeed().getValue()));
                }
            }
        });
    }

    private void onSeekBarChange() {
        speedBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    viewModel.setSpeedProg(progress);
                    speedText.setText(String.valueOf(viewModel.getSpeed().getValue()));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void onRadioChange() {
        themeRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(group == themeRadio) {
                    int index = themeRadio.indexOfChild(themeRadio.findViewById(checkedId));
                    viewModel.getThemeMode().setValue(index);
                }
            }
        });
    }

    private void onClickSave() {
        viewSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // parse speed to ensure data is in range and correct format
                viewModel.setSpeed(speedText.getText().toString());

                // add data back to intent
                Intent intent = new Intent();
                intent.putExtra("speed", Float.toString(viewModel.getSpeed().getValue()));
                intent.putExtra("themeMode", Integer.toString(viewModel.getThemeMode().getValue()));

                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }


}
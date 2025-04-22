package com.example.cw_3.model;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

public class SettingViewModel extends ViewModel {
    private final static float speedMin = 0.25f, speedMax = 4;
    private static final DecimalFormat df = new DecimalFormat("0.00");
    private final MutableLiveData<Float> speed = new MutableLiveData<>();
    private final MutableLiveData<Integer> speedProg = new MutableLiveData<>();
    private final MutableLiveData<Integer> themeMode = new MutableLiveData<>();
    private MutableLiveData<ArrayList<Integer>> colourTheme = new MutableLiveData<>();


    public MutableLiveData<Float> getSpeed() {
        return speed;
    }

    public MutableLiveData<Integer> getSpeedProg() {
        return speedProg;
    }

    public void setSpeed(float speed){
        // set speed in range
        if(speed < speedMin) {
            speed = speedMin;
        } else if(speed > speedMax) {
            speed = speedMax;
        }

        // change speed to two d.p.
        String speedString = df.format(speed);

        // apply changes to speed and progress
        this.speed.setValue(Float.parseFloat(speedString));
        applySpeedToProg();
    }

    public void setSpeed(String speed) {
        try {
            // set speed if string can be changed to float
            setSpeed(Float.parseFloat(speed));
        } catch (Exception e) {
            Log.e("COMP3018", "Unparsable speed value entered:\n" + e);
        }
    }

    public void setSpeedProg(int speedProg) {
        this.speedProg.setValue(speedProg);
        applyProgToSpeed();
    }

    private void applySpeedToProg() {
        speedProg.setValue((int)((Math.log(speed.getValue()) / Math.log(2))*200));
    }

    private void applyProgToSpeed() {
        String speedString = df.format((float) Math.pow(2, (double) speedProg.getValue() / 200));
        speed.setValue(Float.parseFloat(speedString));
    }

    public MutableLiveData<Integer> getThemeMode() {
        return themeMode;
    }

    public void setColourTheme(int main, int accent, int text) {
        this.colourTheme.setValue(new ArrayList<>(Arrays.asList(main, accent, text)));
    }
}

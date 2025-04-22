package com.example.cw_3.model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class PlayerViewModel extends ViewModel {
    private final MutableLiveData<Integer> audioID = new MutableLiveData<>();
    private final MutableLiveData<String> audioName = new MutableLiveData<>();
    private final MutableLiveData<Float> audioSpeed = new MutableLiveData<>();
//    private final MutableLiveData<Integer> audioProg = new MutableLiveData<>();
    private final MutableLiveData<Integer> themeMode = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<Integer>> colourTheme = new MutableLiveData<>();

    public MutableLiveData<Integer> getAudioID() {
        return audioID;
    }

    public MutableLiveData<String> getAudioName() {
        return audioName;
    }

    public MutableLiveData<Float> getAudioSpeed() {
        return audioSpeed;
    }

//    public MutableLiveData<Integer> getAudioProg() {
//        return audioProg;
//    }

    public MutableLiveData<Integer> getThemeMode() {
        return themeMode;
    }

    public MutableLiveData<ArrayList<Integer>> getColourTheme() {
        return colourTheme;
    }
}

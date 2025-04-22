package com.example.cw_3.model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cw_3.object.Audio;

import java.util.ArrayList;
import java.util.Arrays;

public class MainViewModel extends ViewModel {
    private final MutableLiveData<Float> speed = new MutableLiveData<>();
    private final MutableLiveData<Integer> themeMode = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<Audio>> audioList = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<Audio>> bookmarkList = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<Integer>> colourTheme = new MutableLiveData<>();

    public MutableLiveData<Float> getSpeed() {
        return speed;
    }

    public MutableLiveData<Integer> getThemeMode() {
        return themeMode;
    }

    public MutableLiveData<ArrayList<Audio>> getAudioList() {
        return audioList;
    }

    public MutableLiveData<ArrayList<Audio>> getBookmarkList() {
        if(!bookmarkList.isInitialized()) {
            bookmarkList.setValue(new ArrayList<Audio>());
        }
        return bookmarkList;
    }

    public void addBookmarkList(Audio audio) {
        if(!bookmarkList.isInitialized()) {
            getBookmarkList();
        }
        bookmarkList.getValue().add(audio);
    }

    public MutableLiveData<ArrayList<Integer>> getColourTheme() {
        return colourTheme;
    }

    public int getColourTheme(int type) {
        if(colourTheme.isInitialized()) {
            return colourTheme.getValue().get(type);
        }
        return -1;
    }

    public void setColourTheme(int main, int accent, int text) {
        this.colourTheme.setValue(new ArrayList<>(Arrays.asList(main, accent, text)));
    }
}

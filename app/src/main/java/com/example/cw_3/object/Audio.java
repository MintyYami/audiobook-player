package com.example.cw_3.object;

public class Audio {
    private String audioName;
    private String audioPath;
    private int audioProgress;

    public Audio(String audioName, String folderPath) {
        this.audioName = audioName;
        this.audioPath = folderPath + String.format("/%s.mp3", audioName);
        this.audioProgress = 0;
    }

    public Audio(String audioName, String folderPath, int audioProgress) {
        this.audioName = audioName;
        this.audioPath = folderPath + String.format("/%s.mp3", audioName);
        this.audioProgress = audioProgress;
    }

    public String getAudioName() {
        return audioName;
    }

    public String getAudioPath() {
        return audioPath;
    }

    public int getAudioProgress() {
        return audioProgress;
    }
}

package com.example.cw_3;

import android.content.res.Resources;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cw_3.object.Audio;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class util {
    public static String[] getAudioNames(Resources resource){
        String fileType = resource.getString(R.string.audioType);
        File[] files = (new File(resource.getString(R.string.audioFolderPath))).listFiles();

        ArrayList<String> arrayFiles = new ArrayList<>();
        if (files == null)
            return null;
        else {
            for(File file : files) {
                if(file.getName().endsWith(fileType)) {
                    arrayFiles.add(file.getName().replace(fileType, ""));
                }
            }
        }
        return arrayFiles.toArray(new String[0]);
    }

    public static String getAudioName(Resources resources, int audioID) {
        String[] list = getAudioNames(resources);
        if(audioID < list.length) {
            return list[audioID];
        }
        return "<UNKNOWN>";
    }

    public static int[] getThemeColours(AppCompatActivity activity, int themeMode) {
        int[] colours;
        // get colours
        switch (themeMode) {
            case 1:
                colours = new int[] {
                        activity.getColor(R.color.main1),
                        activity.getColor(R.color.accent1),
                        activity.getColor(R.color.text1)
                };
                break;
            case 2:
                colours = new int[] {
                        activity.getColor(R.color.main2),
                        activity.getColor(R.color.accent2),
                        activity.getColor(R.color.text2)
                };
                break;
            default: // for index == 0
                colours = new int[] {
                        activity.getColor(R.color.main0),
                        activity.getColor(R.color.accent0),
                        activity.getColor(R.color.text0)
                };
                break;
        }
        return colours;
    }

    public static ArrayList<Audio> getAudioList(Resources resource) {
        // instantiate audio choices
        ArrayList<Audio> audioList = new ArrayList<>();
        String[] audioNames = getAudioNames(resource);

        for(String audioName : audioNames) {
            Audio audio = new Audio(audioName, resource.getString(R.string.audioFolderPath));
            audioList.add(audio);
        }

        return audioList;
    }

    public static String getAudioFilepath(Resources resources, int pos) {
        return getAudioList(resources).get(pos).getAudioPath();
    }

    public static Audio getAudio(Resources resource, int pos) {
        return getAudioList(resource).get(pos);
    }

    public static int getAudioID(String name, Resources resource) {
        String[] audioNames = getAudioNames(resource);

        return Arrays.asList(audioNames).indexOf(name);
    }

    public static String getFormatProg(int progress) {
        int totalSec = progress/1000;
        int s = totalSec % 60;
        int m = (totalSec / 60) % 60;
        int h = totalSec / 3600;

        return String.format("Progress: %02d:%02d:%02d", h, m, s);
    }
}

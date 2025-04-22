package com.example.cw_3;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cw_3.adaptor.AudioRecyclerViewAdaptor;
import com.example.cw_3.adaptor.BookmarkRecylerViewAdaptor;
import com.example.cw_3.model.MainViewModel;
import com.example.cw_3.object.Audio;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements AudioRecyclerViewAdaptor.onAudioListener, BookmarkRecylerViewAdaptor.onBookmarkListener {
    private static final String TAG = "COMP3018";
    private static final int MY_PERMISSIONS_REQUEST_READ_MEDIA_AUDIO = 1;
    private static final int MY_PERMISSIONS_REQUEST_NOTIFICATION = 2;
    private MainViewModel viewModel;
    private ImageView viewSetting;
    private RecyclerView recyclerAudio, recyclerBookmark;
    private ActivityResultLauncher<Intent> resultLauncher1, resultLauncher2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // get views
        viewSetting = findViewById(R.id.imageViewMenu);
        recyclerAudio = findViewById(R.id.recyclerAudio);
        recyclerBookmark = findViewById(R.id.recyclerBookmark);

        // initialise model for live data
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        // initialise livedata values
        initialiseValues();

        // set livedata observer
        dataObservers(this);

        // check permission and initialise recycler view with audios
        checkAndRequestPermission();

        // initialise launch intent for player
        audioPlayerIntents();

        // initialise click listener for setting button
        onClickSetting();
    }

    private void initialiseValues() {
        // initialise default values if no current
        if(!viewModel.getSpeed().isInitialized()) {
            float speed = Float.parseFloat(getResources().getString(R.string.speedDefault));
            viewModel.getSpeed().setValue(speed);
        }
        if(!viewModel.getThemeMode().isInitialized()) {
            // set theme mode value
            int themeMode = Integer.parseInt(getResources().getString(R.string.themeModeDefault));
            viewModel.getThemeMode().setValue(themeMode);
            // set theme colours
            int[] colours = util.getThemeColours(this, themeMode);
            viewModel.setColourTheme(colours[0], colours[1], colours[2]);
        }
    }

    private void dataObservers(AppCompatActivity activity) {
        viewModel.getThemeMode().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                // get theme colours
                int[] colours = util.getThemeColours(activity, integer);
                // set colours to live data
                viewModel.setColourTheme(colours[0], colours[1], colours[2]);

                // change theme
                View main = findViewById(R.id.main);
                main.setBackgroundColor(viewModel.getColourTheme(1));
                findViewById(R.id.layoutMain).setBackgroundColor(viewModel.getColourTheme(0));
                ((TextView)findViewById(R.id.textTitleMain)).setTextColor(viewModel.getColourTheme(0));
                viewSetting.setBackgroundColor(viewModel.getColourTheme(0));
                viewSetting.setBackgroundTintList(ColorStateList.valueOf(viewModel.getColourTheme(0)));
                ((TextView)findViewById(R.id.textTitleAudio)).setTextColor(viewModel.getColourTheme(2));
                ((TextView)findViewById(R.id.textTitleBookmark)).setTextColor(viewModel.getColourTheme(2));
            }
        });
    }

    private void checkAndRequestPermission() {
        // permission for media read
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_MEDIA_AUDIO)) {
                new AlertDialog.Builder(this)
                        .setTitle("Permission needed")
                        .setMessage("This permission is needed to access the music files on your device.")
                        .setPositiveButton("OK", (dialog, which) -> ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{android.Manifest.permission.READ_MEDIA_AUDIO},
                                MY_PERMISSIONS_REQUEST_READ_MEDIA_AUDIO))
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_AUDIO},
                        MY_PERMISSIONS_REQUEST_READ_MEDIA_AUDIO);
            }
        }
        // permission for notification
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.POST_NOTIFICATIONS)) {
                new AlertDialog.Builder(this)
                        .setTitle("Permission needed")
                        .setMessage("This permission is needed to access the notification, in order to show when foreground service is running!")
                        .setPositiveButton("OK", (dialog, which) -> ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                                MY_PERMISSIONS_REQUEST_NOTIFICATION))
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        MY_PERMISSIONS_REQUEST_NOTIFICATION);
            }
        }else {
            // Permission already granted, load the audiobook
            loadAudiobook();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_MEDIA_AUDIO:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted, load the audiobook
                    loadAudiobook();
                } else {
                    Toast.makeText(this, "Permission for music files DENIED", Toast.LENGTH_SHORT).show(); // Permission denied, show a toast
                }
                break;
            case MY_PERMISSIONS_REQUEST_NOTIFICATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Notification permission DENIED", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    };


    private void loadAudiobook() {
        // retrieve a reference to audio recycler from main layout
        recyclerAudio = findViewById(R.id.recyclerAudio);
        recyclerAudio.setLayoutManager(new LinearLayoutManager(this));
        AudioRecyclerViewAdaptor adapter1 = new AudioRecyclerViewAdaptor(this, util.getAudioList(getResources()), viewModel.getColourTheme(2), this);
        recyclerAudio.setAdapter(adapter1);
        // retrieve a reference to bookmark recycler from main layout
        recyclerBookmark = findViewById(R.id.recyclerBookmark);
        recyclerBookmark.setLayoutManager(new LinearLayoutManager(this));
        BookmarkRecylerViewAdaptor adaptor2 = new BookmarkRecylerViewAdaptor(this, viewModel.getBookmarkList().getValue(), viewModel.getColourTheme(2), this);
        recyclerBookmark.setAdapter(adaptor2);
    }

    private void onClickSetting() {
        viewSetting = findViewById(R.id.imageViewMenu);
        viewSetting.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
            intent.putExtra("speed", String.valueOf(viewModel.getSpeed().getValue()));
            intent.putExtra("themeMode", String.valueOf(viewModel.getThemeMode().getValue()));
            resultLauncher1.launch(intent);
        });
    }

    @Override
    public void onAudioClick(int pos) {
        Intent intent = new Intent(MainActivity.this, PlayerActivity.class);

        intent.putExtra("speed", String.valueOf(viewModel.getSpeed().getValue()));
        intent.putExtra("themeMode", String.valueOf(viewModel.getThemeMode().getValue()));
        intent.putExtra("audioID", String.valueOf(pos));
        intent.putExtra("audioPath", util.getAudioFilepath(getResources(), pos));

        resultLauncher2.launch(intent);
    }

    @Override
    public void onBookmarkClick(int pos, String name, String filepath) {
        Intent intent = new Intent(MainActivity.this, PlayerActivity.class);

        intent.putExtra("speed", String.valueOf(viewModel.getSpeed().getValue()));
        intent.putExtra("themeMode", String.valueOf(viewModel.getThemeMode().getValue()));
        intent.putExtra("audioID", String.valueOf(Arrays.asList(util.getAudioNames(getResources())).indexOf(name)));
        intent.putExtra("audioPath", filepath);
        intent.putExtra("progress", viewModel.getBookmarkList().getValue().get(pos).getAudioProgress());

        resultLauncher2.launch(intent);
    }
//    @Override
//    public void onBookmarkClick(int pos) {
//        Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
//
//        intent.putExtra("speed", String.valueOf(viewModel.getSpeed().getValue()));
//        intent.putExtra("themeMode", String.valueOf(viewModel.getThemeMode().getValue()));
//        intent.putExtra("audioID", String.valueOf(pos));
//        intent.putExtra("progress", String.valueOf(0));
//
//        resultLauncher2.launch(intent);
//    }

    // handling intent returns
    private void audioPlayerIntents() {
        resultLauncher1 = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == RESULT_OK && result.getData() != null) {
                    // store new values
                    if(result.getData().hasExtra("speed")) {
                        float speed = Float.parseFloat(result.getData().getStringExtra("speed"));
                        viewModel.getSpeed().setValue(speed);
                    }
                    if(result.getData().hasExtra("themeMode")) {
                        // set new values
                        int themeMode = Integer.parseInt(result.getData().getStringExtra("themeMode"));
                        viewModel.getThemeMode().setValue(themeMode);
                        int[] colours = util.getThemeColours(this, themeMode);
                        viewModel.setColourTheme(colours[0], colours[1],  colours[2]);

                        AudioRecyclerViewAdaptor adapter1 = new AudioRecyclerViewAdaptor(this, util.getAudioList(getResources()), viewModel.getColourTheme(2), this);
                        recyclerAudio.setAdapter(adapter1);
                        BookmarkRecylerViewAdaptor adaptor2 = new BookmarkRecylerViewAdaptor(this, viewModel.getBookmarkList().getValue(), viewModel.getColourTheme(2), this);
                        recyclerBookmark.setAdapter(adaptor2);
                    }

                    // Toast: changes saved
                    Toast.makeText(this, "Changes saved!", Toast.LENGTH_SHORT).show();
                } else if(result.getResultCode() == RESULT_CANCELED) {
                    // Toast: changes not saved
                    Toast.makeText(this, "Changes discarded...", Toast.LENGTH_SHORT).show();
                }
            }
        );
        resultLauncher2 = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == RESULT_OK && result.getData() != null) {
                    // store any new bookmark
                    Intent data = result.getData();
                    int bookmarkID = data.getIntExtra("audioID", -1);
                    int bookmarkProg = data.getIntExtra("audioProgress", -1);

                    if((bookmarkID != -1) && (bookmarkProg != -1)) {
                        String audioName = util.getAudioName(getResources(), bookmarkID);
                        viewModel.addBookmarkList(new Audio(audioName,
                                getResources().getString(R.string.audioFolderPath),
                                bookmarkProg)
                        );
                        Log.d(TAG, String.format("[MainActivity] audioPlayerIntents: Add %s at %d", audioName, bookmarkProg));

                        // re-retrieve bookmark for main
                        BookmarkRecylerViewAdaptor adaptor2 = new BookmarkRecylerViewAdaptor(this, viewModel.getBookmarkList().getValue(), viewModel.getColourTheme(2), this);
                        recyclerBookmark.setAdapter(adaptor2);

                        // toast to notify bookmark is added
                        Toast.makeText(this, "Bookmark added!", Toast.LENGTH_SHORT).show();
                    }

                } else if(result.getResultCode() == RESULT_CANCELED) {

                }
            }
        );
    }
}
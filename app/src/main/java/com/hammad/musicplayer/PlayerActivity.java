package com.hammad.musicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.internal.NavigationMenuItemView;

import java.io.File;
import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {

    Button btnPause,btnNext,btnPrevious;
    TextView songTextLabel;
    SeekBar songSeekBar;
    String sname;
    static MediaPlayer mediaPlayer;
    int position;

    ArrayList<File> mySongs;
    Thread updateSeekBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        btnPause = findViewById(R.id.btnPause);
        btnPrevious = findViewById(R.id.btnPrevious);
        btnNext = findViewById(R.id.btnNext);
        songTextLabel = findViewById(R.id.songLabel);

        songSeekBar = findViewById(R.id.seekBar);

        getSupportActionBar().setTitle("Now Playing");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        updateSeekBar = new Thread(){
            @Override
            public void run(){
                int totalDuration =  mediaPlayer.getDuration();
                int currentPosition = 0;

                while (currentPosition < totalDuration){
                    try {
                        sleep(500);
                        currentPosition = mediaPlayer.getCurrentPosition();
                        songSeekBar.setProgress(currentPosition);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        if (mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        Intent i = getIntent();
        Bundle bundle = i.getExtras();

        mySongs = (ArrayList) bundle.getParcelableArrayList("songs");

        sname = mySongs.get(position).getName().toString();

        String songName = i.getStringExtra("songname");

        songTextLabel.setText(songName);
        songTextLabel.setSelected(true);

        position = bundle.getInt("position",0);

        final Uri u = Uri.parse(mySongs.get(position).toString());

        mediaPlayer = MediaPlayer.create(getApplicationContext(),u);

        mediaPlayer.start();
        songSeekBar.setMax(mediaPlayer.getDuration());

        updateSeekBar.start();

        songSeekBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorButton),PorterDuff.Mode.MULTIPLY);
        songSeekBar.getThumb().setColorFilter(getResources().getColor(R.color.colorButton),PorterDuff.Mode.SRC_IN);

        songSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                mediaPlayer.seekTo(seekBar.getProgress());

            }
        });

        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                songSeekBar.setMax(mediaPlayer.getDuration());

                if (mediaPlayer.isPlaying()){

                    btnPause.setBackgroundResource(R.drawable.icon_play);
                    mediaPlayer.pause();

                }
                else {
                    btnPause.setBackgroundResource(R.drawable.icon_pause);
                    mediaPlayer.start();
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position = (position+1) % mySongs.size();

                Uri u = Uri.parse(mySongs.get(position).toString());

                mediaPlayer = MediaPlayer.create(getApplicationContext(),u);

                sname = mySongs.get(position).getName().toString();
                songTextLabel.setText(sname);

                mediaPlayer.start();
            }
        });

        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();

                position = (position-1)<0 ?(mySongs.size()-1):(position-1);
                Uri.parse(mySongs.get(position).toString());

                mediaPlayer = MediaPlayer.create(getApplicationContext(), u);

                sname = mySongs.get(position).getName().toString();
                songTextLabel.setText(sname);

                mediaPlayer.start();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}
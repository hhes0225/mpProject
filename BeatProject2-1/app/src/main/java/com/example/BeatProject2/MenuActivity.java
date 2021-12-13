package com.example.BeatProject2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


public class MenuActivity extends AppCompatActivity implements JNIListener {
    public static final int REQUEST_CODE_INTRO = 101;

    JNIDriver mDriver;
    //ReceiveThread mSegThread
    boolean mThreadRun = false;

    MediaPlayer previewMusic;
    boolean musicPlaying=false;

    // Used to load the 'mybeat' library on application startup.
    static {
        System.loadLibrary("BeatProject2");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        mThreadRun=true;
        //easy 버튼 누르면 "easymode" MainActivity에 전달, 전환
        Button easyBtn = findViewById(R.id.easy);
        easyBtn.setBackgroundColor(Color.parseColor("#FFFFFF"));
        easyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(musicPlaying==true){
                    previewMusic.stop();
                }
                Intent easyIntent = new Intent(MenuActivity.this, MainActivity.class);
                easyIntent.putExtra("name", "easymode");
                mDriver.close();
                mThreadRun=false;
                startActivity(easyIntent);
            }
        });

        //hard 버튼 누르면 "hardmode" MainActivity에 전달, 전환
        Button hardBtn = findViewById(R.id.hard);
        hardBtn.setBackgroundColor(Color.parseColor("#FFFFFF"));
        hardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(musicPlaying==true){
                    previewMusic.stop();
                }
                Intent hardIntent = new Intent(MenuActivity.this, MainActivity.class);
                hardIntent.putExtra("name", "hardmode");
                mDriver.close();
                mThreadRun=false;
                startActivity(hardIntent);
            }
        });

        //GPIO 버튼
        mDriver=new JNIDriver();
        mDriver.setListener(this);

        if(mDriver.open("/dev/sm9s5422_interrupt")<0){
            Toast.makeText(MenuActivity.this, "Driver Open Failed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause(){
        //TODO Auto-generated method stub
        mDriver.close();
        super.onPause();
        mThreadRun=false;
    }

    //GPIO 버튼 인터럽트 핸들러
    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler(new Handler.Callback(){
        @Override
        public boolean handleMessage(@NonNull Message msg){return false;}
    }){
        public void handleMessage(Message msg){
            //Center 버튼을 눌러도 MenuActivity 전환 가능하다
            switch(msg.arg1){
                case 1://up, easy mode music preview
                    if(musicPlaying==true){
                        previewMusic.stop();
                        musicPlaying=false;
                    }
                    if(musicPlaying==false) {
                        previewMusic = MediaPlayer.create(MenuActivity.this, R.raw.candy);
                        previewMusic.start();
                        musicPlaying=true;
                    }
                    break;
                case 2://down, hard mode music preview
                    if(musicPlaying==true){
                        previewMusic.stop();
                        musicPlaying=false;
                    }
                    if(musicPlaying==false) {
                        previewMusic = MediaPlayer.create(MenuActivity.this, R.raw.beethovenvirus);
                        previewMusic.start();
                        musicPlaying=true;
                    }
                    break;
                case 3://left, easy mode play
                    if(musicPlaying==true){
                        previewMusic.stop();
                    }
                    Button easyBtn = findViewById(R.id.easy);
                    easyBtn.setBackgroundColor(Color.parseColor("#B99E97"));
                    Intent easyIntent = new Intent(MenuActivity.this, MainActivity.class);
                    easyIntent.putExtra("name", "easymode");
                    mDriver.close();
                    startActivity(easyIntent);
                    break;
                case 4://right, hard mode play
                    if(musicPlaying==true){
                        previewMusic.stop();
                    }
                    Button hardBtn = findViewById(R.id.hard);
                    hardBtn.setBackgroundColor(Color.parseColor("#B99E97"));
                    Intent hardIntent = new Intent(MenuActivity.this, MainActivity.class);
                    hardIntent.putExtra("name", "hardmode");
                    mDriver.close();
                    startActivity(hardIntent);
                    break;
                default:

                    break;
            }
        }
    };

    @Override
    protected void onResume(){
        //TODO Auto-generated method stub
        Button easyBtn = findViewById(R.id.easy);
        easyBtn.setBackgroundColor(Color.parseColor("#FFFFFF"));
        Button hardBtn = findViewById(R.id.hard);
        hardBtn.setBackgroundColor(Color.parseColor("#FFFFFF"));
        //GPIO 버튼 재연결
        if(mThreadRun==false){
            mDriver=new JNIDriver();
            mDriver.setListener(this);

            if(mDriver.open("/dev/sm9s5422_interrupt")<0){
                Toast.makeText(MenuActivity.this, "Driver Open Failed", Toast.LENGTH_SHORT).show();
            }
        }
        mThreadRun=true;
        super.onResume();
    }

    @Override
    public void onReceive(int val){
        //TODO Auto-generated method stub
        Log.e("test", "onReceive start");
        Message text = Message.obtain();
        //Log.e("test", "1501");
        text.arg1=val;
        System.out.println(val);
        //Log.e("test", "1502");
        handler.sendMessage(text);
        Log.e("test", "onReceive end");

    }

}
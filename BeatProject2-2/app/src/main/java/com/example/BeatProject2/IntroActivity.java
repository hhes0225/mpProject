package com.example.BeatProject2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;


public class IntroActivity extends AppCompatActivity implements JNIListener{
    //MediaPlayer introBgm;
    ImageView imgV;
    Bitmap buf_bitmap;

    JNIDriver mDriver;
    //ReceiveThread mSegThread
    boolean mThreadRun = true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);


        Button button = findViewById(R.id.gomenu);
        LinearLayoutCompat back = findViewById(R.id.back);
        //introBgm= MediaPlayer.create(this, R.raw.sugar);
        //introBgm.start();

        back.setBackground(ContextCompat.getDrawable(this, R.drawable.back));
        button.setBackgroundColor(Color.parseColor("#B99E97"));

        //MenuActivity로 가기 위한 버튼 리스너
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //introBgm.stop();
                button.setBackgroundColor(Color.parseColor("#FFFFFF"));
                Intent intent = new Intent(IntroActivity.this, MenuActivity.class);
                startActivity(intent);
            }
        });

        //GPIO 버튼
        mDriver=new JNIDriver();
        mDriver.setListener(this);

        if(mDriver.open("/dev/sm9s5422_interrupt")<0){
            Toast.makeText(IntroActivity.this, "Driver Open Failed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause(){
        //TODO Auto-generated method stub
        mDriver.close();
        super.onPause();
        mThreadRun=false;
    }

    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler(new Handler.Callback(){
        @Override
        public boolean handleMessage(@NonNull Message msg){return false;}
    }){
        public void handleMessage(Message msg){
            Button button = findViewById(R.id.gomenu);
            //Center 버튼을 눌러도 MenuActivity 전환 가능하다
            if(msg.arg1 == 5){
                button.setBackgroundColor(Color.parseColor("#FFFFFF"));
                Intent intent = new Intent(IntroActivity.this, MenuActivity.class);
                startActivity(intent);
            }
        }
    };

    @Override
    protected void onResume(){
        //TODO Auto-generated method stub
        Button button = findViewById(R.id.gomenu);
        button.setBackgroundColor(Color.parseColor("#B99E97"));

        //GPIO 버튼 재연결
        if(mThreadRun==false){
            mDriver=new JNIDriver();
            mDriver.setListener(this);

            if(mDriver.open("/dev/sm9s5422_interrupt")<0){
                Toast.makeText(IntroActivity.this, "Driver Open Failed", Toast.LENGTH_SHORT).show();
            }
        }
        super.onResume();
    }

    @Override
    public void onReceive(int val){
        //TODO Auto-generated method stub
        Log.e("test", "1500");
        Message text = Message.obtain();
        Log.e("test", "1501");
        text.arg1=val;
        System.out.println(val);
        Log.e("test", "1502");
        handler.sendMessage(text);
        Log.e("test", "1503");
    }
}
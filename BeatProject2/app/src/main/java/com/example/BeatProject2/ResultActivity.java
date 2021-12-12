package com.example.BeatProject2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;


public class ResultActivity extends AppCompatActivity implements JNIListener{
    JNIDriver mDriver;

    //JNI function
    static {
        System.loadLibrary("7segDriver");
    }
    private native static int openDriverSeg(String path);
    private native static void closeDriverSeg();
    private native static void writeDriverSeg(byte[] data, int length);

    //thread, variables
    int data_int, i;
    boolean mThreadRun, mStart;
    SegmentThread mSegThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Button gohome=findViewById(R.id.gohome);
        gohome.setBackgroundColor(Color.parseColor("#B99E97"));

        LinearLayoutCompat back = findViewById(R.id.resultback);
        back.setBackground(ContextCompat.getDrawable(this, R.drawable.back));

        //MainActivity의 score을 보여주기
        TextView resultScore=findViewById(R.id.resultScore);
        Intent receive_intent = getIntent();
        String rcvScore = receive_intent.getStringExtra("name");

        resultScore.setText(rcvScore);

        //button onClickListener
        gohome.setOnClickListener(new View.OnClickListener() {//left
            @Override
            public void onClick(View v) {
                //introBgm.stop();
                gohome.setBackgroundColor(Color.parseColor("#FFFFFF"));
                Intent intent = new Intent(ResultActivity.this, MenuActivity.class);
                startActivity(intent);
            }
        });

        //GPIO 버튼
        mDriver=new JNIDriver();
        mDriver.setListener(this);

        if(mDriver.open("/dev/sm9s5422_interrupt")<0){
            Toast.makeText(ResultActivity.this, "Driver Open Failed", Toast.LENGTH_SHORT).show();
        }

        //7-seg
        try {
            data_int = Integer.parseInt(rcvScore);
            mStart = true;
        }catch(NumberFormatException E){
            Toast.makeText(ResultActivity.this, "Input Error", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler(new Handler.Callback(){
        @Override
        public boolean handleMessage(@NonNull Message msg){return false;}
    }){
        public void handleMessage(Message msg){
            Button gohome = findViewById(R.id.gohome);
            //Center 버튼을 눌러도 MenuActivity 전환 가능하다
            if(msg.arg1 == 5){
                gohome.setBackgroundColor(Color.parseColor("#FFFFFF"));
                Intent intent = new Intent(ResultActivity.this, MenuActivity.class);
                closeDriverSeg();
                mDriver.close();
                startActivity(intent);
            }
        }
    };

    private class SegmentThread extends Thread{
        @Override
        public void run(){
            super.run();
            while(mThreadRun){
                byte[] n = {0,0,0,0,0,0,0};

                if(mStart==false){writeDriverSeg(n, n.length);}
                else{
                    for(i = 0; i<100;i++){
                        n[0]=(byte) (data_int % 1000000 / 100000);
                        n[1]=(byte) (data_int % 100000 / 10000);
                        n[2]=(byte) (data_int % 10000 / 1000);
                        n[3]=(byte) (data_int % 1000 / 100);
                        n[4]=(byte) (data_int % 100 / 10);
                        n[5]=(byte) (data_int % 10);
                        writeDriverSeg(n, n.length);
                    }
                   // if(data_int>0)
                        //data_int--;
                }
            }
        }
    }

    @Override
    protected void onPause(){
        //TODO Auto-generated method stub
        byte[] closeData={0,0,0,0,0,0,0,0};
        writeDriverSeg(closeData, closeData.length);
        closeDriverSeg();
        mDriver.close();
        mThreadRun=false;
        mSegThread=null;
        super.onPause();
    }

    @Override
    protected void onResume(){
        //TODO Auto-generated method stub
        //GPIO 재연결
        if(mThreadRun==false){
            mDriver=new JNIDriver();
            mDriver.setListener(this);
            Log.e("test", "GPIO open");
            if(mDriver.open("/dev/sm9s5422_interrupt")<0){
                Toast.makeText(ResultActivity.this, "Driver Open Failed", Toast.LENGTH_SHORT).show();
            }
        }

        //7seg 재연결
        if(openDriverSeg("/dev/sm9s5422_segment")<0){
            Toast.makeText(ResultActivity.this, "Driver Open Failed", Toast.LENGTH_SHORT).show();
        }
        mThreadRun=true;
        mSegThread=new SegmentThread();
        mSegThread.start();

        super.onResume();
    }

    @Override
    public void onReceive(int val){
        //TODO Auto-generated method stub
        //Log.e("test", "1500");
        Message text = Message.obtain();
        //Log.e("test", "1501");
        text.arg1=val;
        System.out.println(val);
        //Log.e("test", "1502");
        handler.sendMessage(text);
        //Log.e("test", "1503");

    }
}

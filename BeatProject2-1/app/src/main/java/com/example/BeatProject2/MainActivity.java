package com.example.BeatProject2;

import static android.net.wifi.p2p.WifiP2pDevice.FAILED;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.BeatProject2.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements JNIListener{
    public static final int REQUEST_CODE_INTRO = 101;

    JNIDriver mDriver;
    //ReceiveThread mSegThread

    MediaPlayer gameMusic;
    boolean musicPlaying = false;

    private native static int openDriver(String path);
    private native static void closeDriver();
    private native static void writeDriver(byte[] data, int length);

    boolean mThreadRun = false;
    Button btn1, btn2, btn3, btn4, btn5;
    ImageView note1, note2, note3, note4, note5;
    LinearLayoutCompat back;
    byte[]data={1,1,1,1,1,1,1,1};
    int chance = 8;
    int resultScore=12345;
    ArrayList<Note> noteList=new ArrayList<Note>();

    //game
    public static final int noteSpeed=7;
    public static final int sleepTime=10;


    // Used to load the 'mybeat' library on application startup.
    static {
        System.loadLibrary("JNILedDriver");
        System.loadLibrary("7segDriver");
    }

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        mThreadRun=true;
        if(musicPlaying==true){
            gameMusic.stop();
            musicPlaying=false;
        }

        TextView tv = findViewById(R.id.tmpText);
        TextView score=findViewById(R.id.score);
        back = findViewById(R.id.back);
        btn1=findViewById(R.id.left);
        btn2=findViewById(R.id.up);
        btn3=findViewById(R.id.center);
        btn4=findViewById(R.id.down);
        btn5=findViewById(R.id.right);

        note1=findViewById(R.id.note1);
        note2=findViewById(R.id.note2);
        note3=findViewById(R.id.note3);
        note4=findViewById(R.id.note4);
        note5=findViewById(R.id.note5);

        //GPIO 버튼
        mDriver=new JNIDriver();
        mDriver.setListener(this);

        if(mDriver.open("/dev/sm9s5422_interrupt")<0){
            Toast.makeText(MainActivity.this, "Driver Open Failed", Toast.LENGTH_SHORT).show();
        }


        //textview 색상 흰색으로 변경
        tv.setTextColor(Color.WHITE);
        score.setTextColor(Color.WHITE);

        Intent receive_intent = getIntent();
        String temp = receive_intent.getStringExtra("name");

        //이전 액티비티에서 easy/hard 선택에 따라 달라짐
        if(temp.equals("easymode")){
            //tv.setText(tv.getText()+temp+" score : ");
            back.setBackground(ContextCompat.getDrawable(this, R.drawable.sugar));
            if(musicPlaying==false) {
                gameMusic = MediaPlayer.create(MainActivity.this, R.raw.candy);
                gameMusic.start();
                musicPlaying=true;
            }
        }
        else if(temp.equals("hardmode")){
            //tv.setText(tv.getText()+temp+" score : ");
            back.setBackground(ContextCompat.getDrawable(this, R.drawable.beethoven));
            if(musicPlaying==false) {
                gameMusic = MediaPlayer.create(MainActivity.this, R.raw.beethovenvirus);
                gameMusic.start();
                musicPlaying=true;
            }
        }
        else{
            tv.setText("error");
        }
        //음악이 끝나면 2초 대기 후 resultActivity로 전환됨
        gameMusic.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){

            @Override
            public void onCompletion(MediaPlayer mp) {
                // TODO Auto-generated method stub
                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if(chance>0){
                            Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                            intent.putExtra("name", Integer.toString(resultScore));
                            startActivity(intent);
                        }
                        mDriver.close();
                    }
                }, 2000);// 2초 정도 딜레이를 준 후 시작
            }

        });


        //보드 디스플레이 사이즈 구함
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics ();
        display.getMetrics(outMetrics);

        float density = getResources().getDisplayMetrics().density;
        float dpHeight = outMetrics.heightPixels / density;
        float dpWidth = outMetrics.widthPixels / density;

        //dp별 layout 별도 적용
        Log.d("Device dp","dpHeight : : "+dpHeight+"  dpWidth : "+dpWidth+"  density : "+density);




        //LED
        if(openDriver("/dev/sm9s5422_led")<0){
            Toast.makeText(MainActivity.this, "Driver Open Failed", Toast.LENGTH_SHORT).show();
        }
        Log.e("test", "led write start");
        writeDriver(data, data.length);
        Log.e("test", "led write end");


        //button onClickListener
        btn1.setOnClickListener(new View.OnClickListener() {//left
            @Override
            public void onClick(View v) {
                btn1.setBackgroundColor(Color.parseColor("#FFFFFF"));

                //버튼 클릭 시 흰색으로 바뀌었다가 다시 원래 색으로 돌아감
                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        btn1.setBackgroundColor(Color.parseColor("#B99E97"));//딜레이 후 시작할 코드 작성
                    }
                }, 400);// 0.4초 정도 딜레이를 준 후 시작

            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {//up
            @Override
            public void onClick(View v) {
                btn2.setBackgroundColor(Color.parseColor("#FFFFFF"));

                //버튼 클릭 시 흰색으로 바뀌었다가 다시 원래 색으로 돌아감
                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        btn2.setBackgroundColor(Color.parseColor("#B99E97"));//딜레이 후 시작할 코드 작성
                    }
                }, 400);// 0.4초 정도 딜레이를 준 후 시작

                if(musicPlaying==true){
                    gameMusic.stop();
                    musicPlaying=false;
                }

            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {//center
            @Override
            public void onClick(View v) {
                btn3.setBackgroundColor(Color.parseColor("#FFFFFF"));
                //버튼 클릭 시 흰색으로 바뀌었다가 다시 원래 색으로 돌아감
                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        btn3.setBackgroundColor(Color.parseColor("#B99E97"));//딜레이 후 시작할 코드 작성
                    }
                }, 400);// 0.4초 정도 딜레이를 준 후 시작

                if(chance-1>=0){
                    data[chance-1]=0;
                    chance--;
                    Log.e("test", chance+" chances left");
                    writeDriver(data, data.length);
                    Log.e("test", data[0]+" "+data[1]+" "+data[2]+" "+data[3]+" "+data[4]+" "+data[5]+" "+data[6]+" "+data[7]);
                }
                else {
                    Log.e("test", chance+" left. you lose");

                }
            }
        });
        btn4.setOnClickListener(new View.OnClickListener() {//down
            @Override
            public void onClick(View v) {
                btn4.setBackgroundColor(Color.parseColor("#FFFFFF"));

                //버튼 클릭 시 흰색으로 바뀌었다가 다시 원래 색으로 돌아감
                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        btn4.setBackgroundColor(Color.parseColor("#B99E97"));//딜레이 후 시작할 코드 작성
                    }
                }, 400);// 0.4초 정도 딜레이를 준 후 시작
            }
        });
        btn5.setOnClickListener(new View.OnClickListener() {//right
            @Override
            public void onClick(View v) {
                btn5.setBackgroundColor(Color.parseColor("#FFFFFF"));

                //버튼 클릭 시 흰색으로 바뀌었다가 다시 원래 색으로 돌아감
                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        btn5.setBackgroundColor(Color.parseColor("#B99E97"));//딜레이 후 시작할 코드 작성
                    }
                }, 400);// 0.4초 정도 딜레이를 준 후 시작
            }
        });
    }

    @Override
    protected void onPause(){
        //TODO Auto-generated method stub
        mThreadRun=false;
        byte[] closeData={0,0,0,0,0,0,0,0};
        mDriver.close();
        super.onPause();
        writeDriver(closeData, closeData.length);
        closeDriver();
        if(musicPlaying==true){
            gameMusic.pause();
            musicPlaying=false;
        }
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
                case 1://up
                    btn2.setBackgroundColor(Color.parseColor("#FFFFFF"));

                    //버튼 클릭 시 흰색으로 바뀌었다가 다시 원래 색으로 돌아감
                    new Handler().postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            btn2.setBackgroundColor(Color.parseColor("#B99E97"));//딜레이 후 시작할 코드 작성
                        }
                    }, 400);// 0.4초 정도 딜레이를 준 후 시작

                    if(musicPlaying==true){
                        gameMusic.stop();
                        musicPlaying=false;
                    }

                    break;
                case 2://down
                    btn4.setBackgroundColor(Color.parseColor("#FFFFFF"));

                    //버튼 클릭 시 흰색으로 바뀌었다가 다시 원래 색으로 돌아감
                    new Handler().postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            btn4.setBackgroundColor(Color.parseColor("#B99E97"));//딜레이 후 시작할 코드 작성
                        }
                    }, 400);// 0.4초 정도 딜레이를 준 후 시작

                    break;
                case 3://left
                    btn1.setBackgroundColor(Color.parseColor("#FFFFFF"));

                    //버튼 클릭 시 흰색으로 바뀌었다가 다시 원래 색으로 돌아감
                    new Handler().postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            btn1.setBackgroundColor(Color.parseColor("#B99E97"));//딜레이 후 시작할 코드 작성
                        }
                    }, 400);// 0.4초 정도 딜레이를 준 후 시작
                    break;
                case 4://right
                    btn5.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    //버튼 클릭 시 흰색으로 바뀌었다가 다시 원래 색으로 돌아감
                    new Handler().postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            btn5.setBackgroundColor(Color.parseColor("#B99E97"));//딜레이 후 시작할 코드 작성
                        }
                    }, 400);// 0.4초 정도 딜레이를 준 후 시작
                    break;
                case 5://center
                    btn3.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    //버튼 클릭 시 흰색으로 바뀌었다가 다시 원래 색으로 돌아감
                    new Handler().postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            btn3.setBackgroundColor(Color.parseColor("#B99E97"));//딜레이 후 시작할 코드 작성
                        }
                    }, 400);// 0.4초 정도 딜레이를 준 후 시작

                    if(chance-1>=0){
                        data[chance-1]=0;
                        chance--;
                        Log.e("test", chance+" chances left");
                        writeDriver(data, data.length);
                        Log.e("test", data[0]+" "+data[1]+" "+data[2]+" "+data[3]+" "+data[4]+" "+data[5]+" "+data[6]+" "+data[7]);
                    }
                    if(chance==0){
                        Log.e("test", chance+" left. you lose");

                        if(musicPlaying==true){
                            gameMusic.stop();
                        }

                        Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                        intent.putExtra("name", Integer.toString(resultScore));
                        mDriver.close();
                        mThreadRun=false;
                        startActivity(intent);
                    }
                    break;
                default:

                    break;
            }
        }
    };



    @Override
    protected void onResume(){
        //TODO Auto-generated method stub
        //LED
        if(openDriver("/dev/sm9s5422_led")<0){
            Toast.makeText(MainActivity.this, "Driver Open Failed", Toast.LENGTH_SHORT).show();
        }
        writeDriver(data, data.length);

        //GPIO 버튼 재연결
        if(mThreadRun==false){
            mDriver=new JNIDriver();
            mDriver.setListener(this);
            Log.e("test", "GPIO open");
            if(mDriver.open("/dev/sm9s5422_interrupt")<0){
                Toast.makeText(MainActivity.this, "Driver Open Failed", Toast.LENGTH_SHORT).show();
            }
        }

        if(musicPlaying==false){
            gameMusic.start();
        }

        //animation
        //note1=findViewById(R.id.note1);
        //note1.setVisibility(View.VISIBLE);
        //dropNotes();
        Log.e("text", Float.toString(note1.getX())+", "+ Float.toString(note1.getY()));
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
        System.out.println("pushed bnt num : "+val);
        //Log.e("test", "1502");
        handler.sendMessage(text);
        Log.e("test", "onReceive end");

    }

    // onResume과의 구별을 위해 넣음
    @Override
    public void onStart(){
        //dropNotes();
        super.onStart();
    }

    //이 부분부터는 java로 리듬게임 만들기 강좌 참고.
    //dropNotes 메서드, Note 클래스, Beat 클래스
    //https://www.youtube.com/watch?v=NZxMsEKdjbM&list=PLRx0vPvlEmdDySO3wDqMYGKMVH4Qa4QhR&index=14
    public void dropNotes(){
        Note note;
        Beat[] beats={
                new Beat(5000, "note1"),
                new Beat(10000, "note2"),
                new Beat(20000, "note3"),
                };
        int i =0;
        while(true){
            if(beats[i].getTime()<=gameMusic.getCurrentPosition()){
                switch(beats[i].getNoteName()){
                    case "note1":
                        note=new Note(findViewById(R.id.note1));
                        note.start();
                        noteList.add(note);
                        break;
                    case "note2":
                        note=new Note(findViewById(R.id.note2));
                        note.start();
                        noteList.add(note);
                        break;
                    case "note3":
                        note=new Note(findViewById(R.id.note3));
                        note.start();
                        noteList.add(note);
                        break;
                    case "note4":
                        note=new Note(findViewById(R.id.note4));
                        note.start();
                        noteList.add(note);
                        break;
                    case "note5":
                        note=new Note(findViewById(R.id.note5));
                        note.start();
                        noteList.add(note);
                        break;
                }

                i++;
                if(i>=beats.length)
                    break;
            }
        }
    }

    public class Note extends Thread{
        //private ImageView = new ImageView();
        private int x, y;
        ImageView v;

        public Note(int x, int y){
            this.x=x;
            this.y=y;
        }
        public Note(ImageView input){
            v=input;
        }

        public void drop(){
            //안드로이드에서 애니메이션 구현
            TranslateAnimation anim = new TranslateAnimation
                    (note1.getX(),   // fromXDelta
                            note1.getX(),  // toXDelta
                            note1.getY(),    // fromYDelta
                            1280);// toYDelta
            anim.setDuration(3000);
            note1.startAnimation(anim);
        }

        @Override
        public void run(){
            try {
                while(true){
                    drop();
                }
            }catch (Exception e){
                Log.e("test",  e.getMessage());
            }
        }
    }

    public class Beat{
        private int time;
        private String noteName;

        public int getTime(){
            return time;
        }
        public void setTime(int time){
            this.time=time;
        }
        public String getNoteName(){
            return noteName;
        }
        public void setNoteName(String name){
            this.noteName=name;
        }
        public Beat(int time, String noteName){
            super();
            this.time=time;
            this.noteName=noteName;
        }

    }

}
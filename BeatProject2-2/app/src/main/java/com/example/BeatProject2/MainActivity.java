package com.example.BeatProject2;

import static android.net.wifi.p2p.WifiP2pDevice.FAILED;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
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
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.BeatProject2.databinding.ActivityMainBinding;

import org.w3c.dom.Text;

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
    String mode;
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


    private Thread thread=null;
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

        //note1=findViewById(R.id.note1);
        //note2=findViewById(R.id.note2);
        //note3=findViewById(R.id.note3);
        //note4=findViewById(R.id.note4);
        //note5=findViewById(R.id.note5);

        //GPIO ??????
        mDriver=new JNIDriver();
        mDriver.setListener(this);

        if(mDriver.open("/dev/sm9s5422_interrupt")<0){
            Toast.makeText(MainActivity.this, "Driver Open Failed", Toast.LENGTH_SHORT).show();
        }


        //textview ?????? ???????????? ??????
        tv.setTextColor(Color.WHITE);
        score.setTextColor(Color.WHITE);

        Intent receive_intent = getIntent();
        String temp = receive_intent.getStringExtra("name");

        //?????? ?????????????????? easy/hard ????????? ?????? ?????????
        if(temp.equals("easymode")){
            //tv.setText(tv.getText()+temp+" score : ");
            mode="easy";
            back.setBackground(ContextCompat.getDrawable(this, R.drawable.sugar));
            if(musicPlaying==false) {
                gameMusic = MediaPlayer.create(MainActivity.this, R.raw.candy);
                gameMusic.start();
                musicPlaying=true;
            }
        }
        else if(temp.equals("hardmode")){
            //tv.setText(tv.getText()+temp+" score : ");
            mode="hard";
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

        //????????? ????????? 2??? ?????? ??? resultActivity??? ?????????
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
                }, 2000);// 2??? ?????? ???????????? ??? ??? ??????
            }

        });


        //?????? ??????????????? ????????? ??????
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics ();
        display.getMetrics(outMetrics);

        float density = getResources().getDisplayMetrics().density;
        float dpHeight = outMetrics.heightPixels / density;
        float dpWidth = outMetrics.widthPixels / density;

        //dp??? layout ?????? ??????
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

                //?????? ?????? ??? ???????????? ??????????????? ?????? ?????? ????????? ?????????
                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        btn1.setBackgroundColor(Color.parseColor("#B99E97"));//????????? ??? ????????? ?????? ??????
                    }
                }, 400);// 0.4??? ?????? ???????????? ??? ??? ??????

            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {//up
            @Override
            public void onClick(View v) {
                btn2.setBackgroundColor(Color.parseColor("#FFFFFF"));

                //?????? ?????? ??? ???????????? ??????????????? ?????? ?????? ????????? ?????????
                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        btn2.setBackgroundColor(Color.parseColor("#B99E97"));//????????? ??? ????????? ?????? ??????
                    }
                }, 400);// 0.4??? ?????? ???????????? ??? ??? ??????

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
                //?????? ?????? ??? ???????????? ??????????????? ?????? ?????? ????????? ?????????
                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        btn3.setBackgroundColor(Color.parseColor("#B99E97"));//????????? ??? ????????? ?????? ??????
                    }
                }, 400);// 0.4??? ?????? ???????????? ??? ??? ??????

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

                //?????? ?????? ??? ???????????? ??????????????? ?????? ?????? ????????? ?????????
                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        btn4.setBackgroundColor(Color.parseColor("#B99E97"));//????????? ??? ????????? ?????? ??????
                    }
                }, 400);// 0.4??? ?????? ???????????? ??? ??? ??????
            }
        });
        btn5.setOnClickListener(new View.OnClickListener() {//right
            @Override
            public void onClick(View v) {
                btn5.setBackgroundColor(Color.parseColor("#FFFFFF"));

                //?????? ?????? ??? ???????????? ??????????????? ?????? ?????? ????????? ?????????
                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        btn5.setBackgroundColor(Color.parseColor("#B99E97"));//????????? ??? ????????? ?????? ??????
                    }
                }, 400);// 0.4??? ?????? ???????????? ??? ??? ??????
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

    //GPIO ?????? ???????????? ?????????
    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler(new Handler.Callback(){
        @Override
        public boolean handleMessage(@NonNull Message msg){return false;}
    }){
        public void handleMessage(Message msg){
            //Center ????????? ????????? MenuActivity ?????? ????????????
            switch(msg.arg1){
                case 1://up
                    btn2.setBackgroundColor(Color.parseColor("#FFFFFF"));

                    //?????? ?????? ??? ???????????? ??????????????? ?????? ?????? ????????? ?????????
                    new Handler().postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            btn2.setBackgroundColor(Color.parseColor("#B99E97"));//????????? ??? ????????? ?????? ??????
                        }
                    }, 200);// 0.4??? ?????? ???????????? ??? ??? ??????

                    judge("up");
                    break;
                case 2://down
                    btn4.setBackgroundColor(Color.parseColor("#FFFFFF"));

                    //?????? ?????? ??? ???????????? ??????????????? ?????? ?????? ????????? ?????????
                    new Handler().postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            btn4.setBackgroundColor(Color.parseColor("#B99E97"));//????????? ??? ????????? ?????? ??????
                        }
                    }, 200);// 0.4??? ?????? ???????????? ??? ??? ??????
                    judge("down");
                    break;
                case 3://left
                    btn1.setBackgroundColor(Color.parseColor("#FFFFFF"));

                    //?????? ?????? ??? ???????????? ??????????????? ?????? ?????? ????????? ?????????
                    new Handler().postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            btn1.setBackgroundColor(Color.parseColor("#B99E97"));//????????? ??? ????????? ?????? ??????
                        }
                    }, 200);// 0.4??? ?????? ???????????? ??? ??? ??????
                    judge("left");
                    break;
                case 4://right
                    btn5.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    //?????? ?????? ??? ???????????? ??????????????? ?????? ?????? ????????? ?????????
                    new Handler().postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            btn5.setBackgroundColor(Color.parseColor("#B99E97"));//????????? ??? ????????? ?????? ??????
                        }
                    }, 200);// 0.4??? ?????? ???????????? ??? ??? ??????
                    judge("right");
                    break;
                case 5://center

                    btn3.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    //?????? ?????? ??? ???????????? ??????????????? ?????? ?????? ????????? ?????????
                    new Handler().postDelayed(new Runnable()
                    {

                        @Override
                        public void run()
                        {
                            btn3.setBackgroundColor(Color.parseColor("#B99E97"));//????????? ??? ????????? ?????? ??????
                        }
                    }, 200);// 0.4??? ?????? ???????????? ??? ??? ??????
                    judge("center");
                /*
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
                    }*/
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

        //GPIO ?????? ?????????
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
        Log.e("test", "Main onReceive end");

    }

    // onResume?????? ????????? ?????? ??????
    @Override
    public void onStart(){
        super.onStart();
        dropNotes();
    }

    //??? ??????????????? java??? ???????????? ????????? ?????? ??????.
    //dropNotes ?????????, Note ?????????, Beat ?????????
    //https://www.youtube.com/watch?v=NZxMsEKdjbM&list=PLRx0vPvlEmdDySO3wDqMYGKMVH4Qa4QhR&index=14
    public void dropNotes() {
        Beat[] beats = null;
        if (mode.equals("easy")) {//easy mode: ?????? ????????? ??? ?????? ??????
            int startTime=1000;//1sec
            int gap = 500;//?????? 2/4??????????????? ?????? 250ms
            beats = new Beat[]{
                    new Beat(startTime+gap*2,"left"),
                    new Beat(startTime+gap*4,"right"),
                    new Beat(startTime+gap*6,"up"),
                    new Beat(startTime+gap*8,"down"),

                    new Beat(startTime+gap*11,"center"),
                    new Beat(startTime+gap*13,"up"),
                    new Beat(startTime+gap*14,"down"),
                    new Beat(startTime+gap*15,"left"),
                    new Beat(startTime+gap*17,"right"),
                    new Beat(startTime+gap*19,"center"),
                    new Beat(startTime+gap*22,"center"),
                    new Beat(startTime+gap*24,"left"),

                    new Beat(startTime+gap*26,"right"),
                    new Beat(startTime+gap*29,"down"),
                    new Beat(startTime+gap*30,"down"),
                    new Beat(startTime+gap*31,"up"),
                    new Beat(startTime+gap*32,"up"),
                    new Beat(startTime+gap*33,"center"),
                    new Beat(startTime+gap*34,"center"),
                    new Beat(startTime+gap*35,"down"),
                    new Beat(startTime+gap*36,"right"),
                    new Beat(startTime+gap*36+gap/2,"left"),

                    new Beat(startTime+gap*39,"center"),
                    new Beat(startTime+gap*41,"down"),
                    new Beat(startTime+gap*43,"right"),
                    new Beat(startTime+gap*45,"left"),
                    new Beat(startTime+gap*47,"up"),
                    new Beat(startTime+gap*48,"down"),
                    new Beat(startTime+gap*50,"center"),
            };
        }
        else{//hard mode: ????????? ???????????? ?????? ??????
            int startTime=1000;//1sec
            beats = new Beat[]{
                    new Beat(startTime,"up")
            };
        }
        for(int i=0;i<beats.length;i++){
            Note note=new Note(beats[i].getNoteName());
            thread=new Thread(new Note(beats[i].getNoteName()));
            new Handler().postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    note.screenDraw();
                    note.start();
                    noteList.add(note);
                    //????????? ??? ????????? ?????? ??????
                }
            }, beats[i].getTime());
            //note.iv.setVisibility(View.GONE);
        }
    }

    public void judge(String input){
        for(int i=0;i<noteList.size();i++){
            Note note=noteList.get(i);
            if(input.equals(note.getNotetype())){
                note.judge();
                break;
            }
        }
    }
    public class Note extends Thread{
        private int x, y;
        ImageView iv;
        private String notetype;
        private boolean proceeded = true;
        Handler animHandler;


        public String getNotetype(){
            return notetype;
        }
        public boolean isProceeded(){
            return proceeded;
        }
        //?????? ??????????????? ???????????? ?????????
        public void close(){
            proceeded=false;
        }
        public Note(String notetype){
            switch (notetype){
                case "left":
                    x=5;break;
                case "up":
                    x=84;break;
                case "center":
                    x=163;break;
                case "down":
                    x=242;break;
                case "right":
                    x=321;break;
            }
            y=0;
            this.notetype=notetype;
        }
        public void screenDraw(){
            FrameLayout flay=findViewById(R.id.frame);

            iv = new ImageView(MainActivity.this);
            iv.setBackgroundColor(Color.parseColor("#000000"));
            FrameLayout.LayoutParams param =new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);

            param.width = 148; //????????? ??????
            param.height = 80; //????????? ??????
            param.setMargins(x*2,y*2,0,0); //????????? ??????(??????, ???, ?????????, ??????)
            flay.addView(iv, param);

            TextView tv = findViewById(R.id.score);
            }
        public void drop(){
            //????????????????????? ??????????????? ??????
            ObjectAnimator anim = ObjectAnimator.ofFloat(iv,"translationY", 1280f);
            anim.setDuration(2800); // duration 5 second
            anim.start();
            /*
            TranslateAnimation anim = new TranslateAnimation
                    (0,   // fromXDelta
                            0,  // toXDelta
                            iv.getY(),    // fromYDelta
                            1280);// toYDelta
            anim.setDuration(2800);
            anim.setFillAfter(true);
            anim.setAnimationListener(new TranslateAnimation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }
                @Override
                public void onAnimationRepeat(Animation animation) { }
                @Override
                public void onAnimationEnd(Animation animation) {
                    y=1280;
                } });

            iv.startAnimation(anim);*/


            //iv.setVisibility(View.GONE);
            //Log.e("test", "iv.getX = "+ iv.getX()+" iv.getY = "+ iv.getY());
            /*
            Handler nHandler = new Handler(Looper.getMainLooper());
            nHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.e("note input: ", "miss");
                    Log.e("y: ", Integer.toString(y));
                }
            }, 100);*/

            //int[] a={0,0};
            //iv.getLocationOnScreen(a);
            //Log.e("????????????", "X: "+a[0]+" Y: "+a[1]);

            Log.e("y: ", Integer.toString(y));
            //miss??? ?????? ??????
           if(y>1200){
               //Message msg = nHandler.obtainMessage();
               //nHandler.sendMessage(msg);
               close();
            }
        }

        @Override
        public void run(){
            try {
                while(true){
                    drop();

                    if(!proceeded){
                        interrupt();
                        break;
                    }
                }
            }catch (Exception e){
                if(e.getMessage()!=null)
                    Log.e("test",  e.getMessage());
                else
                    Log.e("test",  "error!!");
            }
        }
        public void judge(){
            //TextView tv=findViewById(R.id.score);
            if(y>=1210){
                Log.e("??????", "late");
                resultScore+=0;
                close();
            }
            else if(y>=1200){
                Log.e("??????", "good");
                resultScore+=50;
                close();
            }
            else if(y>=1180){
                Log.e("??????", "perfect");
                resultScore+=100;
                close();
            }
            else if(y>=1000){
                Log.e("??????", "good");
                resultScore+=50;
                close();
            }
            else if(y>=800){
                Log.e("??????", "early");
                resultScore+=0;
                close();
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
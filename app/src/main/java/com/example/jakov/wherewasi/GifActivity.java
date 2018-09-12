package com.example.jakov.wherewasi;

import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

public class GifActivity extends AppCompatActivity {

    private ImageView imageView1;
    private TextView textView, updateSpeedTextView;
    private DatabaseHelper db;
    private ArrayList<timeImage> timeImages;
    private Handler mHandler;
    private Button startStopBtn;
    private SeekBar speedBar;
    private int updateSpeed = 500;
    private boolean running = false;
    private boolean started = false;
    private Object mPauseLock;

    private void setHandler() {
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {

                if (message.arg1 == 1) {
                    timeImage tm = (timeImage) message.obj;
                    textView.setText(tm.text);
                    imageView1.setImageBitmap(BitmapFactory.decodeFile(tm.path));
                }

                else if (message.arg1 == 2) {
                    startStopBtn.setText("START");
                }


            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gif);

        imageView1 = findViewById(R.id.imageView1);
        textView = findViewById(R.id.textView);
        db = new DatabaseHelper(this);
        timeImages = new ArrayList<>();
        startStopBtn = findViewById(R.id.startStopBtn);
        speedBar = findViewById(R.id.seekBar);
        mPauseLock = new Object();
        updateSpeedTextView = findViewById(R.id.updateSpeedTextView);
        updateSpeedTextView.setText(updateSpeed + " ms");

        setHandler();

        speedBar.setMax(2000);
        speedBar.setProgress(updateSpeed);

        speedBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                updateSpeed = i;
                updateSpeedTextView.setText(updateSpeed + " ms");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



        Cursor data = db.getGifData();

        while (data.moveToNext()) {

            timeImage tm = new timeImage(data.getString(0),data.getString(1));

            timeImages.add(tm);

            Log.d("gif", "giffy: " + data.getString(0) + "|" + data.getString(1));

        }


        final Runnable gifRunnable = new Runnable(){
            @Override
            public void run()
            {
                for (timeImage tm : timeImages) {


                    Message message = mHandler.obtainMessage();
                    message.obj = tm;
                    message.arg1 = 1;
                    message.sendToTarget();

                    try {
                        Thread.sleep(updateSpeed);
                        synchronized (mPauseLock) {
                            while (!running) {
                                mPauseLock.wait();

                            }
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                }
                started = false;
                Message message = mHandler.obtainMessage();
                message.arg1 = 2;
                message.sendToTarget();


            }
        };



        startStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (started && running) {
                    startStopBtn.setText("CONTINUE");
                    synchronized (mPauseLock) {
                        running = false;
                    }
                }
                else if (started && !running) {
                    synchronized (mPauseLock) {
                        running = true;
                        mPauseLock.notifyAll();
                    }
                    startStopBtn.setText("STOP");

                }
                else if (!started) {
                    Thread myThread = new Thread(gifRunnable);
                    myThread.start();
                    startStopBtn.setText("STOP");
                    started = true;
                    synchronized (mPauseLock) {
                        running = true;
                        mPauseLock.notifyAll();
                    }
                }

            }
        });

    }

    private class timeImage {
        String text;
        String path;

        private timeImage(String text, String path) {
            this.text = text;
            this.path = path;
        }
    }


}

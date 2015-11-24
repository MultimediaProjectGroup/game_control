package com.example.jyqiu.multpro;

import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View.OnClickListener;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    String TAG = "AudioRecord";
    final int SAMPLE_RATE_IN_HZ = 8000;
    int BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE_IN_HZ,
            AudioFormat.CHANNEL_IN_DEFAULT, AudioFormat.ENCODING_PCM_16BIT);
    AudioRecord mAudioRecord;
    boolean isGetVoiceRun = true;
    short[] buffer = new short[BUFFER_SIZE];

    public int result = 1;
    private static Button btn;

    RootShellCmd rootShellCmd = new RootShellCmd();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //AudioRecordDemo ad = new  AudioRecordDemo();
        //ad.getNoiseLevel();
        mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE_IN_HZ, AudioFormat.CHANNEL_IN_DEFAULT,
                AudioFormat.ENCODING_PCM_16BIT, BUFFER_SIZE);
        mAudioRecord.startRecording();
        btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(new OnClickListener() {
            public void onClick(View viewParam) {
                new MyTask().execute();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class MyTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... params) {
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            while (isGetVoiceRun) {
                //r是实际读取的数据长度，一般而言r会小于buffersize
                int r = mAudioRecord.read(buffer, 0, BUFFER_SIZE);
                long v = 0;
                // 将 buffer 内容取出，进行平方和运算
                for (int i = 0; i < buffer.length; i++) {
                    //Log.i(TAG, "buffer: "+ buffer[i]);
                    v += buffer[i] * buffer[i];
                }
                // 平方和除以数据总长度，得到音量大小。
                double mean = v / (double) r;
                double volume = 10 * Math.log10(mean);
//                Log.i(TAG, "volume: " + (int)volume);
                if (volume > 50) {
                    Log.i(TAG, "volume: " + (int)volume);
                    rootShellCmd.exec("input tap 250 250");
                    return (int)volume;
                }
                //isGetVoiceRun = false;
            }
            mAudioRecord.stop();
            mAudioRecord.release();
            return 0;
        }

        @Override
        public void onPostExecute(Integer result) {
            if(result == 0){
                Toast.makeText(getApplicationContext(),
                        "wrong username or password",
                        Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getApplicationContext(),
                        "1111",
                        Toast.LENGTH_SHORT).show();
                btn.performClick();
            }
        }
    }

}



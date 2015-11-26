package com.example.jyqiu.multpro;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.text.format.Time;
import android.util.Log;

import java.util.Timer;


/**
 * Created by JYQiu on 21/11/15.
 */
public class AudioRecordDemo {

    private static final String TAG = "AudioRecord";
    static final int SAMPLE_RATE_IN_HZ = 44100;
    static final int BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE_IN_HZ,
            AudioFormat.CHANNEL_IN_DEFAULT, AudioFormat.ENCODING_PCM_16BIT);
    AudioRecord mAudioRecord;
    boolean isGetVoiceRun;
    short[] buffer = new short[BUFFER_SIZE];

    RootShellCmd rootShellCmd = new RootShellCmd();
    boolean tag = true;

    public void getNoiseLevel() {

        mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE_IN_HZ, AudioFormat.CHANNEL_IN_DEFAULT,
                AudioFormat.ENCODING_PCM_16BIT, BUFFER_SIZE);
        if (mAudioRecord == null) {
            Log.e(TAG, "mAudioRecord初始化失败");
        }
        else
            Log.e(TAG, "mAudioRecord初始化成功");
        isGetVoiceRun = true;

        new Thread(new Runnable() {
            @Override
            public void run() {
                mAudioRecord.startRecording();
                while (isGetVoiceRun) {
                    int r = mAudioRecord.read(buffer, 0, BUFFER_SIZE);
                    long v = 0;
                    for (int i = 0; i < buffer.length; i++) {
                        //Log.i(TAG, "buffer:"+buffer[i]);
                        v += buffer[i] * buffer[i];
                    }
                    double mean = v / (double) r;
                    double volume = 10 * Math.log10(mean);
                    Log.i(TAG, "volume : " + volume);
                    if(volume < 50){
                        tag = true;
                    }
                    if(volume > 50 && tag == true){
                        tag = false;
                        rootShellCmd.exec("input tap 250 250");
                        Log.i(TAG, "test: " + volume);
                    }
                }
                mAudioRecord.stop();
                mAudioRecord.release();
                mAudioRecord = null;
            }
        }).start();
    }
}



package com.zaeb.app;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;
import fi.iki.elonen.NanoHTTPD;
import java.io.IOException;
import java.io.OutputStream;

public class AudioHttpServer extends NanoHTTPD {

    private AudioRecord recorder;
    private volatile boolean running = false;

    public AudioHttpServer(int port) {
        super(port);
    }

    public void startServer() {
        try {
            start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
            running = true;
            startAudio();
            Log.i("AudioHttpServer","Started on port: " + getListeningPort());
        } catch (IOException e) {
            Log.e("AudioHttpServer","Failed to start", e);
        }
    }

    public void stopServer() {
        running = false;
        stop();
        stopAudio();
        Log.i("AudioHttpServer","Stopped"); 
    }

    private void startAudio() {
        int sampleRate = 16000;
        int channelConfig = AudioFormat.CHANNEL_IN_MONO;
        int audioFormat = AudioFormat.ENCODING_PCM_16BIT;

        int bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);
        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, channelConfig, audioFormat, bufferSize);
        recorder.startRecording();

        // run a thread to stream audio to clients if needed; for now recorder is active
    }

    private void stopAudio() {
        if (recorder != null) {
            try {
                recorder.stop();
                recorder.release();
            } catch (Exception ignored) {}
            recorder = null;
        }
    }

    @Override
    public Response serve(IHTTPSession session) {
        String msg = "Audio Server is running on port " + getListeningPort();
        return newFixedLengthResponse(msg);
    }
}

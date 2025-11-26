package com.zaeb.app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private TextView ipText, urlText;
    private Button startButton, stopButton;

    private final ActivityResultLauncher<String[]> requestPermissions =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                Boolean record = result.getOrDefault(Manifest.permission.RECORD_AUDIO, false);
                if (record) startService();
                else urlText.setText("Need RECORD_AUDIO permission");
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ipText = findViewById(R.id.textView);
        urlText = findViewById(R.id.urlText);
        startButton = findViewById(R.id.startButton);
        stopButton = findViewById(R.id.stopButton);

        startButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions.launch(new String[]{Manifest.permission.RECORD_AUDIO});
            } else {
                startService();
            }
        });
        stopButton.setOnClickListener(v -> stopService());
    }

    private void startService() {
        Intent i = new Intent(this, AudioService.class);
        startForegroundService(i);
        urlText.setText("Сервер запущен");
    }

    private void stopService() {
        Intent i = new Intent(this, AudioService.class);
        stopService(i);
        urlText.setText("Сервер остановлен");
    }
}

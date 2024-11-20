package com.example.videoediting;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.Manifest;

import org.opencv.android.OpenCVLoader;

import java.io.File;

@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
public class MainActivity extends AppCompatActivity {
    private final ActivityResultLauncher<Intent> videoPickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri videoUri = result.getData().getData();
                    if (videoUri != null) {
                        Log.i("khac.dat", "Video Uri: " + videoUri);
                        String videoPath = getPathFromUri(videoUri);
                        Log.i("khac.dat", "Input Path: " + videoPath);

                        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), "VideoEditing");
                        if (!directory.exists()) {
                            directory.mkdirs();
                        }
                        File outputFile = new File(directory, "edited_video" + System.currentTimeMillis() + ".avi");
                        String outputPath = outputFile.getAbsolutePath();
                        Log.i("khac.dat", "Output Path: " + outputPath);

                        VideoProcessor.processVideo(videoPath, outputPath);
                    } else {
                        Log.e("khac.dat", "Can't select a video!");
                    }
                }
            });

    private void pickVideo() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("video/*");
        videoPickerLauncher.launch(intent);
    }

    private String getPathFromUri(Uri uri) {
        String[] projection = {MediaStore.Video.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(columnIndex);
            cursor.close();
            return path;
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (OpenCVLoader.initLocal()) {
            Log.i("khac.dat", "OpenCV loaded successfully");
        } else {
            Log.e("khac.dat", "OpenCV initialization failed!");
            (Toast.makeText(this, "OpenCV initialization failed!", Toast.LENGTH_LONG)).show();
        }

        Button button = findViewById(R.id.grayscale_button);
        button.setOnClickListener(v -> pickVideo());
        requestManageExternalStoragePermission();
        ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS);
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void requestManageExternalStoragePermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, REQUEST_PERMISSIONS);
    }

    private final int REQUEST_PERMISSIONS = 100;
    private final String[] permissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.MANAGE_EXTERNAL_STORAGE,
            Manifest.permission.READ_MEDIA_VIDEO
    };
}
package com.example.mytask2;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

public class MainActivity extends AppCompatActivity {
    private Button selectFile;
    private  static final int PER_REQ_STORAGE = 1000;
    private  static final int READ_REQ_CODE = 42;
    private static final String TAG = "Base64String:";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        selectFile = findViewById(R.id.selectFile);
        selectFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},PER_REQ_STORAGE);
                }
                try {
                    Intent it = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    it.addCategory(Intent.CATEGORY_OPENABLE);
                    it.setType("*/*");
                    startActivityForResult(it,READ_REQ_CODE);
                    //it.setType("*/*")
                    // Intent i = Intent.createChooser(it, "Select File");
                    // startActivityForResult(Intent.getIntent(Intent.ACTION_GET_CONTENT), FILE_SELECT_CODE);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getApplicationContext(), "Please install File Manager", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent Data) {
        if ( requestCode == READ_REQ_CODE && resultCode == Activity.RESULT_OK)
        {
            if(Data != null)
            {
                Uri uri=Data.getData();
                String path= uri.getPath();
                path = path.substring(path.indexOf(":")+1);
                String encodedString = null;
                try {
                    byte[] input_file = Files.readAllBytes(Paths.get(path));
                    byte[] encodedBytes = Base64.getEncoder().encode(input_file);
                    encodedString = new String(encodedBytes);
                    //byte[] decodedBytes = Base64.getDecoder().decode(encodedString.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //Toast.makeText(this,""+path,Toast.LENGTH_LONG).show();
                Log.d(TAG, encodedString);
                Toast.makeText(this,encodedString,Toast.LENGTH_LONG).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, Data);
    }

    @Override
    public void onRequestPermissionsResult(int reqCode, String[] permissions, int[] grantResults) {
        if(reqCode == PER_REQ_STORAGE)
        {
            if(grantResults[0] ==  PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this,"Permission granted",Toast.LENGTH_SHORT).show();
            }else
            {
                Toast.makeText(this,"Permission denied",Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        super.onRequestPermissionsResult(reqCode, permissions, grantResults);
    }
}
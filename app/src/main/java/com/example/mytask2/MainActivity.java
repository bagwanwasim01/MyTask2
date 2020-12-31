package com.example.mytask2;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

public class MainActivity extends AppCompatActivity {
    private Button selectFile;
    private  static final int PER_REQ_STORAGE = 1000;
    private  static final int READ_REQ_CODE = 42;
    private static final String TAG = "Base64String:";
    private String[] mimeTypes =
            {
                    "application/msword","application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                    "application/vnd.ms-powerpoint","application/vnd.openxmlformats-officedocument.presentationml.presentation",
                    "application/vnd.ms-excel","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    "text/plain",
                    "application/pdf",
                    "application/zip"
            };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        selectFile = findViewById(R.id.selectFile);
        selectFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},PER_REQ_STORAGE);
                }
                try {
                    Intent it = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    it.addCategory(Intent.CATEGORY_OPENABLE);
                    it.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
                    if(mimeTypes.length > 0){
                        it.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
                    }
                    startActivityForResult(Intent.createChooser(it,"Choose File"),READ_REQ_CODE);
//                  it.setType("*/*");
//                  startActivityForResult(it,READ_REQ_CODE);
                }catch (ActivityNotFoundException ex) {
                    Toast.makeText(getApplicationContext(), "Please install File Manager" + ex ,Toast.LENGTH_SHORT).show();
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
                Cursor returnC = getContentResolver().query(uri,null,null,null,null);
                String mime = getContentResolver().getType(uri);

                int nameIndex = returnC.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                int sizeIndex = returnC.getColumnIndex(OpenableColumns.SIZE);
                returnC.moveToFirst();
                //String name = returnC.getString(nameIndex);
                Long size = Long.valueOf(Long.toString(returnC.getLong(sizeIndex)));
                if(size > 256000){
                    Toast.makeText(this,"Please select file size upto 256kb",Toast.LENGTH_LONG).show();
                    return;
                }
                //Toast.makeText(this,name + size , Toast.LENGTH_LONG).show();
                String path= uri.getPath();
                path = path.substring(path.indexOf(":")+1);
                String encodedString = null;
                try {
                    byte[] input_file = Files.readAllBytes(Paths.get(path));
                    byte[] encodedBytes = Base64.getEncoder().encode(input_file);
                    encodedString = new String(encodedBytes);
                    //byte[] decodedBytes = Base64.getDecoder().decode(encodedString.getBytes());
                } catch (IOException e) {
                    Log.e(TAG, "onActivityResult: ", e);
                    Toast.makeText(this, (CharSequence) e,Toast.LENGTH_LONG).show();
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
                Toast.makeText(this,"PERMISSION GRANTED",Toast.LENGTH_LONG).show();
            }else
            {
                Toast.makeText(this,"PERMISSION DENIED ",Toast.LENGTH_LONG).show();
                finish();
            }
        }
        super.onRequestPermissionsResult(reqCode, permissions, grantResults);
    }
}
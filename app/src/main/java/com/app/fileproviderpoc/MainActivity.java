package com.app.fileproviderpoc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    public static final int ALL_PERMISSIONS = 1001;
    public static final String directoryName = "test_images" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        methodRequiresAllPermission();
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        Log.e("test","permission granted");
        openFile();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Log.e("test","permission denied");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(ALL_PERMISSIONS)
    private void methodRequiresAllPermission() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // Already have permission, do the thing
            openFile();

        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "Need All permissions",
                    ALL_PERMISSIONS, perms);
        }
    }

    private void openFile(){
        String filePath = getFolderPath()+"/test.png";
        openImage(filePath);
    }

    public File getFolderPath(){
        File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM + "/"+directoryName+"/");
        //File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS+ "/"+directoryName+"/");
        return directory;
    }

    public void openImage(String path){
        File file = new File(path);
        Intent target = new Intent("android.intent.action.VIEW");
        Uri imageUri = FileProvider.getUriForFile(this, getPackageName() +".provider", file);
        target.setDataAndType(imageUri, "image/*");
        target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Intent intent = Intent.createChooser(target, "Open File");

        try {
            startActivity(intent);
        } catch (ActivityNotFoundException err) {
            err.printStackTrace();
        }
    }
}
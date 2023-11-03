package com.example.pre_alpha;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;

import java.io.File;


public class SelectImageFragment extends Fragment {

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;
    String cameraPermissions[];
    String storagePermissions[];
    ImageView image;
    Uri image_uri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_select_image, container, false);

        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        image=view.findViewById(R.id.selectImage);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageDialog();
            }
        });

        return view;
    }

    private void showImageDialog(){
        String options[] = {"מצלמה", "גלריה"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("בחר פעולה");
        builder.setItems((options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which==0){
                    if(!checkCameraPermission()){
                        requestCameraPermission();
                    }
                    else pickFromCamera();
                }
                if(which==1){
                    if(!checkStoragePermission()){
                        requestStoragePermission();
                    }
                    pickFromGallery();
                }
            }
        }))
    }

    private boolean checkStoragePermission() {
        boolean result= ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result;

    }
    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(getActivity(),storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        boolean result1= ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
        boolean result2= ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
        return result1 && result2;
    }
    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(getActivity(),cameraPermissions, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if(grantResults.length>0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted && writeStorageAccepted){
                        pickFromCamera();
                    }
                    else{
                        Toast.makeText(getActivity(),"Please enable camera and storage permissions", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE:{
                if(grantResults.length>0){
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if(writeStorageAccepted){
                        pickFromGallery();
                    }
                    else{
                        Toast.makeText(getActivity(),"Please enable storage permission", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void pickFromCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");

        image_uri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
    }
    private void pickFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("images/*");
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode == IMAGE_PICK_GALLERY_CODE){
                image_uri = data.getData();
                uploadPostImage(image_uri);
            }
            if(requestCode == IMAGE_PICK_CAMERA_CODE){
                uploadPostImage(image_uri);
            }
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadPostImage(Uri imageUri) {

    }
}
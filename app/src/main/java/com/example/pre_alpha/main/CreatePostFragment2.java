package com.example.pre_alpha.main;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static com.example.pre_alpha.models.FBref.refPosts;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.pre_alpha.R;
import com.example.pre_alpha.models.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;


public class CreatePostFragment2 extends Fragment {

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;
    String[] cameraPermissions;
    String[] storagePermissions;
    Button upload, btnUpload;
    ImageView image;
    Uri image_uri;
    FirebaseAuth auth;
    Dialog dialog;
    Post post;
    FirebaseUser fbUser;
    BottomNavigationView bottomNavigationView;
    HomeFragment homeFragment = new HomeFragment();
    String storagePath = "Users_Posts_Images/";
    Uri downloadUri;
    String postId;
    TextInputEditText etAbout;
    StorageReference storageReference;
    boolean result, result1, result2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post2, container, false);

        storageReference = FirebaseStorage.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        fbUser = auth.getCurrentUser();
        bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationBar);
        etAbout=view.findViewById(R.id.about);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            cameraPermissions = new String[]{Manifest.permission.CAMERA};
            storagePermissions = new String[]{Manifest.permission.READ_MEDIA_IMAGES};
        }


        upload=view.findViewById(R.id.upload);
        image=view.findViewById(R.id.selectImage);

        DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference("Users/" + fbUser.getUid() + "/Posts");
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageDialog();
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postId = postsRef.push().getKey();
                String filePathAndName = storagePath + "image" + "_" + postId;
                StorageReference storageReference2 = storageReference.child(filePathAndName);
                if(image_uri!=null) {
                    storageReference2.putFile(image_uri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                    while (!uriTask.isSuccessful()) ;
                                    downloadUri = uriTask.getResult();
                                    updateDatabase();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
                else
                    updateDatabase();
            }
        });
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        image_uri=null;
    }

    private void updateDatabase(){
        String name = getArguments().getString("name");
        String item = getArguments().getString("item");
        double latitude = getArguments().getDouble("latitude");
        double longitude = getArguments().getDouble("longitude");
        int radius = getArguments().getInt("radius");

        String date = getArguments().getString("date");
        String[] dateComponents = date.split(" ");
        int month = getMonthFormat(dateComponents[0]) - 1; // Calendar months are 0-based
        int dayOfMonth = Integer.parseInt(dateComponents[1]);
        int year = Integer.parseInt(dateComponents[2]);
        Calendar calender = Calendar.getInstance();
        calender.set(Calendar.YEAR, year);
        calender.set(Calendar.MONTH, month);
        calender.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        String about = etAbout.getText().toString();
        SharedPreferences state = getActivity().getSharedPreferences("state", MODE_PRIVATE);
        String checkState = state.getString("state", "");
        if(image_uri!=null){
            post=new Post(name, item, latitude, longitude, radius, about, downloadUri.toString(), checkState, fbUser.getUid(), postId, calender.getTimeInMillis());
        }
        else{
            post=new Post(name, item, latitude, longitude, radius, about, "", checkState, fbUser.getUid(), postId, calender.getTimeInMillis());
        }
        refPosts.child(postId).setValue(post).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                dialog=new Dialog(getActivity());
                dialog.setContentView(R.layout.upload_post_dialog_layout);
                btnUpload=dialog.findViewById(R.id.uploadSuccessfully);
                btnUpload.setOnClickListener(new View.OnClickListener(){
                    public void onClick(View view){
                        dialog.cancel();
                        getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, homeFragment).commit();
                        bottomNavigationView.setSelectedItemId(R.id.home);
                        bottomNavigationView.setItemIconTintList(null);
                    }
                });
                dialog.show();
            }
        });
    }
    private void showImageDialog(){
        String options[] = {"מצלמה", "גלריה"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("בחר פעולה");
        builder.setItems(options, new DialogInterface.OnClickListener() {
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
                    else pickFromGallery();
                }
            }
        });
        builder.show();
    }

    private boolean checkStoragePermission() {
        boolean result1 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_MEDIA_IMAGES)
                == (PackageManager.PERMISSION_GRANTED);
        boolean result2 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result1||result2;

    }
    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(getActivity(),storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            result1 = ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED;
            result2 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED;
            return result1&&result2;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED;
            return result;
        }
        return false;

    }
    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(getActivity(),cameraPermissions, CAMERA_REQUEST_CODE);
    }

    private void pickFromCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");
        image_uri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
    }
    private void pickFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode == IMAGE_PICK_GALLERY_CODE){
                image_uri = data.getData();
                image.setImageURI(image_uri);
            }
            if(requestCode == IMAGE_PICK_CAMERA_CODE){
                image.setImageURI(image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private int getMonthFormat(String month){
        if(month.equals("JAN")) return 1;
        if(month.equals("FEB")) return 2;
        if(month.equals("MAR")) return 3;
        if(month.equals("APR")) return 4;
        if(month.equals("MAY")) return 5;
        if(month.equals("JUN")) return 6;
        if(month.equals("JUL")) return 7;
        if(month.equals("AUG")) return 8;
        if(month.equals("SEP")) return 9;
        if(month.equals("OCT")) return 10;
        if(month.equals("NOV")) return 11;
        if(month.equals("DEC")) return 12;

        return 1;
    }


}
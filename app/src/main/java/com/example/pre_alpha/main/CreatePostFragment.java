package com.example.pre_alpha.main;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import static com.example.pre_alpha.models.FBref.refPosts;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.pre_alpha.R;
import com.example.pre_alpha.models.Post;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.slider.Slider;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;


public class CreatePostFragment extends Fragment {


    static final int ERROR_DIALOG_REQUEST= 9001;
    private static final String TAG = "CreatePostFragment";
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;


    String[] items={"ארנק", "תיק", "סמארטפון", "משקפי ראייה / שמש","שעון חכם", "מצלמה" ,"תעודת זהות / דרכון", "רישיון נהיגה", "חוגר / חוגרון", "כרטיס רב קו", "כרטיסים כללי", "מפתחות בית / רכב", "שקית / שקית קניות", "אוזניות / קייס אוזניות", "שרשרת / תיליון", "צמיד", "טבעת", "תפילין", "מחשב נייד", "מטען", "כרטיס זיכרון / דיסק און קיי", "מעיל / סווטשירט", "קיטבג / מזוודה", "מכשיר שמיעה", "אחר"};
    String[] cameraPermissions;
    String[] storagePermissions;
    String name, item, postId, checkState;
    double latitude, longitude;
    int radius;
    TextView tvState, tvRadius;
    TextInputEditText etName, etAbout;
    ImageView mapIv, checkPickLocation, image;
    DatePickerDialog datePickerDialog;
    Slider radiusSlider;
    Button dateButton, upload, btnUpload;
    TextInputLayout layoutItem;
    AutoCompleteTextView pickItem;
    ArrayAdapter<String> adapterItems;
    Uri image_uri, downloadUri;
    FirebaseAuth auth;
    Dialog dialog;
    Post post;
    FirebaseUser fbUser;
    BottomNavigationView bottomNavigationView;
    HomeFragment homeFragment = new HomeFragment();
    String storagePath = "Users_Posts_Images/";
    StorageReference storageReference;
    DatabaseReference postsRef;
    boolean result, result1, result2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post, container, false);
        tvState=view.findViewById(R.id.message_state);
        etName = view.findViewById(R.id.name);
        mapIv=view.findViewById(R.id.select_location);
        radiusSlider=view.findViewById(R.id.radius_slider);
        adapterItems = new ArrayAdapter<>(getActivity(),R.layout.list_item,items);
        pickItem = view.findViewById(R.id.list_of_items);
        layoutItem = view.findViewById(R.id.item);
        checkPickLocation=view.findViewById(R.id.pick_location_check);
        dateButton=view.findViewById(R.id.datePickerButton);
        tvRadius=view.findViewById(R.id.radius_value);
        dateButton.setText(getTodaysDate());
        pickItem.setAdapter(adapterItems);
        SharedPreferences state = getActivity().getSharedPreferences("state", MODE_PRIVATE);
        checkState = state.getString("state", "");
        if(checkState.equals("lost")) tvState.setText("מה איבדת?");
        else tvState.setText("מה מצאת?");
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

        postsRef = FirebaseDatabase.getInstance().getReference("Users/" + fbUser.getUid() + "/Posts");


        return view;
    }


    @Override
    public void onStart() {
        super.onStop();
        if(getArguments()!=null){
            etName.setText(getArguments().getString("state_name", ""));
            pickItem.setText(getArguments().getString("state_item", ""));
            dateButton.setText(getArguments().getString("state_date", getTodaysDate()));
            etAbout.setText(getArguments().getString("state_about", ""));

        }
        if(isServicesOk()){
            initMap();
        }
        init();
        initDatePicker();
    }

    private void initMap(){
        if(this.getArguments()!=null) {
            latitude = this.getArguments().getDouble("latitude");
            longitude = this.getArguments().getDouble("longitude");
            if(latitude!=0 && longitude!=0)  checkPickLocation.setImageResource(R.drawable.baseline_library_add_check_24);
        }

        mapIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapFragment mapFragment = new MapFragment();
                Bundle bundle = new Bundle();
                bundle.putString("from_where", "create_post");
                bundle.putString("state_name", etName.getText().toString());
                bundle.putString("state_item", pickItem.getText().toString());
                bundle.putString("state_date", dateButton.getText().toString());
                bundle.putString("state_about", etAbout.getText().toString());
                mapFragment.setArguments(bundle);
                getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, mapFragment).commit();
            }
        });

    }

    private void init(){
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageDialog();
            }
        });

        radiusSlider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                tvRadius.setText(Integer.toString((int) value));
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
    }

    private void updateDatabase(){
        name = etName.getText().toString();
        item = pickItem.getText().toString();
        radius = (int) radiusSlider.getValue();
        String date = dateButton.getText().toString();
        String[] dateComponents = date.split(" ");
        int month = getMonthFormatFromInt(dateComponents[0]) - 1; // Calendar months are 0-based
        int dayOfMonth = Integer.parseInt(dateComponents[1]);
        int year = Integer.parseInt(dateComponents[2]);
        Calendar calender = Calendar.getInstance();
        calender.set(Calendar.YEAR, year);
        calender.set(Calendar.MONTH, month);
        calender.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        String about = etAbout.getText().toString();
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


    private void initDatePicker(){
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month=month+1;
                String date = makeDateString(dayOfMonth, month, year);
                dateButton.setText(date);
            }
        };

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_DARK;

        datePickerDialog = new DatePickerDialog(getActivity(), style, dateSetListener, year, month, day);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

    }

    private String getTodaysDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        month=month+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }

    private String makeDateString(int day, int month, int year){
        return getMonthFormatFromString(month) + " " + day + " " + year;
    }

    private String getMonthFormatFromString(int month){
        if(month==1) return "JAN";
        if(month==2) return "FEB";
        if(month==3) return "MAR";
        if(month==4) return "APR";
        if(month==5) return "MAY";
        if(month==6) return "JUN";
        if(month==7) return "JUL";
        if(month==8) return "AUG";
        if(month==9) return "SEP";
        if(month==10) return "OCT";
        if(month==11) return "NOV";
        if(month==12) return "DEC";

        return "JAN";
    }

    private int getMonthFormatFromInt(String month){
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

    public boolean isServicesOk(){
        Log.d(TAG, "isServicesOk: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getActivity());
        if(available == ConnectionResult.SUCCESS){
            Log.d(TAG, "isServicesOk: google play services is working");
            return true;
        }
        else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Log.d(TAG, "an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        else{
            Toast.makeText(getActivity(), "you can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
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

}
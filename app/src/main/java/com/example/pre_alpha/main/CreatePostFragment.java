package com.example.pre_alpha.main;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import static com.example.pre_alpha.models.FBref.refPosts;

import android.Manifest;
import android.app.Activity;
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
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.example.pre_alpha.R;
import com.example.pre_alpha.models.Post;
import com.example.pre_alpha.models.FBref;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
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


    //request codes
    static final int ERROR_DIALOG_REQUEST= 9001;
    private static final String TAG = "CreatePostFragment";
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;

    //type of items
    String[] items={"ארנק", "תיק", "סמארטפון", "משקפי ראייה / שמש","שעון חכם", "מצלמה" ,"תעודת זהות / דרכון", "רישיון נהיגה", "חוגר / חוגרון", "כרטיס רב קו", "כרטיסים כללי", "מפתחות בית / רכב", "שקית / שקית קניות", "אוזניות / קייס אוזניות", "שרשרת / תיליון", "צמיד", "טבעת", "תפילין", "מחשב נייד", "מטען", "כרטיס זיכרון / דיסק און קיי", "מעיל / סווטשירט", "קיטבג / מזוודה", "מכשיר שמיעה", "אחר"};
    String[] cameraPermissions;
    String[] storagePermissions;
    // private static because I want them to be saved when I leave the fragment
    private static String name="", item="", postId, checkState, about="", date=getTodaysDate(), address="";
    private static double latitude=0, longitude=0;
    private static int radius=3;
    public static boolean editMyPost=true;
    private static Uri image_uri;
    TextView tvState, tvRadius, addressTv;
    TextInputEditText etName, etAbout;
    ImageView mapIv, checkPickLocation, image;
    DatePickerDialog datePickerDialog;
    Slider radiusSlider;
    Button dateButton, upload, btnUpload, resetImage;
    TextInputLayout layoutItem;
    AutoCompleteTextView pickItem;
    ArrayAdapter<String> adapterItems;
    FirebaseAuth auth;
    Dialog dialog;
    Post post;
    FirebaseUser fbUser;
    BottomNavigationView bottomNavigationView;
    MyPostsFragment myPostsFragment = new MyPostsFragment();
    String storagePath = "Users_Posts_Images/";
    StorageReference storageReference;
    boolean result, result1, result2;
    FragmentActivity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post, container, false);
        //init
        tvState=view.findViewById(R.id.message_state);
        etName = view.findViewById(R.id.name);
        mapIv=view.findViewById(R.id.select_location);
        addressTv=view.findViewById(R.id.picked_address);
        radiusSlider=view.findViewById(R.id.radius_slider);
        adapterItems = new ArrayAdapter<>(getActivity(),R.layout.list_item,items);
        pickItem = view.findViewById(R.id.list_of_items);
        resetImage=view.findViewById(R.id.reset_image);
        layoutItem = view.findViewById(R.id.item);
        checkPickLocation=view.findViewById(R.id.pick_location_check);
        dateButton=view.findViewById(R.id.datePickerButton);
        tvRadius=view.findViewById(R.id.radius_value);
        upload=view.findViewById(R.id.upload);
        image=view.findViewById(R.id.selectImage);

        dateButton.setText(getTodaysDate());
        pickItem.setAdapter(adapterItems);
        SharedPreferences state = getActivity().getSharedPreferences("state", MODE_PRIVATE);
        checkState = state.getString("state", "");
        //check if the user want to create a lost or found post
        if(checkState.equals("lost"))
            tvState.setText("מה איבדת?");
        else if(checkState.equals("found"))
            tvState.setText("מה מצאת?");
        else {
            tvState.setText("עריכת מודעה");
            upload.setText("שמור שינויים");
        }
        storageReference = FirebaseStorage.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        fbUser = auth.getCurrentUser();
        bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationBar);
        etAbout=view.findViewById(R.id.about);

        //check which permissions is needed according to the version of the phone
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            cameraPermissions = new String[]{Manifest.permission.CAMERA};
            storagePermissions = new String[]{Manifest.permission.READ_MEDIA_IMAGES};
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(getArguments()!=null){
            //applying the necessary attributes of the post the user wants to edit
            if(editMyPost){
                postId=getArguments().getString("state_post_id", "");
                name=getArguments().getString("state_name", "");
                item=getArguments().getString("state_item", "");
                date=getArguments().getString("state_date", getTodaysDate());
                radius=getArguments().getInt("state_radius", 3);
                image_uri=Uri.parse(getArguments().getString("state_image", ""));
                about=getArguments().getString("state_about", "");
                editMyPost=false;
            }
            address=getArguments().getString("state_address", "");
            etName.setText(name);
            pickItem.setText(item);
            dateButton.setText(date);
            etAbout.setText(about);
            addressTv.setText("כתובת: " + address);
            if(image_uri!=null && !image_uri.toString().isEmpty()){
                Glide.with(getActivity())
                        .load(image_uri)  // Pass the URI to load the image from
                        .into(image);
            }
            radiusSlider.setValue(radius);
            tvRadius.setText(Integer.toString(radius));
        }

        //checking if the services that run the map are working fine
        if(isServicesOk()){
            initMap();
        }
        initListeners(); //initializing the listeners
        initDatePicker(); //initializing the date picker options along with the details it comprising
    }

    @Override
    public void onPause() {
        super.onPause();

        //get the values in order to the applications to remember them
        name = etName.getText().toString();
        item = pickItem.getText().toString();
        radius = (int) radiusSlider.getValue();
        about = etAbout.getText().toString();
        date = dateButton.getText().toString();
        address = addressTv.getText().toString();
    }

    private void initMap(){
        if(this.getArguments()!=null) {
            latitude = this.getArguments().getDouble("latitude", 0);
            longitude = this.getArguments().getDouble("longitude", 0);
            if(latitude!=0 && longitude!=0)  checkPickLocation.setImageResource(R.drawable.baseline_library_add_check_24); //if the landmark isn't (0,0), then it applies the "location was added" icon
        }

        mapIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapFragment mapFragment = new MapFragment();
                Bundle bundle = new Bundle();
                //sending the attributes so that the application will get them and doesn't forgets
                bundle.putString("from_where", "create_post"); //state from where the user is moving to the map fragment from (and operates in accordance)
                bundle.putString("state_name", etName.getText().toString());
                bundle.putString("state_item", pickItem.getText().toString());
                bundle.putString("state_date", dateButton.getText().toString());
                bundle.putString("state_about", etAbout.getText().toString());
                bundle.putInt("state_radius", (int) radiusSlider.getValue());

                mapFragment.setArguments(bundle);
                getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, mapFragment).commit(); //move to map fragment
            }
        });

    }

    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        this.activity = (FragmentActivity) activity; //applying the current activity to the attribute "activity"
    }

    private void initListeners(){
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show(); //open up the date picker dialog that enables the user to pick a date
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageDialog(); //gallery or camera dialog
            }
        });

        resetImage.setOnClickListener(new View.OnClickListener() { // resetting the image the user picked (if he picked something)
            @Override
            public void onClick(View v) {
                image_uri=null;
                image.setImageResource(R.drawable.baseline_image_24);
            }
        });

        radiusSlider.addOnChangeListener(new Slider.OnChangeListener() { //applying the radius value to the textview above it
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                tvRadius.setText(Integer.toString((int) value));
            }
        });

        upload.setOnClickListener(new View.OnClickListener() { //preparations before uploading the post
            @Override
            public void onClick(View v) {
                if(postId==null || postId.isEmpty())
                    postId = refPosts.push().getKey(); //getting the postId
                String filePathAndName = storagePath + "image" + "_" + postId;
                if(image_uri!=null && !image_uri.toString().isEmpty()) {
                    storageReference.child(filePathAndName).putFile(image_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageReference.child(filePathAndName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    image_uri=uri;
                                    updateDatabase();
                                }
                            });
                        }
                    });
                }

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

        //checking limitations
        if(item.isEmpty())
            layoutItem.setHelperText("יש לבחור את סוג החפץ");

        if(latitude==0 && longitude==0)
            Toast.makeText(getActivity(),"יש לבחור את מיקום המציאה/אבידה", Toast.LENGTH_SHORT).show();

        if((latitude!=0 && longitude!=0) && !item.isEmpty() && (!name.isEmpty() && name.length()<=30) && about.length()<=150) {
            if (image_uri != null) { //creating the object post accordance to if the image is null or not
                post = new Post(name, item, latitude, longitude, address, radius, about, image_uri.toString(), checkState, fbUser.getUid(), postId, calender.getTimeInMillis());
            } else {
                post = new Post(name, item, latitude, longitude, address, radius, about, "", checkState, fbUser.getUid(), postId, calender.getTimeInMillis());
            }
            refPosts.child(postId).setValue(post).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) { //showing dialog that saying that the post has been uploaded successfully
                    if (activity != null) {
                        dialog = new Dialog(activity);
                        dialog.setContentView(R.layout.upload_post_dialog_layout);
                        btnUpload = dialog.findViewById(R.id.uploadSuccessfully);
                        btnUpload.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View view) {
                                image_uri=null;
                                image.setImageResource(R.drawable.baseline_image_24);
                                dialog.cancel();
                                if (!checkState.equals("edit")){
                                    bottomNavigationView.setSelectedItemId(R.id.home);
                                }
                                else {
                                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, myPostsFragment).commit();
                                }
                                bottomNavigationView.setItemIconTintList(null);
                            }
                        });
                        dialog.show();
                    } else {
                        Log.e("CreatePostFragment", "Activity is null");
                    }
                }
            });
        }
    }


    private void initDatePicker(){
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month=month+1; //months Integer are indexes
                String date = makeDateString(dayOfMonth, month, year);
                dateButton.setText(date); //applying the date to the button
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

    private static String getTodaysDate() { //getting today's date
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        month=month+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }

    private static String makeDateString(int day, int month, int year){
        return getMonthFormatFromString(month) + " " + day + " " + year;
    }

    private static String getMonthFormatFromString(int month){ //converting the month's number into abbreviation
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

    private int getMonthFormatFromInt(String month){ //converting the month's abbreviation into number
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

    public boolean isServicesOk(){ //checking if the services that running the map are working
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
                if(which==0){ //picked camera
                    if(!checkCameraPermission()){ //if the camera permissions aren't granted already the system request to the user to apply them, but if they do the system move to the camera screen
                        requestCameraPermission();
                    }
                    else pickFromCamera();
                }
                if(which==1){ //picked gallery
                    if(!checkStoragePermission()){ //if the gallery permissions aren't granted already the system requests to the user to apply them, but if they do the system move to the gallery screen
                        requestStoragePermission();
                    }
                    else pickFromGallery();
                }
            }
        });
        builder.show();
    }

    private boolean checkStoragePermission() { //checking if the gallery permissions are granted
        boolean result1 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_MEDIA_IMAGES)
                == (PackageManager.PERMISSION_GRANTED);
        boolean result2 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result1||result2;

    }
    private void requestStoragePermission(){ //requesting gallery permissions
        ActivityCompat.requestPermissions(getActivity(),storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission() { //checking if the camera permissions are granted
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
    private void requestCameraPermission(){ //requesting camera permissions
        ActivityCompat.requestPermissions(getActivity(),cameraPermissions, CAMERA_REQUEST_CODE);
    }

    private void pickFromCamera() { //moving to camera's screen
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");
        image_uri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values); //applying the image's uri to the attribute image_uri
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE); //calling the onActivityResult function with the code that indicates if all is working fine and with the image uri that inside the cameraIntent among another thing
    }
    private void pickFromGallery() { //moving to gallery's screen
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {//applying the picked image to the imageview
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
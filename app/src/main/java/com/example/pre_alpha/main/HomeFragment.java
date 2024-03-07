package com.example.pre_alpha.main;

import static android.content.Context.MODE_PRIVATE;
import static com.example.pre_alpha.models.FBref.refPosts;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.pre_alpha.R;
import com.example.pre_alpha.chat.ChatActivity;
import com.example.pre_alpha.models.Post;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;


public class HomeFragment extends Fragment {

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final float DEFAULT_ZOOM = 15f;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final String TAG = "MainActivity";

    boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    double myLatitude, myLongitude, chosenLatitude, chosenLongitude;
    Button showChats;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        showChats=view.findViewById(R.id.show_chats);
        getLocationPermission();


        return view;
    }

    private void init(){
        Log.d(TAG, "init: initializing");

        showChats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                SharedPreferences chat = getActivity().getSharedPreferences("chat_pick", MODE_PRIVATE);
                SharedPreferences.Editor editor = chat.edit();
                editor.putString("chat_pick", "see chats");
                editor.apply();
                editor.commit();
                startActivity(intent);
            }
        });

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                DetailedPostFragment detailedPostFragment = new DetailedPostFragment();
                Bundle bundle = new Bundle();
                bundle.putString("post_id", marker.getTag().toString());
                bundle.putString("from_home_or_search", "home");
                detailedPostFragment.setArguments(bundle);
                getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, detailedPostFragment).commit();
            }

            @Override
            public void onMarkerDrag(Marker marker) {
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
            }
        });


        refPosts.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    Post postTmp = data.getValue(Post.class);
                    if (postTmp != null) {
                        if(postTmp.getLostOrFound().equals("lost"))
                            createLostMarker(new LatLng(postTmp.getLatitude(), postTmp.getLongitude()), postTmp.getItem(), postTmp.getPostId());
                        else
                            createFoundMarker(new LatLng(postTmp.getLatitude(), postTmp.getLongitude()), postTmp.getItem(), postTmp.getPostId());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", error.getMessage());
            }
        });

    }



    private void initMap() {
        SupportMapFragment map = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        map.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                Toast.makeText(getActivity(), "המפה מוכנה", Toast.LENGTH_SHORT).show();
                mMap = googleMap;

                if (mLocationPermissionsGranted) {
                    getDeviceLocation();

                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    mMap.setMyLocationEnabled(true);

                    init();

                }
            }
        });
    }



    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getting the device's current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        try {
            Task location = mFusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        Log.d(TAG, "onComplete: found location");
                        Location currentLocation = (Location) task.getResult();
                        myLatitude=currentLocation.getLatitude();
                        myLongitude=currentLocation.getLongitude();

                        moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM, "My Location");
                    }
                    else{
                        Log.d(TAG, "onComplete: current location is null");
                        Toast.makeText(getActivity(), "לא מסוגל להשיג מיקום נוכחי", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());

        }
    }

    private void moveCamera(LatLng latLng, float zoom, String title){
        Log.d(TAG, "moveCamera: moving camera to lat: " + latLng.latitude + ", lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private void createLostMarker(LatLng latLng, String title, String postId) {
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title(title)
                .draggable(true);
        Marker marker = mMap.addMarker(markerOptions);
        marker.setTag(postId);
    }

    private void createFoundMarker(LatLng latLng, String title, String postId) {
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title(title)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .draggable(true);

        Marker marker = mMap.addMarker(markerOptions);
        marker.setTag(postId);
    }

    private void getLocationPermission(){
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if(ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted=true;
                initMap();
            }
            else{
                ActivityCompat.requestPermissions(getActivity(), permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        }
        else{
            ActivityCompat.requestPermissions(getActivity(), permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("MapActivity", "getLocationPermissions: getting location permissions");
        mLocationPermissionsGranted = false;
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            mLocationPermissionsGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionsGranted = true;
                    initMap();
                }
            }
        }
    }
}
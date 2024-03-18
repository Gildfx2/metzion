package com.example.pre_alpha.main;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.pre_alpha.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.List;
import java.util.Locale;


public class MapFragment extends Fragment {

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final float DEFAULT_ZOOM = 15f;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final String TAG = "MapActivity";

    boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    String returnTo;
    TextView addressTv;
    Button applyBtn;
    Switch myPosition;
    double myLatitude, myLongitude, chosenLatitude, chosenLongitude;
    CreatePostFragment createPostFragment = new CreatePostFragment();
    FilterFragment filterFragment = new FilterFragment();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        addressTv=view.findViewById(R.id.addressTv);
        applyBtn=view.findViewById(R.id.applyBtn);
        myPosition=view.findViewById(R.id.my_location);
        returnTo = getArguments().getString("from_where", "");
        getLocationPermission();
        return view;
    }

    private void init(){
        Log.d(TAG, "init: initializing");

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                mMap.clear();
                chosenLatitude=latLng.latitude;
                chosenLongitude=latLng.longitude;
                moveCamera(new LatLng(chosenLatitude, chosenLongitude), DEFAULT_ZOOM, "Chosen Location");
            }
        });

        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                if(myPosition.isChecked()){
                    bundle.putDouble("latitude", myLatitude);
                    bundle.putDouble("longitude", myLongitude);
                }
                else{
                    bundle.putDouble("latitude", chosenLatitude);
                    bundle.putDouble("longitude", chosenLongitude);
                }
                if(returnTo.equals("create_post")){
                    String name = getArguments().getString("state_name", "");
                    String item = getArguments().getString("state_item", "");
                    String date = getArguments().getString("state_date", "");
                    String about = getArguments().getString("state_about", "");
                    int radius = getArguments().getInt("state_radius", 3);
                    bundle.putString("state_name", name);
                    bundle.putString("state_item", item);
                    bundle.putString("state_date", date);
                    bundle.putString("state_about", about);
                    bundle.putInt("state_radius", radius);
                    createPostFragment.setArguments(bundle);
                    getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, createPostFragment).commit();
                }
                if(returnTo.equals("filter_posts")){
                    String lostOrFound = getArguments().getString("lost_or_found", "");
                    String item = getArguments().getString("state_item", "");
                    String dateFrom = getArguments().getString("state_date_from", "");
                    String dateTo = getArguments().getString("state_date_to", "");
                    bundle.putString("lost_or_found", lostOrFound);
                    bundle.putString("state_item", item);
                    bundle.putString("state_date_from", dateFrom);
                    bundle.putString("state_date_to", dateTo);
                    filterFragment.setArguments(bundle);
                    getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, filterFragment).commit();
                }

            }
        });




    }

    private void getAddressAsync(double latitude, double longitude){
        new AsyncTask<Double, Void, String>() {
            @Override
            protected String doInBackground(Double... params) {
                String address = "";
                Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocation(params[0], params[1], 1);
                    if (addresses != null && addresses.size() > 0) {
                        address = addresses.get(0).getAddressLine(0);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "getAddress: " + e.getMessage());
                }
                return address;
            }

            @Override
            protected void onPostExecute(String address) {
                super.onPostExecute(address);
                // Handle the obtained address here
                if (!address.isEmpty()) {
                    moveCamera(new LatLng(latitude, longitude), DEFAULT_ZOOM, "Chosen Location");
                    chosenLatitude=latitude;
                    chosenLongitude=longitude;
                    addressTv.setText(address);
                }
            }
        }.execute(latitude, longitude);
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

        if(!title.equals("My Location")){
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title);
            mMap.addMarker(options);
        }

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
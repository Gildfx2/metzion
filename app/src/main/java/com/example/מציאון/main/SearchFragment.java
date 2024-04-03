package com.example.מציאון.main;

import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.מציאון.R;
import com.example.מציאון.adapters.PostAdapter;
import com.example.מציאון.adapters.PostData;
import com.example.מציאון.databinding.FragmentSearchBinding;
import com.example.מציאון.models.Post;
import com.example.מציאון.models.FBref;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class SearchFragment extends Fragment {
    private static final String TAG = "SearchFragment";
    ArrayList<Post> postValues = new ArrayList<Post>();
    PostAdapter postAdapter;
    ArrayList<PostData> arrayList = new ArrayList<>();
    PostData postData;
    Uri image_uri= Uri.parse("");
    FragmentSearchBinding binding;
    DetailedPostFragment detailedPostFragment = new DetailedPostFragment();
    ValueEventListener postListener;
    Button filter;
    FilterFragment filterFragment=new FilterFragment();
    String lostOrFound, item, fromDate, toDate;
    double longitude, latitude;
    Calendar postDateCalendar, fromDateCalendar, toDateCalendar;
    int month, dayOfMonth, year;
    Location postLocation, filterLocation;
    Bundle bundle;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        init();

        filter=binding.getRoot().findViewById(R.id.filter);
        postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dS) {
                postValues.clear();
                for (DataSnapshot data : dS.getChildren()) {
                    Post postTmp = data.getValue(Post.class);
                    if (postTmp != null && isValidPost(postTmp)) {
                        postValues.add(postTmp);
                    }
                }
                Collections.sort(postValues, (post1, post2) -> Long.compare(post2.getTimeStamp(), post1.getTimeStamp()));
                showPosts();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("FirebaseError", error.getMessage());
            }
        };
        FBref.refPosts.addValueEventListener(postListener);

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterFragment.setArguments(getArguments());
                getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, filterFragment).commit();
            }
        });

        return binding.getRoot();
    }

    private void init(){
        bundle = new Bundle(getArguments());
        lostOrFound = getArguments().getString("lost_or_found", "");
        item = getArguments().getString("state_item", "");
        fromDate = getArguments().getString("state_date_from");
        toDate = getArguments().getString("state_date_to");
        latitude = getArguments().getDouble("latitude", 0);
        longitude = getArguments().getDouble("longitude", 0);
        String[] fromDateComponents = fromDate.split(" ");
        month = getMonthFormat(fromDateComponents[0]) - 1; // Calendar months are 0-based
        dayOfMonth = Integer.parseInt(fromDateComponents[1]);
        year = Integer.parseInt(fromDateComponents[2]);
        fromDateCalendar = Calendar.getInstance();
        fromDateCalendar.clear();
        fromDateCalendar.set(Calendar.YEAR, year);
        fromDateCalendar.set(Calendar.MONTH, month);
        fromDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String[] toDateComponents = toDate.split(" ");
        month = getMonthFormat(toDateComponents[0]) - 1; // Calendar months are 0-based
        dayOfMonth = Integer.parseInt(toDateComponents[1]);
        year = Integer.parseInt(toDateComponents[2]);
        toDateCalendar = Calendar.getInstance();
        toDateCalendar.clear();
        toDateCalendar.set(Calendar.YEAR, year);
        toDateCalendar.set(Calendar.MONTH, month);
        toDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        postDateCalendar = Calendar.getInstance();
        filterLocation = new Location("filter_location");
        postLocation = new Location("post_location");
        filterLocation.setLatitude(latitude);
        filterLocation.setLongitude(longitude);
        filterLocation = new Location("filter_location");
        postLocation = new Location("post_location");
        filterLocation.setLatitude(latitude);
        filterLocation.setLongitude(longitude);
    }

    private boolean isValidPost(Post postTmp) {
        postDateCalendar.setTimeInMillis(postTmp.getTimeStamp());
        postLocation.setLatitude(postTmp.getLatitude());
        postLocation.setLongitude(postTmp.getLongitude());

        if (!postTmp.getLostOrFound().equals(lostOrFound)) {
            Log.d(TAG, "isValidPost: the state is different");
            return false;
        }

        if (postDateCalendar.compareTo(fromDateCalendar) < 0 || postDateCalendar.compareTo(toDateCalendar) > 0) {
            Log.d(TAG, "isValidPost: the date is not in the range");
            Log.d(TAG, "post:" + postDateCalendar.getTimeInMillis() + "from:" + fromDateCalendar.getTimeInMillis() + "to:" + toDateCalendar.getTimeInMillis());
            return false;
        }

        if (!item.isEmpty() && !item.equals(postTmp.getItem())) {
            Log.d(TAG, "isValidPost: the item is different");
            return false;
        }

        if (latitude != 0 && longitude != 0 && !distanceIsAccepted(filterLocation, postLocation, postTmp.getRadius())) {
            Log.d(TAG, "isValidPost: the distance is above the given radius");
            return false;
        }

        return true;
    }
    private boolean distanceIsAccepted(Location location1, Location location2, int radius){
        if((location1.distanceTo(location2)/1000)<=radius) return true;
        return false;
    }

    @Override
    public void onPause() {
        super.onPause();
        arrayList.clear();
        if (postListener != null) {
            FBref.refPosts.removeEventListener(postListener);
        }
    }

    private void showPosts() {
        for (Post postValue : postValues) {
            if (postValue.getImage() != null) {
                image_uri = Uri.parse(postValue.getImage());
            }
            postData = new PostData(postValue.getName(), postValue.getItem(), image_uri, postValue.getAbout(), postValue.getCreatorUid(), postValue.getPostId(), postValue.getTimeStamp());
            arrayList.add(postData);
        }
        postAdapter = new PostAdapter(getActivity(), arrayList);
        binding.listOfPosts.setAdapter(postAdapter);
        binding.listOfPosts.setClickable(true);
        binding.listOfPosts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                bundle.putString("post_id", arrayList.get(position).getPostId());
                bundle.putString("from_home_or_search", "search");
                detailedPostFragment.setArguments(bundle);
                getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, detailedPostFragment).commit();
            }
        });
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
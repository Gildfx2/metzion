package com.example.pre_alpha;

import static android.content.Context.MODE_PRIVATE;

import static com.example.pre_alpha.FBref.FBDB;
import static com.example.pre_alpha.FBref.refUsers;
import static com.example.pre_alpha.FBref.refPosts;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.renderscript.ScriptGroup;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputBinding;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.bumptech.glide.Glide;
import com.example.pre_alpha.databinding.ActivityMainBinding;
import com.example.pre_alpha.databinding.FragmentListPostBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListPostFragment extends Fragment {
    ArrayList<Post> foundValues = new ArrayList<Post>();
    ArrayList<Post> lostValues = new ArrayList<Post>();
    ListAdapter listAdapter;
    ArrayList<ListData> arrayList = new ArrayList<>();
    ListData listData;
    Uri image_uri;
    FragmentListPostBinding binding;
    DetailedPostFragment detailedPostFragment = new DetailedPostFragment();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentListPostBinding.inflate(inflater, container, false);
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dS) {
                foundValues.clear();
                lostValues.clear();
                for (DataSnapshot data : dS.getChildren()) {
                    Post postTmp = data.getValue(Post.class);
                    if(postTmp!=null){
                        if(postTmp.getLostOrFound().equals("found")) foundValues.add(postTmp);
                        else lostValues.add(postTmp);
                    }
                }
                showPosts();
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("FirebaseError", error.getMessage());
            }
        };
        refPosts.addValueEventListener(postListener);

        return binding.getRoot();
    }

    private void showPosts() {
        SharedPreferences listState = getActivity().getSharedPreferences("list_state", MODE_PRIVATE);
        String checkListState = listState.getString("list_state", "");
        if (checkListState.equals("found")) {
            for (Post foundValue : foundValues) {
                if (!foundValue.getImage().isEmpty()) {
                    image_uri = Uri.parse(foundValue.getImage());
                    listData = new ListData(foundValue.getName(), foundValue.getArea(), foundValue.getItem(), image_uri, foundValue.getAbout());
                } else
                    listData = new ListData(foundValue.getName(), foundValue.getArea(), foundValue.getItem(), null, foundValue.getAbout());
                arrayList.add(listData);
            }
        } else {
            for (Post lostValue : lostValues) {
                if (lostValue.getImage() != null) {
                    image_uri = Uri.parse(lostValue.getImage());
                    listData = new ListData(lostValue.getName(), lostValue.getArea(), lostValue.getItem(), image_uri, lostValue.getAbout());
                } else
                    listData = new ListData(lostValue.getName(), lostValue.getArea(), lostValue.getItem(), null, lostValue.getAbout());
                arrayList.add(listData);
            }
        }
        listAdapter = new ListAdapter(getActivity(), arrayList);
        binding.listOfPosts.setAdapter(listAdapter);
        binding.listOfPosts.setClickable(true);
        binding.listOfPosts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString("name", arrayList.get(position).name.toString());
                bundle.putString("item", arrayList.get(position).item.toString());
                bundle.putString("area", arrayList.get(position).area.toString());
                bundle.putString("about", arrayList.get(position).about.toString());
                if(arrayList.get(position)!=null && arrayList.get(position).image!=null)
                    bundle.putString("image", arrayList.get(position).image.toString());
                else bundle.putString("image", "");
                detailedPostFragment.setArguments(bundle);
                getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, detailedPostFragment).commit();
            }
        });
    }

    @Override
    public void onPause() {
        arrayList.clear();
        super.onPause();
    }
}
package com.example.pre_alpha.main;

import static android.content.Context.MODE_PRIVATE;
import static com.example.pre_alpha.models.FBref.refPosts;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.pre_alpha.R;
import com.example.pre_alpha.adapters.PostAdapter;
import com.example.pre_alpha.adapters.PostData;
import com.example.pre_alpha.databinding.FragmentSearchBinding;
import com.example.pre_alpha.models.Post;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SearchFragment extends Fragment {
    ArrayList<Post> foundValues = new ArrayList<Post>();
    ArrayList<Post> lostValues = new ArrayList<Post>();
    PostAdapter postAdapter;
    ArrayList<PostData> arrayList = new ArrayList<>();
    PostData postData;
    Uri image_uri= Uri.parse("");
    FragmentSearchBinding binding;
    DetailedPostFragment detailedPostFragment = new DetailedPostFragment();
    ValueEventListener postListener;
    Button filter;
    FilterFragment filterFragment=new FilterFragment();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);

        filter=binding.getRoot().findViewById(R.id.filter);
        postListener = new ValueEventListener() {
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
                Collections.sort(foundValues, new Comparator<Post>() {
                    @Override
                    public int compare(Post post1, Post post2) {
                        return Long.compare(post2.getTimeStamp(), post1.getTimeStamp());
                    }
                });
                Collections.sort(lostValues, new Comparator<Post>() {
                    @Override
                    public int compare(Post post1, Post post2) {
                        return Long.compare(post2.getTimeStamp(), post1.getTimeStamp());
                    }
                });
                showPosts();
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("FirebaseError", error.getMessage());
            }
        };
        refPosts.addValueEventListener(postListener);
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, filterFragment).commit();
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onPause() {
        super.onPause();
        arrayList.clear();
        if (postListener != null) {
            refPosts.removeEventListener(postListener);
        }
    }

    private void showPosts() {
        SharedPreferences listState = getActivity().getSharedPreferences("list_state", MODE_PRIVATE);
        String checkListState = listState.getString("list_state", "");
        if (checkListState.equals("found")) {
            for (Post foundValue : foundValues) {
                if (foundValue.getImage() != null) {
                    image_uri = Uri.parse(foundValue.getImage());
                }
                postData = new PostData(foundValue.getName(), foundValue.getItem(), image_uri, foundValue.getAbout(), foundValue.getCreatorUid(), foundValue.getPostId(), foundValue.getTimeStamp());
                arrayList.add(postData);
            }
        } else {
            for (Post lostValue : lostValues) {
                if (lostValue.getImage() != null) {
                    image_uri = Uri.parse(lostValue.getImage());
                }
                postData = new PostData(lostValue.getName(), lostValue.getItem(), image_uri, lostValue.getAbout(), lostValue.getCreatorUid(), lostValue.getPostId(), lostValue.getTimeStamp());
                arrayList.add(postData);
            }
        }
        postAdapter = new PostAdapter(getActivity(), arrayList);
        binding.listOfPosts.setAdapter(postAdapter);
        binding.listOfPosts.setClickable(true);
        binding.listOfPosts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences chatOrList = getActivity().getSharedPreferences("chat_or_list", MODE_PRIVATE);
                SharedPreferences.Editor editor = chatOrList.edit();
                editor.putString("chat_or_list", "list");
                editor.apply();
                editor.commit();
                Bundle bundle = new Bundle();
                bundle.putString("post_id", arrayList.get(position).getPostId());
                detailedPostFragment.setArguments(bundle);
                getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, detailedPostFragment).commit();
            }
        });
    }

}
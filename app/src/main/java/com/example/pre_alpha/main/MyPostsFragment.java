package com.example.pre_alpha.main;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.pre_alpha.models.FBref;
import com.example.pre_alpha.R;
import com.example.pre_alpha.adapters.PostData;
import com.example.pre_alpha.adapters.MyPostsAdapter;
import com.example.pre_alpha.databinding.FragmentMyPostsBinding;
import com.example.pre_alpha.models.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class MyPostsFragment extends Fragment {

    ArrayList<Post> postValues = new ArrayList<Post>();
    PostData postData;
    ArrayList<PostData> arrayList = new ArrayList<>();
    MyPostsAdapter myPostsAdapter;
    Uri image_uri= Uri.parse("");
    FragmentMyPostsBinding binding;
    FirebaseUser fbUser;
    DetailedPostFragment detailedPostFragment = new DetailedPostFragment();
    ValueEventListener postListener;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMyPostsBinding.inflate(inflater, container, false);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        fbUser = auth.getCurrentUser();

        //getting the current user's posts and sorting them in order of the item that has been lost/found
        postListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postValues.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Post postTmp = data.getValue(Post.class);
                    if (postTmp != null && postTmp.getCreatorUid().equals(fbUser.getUid())) {
                        postValues.add(postTmp);
                    }
                }
                Collections.sort(postValues, (post1, post2) -> Long.compare(post2.getTimeStamp(), post1.getTimeStamp()));
                showPosts();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", error.getMessage());
            }
        };
        FBref.refPosts.addValueEventListener(postListener);

        return binding.getRoot();
    }

    private void showPosts() {
        arrayList.clear();
        for (Post postValue : postValues) {
            if (postValue.getImage() != null) {
                image_uri = Uri.parse(postValue.getImage());
            }
            //init the posts attributes into postData object
            postData = new PostData(postValue.getName(), postValue.getItem(), image_uri, postValue.getPostId());
            arrayList.add(postData);
        }
        //creating the adapter that shows the posts
        myPostsAdapter = new MyPostsAdapter(getActivity(), arrayList, getActivity().getSupportFragmentManager());
        binding.listOfCreatedPosts.setAdapter(myPostsAdapter);
        binding.listOfCreatedPosts.setClickable(true);
        binding.listOfCreatedPosts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString("post_id", arrayList.get(position).getPostId());
                bundle.putString("from_map_search_myPosts_chat", "my_posts");
                detailedPostFragment.setArguments(bundle);
                getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, detailedPostFragment).commit();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (postListener != null) {
            FBref.refPosts.removeEventListener(postListener);
        }
    }
}
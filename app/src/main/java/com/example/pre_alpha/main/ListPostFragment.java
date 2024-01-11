package com.example.pre_alpha.main;

import static android.content.Context.MODE_PRIVATE;

import static com.example.pre_alpha.models.FBref.refPosts;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.pre_alpha.R;
import com.example.pre_alpha.adapters.PostAdapter;
import com.example.pre_alpha.databinding.FragmentListPostBinding;
import com.example.pre_alpha.models.Post;
import com.example.pre_alpha.adapters.PostData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListPostFragment extends Fragment {
    ArrayList<Post> foundValues = new ArrayList<Post>();
    ArrayList<Post> lostValues = new ArrayList<Post>();
    PostAdapter postAdapter;
    ArrayList<PostData> arrayList = new ArrayList<>();
    PostData postData;
    Uri image_uri= Uri.parse("");
    FragmentListPostBinding binding;
    DetailedPostFragment detailedPostFragment = new DetailedPostFragment();
    ValueEventListener postListener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentListPostBinding.inflate(inflater, container, false);
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
                    postData = new PostData(foundValue.getName(), foundValue.getArea(), foundValue.getItem(), image_uri, foundValue.getAbout(), foundValue.getCreatorUid(), foundValue.getPostId());
                } else
                    postData = new PostData(foundValue.getName(), foundValue.getArea(), foundValue.getItem(), image_uri, foundValue.getAbout(), foundValue.getCreatorUid(), foundValue.getPostId());
                arrayList.add(postData);
            }
        } else {
            for (Post lostValue : lostValues) {
                if (lostValue.getImage() != null) {
                    image_uri = Uri.parse(lostValue.getImage());
                    postData = new PostData(lostValue.getName(), lostValue.getArea(), lostValue.getItem(), image_uri, lostValue.getAbout(), lostValue.getCreatorUid(), lostValue.getPostId());
                } else
                    postData = new PostData(lostValue.getName(), lostValue.getArea(), lostValue.getItem(), image_uri, lostValue.getAbout(), lostValue.getCreatorUid(), lostValue.getPostId());
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
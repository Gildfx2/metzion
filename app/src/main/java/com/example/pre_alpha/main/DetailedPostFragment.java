package com.example.pre_alpha.main;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.pre_alpha.chat.ChatActivity;
import com.example.pre_alpha.models.FBref;
import com.example.pre_alpha.R;
import com.example.pre_alpha.models.Post;
import com.example.pre_alpha.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class DetailedPostFragment extends Fragment {
    TextView tvName, tvItem, tvAbout, tvDate, tvAddress;
    ImageView ivImage;
    ImageView returnBack;
    Button sendMessage;
    List<User> userValues = new ArrayList<>();
    List<Post> postValues = new ArrayList<>();
    FirebaseUser fbUser;
    Dialog dialog;
    Button btnOkay;
    long timeStamp;
    Calendar calendar;
    String name, item, about, image="", creatorUid, postId, address;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detailed_post, container, false);
        tvName=view.findViewById(R.id.post_name);
        tvItem=view.findViewById(R.id.post_item);
        tvAbout=view.findViewById(R.id.post_about);
        ivImage=view.findViewById(R.id.post_image);
        tvAddress=view.findViewById(R.id.post_address);
        returnBack=view.findViewById(R.id.return_back);
        sendMessage=view.findViewById(R.id.send_message);
        tvDate=view.findViewById(R.id.post_date);
        fbUser= FirebaseAuth.getInstance().getCurrentUser();
        Bundle bundle = this.getArguments();
        calendar = Calendar.getInstance();
        postId = bundle.getString("post_id");
        FBref.refPosts.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    Post postTmp = data.getValue(Post.class);
                    postValues.add(postTmp);
                }
                initialization();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", error.getMessage());
            }
        });
        FBref.refUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    User userTmp = data.getValue(User.class);
                    userValues.add(userTmp);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", error.getMessage());
            }
        });

        returnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getArguments().getString("from_home_or_search").equals("search")){
                    Bundle bundle = new Bundle(getArguments());
                    SearchFragment searchFragment = new SearchFragment();
                    searchFragment.setArguments(bundle);
                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.frameLayout, searchFragment);
                    transaction.commit();
                } else if (getArguments().getString("from_home_or_search").equals("home")) {
                    HomeFragment homeFragment = new HomeFragment();
                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.frameLayout, homeFragment);
                    transaction.commit();
                } else if (getArguments().getString("from_home_or_search").equals("my_posts")) {
                    MyPostsFragment  myPostsFragment= new MyPostsFragment();
                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.frameLayout, myPostsFragment);
                    transaction.commit();
                }


            }
        });
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(creatorUid!=null) {
                    if (creatorUid.equals(fbUser.getUid())) {
                        dialog = new Dialog(getActivity());
                        dialog.setContentView(R.layout.same_user_dialog);
                        btnOkay = dialog.findViewById(R.id.same_user);
                        btnOkay.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View view) {
                                dialog.cancel();
                            }
                        });
                        dialog.show();
                    } else {
                        moveToChatScreen();
                    }
                }
            }
        });
        return view;
    }

    private void moveToChatScreen(){
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("post_id", postId);
        bundle.putString("username", getUsernameFromUid(creatorUid));
        bundle.putString("other_user_uid", creatorUid);
        intent.putExtras(bundle);
        SharedPreferences chat = getActivity().getSharedPreferences("chat_pick", MODE_PRIVATE);
        SharedPreferences.Editor editor = chat.edit();
        editor.putString("chat_pick", "send message");
        editor.apply();
        editor.commit();
        startActivity(intent);
    }
    private void initialization() {
        for(Post post : postValues){
            if(post.getPostId().equals(postId)){
                name=post.getName();
                item=post.getItem();
                about=post.getAbout();
                creatorUid=post.getCreatorUid();
                address=post.getAddress();
                if(post.getImage()!=null)
                    image=post.getImage();
                timeStamp=post.getTimeStamp();
            }
        }
        tvName.setText(name);
        tvItem.setText("סוג החפץ: "+item);
        tvAbout.setText("פרטים:\n"+about);
        calendar.setTimeInMillis(timeStamp);
        tvDate.setText(formatDate(calendar));
        tvAddress.setText("כתובת: " + address);
        if(getActivity()!=null && !image.isEmpty())
            Glide.with(this)
                    .load(Uri.parse(image))
                    .into(ivImage);
    }
    private String formatDate(Calendar calendar) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = dateFormat.format(calendar.getTime());
        return dateString;
    }


    private String getUsernameFromUid(String uid){
        for(User user : userValues){
            if(user.getUid().equals(uid)) return user.getUsername();
        }
        return "אין שם";
    }
}
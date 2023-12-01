package com.example.pre_alpha.main;

import static android.content.Context.MODE_PRIVATE;

import static com.example.pre_alpha.models.FBref.refPosts;
import static com.example.pre_alpha.models.FBref.refUsers;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.pre_alpha.R;
import com.example.pre_alpha.chat.ChatActivity;
import com.example.pre_alpha.chat.ChatFragment;
import com.example.pre_alpha.models.Post;
import com.example.pre_alpha.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class DetailedPostFragment extends Fragment {
    TextView tvName, tvItem, tvArea, tvAbout;
    ImageView ivImage;
    ImageView returnBack;
    Button sendMessage;
    List<User> userValues = new ArrayList<>();
    List<Post> postValues = new ArrayList<>();
    FirebaseUser fbUser;
    Dialog dialog;
    Button btnOkay;
    String name, item, area, about, image="", creatorUid, postId;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detailed_post, container, false);
        tvName=view.findViewById(R.id.post_name);
        tvItem=view.findViewById(R.id.post_item);
        tvArea=view.findViewById(R.id.post_area);
        tvAbout=view.findViewById(R.id.post_about);
        ivImage=view.findViewById(R.id.post_image);
        returnBack=view.findViewById(R.id.return_back);
        sendMessage=view.findViewById(R.id.send_message);
        fbUser= FirebaseAuth.getInstance().getCurrentUser();
        Bundle bundle = this.getArguments();
        postId = bundle.getString("post_id");
        refPosts.addListenerForSingleValueEvent(new ValueEventListener() {
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
        refUsers.addListenerForSingleValueEvent(new ValueEventListener() {
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
                ListPostFragment listPostFragment = new ListPostFragment();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.frameLayout, listPostFragment);
                transaction.addToBackStack(null);
                transaction.commit();
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
        intent.putExtra("post_name", name);
        intent.putExtra("post_area", area);
        intent.putExtra("post_image", image);
        intent.putExtra("creator_uid", creatorUid);
        intent.putExtra("post_id", postId);
        intent.putExtra("username", getUsernameFromUid(creatorUid));
        intent.putExtra("other_user_uid", creatorUid);
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
                area=post.getArea();
                about=post.getAbout();
                creatorUid=post.getCreatorUid();
                if(post.getImage()!=null)
                    image=post.getImage();
            }
        }
        tvName.setText(name);
        tvItem.setText("שם החפץ: "+item);
        tvArea.setText("שם הישוב: "+area);
        tvAbout.setText("פרטים:\n"+about);
        if(getActivity()!=null && !image.isEmpty())
            Glide.with(this)
                    .load(Uri.parse(image))
                    .into(ivImage);
    }


    private String getUsernameFromUid(String uid){
        for(User user : userValues){
            if(user.getUid().equals(uid)) return user.getUsername();
        }
        return "אין שם";
    }
}
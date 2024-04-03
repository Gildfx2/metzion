package com.example.pre_alpha.chat;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pre_alpha.R;
import com.example.pre_alpha.models.Post;
import com.example.pre_alpha.models.FBref;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class ChatActivity extends AppCompatActivity {

    FirebaseAuth auth;
    String postId, username, otherUserUid, pick;
    FirebaseUser fbUser;
    SharedPreferences chat;
    Post post;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        auth=FirebaseAuth.getInstance();
        fbUser= auth.getCurrentUser();
        chat = getSharedPreferences("chat_pick", MODE_PRIVATE);
        pick = chat.getString("chat_pick", "");
        FBref.refUsers.child(fbUser.getUid()).child("status").setValue("online");
        if(pick.equals("send message")) {
            Bundle bundleExt = getIntent().getExtras();
            postId = bundleExt.getString("post_id");
            otherUserUid = bundleExt.getString("other_user_uid");
            username = bundleExt.getString("username");
            FBref.refPosts.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot ds : snapshot.getChildren()){
                        post = ds.getValue(Post.class);
                        if(post.getPostId().equals(postId)) break;
                    }
                    goToChat();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else if(pick.equals("see chats")){
            HomeChatFragment homeChatFragment = new HomeChatFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.chatFrameLayout, homeChatFragment).commit();
        }

    }

    private void goToChat(){
        ChatFragment chatFragment = new ChatFragment();
        Bundle bundle = new Bundle();
        bundle.putString("post_name", post.getName());
        bundle.putString("post_image", post.getImage());
        bundle.putString("creator_uid", post.getCreatorUid());
        bundle.putString("post_id", postId);
        bundle.putString("other_user_uid", otherUserUid);
        bundle.putString("username", username);
        chatFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.chatFrameLayout, chatFragment).commit();

    }

    @Override
    protected void onPause() {
        super.onPause();
        FBref.refUsers.child(fbUser.getUid()).child("status").setValue(String.valueOf(System.currentTimeMillis()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        currentUser();
    }

    private void currentUser() {
        SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
        editor.putString("currentuser", otherUserUid);
        editor.apply();
        editor.commit();
    }
}
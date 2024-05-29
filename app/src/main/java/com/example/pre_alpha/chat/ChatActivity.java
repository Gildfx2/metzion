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
    String postId, username, otherUserUid, pick, fromWhere;
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
        if(pick.equals("send message")) { //moving to chat from detailed post fragment (wants a specific chat)
            Bundle bundleExt = getIntent().getExtras();
            if(bundleExt!=null) {
                postId = bundleExt.getString("post_id");
                otherUserUid = bundleExt.getString("other_user_uid");
                username = bundleExt.getString("username");
                fromWhere = bundleExt.getString("from_post_or_chatlist");
            }
            FBref.refPosts.child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    post = snapshot.getValue(Post.class);
                    goToChat();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else if(pick.equals("see chats")){ //moving to chat from profile or home fragment
            HomeChatFragment homeChatFragment = new HomeChatFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.chatFrameLayout, homeChatFragment).commit();
        }

    }

    private void goToChat(){ //moving to the specific chat
        ChatFragment chatFragment = new ChatFragment();
        Bundle bundle = new Bundle();
        bundle.putString("post_name", post.getName());
        bundle.putString("post_image", post.getImage());
        bundle.putString("creator_uid", post.getCreatorUid());
        bundle.putString("post_id", postId);
        bundle.putString("other_user_uid", otherUserUid);
        bundle.putString("username", username);
        bundle.putString("from_post_or_chatlist", fromWhere);
        chatFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.chatFrameLayout, chatFragment).commit();

    }

    @Override
    protected void onPause() {
        super.onPause();
        FBref.refUsers.child(fbUser.getUid()).child("status").setValue(String.valueOf(System.currentTimeMillis())); //setting the status to the last seen time stamp
    }

}
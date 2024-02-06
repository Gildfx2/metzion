package com.example.pre_alpha.chat;

import static com.example.pre_alpha.models.FBref.refUsers;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pre_alpha.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class ChatActivity extends AppCompatActivity {

    FirebaseAuth auth;
    String postName, postArea, postImage, postId, creatorUid, username, otherUserUid, pick;
    FirebaseUser fbUser;
    ValueEventListener otherUserListener;
    SharedPreferences chat;
    static String otherUserStatus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        auth=FirebaseAuth.getInstance();
        fbUser= auth.getCurrentUser();
        chat = getSharedPreferences("chat_pick", MODE_PRIVATE);
        pick = chat.getString("chat_pick", "");
        refUsers.child(fbUser.getUid()).child("status").setValue("online");
        if(pick.equals("send message")) {
            postName = getIntent().getStringExtra("post_name");
            postArea = getIntent().getStringExtra("post_area");
            postImage = getIntent().getStringExtra("post_image");
            creatorUid = getIntent().getStringExtra("creator_uid");
            postId = getIntent().getStringExtra("post_id");
            username = getIntent().getStringExtra("username");
            otherUserUid = getIntent().getStringExtra("other_user_uid");

            ChatFragment chatFragment = new ChatFragment();
            Bundle bundle = new Bundle();
            bundle.putString("post_name", postName);
            bundle.putString("post_area", postArea);
            bundle.putString("post_image", postImage);
            bundle.putString("creator_uid", creatorUid);
            bundle.putString("post_id", postId);
            bundle.putString("username", username);
            bundle.putString("other_user_uid", otherUserUid);
            chatFragment.setArguments(bundle);
            otherUserListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    otherUserStatus = snapshot.getValue(String.class);
                    getSupportFragmentManager().beginTransaction().replace(R.id.chatFrameLayout, chatFragment).commit();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("FirebaseError", error.getMessage());
                }
            };
            refUsers.child(otherUserUid).child("status").addValueEventListener(otherUserListener);

        }
        else if(pick.equals("see chats")){
            HomeChatFragment homeChatFragment = new HomeChatFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.chatFrameLayout, homeChatFragment).commit();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        refUsers.child(fbUser.getUid()).child("status").setValue(String.valueOf(System.currentTimeMillis()));
        if (otherUserListener != null) {
            refUsers.removeEventListener(otherUserListener);
        }
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
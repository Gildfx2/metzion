package com.example.pre_alpha.chat;

import static com.example.pre_alpha.models.FBref.refUsers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.example.pre_alpha.R;
import com.example.pre_alpha.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class ChatActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser fbUser;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        auth=FirebaseAuth.getInstance();
        fbUser= auth.getCurrentUser();
        SharedPreferences chat = getSharedPreferences("chat_pick", MODE_PRIVATE);
        String pick = chat.getString("chat_pick", "");
        if(pick.equals("send message")) {
            String postName = getIntent().getStringExtra("post_name");
            String postArea = getIntent().getStringExtra("post_area");
            String postImage = getIntent().getStringExtra("post_image");
            String creatorUid = getIntent().getStringExtra("creator_uid");
            String postId = getIntent().getStringExtra("post_id");
            String username = getIntent().getStringExtra("username");
            String otherUserUid = getIntent().getStringExtra("other_user_uid");

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

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.chatFrameLayout, chatFragment)
                    .commit();
        }
        else if(pick.equals("see chats")){
            HomeChatFragment homeChatFragment = new HomeChatFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.chatFrameLayout, homeChatFragment)
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dS) {
                for (DataSnapshot data : dS.getChildren()) {
                    User userTmp = data.getValue(User.class);
                    if(userTmp.getUid().equals(fbUser.getUid())) {
                        user = new User(userTmp);
                        user.setStatus("online");
                        refUsers.child(fbUser.getUid()).setValue(user);
                        break;
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("FirebaseError", error.getMessage());
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        user.setStatus(String.valueOf(System.currentTimeMillis()));
        refUsers.child(fbUser.getUid()).setValue(user);
    }

}
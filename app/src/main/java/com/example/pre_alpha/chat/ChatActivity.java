package com.example.pre_alpha.chat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.pre_alpha.R;

public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        SharedPreferences chat = getSharedPreferences("chat_pick", MODE_PRIVATE);
        String pick = chat.getString("chat_pick", "");
        if(pick.equals("send message")) {
            String postName = getIntent().getStringExtra("post_name");
            String postArea = getIntent().getStringExtra("post_area");
            String postImage = getIntent().getStringExtra("post_image");
            String creatorUid = getIntent().getStringExtra("creator_uid");
            String postUid = getIntent().getStringExtra("post_uid");
            String username = getIntent().getStringExtra("username");
            String otherUserUid = getIntent().getStringExtra("other_user_uid");

            ChatFragment chatFragment = new ChatFragment();
            Bundle bundle = new Bundle();
            bundle.putString("post_name", postName);
            bundle.putString("post_area", postArea);
            bundle.putString("post_image", postImage);
            bundle.putString("creator_uid", creatorUid);
            bundle.putString("post_uid", postUid);
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

}
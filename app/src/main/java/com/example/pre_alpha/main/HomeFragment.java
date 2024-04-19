package com.example.pre_alpha.main;

import static android.content.Context.MODE_PRIVATE;

import static com.example.pre_alpha.models.FBref.refChatList;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pre_alpha.R;
import com.example.pre_alpha.chat.ChatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;


public class HomeFragment extends Fragment {
    Button createPost, searchPosts;
    BottomNavigationView bottomNavigationView;
    ImageView showChats;
    ValueEventListener chatsListener;
    TextView newMessagesTv;
    int newMessagesCount=0;
    FirebaseUser fbUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationBar);
        createPost=view.findViewById(R.id.create_post_button);
        searchPosts=view.findViewById(R.id.search_button);
        showChats=view.findViewById(R.id.show_chats);
        newMessagesTv=view.findViewById(R.id.new_messages);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        fbUser=auth.getCurrentUser();

        showChats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                SharedPreferences chat = getActivity().getSharedPreferences("chat_pick", MODE_PRIVATE);
                SharedPreferences.Editor editor = chat.edit();
                editor.putString("chat_pick", "see chats");
                editor.apply();
                editor.commit();
                startActivity(intent);
            }
        });
        createPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomNavigationView.setSelectedItemId(R.id.post);
                bottomNavigationView.setItemIconTintList(null);
            }
        });
        searchPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomNavigationView.setSelectedItemId(R.id.search);
                bottomNavigationView.setItemIconTintList(null);
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        chatsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                newMessagesCount=0;
                if (snapshot.exists()) {
                    for (DataSnapshot postIdSnapshot : snapshot.getChildren()) {
                        for (DataSnapshot userId2Snapshot : postIdSnapshot.getChildren()) {
                            newMessagesCount += userId2Snapshot.child("unseenMessages").getValue(Integer.class);
                        }
                    }

                } else {
                    Log.e("FirebaseError", "Snapshot does not exist");
                }
                if(newMessagesCount!=0) {
                    newMessagesTv.setBackgroundResource(R.drawable.custom_button);
                    newMessagesTv.setText(String.valueOf(newMessagesCount));
                }
                else{
                    newMessagesTv.setBackground(null);
                    newMessagesTv.setText("");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", error.getMessage());
            }
        };
        refChatList.child(fbUser.getUid()).addValueEventListener(chatsListener);
    }
}
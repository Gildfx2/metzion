package com.example.pre_alpha.main;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.pre_alpha.R;
import com.example.pre_alpha.chat.ChatActivity;


public class HomeFragment extends Fragment {

    Button showChats;
    SearchFragment searchFragment = new SearchFragment();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        showChats=view.findViewById(R.id.show_chats);
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

        return view;
    }
}
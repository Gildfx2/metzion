package com.example.pre_alpha.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.pre_alpha.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class HomeFragment extends Fragment {
    Button createPost, searchPosts;
    BottomNavigationView bottomNavigationView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationBar);
        createPost=view.findViewById(R.id.create_post_button);
        searchPosts=view.findViewById(R.id.search_button);
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
}
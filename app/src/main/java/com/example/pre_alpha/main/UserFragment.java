package com.example.pre_alpha.main;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.pre_alpha.R;
import com.example.pre_alpha.entry.Login;
import com.google.firebase.auth.FirebaseAuth;


public class UserFragment extends Fragment {

    FirebaseAuth auth;
    Button logout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        auth=FirebaseAuth.getInstance();
        logout=view.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences remember = getActivity().getSharedPreferences("check", MODE_PRIVATE);
                SharedPreferences.Editor editor = remember.edit();
                editor.putString("remember", "false");
                editor.commit();
                auth.signOut();
                Intent intent = new Intent(getActivity(), Login.class);
                startActivity(intent);
            }
        });
        return view;
    }



}
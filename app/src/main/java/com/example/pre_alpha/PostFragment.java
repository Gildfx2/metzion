package com.example.pre_alpha;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;


public class PostFragment extends Fragment {

    String[] item={"ארנק", "מפתחות", "תיק"};
    String[] cities={};
    TextView tvState;
    AutoCompleteTextView pickItem;
    ArrayAdapter<String> adapterItems;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post, container, false);
        tvState=view.findViewById(R.id.message_state);
        adapterItems = new ArrayAdapter<String>(getActivity(),R.layout.list_item,item);
        pickItem = view.findViewById(R.id.list_of_items);
        SharedPreferences state = getActivity().getSharedPreferences("state", MODE_PRIVATE);
        String checkState = state.getString("state", "");
        if(checkState.equals("lost")){
            tvState.setText("מה איבדת?");
        }
        else if (checkState.equals("found")) {
            tvState.setText("מה מצאת?");
        }
        pickItem.setAdapter(adapterItems);
        pickItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
            }
        });
        return view;
    }
}
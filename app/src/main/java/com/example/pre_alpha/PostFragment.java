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

    String[] items={"ארנק", "מפתחות", "תיק"};
    String[] areas={"באר שבע", "קריית שמונה", "הרצליה"};
    TextView tvState;
    Button next;
    AutoCompleteTextView pickItem, pickArea;
    ArrayAdapter<String> adapterItems, adapterAreas;
    SelectImageFragment selectImageFragment=new SelectImageFragment();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post, container, false);
        tvState=view.findViewById(R.id.message_state);
        next=view.findViewById(R.id.next);
        adapterItems = new ArrayAdapter<String>(getActivity(),R.layout.list_item,items);
        adapterAreas = new ArrayAdapter<String>(getActivity(),R.layout.list_item,areas);
        pickItem = view.findViewById(R.id.list_of_items);
        pickArea = view.findViewById(R.id.list_of_areas);
        SharedPreferences state = getActivity().getSharedPreferences("state", MODE_PRIVATE);
        String checkState = state.getString("state", "");
        if(checkState.equals("lost")){
            tvState.setText("מה איבדת?");
        }
        else if (checkState.equals("found")) {
            tvState.setText("מה מצאת?");
        }
        pickItem.setAdapter(adapterItems);
        pickArea.setAdapter(adapterAreas);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, selectImageFragment).commit();
            }
        });
        return view;
    }
}
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

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;


public class PostFragment extends Fragment {

    String[] items={"ארנק", "מפתחות", "תיק"};
    String[] areas={"באר שבע", "קריית שמונה", "הרצליה"};
    String name, item, area, about;
    TextView tvState;
    TextInputEditText etName, etAbout;
    Button next;
    TextInputLayout layoutItem, layoutArea;
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
        layoutItem = view.findViewById(R.id.item);
        layoutArea = view.findViewById(R.id.area);
        etName = view.findViewById(R.id.name);
        etAbout = view.findViewById(R.id.about);
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
                layoutItem.setHelperText("");
                layoutArea.setHelperText("");
                name=etName.getText().toString();
                item=pickItem.getText().toString();
                area=pickArea.getText().toString();
                about=etAbout.getText().toString();
                if(item.isEmpty()){
                    layoutItem.setHelperText("אל תשכח לבחור סוג חפץ");
                }
                if(area.isEmpty()){
                    layoutArea.setHelperText("אל תשכח לבחור ישוב");
                }
                if(!name.isEmpty() && !item.isEmpty() && !area.isEmpty()) {
                    SharedPreferences postName = getActivity().getSharedPreferences("name", MODE_PRIVATE);
                    SharedPreferences postItem = getActivity().getSharedPreferences("item", MODE_PRIVATE);
                    SharedPreferences postArea = getActivity().getSharedPreferences("area", MODE_PRIVATE);
                    SharedPreferences postAbout = getActivity().getSharedPreferences("about", MODE_PRIVATE);
                    SharedPreferences.Editor editorName = postName.edit();
                    SharedPreferences.Editor editorItem = postItem.edit();
                    SharedPreferences.Editor editorArea = postArea.edit();
                    SharedPreferences.Editor editorAbout = postAbout.edit();
                    editorName.putString("name", name);
                    editorName.apply();
                    editorName.commit();
                    editorItem.putString("item", item);
                    editorItem.apply();
                    editorItem.commit();
                    editorArea.putString("area", area);
                    editorArea.apply();
                    editorArea.commit();
                    editorAbout.putString("about", about);
                    editorAbout.apply();
                    editorAbout.commit();
                    getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, selectImageFragment).commit();
                }
            }
        });
        return view;
    }
}
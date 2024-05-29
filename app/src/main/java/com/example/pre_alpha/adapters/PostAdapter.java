package com.example.pre_alpha.adapters;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.pre_alpha.R;

import java.util.ArrayList;

public class PostAdapter extends ArrayAdapter<PostData>{

    public PostAdapter(@NonNull Context context, ArrayList<PostData> dataArrayList) {
        super(context, R.layout.list_of_items, dataArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        PostData postData = getItem(position);
        if(view==null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.list_of_items, parent, false);
        }
        //initializing
        ImageView image = view.findViewById(R.id.image);
        TextView item = view.findViewById(R.id.item);
        TextView name = view.findViewById(R.id.name);

        //setting the parameters into the views
        if(postData!=null && !postData.getImage().toString().isEmpty()) {
            Glide.with(this.getContext())
                    .load(postData.getImage())
                    .into(image);
        }
        else
            image.setImageResource(R.drawable.default_image);
        item.setText(postData.getItem());
        name.setText(postData.getName());
        return view;
    }
}

package com.example.pre_alpha;
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

import java.util.ArrayList;

public class ListAdapter extends ArrayAdapter<ListData>{

    public ListAdapter(@NonNull Context context, ArrayList<ListData> dataArrayList) {
        super(context, R.layout.list_of_items, dataArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        ListData listData = getItem(position);
        if(view==null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.list_of_items, parent, false);
        }
        ImageView image = view.findViewById(R.id.image);
        TextView item = view.findViewById(R.id.item);
        TextView name = view.findViewById(R.id.name);
        TextView area = view.findViewById(R.id.area);

        if(listData!=null && listData.image!=null) {
            Glide.with(this.getContext())
                    .load(listData.image)
                    .into(image);
        }
        item.setText(listData.item);
        name.setText(listData.name);
        area.setText(listData.area);
        return view;
    }
}

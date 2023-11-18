package com.example.pre_alpha.main;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.pre_alpha.R;


public class DetailedPostFragment extends Fragment {
    TextView tvName, tvItem, tvArea, tvAbout;
    ImageView ivImage;
    ImageView returnBack;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detailed_post, container, false);
        tvName=view.findViewById(R.id.post_name);
        tvItem=view.findViewById(R.id.post_item);
        tvArea=view.findViewById(R.id.post_area);
        tvAbout=view.findViewById(R.id.post_about);
        ivImage=view.findViewById(R.id.post_image);
        returnBack=view.findViewById(R.id.return_back);

        Bundle bundle = this.getArguments();
        String name = bundle.getString("name");
        String item = bundle.getString("item");
        String area = bundle.getString("area");
        String about = bundle.getString("about");
        String image = bundle.getString("image");
        tvName.setText(name);
        tvItem.setText("שם החפץ: "+item);
        tvArea.setText("שם הישוב: "+area);
        tvAbout.setText("פרטים:\n"+about);
        if(!image.isEmpty())
            Glide.with(this)
                    .load(Uri.parse(image))
                    .into(ivImage);

        returnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListPostFragment listPostFragment = new ListPostFragment();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.frameLayout, listPostFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        return view;
    }
}
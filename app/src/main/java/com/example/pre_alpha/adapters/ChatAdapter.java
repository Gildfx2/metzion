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

public class ChatAdapter extends ArrayAdapter<ChatData> {


    public ChatAdapter(@NonNull Context context, ArrayList<ChatData> dataArrayList) {
        super(context, R.layout.list_of_chats, dataArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        ChatData chatData = getItem(position);
        if(view==null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.list_of_chats, parent, false);
        }
        ImageView image = view.findViewById(R.id.chat_image);
        TextView name = view.findViewById(R.id.name);
        TextView username = view.findViewById(R.id.username);
        TextView lastMessage = view.findViewById(R.id.last_message);
        TextView date = view.findViewById(R.id.date);
        TextView unseenMessages = view.findViewById(R.id.unseen_messages);

        if(chatData!=null && !chatData.image.toString().isEmpty()) {
            Glide.with(getContext())
                    .load(chatData.image)
                    .into(image);
        }

        username.setText(chatData.username);
        name.setText(chatData.name);
        lastMessage.setText(chatData.lastMessage);
        date.setText(chatData.date);
        if (chatData.unseenMessages == 0) {
            unseenMessages.setBackground(null);
            unseenMessages.setText("");
        }
        else{
            unseenMessages.setText(Integer.toString(chatData.unseenMessages));
        }

        return view;
    }
}

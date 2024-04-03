package com.example.pre_alpha.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pre_alpha.models.Message;
import com.example.pre_alpha.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();

    private List<Message> messages;
    private static final int VIEW_TYPE_LEFT = 1;
    private static final int VIEW_TYPE_RIGHT = 2;
    private static final int IMAGE_VIEW_TYPE_LEFT = 3;
    private static final int IMAGE_VIEW_TYPE_RIGHT = 4;

    public MessageAdapter(List<Message> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_LEFT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_left, parent, false);
        } else if (viewType == VIEW_TYPE_RIGHT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_right, parent, false);
        } else if (viewType == IMAGE_VIEW_TYPE_LEFT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_image_left, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_image_right, parent, false);
        }
        return new MessageViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);
        ((MessageViewHolder) holder).bind(message);
    }

    @Override
    public int getItemViewType(int position) {
        if (messages.get(position).getSenderUid().equals(fbUser.getUid())) {
            if(messages.get(position).isImage()) return IMAGE_VIEW_TYPE_RIGHT;
            return VIEW_TYPE_RIGHT;
        } else {
            if(messages.get(position).isImage()) return IMAGE_VIEW_TYPE_LEFT;
            return VIEW_TYPE_LEFT;
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView, messageTimeStamp, messageStatus;
        ImageView messageImageView;

        public MessageViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messages);
            messageTimeStamp = itemView.findViewById(R.id.message_time_stamp);
            if (viewType == IMAGE_VIEW_TYPE_LEFT || viewType == IMAGE_VIEW_TYPE_RIGHT) {
                messageImageView = itemView.findViewById(R.id.chat_images);
            }
        }

        public void bind(Message message) {
            if (message.isImage() && (messageImageView != null)) {
                Glide.with(itemView.getContext()).load(message.getImageUrl()).into(messageImageView);
            } else {
                messageTextView.setText(message.getMessage());
            }
            messageTimeStamp.setText(formatTimestamp(message.getTimeStamp()));
        }

    }
    public static String formatTimestamp(long timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
        Date date = new Date(timestamp);
        return dateFormat.format(date);
    }
}


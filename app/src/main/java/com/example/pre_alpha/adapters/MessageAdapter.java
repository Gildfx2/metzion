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
    FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser(); // Current authenticated Firebase user

    private List<Message> messages; // List to hold messages
    private static final int VIEW_TYPE_LEFT = 1; // Left text message view type
    private static final int VIEW_TYPE_RIGHT = 2; // Right text message view type
    private static final int IMAGE_VIEW_TYPE_LEFT = 3; // Left image message view type
    private static final int IMAGE_VIEW_TYPE_RIGHT = 4; // Right image message view type

    public MessageAdapter(List<Message> messages) { // Constructor to initialize messages list
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
        ((MessageViewHolder) holder).bind(message); // Bind the message to the view holder
    }

    @Override
    public int getItemViewType(int position) { // Determine the view type based on message sender and content type
        if (messages.get(position).getSenderUid().equals(fbUser.getUid())) {
            if (messages.get(position).isImage()) return IMAGE_VIEW_TYPE_RIGHT;
            return VIEW_TYPE_RIGHT;
        } else {
            if (messages.get(position).isImage()) return IMAGE_VIEW_TYPE_LEFT;
            return VIEW_TYPE_LEFT;
        }
    }

    @Override
    public int getItemCount() { // Return the total number of messages
        return messages.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView, messageTimeStamp;
        ImageView messageImageView;

        public MessageViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messages);
            messageTimeStamp = itemView.findViewById(R.id.message_time_stamp);
            if (viewType == IMAGE_VIEW_TYPE_LEFT || viewType == IMAGE_VIEW_TYPE_RIGHT) {
                messageImageView = itemView.findViewById(R.id.chat_images); // ImageView for image messages
            }
        }

        public void bind(Message message) { // Bind message data to the views
            if (message.isImage() && (messageImageView != null)) {
                Glide.with(itemView.getContext()).load(message.getImageUrl()).into(messageImageView);
            } else {
                messageTextView.setText(message.getMessage());
            }
            messageTimeStamp.setText(formatTimestamp(message.getTimeStamp()));
        }
    }

    public static String formatTimestamp(long timestamp) { // Format timestamp into readable date and time string
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
        Date date = new Date(timestamp);
        return dateFormat.format(date);
    }
}

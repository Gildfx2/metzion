package com.example.pre_alpha.chat;

import static com.example.pre_alpha.chat.ChatActivity.currentUserStatus;
import static com.example.pre_alpha.chat.ChatActivity.otherUserStatus;
import static com.example.pre_alpha.models.FBref.refChat;
import static com.example.pre_alpha.models.FBref.refChatList;
import static com.example.pre_alpha.models.FBref.refUsers;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.pre_alpha.R;
import com.example.pre_alpha.adapters.MessageAdapter;
import com.example.pre_alpha.main.MainActivity;
import com.example.pre_alpha.models.ChatList;
import com.example.pre_alpha.models.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class ChatFragment extends Fragment {

    String postName, postArea, image, creatorUid, username, postId, otherUserUid, messageId, myStatus;
    int unseenMessages=1;
    ImageView postImage, returnBack;
    TextView nameTV, areaTV, usernameTV, userStatusTV;
    EditText textMessage;
    ImageButton sendMessage;
    FirebaseUser fbUser;
    FirebaseAuth auth;
    List<Message> messages = new ArrayList<>();
    RecyclerView recyclerView;
    Button getData;
    Message message;
    ValueEventListener userListener, chatListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        auth = FirebaseAuth.getInstance();
        fbUser = auth.getCurrentUser();
        Bundle bundle = this.getArguments();
        nameTV = view.findViewById(R.id.chat_post_name);
        areaTV = view.findViewById(R.id.chat_post_area);
        usernameTV = view.findViewById(R.id.chat_username);
        userStatusTV = view.findViewById(R.id.chat_user_status);
        postImage = view.findViewById(R.id.chat_post_image);
        textMessage = view.findViewById(R.id.text_message);
        sendMessage = view.findViewById(R.id.btn_send);
        recyclerView = view.findViewById(R.id.messages);
        getData = view.findViewById(R.id.get_data);
        returnBack = view.findViewById(R.id.return_back_chat);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(false);
        recyclerView.setLayoutManager(layoutManager);
        postName = bundle.getString("post_name");
        postArea = bundle.getString("post_area");
        image = bundle.getString("post_image");
        creatorUid = bundle.getString("creator_uid");
        postId = bundle.getString("post_id");
        username = bundle.getString("username");
        otherUserUid = bundle.getString("other_user_uid");

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageId = refChat.push().getKey();
                Message message = new Message(textMessage.getText().toString(), fbUser.getUid(), otherUserUid, postId, messageId, System.currentTimeMillis());
                refChat.child(messageId).setValue(message);
                ChatList chatList1 = new ChatList(otherUserUid, postId, System.currentTimeMillis(), message.getMessage());
                ChatList chatList2 = new ChatList(fbUser.getUid(), postId, System.currentTimeMillis(), message.getMessage(), unseenMessages);
                refChatList.child(fbUser.getUid()).child(postId).child(otherUserUid).setValue(chatList1);
                refChatList.child(otherUserUid).child(postId).child(fbUser.getUid()).setValue(chatList2);
                textMessage.setText("");
            }
        });

        getData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.putExtra("post_id", postId);
                intent.putExtra("get_data", "true");
                startActivity(intent);
            }
        });

        returnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeChatFragment homeChatFragment = new HomeChatFragment();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.chatFrameLayout, homeChatFragment);
                transaction.commit();
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refUsers.child(fbUser.getUid()).child("status").setValue("online_" + postId);
        userListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                unseenMessages=1;
                loadChatMessages();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", error.getMessage());
            }
        };
        refUsers.child(otherUserUid).child("status").addValueEventListener(userListener);
    }

    private void loadChatMessages(){
        chatListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messages.clear();
                unseenMessages=1;
                for (DataSnapshot data : snapshot.getChildren()) {
                    Message messageTmp = data.getValue(Message.class);
                    if((messageTmp.getReceiverUid().equals(fbUser.getUid()) && messageTmp.getSenderUid().equals(otherUserUid) && messageTmp.getPostId().equals(postId))
                            || (messageTmp.getSenderUid().equals(fbUser.getUid()) && messageTmp.getReceiverUid().equals(otherUserUid) && messageTmp.getPostId().equals(postId))) {
                        message = new Message(messageTmp);
                        if(message.getReceiverUid().equals(fbUser.getUid()) && currentUserStatus.length()>6 && currentUserStatus.substring(0,7).equals("online_")) {
                            message.setSeen(true);
                            refChat.child(messageTmp.getMessageId()).setValue(message);
                            refChatList.child(fbUser.getUid()).child(postId).child(otherUserUid).child("unseenMessages").setValue(0);
                        }
                        if(!message.isSeen()) unseenMessages++;
                        messages.add(message);
                    }
                }
                updateChatUI();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", error.getMessage());
            }
        };
        refChat.addValueEventListener(chatListener);
    }

    private void updateChatUI(){
        mainChat();

        MessageAdapter adapter = new MessageAdapter(messages);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        if (adapter.getItemCount() > 0) {
            recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
        }
    }
    private void mainChat(){
        usernameTV.setText(username);
        nameTV.setText(postName);
        areaTV.setText(postArea);
        if (getActivity()!=null && !image.isEmpty())
            Glide.with(this)
                    .load(Uri.parse(image))
                    .into(postImage);
        if(otherUserStatus.equals("online_" + postId))
            userStatusTV.setText("בצ'אט");
        else if(otherUserStatus.substring(0,6).equals("online"))
            userStatusTV.setText("מחובר");
        else
            userStatusTV.setText(formatDate(Long.parseLong(otherUserStatus)));
    }

    private String formatDate(long timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        Date date = new Date(timestamp);
        return dateFormat.format(date);
    }

    @Override
    public void onPause() {
        super.onPause();
        textMessage.setText("");
        refUsers.child(fbUser.getUid()).child("status").setValue("online");
        if (userListener != null) {
            refUsers.removeEventListener(userListener);
        }
        if (chatListener != null) {
            refChat.removeEventListener(chatListener);
        }
    }

}
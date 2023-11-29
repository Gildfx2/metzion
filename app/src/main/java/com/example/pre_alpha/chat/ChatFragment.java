package com.example.pre_alpha.chat;

import static android.content.Context.MODE_PRIVATE;
import static com.example.pre_alpha.models.FBref.refChat;
import static com.example.pre_alpha.models.FBref.refChatList;
import static com.example.pre_alpha.models.FBref.refPosts;
import static com.example.pre_alpha.models.FBref.refUsers;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.pre_alpha.entry.Register;
import com.example.pre_alpha.main.DetailedPostFragment;
import com.example.pre_alpha.main.ListPostFragment;
import com.example.pre_alpha.main.MainActivity;
import com.example.pre_alpha.models.ChatList;
import com.example.pre_alpha.models.Message;
import com.example.pre_alpha.models.User;
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

    String postName, postArea, image, creatorUid, username, postUid, otherUserUid;
    String messageUid;
    ImageView postImage, returnBack;
    TextView nameTV, areaTV, usernameTV;
    EditText textMessage;
    ImageButton sendMessage;
    FirebaseUser fbUser;
    FirebaseAuth auth;
    ArrayList<User> userValues = new ArrayList<User>();
    List<Message> messages = new ArrayList<>();
    RecyclerView recyclerView;
    Button getData;

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
        postUid = bundle.getString("post_uid");
        username = bundle.getString("username");
        otherUserUid = bundle.getString("other_user_uid");
        refUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userValues.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    User userTmp = data.getValue(User.class);
                    userValues.add(userTmp);
                }
                loadChatMessages();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", error.getMessage());
            }
        });

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageUid = refChat.push().getKey();
                Message message = new Message(textMessage.getText().toString(), fbUser.getUid(), otherUserUid, postUid, System.currentTimeMillis());
                refChat.child(messageUid).setValue(message);
                ChatList chatList1 = new ChatList(otherUserUid, postUid, System.currentTimeMillis());
                ChatList chatList2 = new ChatList(fbUser.getUid(), postUid, System.currentTimeMillis());
                refChatList.child(fbUser.getUid()).child(postUid).setValue(chatList1);
                refChatList.child(otherUserUid).child(postUid).setValue(chatList2);
                textMessage.setText("");
            }
        });

        getData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.putExtra("post_uid", postUid);
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
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        return view;
    }
    private void loadChatMessages(){
        ValueEventListener chatListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messages.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Message message = data.getValue(Message.class);
                    if((message.getReceiverUid().equals(fbUser.getUid()) && message.getSenderUid().equals(otherUserUid) && message.getPostUid().equals(postUid))
                            || (message.getSenderUid().equals(fbUser.getUid()) && message.getReceiverUid().equals(otherUserUid) && message.getPostUid().equals(postUid)))
                        messages.add(message);
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
    }

    @Override
    public void onPause() {
        textMessage.setText("");
        super.onPause();
    }

}
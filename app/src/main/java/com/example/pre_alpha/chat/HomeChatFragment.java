package com.example.pre_alpha.chat;

import static android.content.Context.MODE_PRIVATE;
import static com.example.pre_alpha.models.FBref.refUsers;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.pre_alpha.R;
import com.example.pre_alpha.adapters.ChatAdapter;
import com.example.pre_alpha.adapters.ChatData;
import com.example.pre_alpha.databinding.FragmentHomeChatBinding;
import com.example.pre_alpha.main.MainActivity;
import com.example.pre_alpha.models.Chat;
import com.example.pre_alpha.models.Post;
import com.example.pre_alpha.models.User;
import com.example.pre_alpha.models.FBref;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeChatFragment extends Fragment {

    List<Chat> chats = new ArrayList<>();
    List<User> userValues = new ArrayList<>();
    List<Post> postValues = new ArrayList<>();
    ArrayList<ChatData> arrayList = new ArrayList<>();
    FirebaseUser fbUser;
    Uri image_uri=null;
    ChatData chatData;
    ChatAdapter chatAdapter;
    FragmentHomeChatBinding binding;
    ChatFragment chatFragment = new ChatFragment();
    ValueEventListener chatsListener;
    FirebaseAuth auth;
    Button returnHome;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeChatBinding.inflate(inflater, container, false);
        auth=FirebaseAuth.getInstance();
        fbUser= auth.getCurrentUser();
        returnHome=binding.getRoot().findViewById(R.id.return_home);
        returnHome.setOnClickListener(new View.OnClickListener() { //returning to main activity
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        refUsers.addListenerForSingleValueEvent(new ValueEventListener() { //getting users
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    User userTmp = data.getValue(User.class);
                    userValues.add(userTmp);
                }
                readChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", error.getMessage());
            }
        });
    }

    private void readChats(){
        chatsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) { //getting chats
                chats.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot postIdSnapshot : snapshot.getChildren()) {
                        String postId = postIdSnapshot.getKey();
                        for (DataSnapshot userId2Snapshot : postIdSnapshot.getChildren()) {
                            String userUid = userId2Snapshot.child("userUid").getValue(String.class);
                            String lastMessage = userId2Snapshot.child("lastMessage").getValue(String.class);
                            long timestamp = userId2Snapshot.child("timeStamp").getValue(Long.class);
                            int unseenMessages = userId2Snapshot.child("unseenMessages").getValue(Integer.class);
                            Chat chat = new Chat(userUid, postId, timestamp, lastMessage, unseenMessages);
                            chats.add(chat);
                        }
                    }
                    Collections.sort(chats, new Comparator<Chat>() {
                        @Override
                        public int compare(Chat chat1, Chat chat2) {
                            return Long.compare(chat2.getTimeStamp(), chat1.getTimeStamp());
                        }
                    });
                    readPosts();
                } else {
                    Log.e("FirebaseError", "Snapshot does not exist");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", error.getMessage());
            }
        };
        FBref.refChatList.child(fbUser.getUid()).addValueEventListener(chatsListener);
    }
    private void readPosts(){ //getting posts
        FBref.refPosts.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    Post post = data.getValue(Post.class);
                    postValues.add(post);
                }
                showChats();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", error.getMessage());
            }
        });
    }
    private String getUsernameFromUid(String uid){
        for(User user : userValues){
            if(user.getUid().equals(uid)) return user.getUsername();
        }
        return "אין שם";
    }

    private String formatDate(long timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        Date date = new Date(timestamp);
        return dateFormat.format(date);
    }

    private void showChats(){
        arrayList.clear();
        for(Chat chat : chats){ //getting all of the chats that the current user include in
            for(Post post : postValues) {
                if(chat.getPostId().equals(post.getPostId())){
                    if(post.getImage() != null) {
                        image_uri = Uri.parse(post.getImage());
                    }
                    chatData = new ChatData(post.getName(), getUsernameFromUid(chat.getUserUid()), image_uri, post.getCreatorUid(),
                            post.getPostId(), chat.getUserUid(), chat.getLastMessage(), formatDate(chat.getTimeStamp()), chat.getUnseenMessages());
                    arrayList.add(chatData);
                    break;
                }
            }
        }
        if(getActivity()!=null) {
            //showing the chats
            chatAdapter = new ChatAdapter(getActivity(), arrayList);
            binding.listOfChats.setAdapter(chatAdapter);
            binding.listOfChats.setClickable(true);
            binding.listOfChats.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Bundle bundle = new Bundle();
                    bundle.putString("post_name", arrayList.get(position).getName());
                    bundle.putString("creator_uid", arrayList.get(position).getCreatorUid());
                    bundle.putString("post_id", arrayList.get(position).getPostId());
                    bundle.putString("username", arrayList.get(position).getUsername());
                    bundle.putString("other_user_uid", arrayList.get(position).getOtherUserUid());
                    bundle.putString("from_post_or_chatlist", "chatlist");
                    if (arrayList.get(position) != null && arrayList.get(position).getImage() != null)
                        bundle.putString("post_image", arrayList.get(position).getImage().toString());
                    else bundle.putString("post_image", "");
                    chatFragment.setArguments(bundle);
                    getParentFragmentManager().beginTransaction().replace(R.id.chatFrameLayout, chatFragment).commit();
                }
            });
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        arrayList.clear();
        if (chatsListener != null) {
            FBref.refChatList.child(fbUser.getUid()).removeEventListener(chatsListener);
        }
    }
}
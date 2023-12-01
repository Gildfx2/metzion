package com.example.pre_alpha.chat;

import static com.example.pre_alpha.models.FBref.FBDB;
import static com.example.pre_alpha.models.FBref.refChat;
import static com.example.pre_alpha.models.FBref.refChatList;
import static com.example.pre_alpha.models.FBref.refPosts;
import static com.example.pre_alpha.models.FBref.refUsers;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.pre_alpha.R;
import com.example.pre_alpha.adapters.ChatAdapter;
import com.example.pre_alpha.adapters.ChatData;
import com.example.pre_alpha.adapters.PostData;
import com.example.pre_alpha.databinding.FragmentHomeChatBinding;
import com.example.pre_alpha.databinding.FragmentListPostBinding;
import com.example.pre_alpha.models.ChatList;
import com.example.pre_alpha.models.Message;
import com.example.pre_alpha.models.Post;
import com.example.pre_alpha.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeChatFragment extends Fragment {

    List<ChatList> chats = new ArrayList<>();
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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeChatBinding.inflate(inflater, container, false);
        auth=FirebaseAuth.getInstance();
        fbUser= auth.getCurrentUser();
        refUsers.addListenerForSingleValueEvent(new ValueEventListener() {
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
        return binding.getRoot();
    }

    private void showChats(){
        arrayList.clear();
        for(ChatList chatList : chats){
            for(Post post : postValues) {
                if(chatList.getPostId().equals(post.getPostId())){
                    if(post.getImage() != null) {
                        image_uri = Uri.parse(post.getImage());
                    }
                    chatData = new ChatData(post.getName(), post.getArea(), getUsernameFromUid(chatList.getUserUid()), image_uri, post.getCreatorUid(),
                            post.getPostId(), chatList.getUserUid(), chatList.getLastMessage(), formatDate(chatList.getTimeStamp()));
                    arrayList.add(chatData);
                    break;
                }
            }
        }
        Log.d("check list", String.valueOf(arrayList.size()));
        if(getActivity()!=null) {
            chatAdapter = new ChatAdapter(getActivity(), arrayList);
            binding.listOfChats.setAdapter(chatAdapter);
            binding.listOfChats.setClickable(true);
            binding.listOfChats.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Bundle bundle = new Bundle();
                    bundle.putString("post_name", arrayList.get(position).getName());
                    bundle.putString("post_area", arrayList.get(position).getArea());
                    bundle.putString("creator_uid", arrayList.get(position).getCreatorUid());
                    bundle.putString("post_id", arrayList.get(position).getPostId());
                    bundle.putString("username", arrayList.get(position).getUsername());
                    bundle.putString("other_user_uid", arrayList.get(position).getOtherUserUid());
                    if (arrayList.get(position) != null && arrayList.get(position).getImage() != null)
                        bundle.putString("post_image", arrayList.get(position).getImage().toString());
                    else bundle.putString("post_image", "");
                    chatFragment.setArguments(bundle);
                    getParentFragmentManager().beginTransaction().replace(R.id.chatFrameLayout, chatFragment).commit();
                }
            });
        }
    }
    private void readChats(){
        Query query = FBDB.getReference().child("ChatList").child(fbUser.getUid()).orderByChild("timeStamp");
        ValueEventListener chatsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chats.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    ChatList chatList = data.getValue(ChatList.class);
                    chats.add(chatList);
                }
                Collections.reverse(chats);
                readPosts();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", error.getMessage());
            }
        };
        query.addValueEventListener(chatsListener);
    }
    private void readPosts(){
        refPosts.addListenerForSingleValueEvent(new ValueEventListener() {
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

    public static String formatDate(long timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        Date date = new Date(timestamp);
        return dateFormat.format(date);
    }

    @Override
    public void onPause() {
        arrayList.clear();
        if (chatsListener != null) {
            refChatList.removeEventListener(chatsListener);
        }
        super.onPause();
    }
}
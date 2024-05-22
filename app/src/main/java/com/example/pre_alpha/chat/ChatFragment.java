package com.example.pre_alpha.chat;

import static android.app.Activity.RESULT_OK;
import static com.example.pre_alpha.models.FBref.refChat;
import static com.example.pre_alpha.models.FBref.refChatList;
import static com.example.pre_alpha.models.FBref.refTokens;
import static com.example.pre_alpha.models.FBref.refUsers;
import static com.google.firebase.appcheck.internal.util.Logger.TAG;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pre_alpha.R;
import com.example.pre_alpha.adapters.MessageAdapter;
import com.example.pre_alpha.main.MainActivity;
import com.example.pre_alpha.models.ChatList;
import com.example.pre_alpha.models.Message;
import com.example.pre_alpha.notifications.APIService;
import com.example.pre_alpha.notifications.Client;
import com.example.pre_alpha.notifications.Data;
import com.example.pre_alpha.notifications.Response;
import com.example.pre_alpha.notifications.Sender;
import com.example.pre_alpha.notifications.Token;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;


public class ChatFragment extends Fragment {
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;
    String[] cameraPermissions;
    String[] storagePermissions;
    String postName, userImage, username, postId, otherUserUid, messageId, msg, otherUserStatus, currentUserUsername;
    boolean result, result1, result2;
    Uri image_uri, download_uri;
    ImageView postImage, returnBack;
    TextView nameTV, usernameTV, userStatusTV;
    ImageView image, resetImage;
    EditText textMessage;
    ImageButton sendMessage;
    FirebaseUser fbUser;
    FirebaseAuth auth;
    StorageReference storageReference;
    List<Message> messages = new ArrayList<>();
    MessageAdapter adapter;
    RecyclerView recyclerView;
    Button getData;
    Message messageOrImageToSend;
    ValueEventListener userListener, chatListener;
    ChatList chatList1, chatList2;
    String storagePath = "Users_messages_Images/";
    APIService apiService;
    boolean backFromPickImage=false;
    private static int unseenMessages=0;
    boolean notify = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        //initializing
        auth = FirebaseAuth.getInstance();
        fbUser = auth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        nameTV = view.findViewById(R.id.chat_post_name);
        usernameTV = view.findViewById(R.id.chat_username);
        userStatusTV = view.findViewById(R.id.chat_user_status);
        postImage = view.findViewById(R.id.chat_post_image);
        textMessage = view.findViewById(R.id.text_message);
        sendMessage = view.findViewById(R.id.btn_send);
        recyclerView = view.findViewById(R.id.messages);
        getData = view.findViewById(R.id.get_data);
        returnBack = view.findViewById(R.id.return_back_chat);
        image=view.findViewById(R.id.image_message);
        resetImage=view.findViewById(R.id.btn_reset_image);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(false);
        recyclerView.setLayoutManager(layoutManager);
        Bundle bundle = this.getArguments();
        postName = bundle.getString("post_name");
        userImage = bundle.getString("post_image");
        postId = bundle.getString("post_id");
        username = bundle.getString("username");
        otherUserUid = bundle.getString("other_user_uid");
        refUsers.child(fbUser.getUid()).child("username").addListenerForSingleValueEvent(new ValueEventListener() { //getting the current user username
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    currentUserUsername = snapshot.getValue(String.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", error.getMessage());
            }
        });
        refUsers.child(fbUser.getUid()).child("status").setValue("online_" + otherUserUid); //setting the correct status

        //setting the permission needed in order to send a photo
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            cameraPermissions = new String[]{Manifest.permission.CAMERA};
            storagePermissions = new String[]{Manifest.permission.READ_MEDIA_IMAGES};
        }

        textMessage.addTextChangedListener(new TextWatcher() { //handling the icon of send and import imager
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().trim().isEmpty()) {
                    sendMessage.setBackgroundResource(R.drawable.baseline_add_circle_24);
                } else {
                    sendMessage.setBackgroundResource(R.drawable.baseline_send_24);
                }
            }
        });

        resetImage.setOnClickListener(new View.OnClickListener() { //resetting the image
            @Override
            public void onClick(View v) {
                sendMessage.setBackgroundResource(R.drawable.baseline_add_circle_24);
                resetImage.setImageDrawable(null);
                image.setImageDrawable(null);
                textMessage.setEnabled(true);
                textMessage.setHint("הקלידו הודעה...");
                image_uri=null;
            }
        });

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!otherUserStatus.substring(6).equals("online")) //checking if it is necessary to send notification or not
                    notify = true;
                msg=textMessage.getText().toString();
                messageId = refChat.push().getKey();
                String filePathAndName = storagePath + "image" + "_" + messageId;
                StorageReference messagesStorageReference = storageReference.child(filePathAndName);
                if (image.getDrawable()==null){ //handling if the message isn't a photo
                    if(!msg.isEmpty()) {
                        //sending the message
                        messageOrImageToSend = new Message(msg, fbUser.getUid(), otherUserUid, postId, messageId, System.currentTimeMillis());
                        textMessage.setText("");
                        if(!otherUserStatus.equals("online_" + fbUser.getUid())) unseenMessages++;
                        chatList1 = new ChatList(otherUserUid, postId, System.currentTimeMillis(), messageOrImageToSend.getMessage());
                        chatList2 = new ChatList(fbUser.getUid(), postId, System.currentTimeMillis(), messageOrImageToSend.getMessage(), unseenMessages);
                        refChat.child(messageId).setValue(messageOrImageToSend);
                        refChatList.child(fbUser.getUid()).child(postId).child(otherUserUid).setValue(chatList1);
                        refChatList.child(otherUserUid).child(postId).child(fbUser.getUid()).setValue(chatList2);
                        if(notify){
                            sendNotification(otherUserUid, currentUserUsername, msg); //sending notification
                        }
                        notify = false;
                    }
                    else showImageDialog(); //show the menu of camera or gallery
                }
                else{ //handling if the message is a photo
                    messagesStorageReference.putFile(image_uri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    //sending the message
                                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                    while (!uriTask.isSuccessful()) ;
                                    download_uri = uriTask.getResult();
                                    messageOrImageToSend = new Message(download_uri, fbUser.getUid(), otherUserUid, postId, messageId, System.currentTimeMillis());
                                    if(!otherUserStatus.equals("online_" + fbUser.getUid())) unseenMessages++;
                                    chatList1 = new ChatList(otherUserUid, postId, System.currentTimeMillis(), "תמונה");
                                    chatList2 = new ChatList(fbUser.getUid(), postId, System.currentTimeMillis(), "תמונה", unseenMessages);
                                    refChat.child(messageId).setValue(messageOrImageToSend);
                                    refChatList.child(fbUser.getUid()).child(postId).child(otherUserUid).setValue(chatList1);
                                    refChatList.child(otherUserUid).child(postId).child(fbUser.getUid()).setValue(chatList2);
                                    sendMessage.setBackgroundResource(R.drawable.baseline_add_circle_24);
                                    image.setImageDrawable(null);
                                    resetImage.setImageDrawable(null);
                                    textMessage.setEnabled(true);
                                    textMessage.setHint("הקלידו הודעה...");
                                    image_uri=null;
                                    if(notify){
                                        sendNotification(otherUserUid, currentUserUsername, "תמונה"); //send notification
                                    }
                                    notify = false;

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                }


            }
        });

        getData.setOnClickListener(new View.OnClickListener() { //move to detailed post fragment
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.putExtra("post_id", postId);
                intent.putExtra("get_data", "true");
                startActivity(intent);
            }
        });

        returnBack.setOnClickListener(new View.OnClickListener() { //move to home chat fragment
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

    private void sendNotification(String otherUserUid, String username, String textMessage) {
        Query query = refTokens.orderByKey().equalTo(otherUserUid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()){
                    Token token = ds.getValue(Token.class);
                    Data data = new Data(fbUser.getUid(), username+":"+textMessage, "New Message", otherUserUid, postId, username, R.drawable.baseline_person_24);
                    Sender sender = new Sender(data, token.getToken());
                    if(!otherUserStatus.contains("online")) {
                        apiService.sendNotification(sender) //sending the notification with the service
                                .enqueue(new Callback<Response>() {
                                    @Override
                                    public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                                        if (response.code() == 200) {
                                            if (response.body().success == 1) {}
                                            else
                                                Toast.makeText(getActivity(), "Failed!", Toast.LENGTH_SHORT).show();
                                        }

                                    }

                                    @Override
                                    public void onFailure(Call<Response> call, Throwable t) {

                                    }
                                });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        refChatList.child(otherUserUid).child(postId).child(fbUser.getUid()).child("unseenMessages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                    unseenMessages = snapshot.getValue(Integer.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        adapter = new MessageAdapter(messages);
        recyclerView.setAdapter(adapter);
        chatListener = new ValueEventListener() { //showing the messages of the chat
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messages.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Message messageTmp = data.getValue(Message.class);
                    if (isMessageRelevant(messageTmp)) {
                        messages.add(messageTmp);
                        Log.d(TAG, "Added new message: " + messageTmp.getMessage());
                    }
                }
                adapter.notifyDataSetChanged();
                if(adapter.getItemCount()>0)
                    recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", error.getMessage());
            }
        };
        refChat.addValueEventListener(chatListener);
        if(!messages.isEmpty())
            refChatList.child(fbUser.getUid()).child(postId).child(otherUserUid).child("unseenMessages").setValue(0); //setting the unseen messages to 0

        userListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) { //setting the unseen messages to 0 when the other user get in the chat
                otherUserStatus = snapshot.getValue(String.class);
                updateChatUI();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", error.getMessage());
            }
        };
        refUsers.child(otherUserUid).child("status").addValueEventListener(userListener);
    }

    private void updateChatUI(){ //setting the text views
        usernameTV.setText(username);
        nameTV.setText(postName);
        if (getActivity()!=null && !userImage.isEmpty())
            Glide.with(this)
                    .load(Uri.parse(userImage))
                    .into(postImage);
        if(otherUserStatus.equals("online"))
            userStatusTV.setText("מחובר");
        else if (otherUserStatus.equals("online_" + fbUser.getUid())) {
            userStatusTV.setText("בצ'אט");
        } else
            userStatusTV.setText(formatDate(Long.parseLong(otherUserStatus)));
    }

    private String formatDate(long timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        Date date = new Date(timestamp);
        return dateFormat.format(date);
    }


    private boolean isMessageRelevant(Message message) { //checking if the message is relevant to this specific chat
        return (message.getReceiverUid().equals(fbUser.getUid()) && message.getSenderUid().equals(otherUserUid) && message.getPostId().equals(postId))
                || (message.getSenderUid().equals(fbUser.getUid()) && message.getReceiverUid().equals(otherUserUid) && message.getPostId().equals(postId));
    }

    private void showImageDialog(){
        String options[] = {"מצלמה", "גלריה"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("בחר פעולה");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { //requesting permissions, if granted than moving to the picking
                if(which==0){
                    if(!checkCameraPermission()){
                        requestCameraPermission();
                    }
                    else pickFromCamera();
                }
                if(which==1){
                    if(!checkStoragePermission()){
                        requestStoragePermission();
                    }
                    else pickFromGallery();
                }
            }
        });
        builder.show();
    }

    private boolean checkStoragePermission() {
        boolean result1 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_MEDIA_IMAGES)
                == (PackageManager.PERMISSION_GRANTED);
        boolean result2 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result1||result2;

    }
    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(getActivity(),storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            result1 = ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED;
            result2 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED;
            return result1&&result2;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED;
            return result;
        }
        return false;

    }
    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(getActivity(),cameraPermissions, CAMERA_REQUEST_CODE);
    }

    private void pickFromCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");
        image_uri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        backFromPickImage=true;
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
    }
    private void pickFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        backFromPickImage=true;
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode == IMAGE_PICK_GALLERY_CODE){
                image_uri = data.getData();
                image.setImageURI(image_uri);
            }
            if(requestCode == IMAGE_PICK_CAMERA_CODE){
                image.setImageURI(image_uri);
            }
            textMessage.setEnabled(false);
            textMessage.setHint("");
            sendMessage.setBackgroundResource(R.drawable.baseline_send_24);
            Glide.with(getContext())
                    .load(R.drawable.trash)
                    .into(resetImage);
            backFromPickImage=false;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }



    @Override
    public void onPause() {
        super.onPause();
        textMessage.setText("");
        messages.clear();
        refUsers.child(fbUser.getUid()).child("status").setValue("online");
        if(!backFromPickImage){
            unseenMessages=0;
            if(userListener!=null)
                refUsers.child(otherUserUid).child("status").removeEventListener(userListener);
        }
        if (chatListener != null) {
            refChat.removeEventListener(chatListener);
        }
    }

}
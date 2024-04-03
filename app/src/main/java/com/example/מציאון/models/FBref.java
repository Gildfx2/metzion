package com.example.מציאון.models;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FBref {
    public static FirebaseDatabase FBDB = FirebaseDatabase.getInstance();
    public static DatabaseReference refUsers=FBDB.getReference("Users");
    public static DatabaseReference refPosts=FBDB.getReference("Posts");
    public static DatabaseReference refChat=FBDB.getReference("Chat");
    public static DatabaseReference refChatList=FBDB.getReference("ChatList");
    public static DatabaseReference refTokens=FBDB.getReference("Tokens");
}

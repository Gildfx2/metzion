package com.example.pre_alpha.main;

import static android.content.Context.MODE_PRIVATE;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import static com.example.pre_alpha.models.FBref.refUsers;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.pre_alpha.R;
import com.example.pre_alpha.chat.ChatActivity;
import com.example.pre_alpha.entry.Login;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class UserFragment extends Fragment {

    FirebaseAuth auth;
    FirebaseUser fbUser;
    Button logout;
    TextView usernameTv;
    ListView lvOperations;
    Button createPost, applyUsername;
    ImageView editUsername;
    String[] operations = {"המודעות שפרסמתי", "כניסה לצ'אט", "תנאי שימוש ומדיניות האפליקציה"};
    MyPostsFragment myPostsFragment = new MyPostsFragment();
    BottomNavigationView bottomNavigationView;
    Dialog dialog;
    TextInputEditText newUsername;
    TextInputLayout layoutEditUsername;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        lvOperations=view.findViewById(R.id.list_of_operations);
        logout=view.findViewById(R.id.logout);
        createPost=view.findViewById(R.id.create_post);
        usernameTv=view.findViewById(R.id.profile_username);
        editUsername=view.findViewById(R.id.edit_username);
        bottomNavigationView=getActivity().findViewById(R.id.bottomNavigationBar);
        auth=FirebaseAuth.getInstance();
        fbUser = auth.getCurrentUser();
        refUsers.child(fbUser.getUid()).child("username").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usernameTv.setText(snapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: " + error.getMessage());
            }
        });

        createPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomNavigationView.setSelectedItemId(R.id.post);
            }
        });

        editUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog=new Dialog(getActivity());
                dialog.setContentView(R.layout.edit_username_dialog);
                applyUsername=dialog.findViewById(R.id.apply_username);
                newUsername=dialog.findViewById(R.id.new_username);
                layoutEditUsername=dialog.findViewById(R.id.layout_edit_username);
                newUsername.setText(usernameTv.getText().toString());
                applyUsername.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!validateUsername(newUsername.getText().toString())){
                            layoutEditUsername.setHelperText("השם משתמש צריך לכלול לפחות 3 תווים, להתחיל באות מסוימת, ללא רווחים");
                        }
                        else {
                            refUsers.child(fbUser.getUid()).child("username").setValue(newUsername.getText().toString());
                            dialog.cancel();
                        }
                    }
                });
                dialog.show();
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences remember = getActivity().getSharedPreferences("check", MODE_PRIVATE);
                SharedPreferences.Editor editor = remember.edit();
                editor.putString("remember", "false");
                editor.commit();
                auth.signOut();
                Intent intent = new Intent(getActivity(), Login.class);
                startActivity(intent);
            }
        });

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, operations);

        lvOperations.setAdapter(arrayAdapter);
        lvOperations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, myPostsFragment).commit();
                } else if (position==1) {
                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                    SharedPreferences chat = getActivity().getSharedPreferences("chat_pick", MODE_PRIVATE);
                    SharedPreferences.Editor editor = chat.edit();
                    editor.putString("chat_pick", "see chats");
                    editor.apply();
                    editor.commit();
                    startActivity(intent);
                }
                else{

                }
            }
        });
        return view;
    }

    private boolean validateUsername(String username) {
        if (username == null && username.isEmpty()) return false;
        String usernameRegex = "^[A-Za-zא-ת]\\w{2,24}$";
        //השם משתמש חייב להכיל בין 3-25 תווים שחייבים להתחיל באות קטנה או אות גדולה
        //נ.ב אסור שיהיו רווחים
        Pattern pattern = Pattern.compile(usernameRegex);
        Matcher m = pattern.matcher(username);
        return m.matches();
    }

}
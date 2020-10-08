package com.team15.chatapp.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.team15.chatapp.Adapter.UserAdapter;
import com.team15.chatapp.Model.ChatList;
import com.team15.chatapp.Model.User;
import com.team15.chatapp.Notifications.Token;
import com.team15.chatapp.R;

import java.util.ArrayList;
import java.util.List;


public class ChatFragment extends Fragment {
    private RecyclerView recyclerView;
    private TextView txtNoData;

    private UserAdapter userAdapter;
    private List<User> mUsers;

    FirebaseUser fUser;
    DatabaseReference reference;

    private List<ChatList> chatList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        txtNoData = view.findViewById(R.id.txtNoData);
        
        mUsers = new ArrayList<>();
        userAdapter=new UserAdapter(mUsers,true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext() ,LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(userAdapter);

        fUser = FirebaseAuth.getInstance().getCurrentUser();

        chatList = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("ChatList").child(fUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatList.clear();
                if (dataSnapshot.exists()){
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        ChatList chatlist = snapshot.getValue(ChatList.class);
                        chatList.add(chatlist);
                    }
                    chatList();
                    txtNoData.setVisibility(View.INVISIBLE);
                }else {
                    txtNoData.setVisibility(View.VISIBLE);
                }
                }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        updateToken(FirebaseInstanceId.getInstance().getToken());


        return view;
    }

    private void updateToken(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(fUser.getUid()).setValue(token1);
    }

    private void chatList() {
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    for (ChatList chatlist : chatList){
                        if (user.getId().equals(chatlist.getId())){
                            mUsers.add(user);
                        }
                    }
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}

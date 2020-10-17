package com.team15.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Time;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.team15.chatapp.Adapter.MessageAdapter;
import com.team15.chatapp.Adapter.UserAdapter;
import com.team15.chatapp.AsyncTask.ImagePicker;
import com.team15.chatapp.AsyncTask.SendNotification;
import com.team15.chatapp.Data.DBHelper;
import com.team15.chatapp.Model.Chat;
import com.team15.chatapp.Model.User;
import com.team15.chatapp.ViewModel.UserViewModel;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {
    private CircleImageView profile_image;
    private TextView username;
    private ImageButton btn_send;
    private EditText text_send;
    private ImageView imgSelect, imgDefaultText;
    private UserViewModel userViewModel;

    FirebaseUser fUser;
    DatabaseReference reference;
    MessageAdapter messageAdapter;
    List<Chat> mChat;
    RecyclerView recyclerView;
    Intent intent;
    ValueEventListener seenListener;
    String receiverId;

    boolean notify = false;
    DBHelper mydb;
    ArrayList arrayChat;

    StorageReference storageReference;
    private static final int IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        mChat = new ArrayList<>();
        messageAdapter = new MessageAdapter(this, mChat);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(messageAdapter);


        mydb = new DBHelper(this);

        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        btn_send = findViewById(R.id.btn_send);
        text_send = findViewById(R.id.text_send);
        imgSelect = findViewById(R.id.imgSelect);
        imgDefaultText = findViewById(R.id.imgDefaultText);

        intent = getIntent();
        receiverId = intent.getStringExtra("userId");
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("chats");



        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notify = true;
                String msg = text_send.getText().toString();
                if (!msg.equals("")) {
                    sendMessage(fUser.getUid(), receiverId, msg, "text");
                } else {
                    Toast.makeText(MessageActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
                }
                text_send.setText("");
            }
        });
        imgSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
            }
        });

        text_send.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0) {
                    imgSelect.setVisibility(View.GONE);
                    imgDefaultText.setVisibility(View.GONE);
                } else {
                    imgSelect.setVisibility(View.VISIBLE);
                    imgDefaultText.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        imgDefaultText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatData();
            }
        });

        setProfile();
        seenMessage(receiverId);
        readMessages(fUser.getUid(), receiverId);
    }

    private void chatData() {
        mydb.numberOfRows();
        arrayChat = mydb.getList();

        AlertDialog.Builder alertDialog = new
                AlertDialog.Builder(this);
        View rowList = getLayoutInflater().inflate(R.layout.row, null);
        ListView listView = rowList.findViewById(R.id.listView);
        ImageButton imgAdd = rowList.findViewById(R.id.imgAdd);
        ImageButton imgClose = rowList.findViewById(R.id.imgClose);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayChat);
        listView.setAdapter(adapter);
        ;
        adapter.notifyDataSetChanged();
        alertDialog.setView(rowList);
        final AlertDialog dialog = alertDialog.create();
        dialog.show();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                text_send.setText(arrayChat.get(position).toString());
                dialog.dismiss();
            }
        });

        imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessageActivity.this, ChatDataActivity.class);
                startActivity(intent);
                dialog.dismiss();
            }
        });

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void setProfile() {
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        userViewModel.getUser(receiverId).observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                username.setText(user.getUsername());
                if (user.getImageURL().equals("default")) {
                    profile_image.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_image);
                }
            }
        });
    }

    private void seenMessage(final String receiverId) {
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(fUser.getUid()) && chat.getSender().equals(receiverId)) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("seen", true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(final String sender, final String receiver, String message, String type) {
        String timeStamp = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));

        //chat
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Chat chat = new Chat(sender, receiver, message, type, false);
        reference.child("Chats").push().setValue(chat);

        //chat list
        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(sender)
                .child(receiver);

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    chatRef.child("id").setValue(receiver);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final DatabaseReference chatRefReceiver = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(receiver)
                .child(sender);
        chatRefReceiver.child("id").setValue(sender);

        final String msg = message;


        //notification

        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        userViewModel.getUser(sender).observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                sendNotification(receiver, sender, user.getUsername(), msg);
            }
        });



//        reference = FirebaseDatabase.getInstance().getReference("Users").child(sender);
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                User user = dataSnapshot.getValue(User.class);
//                if (notify) {
//                    sendNotification(receiver, sender, user.getUsername(), msg);
//                }
//                notify = false;
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
    }

    private void sendNotification(String receiver, String sender, String username, String message) {
        SendNotification.getInstance().send(receiver, username, message, sender);
    }

    private void readMessages(final String myId, final String receiverId) {
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChat.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(myId) && chat.getSender().equals(receiverId) ||
                            chat.getReceiver().equals(receiverId) && chat.getSender().equals(myId)) {
                        mChat.add(chat);
                    }
                }
                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



//        reference = FirebaseDatabase.getInstance().getReference("Chats");
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                mChat.clear();
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    Chat chat = snapshot.getValue(Chat.class);
//                    if (chat.getReceiver().equals(myId) && chat.getSender().equals(receiverId) ||
//                            chat.getReceiver().equals(receiverId) && chat.getSender().equals(myId)) {
//                        mChat.add(chat);
//                    }
//                }
//                messageAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
    }

    private void currentUser(String receiverId) {
        SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
        editor.putString("currentUser", receiverId);
        editor.apply();
    }

    private void status(String status) {
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fUser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
        currentUser(receiverId);
    }

    @Override
    protected void onPause() {
        super.onPause();
        reference.removeEventListener(seenListener);
        status("offline");
        currentUser("none");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    //image upload

    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    private void uploadImage(Bitmap bmp) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] data = stream.toByteArray();

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Uploading");
        pd.show();

        final StorageReference filepath = storageReference.child(System.currentTimeMillis() + "chat");


        UploadTask uploadTask = filepath.putBytes(data);
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw Objects.requireNonNull(task.getException());
                }
                return filepath.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    String mUri = downloadUri.toString();
                    sendMessage(fUser.getUid(), receiverId, mUri, "image");
                    pd.dismiss();
                } else {
                    Toast.makeText(MessageActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MessageActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            Bitmap bmp = ImagePicker.getImageFromResult(this, resultCode, data);
            uploadImage(bmp);
        }
    }
}

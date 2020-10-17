package com.team15.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.team15.chatapp.Fragment.ChatFragment;
import com.team15.chatapp.Fragment.ProfileFragment;
import com.team15.chatapp.Fragment.UserFragment;
import com.team15.chatapp.Model.Chat;
import com.team15.chatapp.Model.User;
import com.team15.chatapp.ViewModel.UserViewModel;
import java.util.HashMap;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    private ChipNavigationBar bottom_menu;
    private CircleImageView profile_image;
    private TextView username;
    private ImageView img_menu;
    private UserViewModel userViewModel;
    String userType = "user";
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        setProfile();
        img_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuClick(v);
            }
        });
        loadFragment(new ChatFragment());
    }

    private void setProfile() {
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        userViewModel.getUser(firebaseUser.getUid()).observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                userType = user.getUserType();
                username.setText(user.getUsername());
                if (user.getImageURL().equals("default")) {
                    profile_image.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_image);
                }
            }
        });

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Chats");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int unread = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(firebaseUser.getUid()) && !chat.isSeen()) {
                        unread++;
                    }
                }
                if (unread >= 0) {
                    bottom_menu.showBadge(R.id.menu_chat, unread);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void menuClick(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.inflate(R.menu.top_menu);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.logout:
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class).
                                setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                        finish();
                        break;
//                            case R.id.menu2:
//                                //handle menu2 click
//                                break;
                    default:
                        break;

                }
                return false;
            }
        });

        popup.show();
    }

    ChipNavigationBar.OnItemSelectedListener listener = new ChipNavigationBar.OnItemSelectedListener() {
        @Override
        public void onItemSelected(int i) {
            Fragment fragment;
            switch (i) {
                case R.id.menu_chat:
                    fragment = new ChatFragment();
                    loadFragment(fragment);
                    break;
                case R.id.menu_users:
                    Bundle bundle = new Bundle();
                    bundle.putString("userType", userType);
                    fragment = new UserFragment();
                    fragment.setArguments(bundle);
                    loadFragment(fragment);
                    break;
                case R.id.menu_profile:
                    fragment = new ProfileFragment();
                    loadFragment(fragment);
                    break;
                default:
                    break;
            }
        }

    };

    private void status(String status) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void init() {
        bottom_menu = findViewById(R.id.bottom_menu);
        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        img_menu = findViewById(R.id.img_menu);
        bottom_menu.setItemSelected(R.id.menu_chat, true);
        bottom_menu.setOnItemSelectedListener(listener);
    }
}
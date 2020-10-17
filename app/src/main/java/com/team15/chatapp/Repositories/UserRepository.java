package com.team15.chatapp.Repositories;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.team15.chatapp.Model.User;
import java.util.List;
import java.util.Observable;


public class UserRepository extends Observable {
    private MutableLiveData<List<User>> allUsers = new MutableLiveData<>();

    private static UserRepository userRepository;
    DatabaseReference reference;
    FirebaseUser firebaseUser;

    public static UserRepository getInstance() {
        if (userRepository == null) {
            userRepository = new UserRepository();
        }
        return userRepository;
    }

    private UserRepository() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }



    public LiveData<User> getCurrentUser(String userId) {
        final MutableLiveData<User> currentUser = new MutableLiveData<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    currentUser.postValue(dataSnapshot.getValue(User.class));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return currentUser;
    }

}

package com.team15.chatapp.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.team15.chatapp.Model.User;
import com.team15.chatapp.Repositories.UserRepository;


public class UserViewModel extends ViewModel {
    private UserRepository repository;
    private LiveData<User> getUser;

    public UserViewModel() {
        super();
        repository = UserRepository.getInstance();
    }



    public LiveData<User> getUser(String userId) {
        getUser = repository.getCurrentUser(userId);

        return getUser;
    }
}
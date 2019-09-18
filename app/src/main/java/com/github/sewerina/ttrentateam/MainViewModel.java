package com.github.sewerina.ttrentateam;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.sewerina.ttrentateam.db.UserDao;
import com.github.sewerina.ttrentateam.db.UserEntity;

import java.util.List;

public class MainViewModel extends ViewModel {
    private UserDao mUserDao;
    private final MutableLiveData<List<UserEntity>> mUsers = new MutableLiveData<>();

    public MainViewModel() {
        mUserDao = TaskApp.sUserDao;
    }

    public LiveData<List<UserEntity>> getUsers() {
        return mUsers;
    }

    public void load() {

    }
}

package com.example.managepurchase.Interface;

import com.example.managepurchase.classes.User;

public interface UserCallBack {
    void onUserRetrieved(User user);
    void onError(String errorMessage);
}

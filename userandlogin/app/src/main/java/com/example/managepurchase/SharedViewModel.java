package com.example.managepurchase;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.managepurchase.classes.User;

public class SharedViewModel extends AndroidViewModel {
    private MutableLiveData<User> userLiveData = new MutableLiveData<>();
    private MutableLiveData<String> dateLiveData = new MutableLiveData<>();
    private MutableLiveData<String> timeLiveData = new MutableLiveData<>();
    public SharedViewModel(Application application) {
        super(application);
    }

    // הסטת User
    public void setUser(User user) {
        userLiveData.setValue(user);
    }

    public LiveData<User> getUser() {
        return userLiveData;
    }
    public boolean getIfUserAdmin() {
        if (userLiveData.getValue() != null) {
            return userLiveData.getValue().isAdmin();
        }
        return false;
    }
    public void setDate(String date) {
        dateLiveData.setValue(date);
    }

    public LiveData<String> getDate() {
        return dateLiveData;
    }

    public void setTime(String time) {
        timeLiveData.setValue(time);
    }

    public LiveData<String> getTime() {
        return timeLiveData;
    }
}

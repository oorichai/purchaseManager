package com.example.managepurchase;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.managepurchase.classes.Appointment;
import com.example.managepurchase.classes.User;
public class SharedViewModel extends AndroidViewModel {
    private MutableLiveData<User> userLiveData = new MutableLiveData<>();
    private MutableLiveData<User> userToUpdate = new MutableLiveData<>();
    private MutableLiveData<String> dateLiveData = new MutableLiveData<>();
    private MutableLiveData<String> timeLiveData = new MutableLiveData<>();

    private MutableLiveData<String> openHours = new MutableLiveData<>();
    private MutableLiveData<String> closeHours = new MutableLiveData<>();

    private MutableLiveData<Appointment> selectedAppointmentLiveData = new MutableLiveData<>();

    public SharedViewModel(Application application) {
        super(application);
    }

    // Setter ו-Getter ל-User
    public void setUser(User user) {
        userLiveData.setValue(user);
    }
    public void setUserToUpdate(User user) {
        userToUpdate.setValue(user);
    }
    public LiveData<User> getUserToUpdate() {
        return userToUpdate;
    }
    public LiveData<User> getUser() {
        return userLiveData;
    }

    public boolean getIfUserAdmin() {
        return userLiveData.getValue() != null && userLiveData.getValue().isAdmin();
    }

    // Setter ו-Getter ל-Date
    public void setDate(String date) {
        dateLiveData.setValue(date);
    }

    public LiveData<String> getDate() {
        return dateLiveData;
    }

    // Setter ו-Getter ל-Time
    public void setTime(String time) {
        timeLiveData.setValue(time);
    }

    public LiveData<String> getTime() {
        return timeLiveData;
    }

    // Setter ו-Getter ל-Appointment הנבחר
    public void setSelectedAppointment(Appointment appointment) {
        selectedAppointmentLiveData.setValue(appointment);
    }
    public void setOpenTime(String hour){
        openHours.setValue(hour);
    }
    public LiveData<String> getOpenTime(){
        return openHours;
    };
    public void setCloseTime(String hour){
        closeHours.setValue(hour);
    }
    public LiveData<String> getCloseTime(){
        return closeHours;
    };
    public LiveData<Appointment> getSelectedAppointment() {
        return selectedAppointmentLiveData;
    }
}


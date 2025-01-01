package com.example.managepurchase.classes;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.managepurchase.Interface.UserCallBack;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class AppointmentRepository {
    private DatabaseReference database;
    private DatabaseReference usersTable;
    private User user = null;
    public AppointmentRepository() {
        database = FirebaseDatabase.getInstance().getReference("appointments");
        usersTable = FirebaseDatabase.getInstance().getReference("users");
    }

    public void addAppointment(String providerId, Appointment appointment) {
        String key = database.child(providerId).push().getKey();
        database.child(providerId).child(key).setValue(appointment);
    }

    public void getUserById(String email, UserCallBack callback) {
        usersTable.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    DataSnapshot userSnapshot = dataSnapshot.getChildren().iterator().next();  // Take the first match
                    User user = userSnapshot.getValue(User.class);  // Convert to User class
                    callback.onUserRetrieved(user);  // Pass the user to the callback
                } else {
                    callback.onError("No user found with UID: " + email);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError("Error: " + databaseError.getMessage());
            }
        });
    }

    public void updateAppointment(String providerId, String appointmentId, Map<String, Object> updates) {
        database.child(providerId).child(appointmentId).updateChildren(updates);
    }

    public void deleteAppointment(String providerId, String appointmentId) {
        database.child(providerId).child(appointmentId).removeValue();
    }
    public void uploadUser(User user) {
        String userId = user.getUserId();
        if (userId != null) {
            usersTable.child(userId).setValue(user)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            System.out.println("User uploaded successfully!");
                        } else {
                            System.err.println("Failed to upload user: " + task.getException());
                        }
                    });
        } else {
            System.err.println("Failed to generate a unique ID for the user.");
        }
    }
    public void saveDate(String date, String time, User user) {
        // Reference to the specific date and time in the database
        DatabaseReference appointmentRef = database.child("appointments").child(date).child(time);
        appointmentRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                if (snapshot.exists()) {
                    System.out.println("This appointment is already taken.");
                } else {
                    Appointment appointment = new Appointment(user, time, date);
                    appointmentRef.setValue(appointment).addOnCompleteListener(saveTask -> {
                        if (saveTask.isSuccessful()) {
                            System.out.println("Succeeded to save the appointment.");
                        } else {
                            System.out.println("Failed to save the appointment.");
                        }
                    });
                }
            } else {
                // טיפול בשגיאה בבדיקה
                System.out.println("Failed to check appointment availability: " + task.getException().getMessage());
            }
        });
    }

//    public void getAppointments(String providerId, DataCallback callback) {
//        database.child(providerId).get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                Map<String, Appointment> appointments = new HashMap<>();
//                task.getResult().getChildren().forEach(snapshot -> {
//                    Appointment appointment = snapshot.getValue(Appointment.class);
//                    appointments.put(snapshot.getKey(), appointment);
//                });
//                callback.onDataLoaded(appointments);
//            } else {
//                callback.onDataError(task.getException());
//            }
//        });
//    }
}

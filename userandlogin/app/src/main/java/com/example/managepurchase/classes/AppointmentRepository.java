package com.example.managepurchase.classes;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.managepurchase.Adapter.AppointmentAdapter;
import com.example.managepurchase.Interface.UserCallBack;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Map;

public class AppointmentRepository {
    private DatabaseReference database;
    private DatabaseReference usersTable;
    private Fragment fragment;

    private User user = null;
    public AppointmentRepository(Fragment fragment) {
        database = FirebaseDatabase.getInstance().getReference("appointments");
        usersTable = FirebaseDatabase.getInstance().getReference("users");
        this.fragment=fragment;
    }

    public void addAppointment(String providerId, Appointment appointment) {
        String key = database.child(providerId).push().getKey();
        database.child(providerId).child(key).setValue(appointment);
    }

    public void getUserByEmail(String email, UserCallBack callback) {
        if (email == null || email.isEmpty()) {
            callback.onError("Email is null or empty.");
            return;
        }

        Query query = usersTable.orderByChild("email").equalTo(email);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    DataSnapshot userSnapshot = dataSnapshot.getChildren().iterator().next();  // Take the first match
                    User user = userSnapshot.getValue(User.class);  // Convert to User class
                    if (user != null) {
                        callback.onUserRetrieved(user);
                        System.out.println("User retrieved successfully!");
                    } else {
                        callback.onError("User data is null or could not be parsed.");
                    }
                } else {
                    callback.onError("No user found with email: " + email);
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

    public void deleteAppointment(String date, String time) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("appointments").child(date).child(time);
        databaseReference.child(time).removeValue();
        Toast.makeText(fragment.getContext(), "Appointment deleted successfully.", Toast.LENGTH_SHORT).show();
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
        DatabaseReference appointmentRef = database.child(date).child(time);
        Context context = fragment.getContext();
        appointmentRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                if (snapshot.exists()) {
                    Toast.makeText(context, "This appointment is already taken.", Toast.LENGTH_SHORT).show();

                } else {
                    Appointment appointment = new Appointment(user, date, time);
                    appointmentRef.setValue(appointment).addOnCompleteListener(saveTask -> {
                        if (saveTask.isSuccessful()) {
                            Toast.makeText(context, "Succeeded to save the appointment.", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(context, "Failed to save the appointment.", Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            } else {
                Toast.makeText(context, "Failed to check appointment availability:."+task.getException().getMessage(), Toast.LENGTH_SHORT).show();


            }
        });
    }

    public void getAppointments(String selectedDate, AppointmentAdapter adapter) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("appointments").child(selectedDate);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Appointment> filteredAppointments = new ArrayList<>();

                for (DataSnapshot timeSlotSnapshot : snapshot.getChildren()) {
                    Appointment appointment = timeSlotSnapshot.getValue(Appointment.class);

                    if (appointment != null && appointment.isAvailable()) {
                        filteredAppointments.add(appointment);
                    }
                }

                if (!filteredAppointments.isEmpty()) {
                    adapter.updateAppointments(filteredAppointments);
                } else {
                    adapter.updateAppointments(new ArrayList<>());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(fragment.getContext(), "שגיאה בטעינת הפגישות.", Toast.LENGTH_SHORT).show();
            }
        });

    }
}

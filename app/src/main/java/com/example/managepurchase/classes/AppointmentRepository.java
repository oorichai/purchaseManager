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

public class AppointmentRepository {
    private DatabaseReference database;
    private DatabaseReference usersTable;
    private DatabaseReference holidaysTable;
    private Fragment fragment;

    public AppointmentRepository(Fragment fragment) {
        database = FirebaseDatabase.getInstance().getReference("appointments");
        usersTable = FirebaseDatabase.getInstance().getReference("users");
        holidaysTable = FirebaseDatabase.getInstance().getReference("holidays");
        this.fragment = fragment;
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
                    DataSnapshot userSnapshot = dataSnapshot.getChildren().iterator().next();
                    User user = userSnapshot.getValue(User.class);
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
        Context context = fragment.getContext();
        holidaysTable.child(date).get().addOnCompleteListener(holidayTask -> {
            if (holidayTask.isSuccessful()) {
                DataSnapshot holidaySnapshot = holidayTask.getResult();
                if (holidaySnapshot.exists()) {
                    Toast.makeText(context, "This day is marked as a holiday. Appointments are not allowed.", Toast.LENGTH_SHORT).show();
                } else {
                    DatabaseReference appointmentRef = database.child(date).child(time);
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
                            Toast.makeText(context, "Failed to check appointment availability: "
                                    + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                Toast.makeText(context, "Failed to check holiday status: "
                        + holidayTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(fragment.getContext(), "faild on meeting upload", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void setHoliday(String date) {
        Context context = fragment.getContext();
        holidaysTable.child(date).setValue(true).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(context, "Holiday set for " + date, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Failed to set holiday: "
                        + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    /**
     * מעדכן את שעות הפתיחה והסגירה עבור תאריך מסוים ב־Firebase, לאחר בדיקה שאין התנגשות עם פגישות קיימות.
     *
     * @param date          התאריך (בפורמט "YYYY-MM-DD")
     * @param openingHour   שעת פתיחה (0-23)
     * @param openingMinute דקות פתיחה
     * @param closingHour   שעת סגירה (0-23)
     * @param closingMinute דקות סגירה
     */
    public void updateBusinessHours(String date, int openingHour, int openingMinute, int closingHour, int closingMinute) {
        Context context = fragment.getContext();
        if (openingHour > closingHour || (openingHour == closingHour && openingMinute >= closingMinute)) {
            Toast.makeText(context, "טווח שעות לא תקין", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference appointmentsDateRef = database.child(date);
        appointmentsDateRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean conflict = false;
                for (DataSnapshot timeSlotSnapshot : snapshot.getChildren()) {
                    Appointment appointment = timeSlotSnapshot.getValue(Appointment.class);
                    if (appointment != null) {
                        // נניח ש-Appointment מכיל מתודה getTime() המחזירה מחרוזת בפורמט "HH:mm"
                        String appointmentTime = appointment.getTime();
                        if (appointmentTime != null && appointmentTime.contains(":")) {
                            String[] parts = appointmentTime.split(":");
                            int appHour = Integer.parseInt(parts[0]);
                            int appMinute = Integer.parseInt(parts[1]);
                            int appTotalMinutes = appHour * 60 + appMinute;
                            int openingTotalMinutes = openingHour * 60 + openingMinute;
                            int closingTotalMinutes = closingHour * 60 + closingMinute;

                            if (appTotalMinutes < openingTotalMinutes || appTotalMinutes > closingTotalMinutes) {
                                conflict = true;
                                break;
                            }
                        }
                    }
                }

                if (conflict) {
                    Toast.makeText(context, "לא ניתן לעדכן שעות: קיימת פגישה שתיפגע מהשינוי", Toast.LENGTH_SHORT).show();
                } else {
                    // עדכון שעות הפעילות ב־Firebase תחת צומת "businessHours"
                    DatabaseReference businessHoursRef = FirebaseDatabase.getInstance().getReference("businessHours");
                    BusinessHours businessHours = new BusinessHours(openingHour, openingMinute, closingHour, closingMinute);
                    businessHoursRef.child(date).setValue(businessHours).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "שעות פתיחה וסגירה עודכנו לתאריך: " + date, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "עדכון שעות נכשל: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "עדכון שעות נכשל: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

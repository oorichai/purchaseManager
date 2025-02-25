package com.example.managepurchase.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.managepurchase.R;
import com.example.managepurchase.SharedViewModel;
import com.example.managepurchase.classes.Appointment;
import com.example.managepurchase.classes.AppointmentRepository;
import com.example.managepurchase.classes.BusinessHours;
import com.example.managepurchase.classes.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AppointmentsFragment extends Fragment {
    private CalendarView calendarView;
    private TimePicker timePicker;
    private AppointmentRepository appointmentRepository;
    private Button btnBookAppointment;
    private Button btnUpdateAppointment;
    private ProgressBar progressBar;
    private  SharedViewModel sharedViewModel;
    private View view;
    private Appointment appointment;
    private DatabaseReference appointmentRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.getDate().observe(getViewLifecycleOwner(), selectedDate -> {
            TextView selectedDateTextView = view.findViewById(R.id.selectedDate);
            selectedDateTextView.setText(selectedDate);
        });
        appointmentRepository = new AppointmentRepository(this);
        return view = inflater.inflate(R.layout.fragment_appointments, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        calendarView = view.findViewById(R.id.calendarView);
        timePicker = view.findViewById(R.id.timePicker);
        btnBookAppointment = view.findViewById(R.id.btnBookAppointment);
        btnUpdateAppointment = view.findViewById(R.id.btnUpdateAppointment);
        progressBar = view.findViewById(R.id.progressBar);

        btnBookAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bookAppointment();
            }
        });
        btnUpdateAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateAppointment();
            }
        });
    }
    private void checkBusinessHoursAndProceed(String date, int hour, int minute, Runnable onSuccess) {
        DatabaseReference businessHoursRef = FirebaseDatabase.getInstance()
                .getReference("businessHours")
                .child(date);
        businessHoursRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int startHour, startMinute, endHour, endMinute;
                if (snapshot.exists()) {
                    BusinessHours bh = snapshot.getValue(BusinessHours.class);
                    if (bh != null) {
                        startHour = bh.getOpeningHour();
                        startMinute = bh.getOpeningMinute();
                        endHour = bh.getClosingHour();
                        endMinute = bh.getClosingMinute();
                    } else {
                        startHour = 9; startMinute = 0; endHour = 17; endMinute = 0;
                    }
                } else {
                    startHour = 9; startMinute = 0; endHour = 17; endMinute = 0;
                }
                int appointmentTime = hour * 60 + minute;
                int businessStart = startHour * 60 + startMinute;
                int businessEnd = endHour * 60 + endMinute;
                if (appointmentTime < businessStart || appointmentTime > businessEnd) {
                    Toast.makeText(getContext(), "Appointments must be scheduled between "
                            + String.format("%02d:%02d", startHour, startMinute)
                            + " and " + String.format("%02d:%02d", endHour, endMinute) + ".", Toast.LENGTH_SHORT).show();
                    return;
                }
                onSuccess.run();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error fetching business hours: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bookAppointment() {
        timePicker = view.findViewById(R.id.timePicker);
        String date = sharedViewModel.getDate().getValue();
        if (date == null || date.isEmpty()) {
            Toast.makeText(getContext(), "Date is not chosen.", Toast.LENGTH_SHORT).show();
            return;
        }

        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();

        if (minute != 0) {
            Toast.makeText(getContext(), "Appointments must be scheduled on round hours.", Toast.LENGTH_SHORT).show();
            return;
        }

        checkBusinessHoursAndProceed(date, hour, minute, () -> {
            String time = String.format("%02d:%02d", hour, minute);
            User currentUser = sharedViewModel.getUser().getValue();
            appointmentRepository.saveDate(date, time, currentUser);
        });
    }

    private void updateAppointment() {
        String newDate = sharedViewModel.getDate().getValue();
        if (newDate == null || newDate.isEmpty()) {
            Toast.makeText(getContext(), "No date selected for update.", Toast.LENGTH_SHORT).show();
            return;
        }

        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();

        // בדיקה: עדכון הפגישה צריך להיות בשעה עגולה
        if (minute != 0) {
            Toast.makeText(getContext(), "Appointments must be scheduled on round hours.", Toast.LENGTH_SHORT).show();
            return;
        }

        // בדיקת שעות פעילות העסק עבור התאריך החדש
        checkBusinessHoursAndProceed(newDate, hour, minute, () -> {
            String newTime = String.format("%02d:%02d", hour, minute);
            Appointment appointment = sharedViewModel.getSelectedAppointment().getValue();
            if (appointment == null) {
                Toast.makeText(getContext(), "No appointment selected for update", Toast.LENGTH_SHORT).show();
                return;
            }

            String oldDate = appointment.getDate();
            String oldTime = appointment.getTime();

            appointment.setDate(newDate);
            appointment.setTime(newTime);

            DatabaseReference newRef = FirebaseDatabase.getInstance()
                    .getReference("appointments")
                    .child(newDate)
                    .child(newTime);

            newRef.setValue(appointment).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "The appointment was updated successfully.", Toast.LENGTH_SHORT).show();

                    if (!oldDate.equals(newDate) || !oldTime.equals(newTime)) {
                        DatabaseReference oldRef = FirebaseDatabase.getInstance()
                                .getReference("appointments")
                                .child(oldDate)
                                .child(oldTime);
                        oldRef.removeValue();
                    }

                    Navigation.findNavController(view).popBackStack();
                } else {
                    Toast.makeText(getContext(), "update the appointment failed.", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }




}
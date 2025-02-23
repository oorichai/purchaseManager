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

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.managepurchase.R;
import com.example.managepurchase.SharedViewModel;
import com.example.managepurchase.classes.Appointment;
import com.example.managepurchase.classes.AppointmentRepository;
import com.example.managepurchase.classes.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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



//    private void bookAppointment() {
//        timePicker = view.findViewById(R.id.timePicker);
//        String date = sharedViewModel.getDate().getValue();
//        if (date == null || date.isEmpty()) {
//            Toast.makeText(getContext(), "date dosen't choose..", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        int hour = timePicker.getHour();
//        int minute = timePicker.getMinute();
//        String time = String.format("%02d:%02d", hour, minute);
//        User currentUser = sharedViewModel.getUser().getValue();
//        appointmentRepository.saveDate(date, time,currentUser);
//
//    }
private void bookAppointment() {
    timePicker = view.findViewById(R.id.timePicker);
    String date = sharedViewModel.getDate().getValue();
    if (date == null || date.isEmpty()) {
        Toast.makeText(getContext(), "Date is not chosen.", Toast.LENGTH_SHORT).show();
        return;
    }

    int hour = timePicker.getHour();
    int minute = timePicker.getMinute();

    // Check that appointment is scheduled on a round hour (minutes must be 0)
    if (minute != 0) {
        Toast.makeText(getContext(), "Appointments must be scheduled on round hours.", Toast.LENGTH_SHORT).show();
        return;
    }

    // Define allowed time range (e.g., from 9:00 to 17:00)
    int startHour = 9;
    int endHour = 17;
    if (hour < startHour || hour > endHour) {
        Toast.makeText(getContext(), "Appointments must be scheduled between "
                + startHour + ":00 and " + endHour + ":00.", Toast.LENGTH_SHORT).show();
        return;
    }

    String time = String.format("%02d:%02d", hour, minute);
    User currentUser = sharedViewModel.getUser().getValue();
    appointmentRepository.saveDate(date, time, currentUser);
}

    private void updateAppointment() {
        String newDate = sharedViewModel.getDate().getValue();
        if (newDate == null || newDate.isEmpty()) {
            Toast.makeText(getContext(), "לא נבחר תאריך לעדכון.", Toast.LENGTH_SHORT).show();
            return;
        }

        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();
        String newTime = String.format("%02d:%02d", hour, minute);

        Appointment appointment = sharedViewModel.getSelectedAppointment().getValue();
        if (appointment == null) {
            Toast.makeText(getContext(), "לא נבחר תור לעדכון.", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getContext(), "התור עודכן בהצלחה.", Toast.LENGTH_SHORT).show();

                if (!oldDate.equals(newDate) || !oldTime.equals(newTime)) {
                    DatabaseReference oldRef = FirebaseDatabase.getInstance()
                            .getReference("appointments")
                            .child(oldDate)
                            .child(oldTime);
                    oldRef.removeValue();
                }

                Navigation.findNavController(view).popBackStack();
            } else {
                Toast.makeText(getContext(), "עדכון התור נכשל.", Toast.LENGTH_SHORT).show();
            }
        });
    }



}
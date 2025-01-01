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
        appointmentRepository = new AppointmentRepository();
        return view = inflater.inflate(R.layout.fragment_appointments, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        calendarView = view.findViewById(R.id.calendarView);
        timePicker = view.findViewById(R.id.timePicker);
        btnBookAppointment = view.findViewById(R.id.btnBookAppointment);
        progressBar = view.findViewById(R.id.progressBar);

        btnBookAppointment.setOnClickListener(v -> bookAppointment());
    }

    private void bookAppointment() {
        timePicker = view.findViewById(R.id.timePicker);
            String date = sharedViewModel.getDate().getValue();
            if (date == null || date.isEmpty()) {
                Toast.makeText(getContext(), "תאריך לא נבחר", Toast.LENGTH_SHORT).show();
                return;
            }

            // שליפת השעה מה-TimePicker
            int hour = timePicker.getHour();
            int minute = timePicker.getMinute();
            String time = String.format("%02d:%02d", hour, minute);
            User currentUser = sharedViewModel.getUser().getValue();
            appointmentRepository.saveDate(date, time,currentUser);
    }

}

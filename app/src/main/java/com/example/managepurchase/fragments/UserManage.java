package com.example.managepurchase.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.managepurchase.Adapter.AppointmentAdapter;
import com.example.managepurchase.R;
import com.example.managepurchase.SharedViewModel;
import com.example.managepurchase.classes.Appointment;
import com.example.managepurchase.classes.AppointmentRepository;
import com.example.managepurchase.classes.User;
import com.example.managepurchase.classes.item_Data;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserManage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserManage extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private ArrayList<item_Data> item_data;
    private AppointmentAdapter adapter;
    private SharedViewModel sharedViewModel;
    private ArrayList<Appointment> appointmentList;
    private View view;
    private AppointmentRepository appointmentRepository;
    private String mParam1;
    private String mParam2;
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters

    public UserManage() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserManage.
     */
    // TODO: Rename and change types and number of parameters
    public static UserManage newInstance(String param1, String param2) {
        UserManage fragment = new UserManage();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        item_data = new ArrayList<>();
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        appointmentList = new ArrayList<>();
        adapter = new AppointmentAdapter(appointmentList, sharedViewModel);
        appointmentRepository = new AppointmentRepository(this);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container,
                         Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    view = inflater.inflate(R.layout.fragment_user_manage, container, false);
    RecyclerView recyclerView = view.findViewById(R.id.RecycleView);
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    recyclerView.setItemAnimator(new DefaultItemAnimator());
    CalendarView calendarView = view.findViewById(R.id.calendarView);
    Button btnNext = view.findViewById(R.id.btnOpenFragmentApoointments);
    Button btnAdminHoliday = view.findViewById(R.id.btnAdminHoliday);
    Button btnSickDay = view.findViewById(R.id.btnSickDay);
    View layoutBusinessHours = view.findViewById(R.id.layoutBusinessHours);

    Button btnSaveHours = view.findViewById(R.id.btnSaveHours);
    TimePicker timePickerOpening = view.findViewById(R.id.timePickerOpening);
    TimePicker timePickerClosing = view.findViewById(R.id.timePickerClosing);
    timePickerOpening.setIs24HourView(true);
    timePickerClosing.setIs24HourView(true);
    timePickerOpening.setHour(8);
    timePickerOpening.setMinute(0);
    timePickerClosing.setHour(17);
    timePickerClosing.setMinute(0);

    final String[] selectedDate = {""};
    calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
        selectedDate[0] = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
        appointmentRepository.getAppointments(selectedDate[0], adapter);
    });

    btnNext.setOnClickListener(v -> {
        if (!selectedDate[0].isEmpty()) {
            sharedViewModel.setDate(selectedDate[0]);
            Navigation.findNavController(v).navigate(R.id.action_userManage_to_appointmentsFragment);
        } else {
            Toast.makeText(getContext(), "choose the date before the transaction", Toast.LENGTH_SHORT).show();
        }
    });

    User currentUser = sharedViewModel.getUser().getValue();
    if (currentUser != null && currentUser.isAdmin()) {
        btnAdminHoliday.setVisibility(View.VISIBLE);
        btnSickDay.setVisibility(view.VISIBLE);
        layoutBusinessHours.setVisibility(View.VISIBLE);
        btnSaveHours.setVisibility(View.VISIBLE);
        btnAdminHoliday.setOnClickListener(v -> {
            if (!selectedDate[0].isEmpty()) {
                appointmentRepository.setHoliday(selectedDate[0]);
                Toast.makeText(getContext(), "holiday define on date : " + selectedDate[0], Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "choose a date to holiday", Toast.LENGTH_SHORT).show();
            }
        });
        btnSickDay.setOnClickListener(v -> {
            if (!selectedDate[0].isEmpty()) {
                appointmentRepository.setHoliday(selectedDate[0]);
                Toast.makeText(getContext(), "sick day define on date : " + selectedDate[0], Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "choose a date to set a sick day", Toast.LENGTH_SHORT).show();
            }
        });
        btnSaveHours.setOnClickListener(v -> {
            int openingHour = timePickerOpening.getHour();
            int openingMinute = timePickerOpening.getMinute();
            int closingHour = timePickerClosing.getHour();
            int closingMinute = timePickerClosing.getMinute();

            boolean conflict = false;
            for (Appointment appointment : appointmentList) {
                String appointmentTime = appointment.getTime();
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

            if (conflict) {
                Toast.makeText(getContext(), "Unable to update hours: There is an appointment that will be affected by the change.", Toast.LENGTH_SHORT).show();
            } else {
                appointmentRepository.updateBusinessHours(selectedDate[0],
                        openingHour, openingMinute, closingHour, closingMinute);
                Toast.makeText(getContext(), "Opening and closing hours have been updated.", Toast.LENGTH_SHORT).show();
            }
        });
    } else {
        btnAdminHoliday.setVisibility(View.GONE);
        btnSickDay.setVisibility(view.GONE);
    }

    recyclerView.setAdapter(adapter);
    return view;
}

}
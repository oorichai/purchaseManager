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
import android.widget.Toast;

import com.example.managepurchase.Adapter.AppointmentAdapter;
import com.example.managepurchase.R;
import com.example.managepurchase.SharedViewModel;
import com.example.managepurchase.classes.Appointment;
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
    private static final String ARG_PARAM2 = "param2";
    private ArrayList<item_Data> item_data;
    private AppointmentAdapter adapter;
    private RecyclerView recyclerView;
    private Appointment appointment;
    private SharedViewModel sharedViewModel;
    private View view;
    private LinearLayoutManager layout;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

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
        recyclerView = view.findViewById(R.id.recyclerView);
        layout = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layout);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user_manage, container, false);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        CalendarView calendarView = view.findViewById(R.id.calendarView);
        Button btnNext = view.findViewById(R.id.btnOpenFragmentApoointments);

        // עדכון התאריך שנבחר
        final String[] selectedDate = {""}; // משתנה לשמירת התאריך שנבחר
        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            selectedDate[0] = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
        });

        // מעבר לפרגמנט הבא
        btnNext.setOnClickListener(v -> {
            if (!selectedDate[0].isEmpty()) {
                sharedViewModel.setDate(selectedDate[0]);
                Navigation.findNavController(v).navigate(R.id.action_userManage_to_appointmentsFragment);
            } else {
                Toast.makeText(getContext(), "בחר תאריך לפני המעבר", Toast.LENGTH_SHORT).show();
            }
        });


        return view;
    }
}
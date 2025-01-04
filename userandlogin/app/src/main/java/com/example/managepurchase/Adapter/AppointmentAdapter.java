package com.example.managepurchase.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.managepurchase.classes.Appointment;
import com.example.managepurchase.R;
import com.example.managepurchase.SharedViewModel;
import com.example.managepurchase.classes.Appointment;

import java.util.ArrayList;
import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {
    private List<Appointment> appointments;
    private SharedViewModel sharedViewModel;

    public AppointmentAdapter(List<Appointment> appointments, SharedViewModel sharedViewModel) {
        this.appointments = appointments;
        this.sharedViewModel = sharedViewModel;
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.appointment_item, parent, false); // Updated to use appointment_item
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        Appointment appointment = appointments.get(position);
        holder.bind(appointment);
    }
    public void updateAppointments(ArrayList<Appointment> newAppointments) {
        this.appointments.clear();
        this.appointments.addAll(newAppointments);
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return appointments.size();
    }

    // ViewHolder inner class
    public static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        TextView clientName, clientPhone, status;

            public AppointmentViewHolder(@NonNull View itemView) {
                super(itemView);
                clientName = itemView.findViewById(R.id.clientName);
                clientPhone = itemView.findViewById(R.id.clientPhone);
                status = itemView.findViewById(R.id.status);
            }

            // Bind data to the views
            public void bind(Appointment appointment) {
                clientName.setText(appointment.getClientName());
                clientPhone.setText(appointment.getClientPhone());
                status.setText(appointment.getFormattedDate()); // Combine date and time
            }
        }



}

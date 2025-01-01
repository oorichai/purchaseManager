package com.example.managepurchase.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.managepurchase.R;
import com.example.managepurchase.SharedViewModel;
import com.example.managepurchase.classes.Appointment;

import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {
    private List<Appointment> appointments;
    private SharedViewModel sharedViewModel;
    private OnAppointmentClickListener listener;

    public AppointmentAdapter(List<Appointment> appointments, SharedViewModel sharedViewModel, OnAppointmentClickListener listener) {
        this.appointments = appointments;
        this.sharedViewModel = sharedViewModel;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.appointment_item, parent, false);
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        Appointment appointment = appointments.get(position);

        holder.clientNameTextView.setText(appointment.getClientName());
        holder.clientPhoneTextView.setText(appointment.getClientPhone());
        holder.statusTextView.setText(appointment.isAvailable() ? "Available" : "Booked");

    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    // ViewHolder inner class
    public static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        TextView clientNameTextView;
        TextView clientPhoneTextView;
        TextView statusTextView;

        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            clientNameTextView = itemView.findViewById(R.id.clientName);
            clientPhoneTextView = itemView.findViewById(R.id.clientPhone);
            statusTextView = itemView.findViewById(R.id.status);
        }
    }

    public interface OnAppointmentClickListener {
        void onAppointmentClick(Appointment appointment);
    }
}

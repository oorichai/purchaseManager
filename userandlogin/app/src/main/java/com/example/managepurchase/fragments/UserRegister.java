package com.example.managepurchase.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.managepurchase.R;
import com.example.managepurchase.classes.AppointmentRepository;
import com.example.managepurchase.classes.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserRegister#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class UserRegister extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View view;
    private FirebaseAuth mAuth;
    private User user;



    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserRegister.
     */
    // TODO: Rename and change types and number of parameters
    public static UserRegister newInstance(String param1, String param2) {
        UserRegister fragment = new UserRegister();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public UserRegister() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mAuth = FirebaseAuth.getInstance();
    }
    private void register() {
        EditText emailEditText = view.findViewById(R.id.emailRegister);
        String email = emailEditText.getText().toString().trim();
        EditText nameEditText = view.findViewById(R.id.NameOfClient);
        String name = nameEditText.getText().toString();
        EditText providerEditText = view.findViewById(R.id.providerID);
        String provider = providerEditText.getText().toString();
        EditText passwordEditText = view.findViewById(R.id.editTextPasswordfirstTime);
        String password = passwordEditText.getText().toString();
        EditText passwordEditText2 = view.findViewById(R.id.editTextPasswordSecoundTime);
        String password2 = passwordEditText2.getText().toString();
        EditText phoneEditText = view.findViewById(R.id.editTextPhoneNumber);
        String phone = phoneEditText.getText().toString().trim();
        CheckBox checkBox= view.findViewById(R.id.checkBoxAdmin);
        AppointmentRepository appointmentRepository = new AppointmentRepository();

        if (email.isEmpty()) {
            Toast.makeText(getContext(), "Please enter an email", Toast.LENGTH_LONG).show();
            return;
        }
        if (name.isEmpty()) {
            Toast.makeText(getContext(), "Please enter a name", Toast.LENGTH_LONG).show();
            return;
        }

        if (!email.contains("@")) {
            Toast.makeText(getContext(), "Email is not valid", Toast.LENGTH_LONG).show();
            return;
        }
        if (password.isEmpty() || password2.isEmpty()) {
            Toast.makeText(getContext(), "Please enter both passwords", Toast.LENGTH_LONG).show();
            return;
        }
        if (!password.equals(password2)) {
            Toast.makeText(getContext(), "Passwords do not match", Toast.LENGTH_LONG).show();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(getContext(), "Password must be at least 6 characters", Toast.LENGTH_LONG).show();
            return;
        }
        if (phone.isEmpty() || phone.length() != 10) {
            Toast.makeText(getContext(), "Phone number is not valid", Toast.LENGTH_LONG).show();
            return;
        }
        if (provider.isEmpty()&&!checkBox.isChecked()) {
            Toast.makeText(getContext(), "Please enter a provider / or tour the provider and enter providerID", Toast.LENGTH_LONG).show();
            return;
        }
        user = new User(name, email, password, phone, provider, mAuth.getUid());
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Create user successful", Toast.LENGTH_LONG).show();
                            appointmentRepository.uploadUser(user);
                            Navigation.findNavController(view).navigate(R.id.action_userRegister_to_userLogin);
                        } else {
                            Toast.makeText(getContext(), "Create user failed", Toast.LENGTH_LONG).show();
                        }
                    }
                });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user_register, container, false);
        Button BackToLoginFragButton = view.findViewById(R.id.BackToLoginFragButton);
        BackToLoginFragButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_userRegister_to_userLogin);
            }
        });
        Button registrationButton = view.findViewById(R.id.registrationButton);
        registrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Register...", Toast.LENGTH_SHORT).show();
                register();

            }
        });



        return view;
    }
}
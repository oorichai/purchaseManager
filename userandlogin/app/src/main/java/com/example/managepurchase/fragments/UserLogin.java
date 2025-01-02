package com.example.managepurchase.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.managepurchase.Interface.UserCallBack;
import com.example.managepurchase.R;
import com.example.managepurchase.SharedViewModel;
import com.example.managepurchase.classes.AppointmentRepository;
import com.example.managepurchase.classes.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserLogin#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserLogin extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private FirebaseAuth mAuth;
    private View view;
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private SharedViewModel sharedViewModel;
    private AppointmentRepository appointmentRepository;
    private DatabaseReference databaseReference;

    public UserLogin() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserLogin.
     */
    // TODO: Rename and change types and number of parameters
    public static UserLogin newInstance(String param1, String param2) {
        UserLogin fragment = new UserLogin();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mAuth=FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        appointmentRepository = new AppointmentRepository();
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    private void login() {

        EditText emailEditText = view.findViewById(R.id.editTextTextEmail);
        EditText passwordEditText = view.findViewById(R.id.editTextpassword);
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();


        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Please enter both email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(getContext(), "Invalid email format", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(getContext(), "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Login successful", Toast.LENGTH_LONG).show();
                            appointmentRepository.getUserByEmail(email, new UserCallBack() {
                                @Override
                                public void onUserRetrieved(User user) {
                                    sharedViewModel.setUser(user);
                                    Navigation.findNavController(view).navigate(R.id.action_userLogin_to_userManage);
                                }
                                @Override
                                public void onError(String errorMessage) {
                                    System.out.println("Error: " + errorMessage);
                                }
                            });
                            }
                         else {
                            Toast.makeText(getContext(), "invalid Username or password ", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                });
        return;
    }
@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user_login, container, false);
         Button LoginButton1 = view.findViewById(R.id.LoginButton);
         LoginButton1.setOnClickListener(new View.OnClickListener() {
             @Override
         public void onClick(View v) {
             Toast.makeText(getContext(), "Logging in...", Toast.LENGTH_SHORT).show();
             login();
         }
        });
          Button registrationButton1 = view.findViewById(R.id.RegisterButton);
          registrationButton1.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  Navigation.findNavController(v).navigate(R.id.action_userLogin_to_userRegister);
              }
          });

          return view;
        }

}
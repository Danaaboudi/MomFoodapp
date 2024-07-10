package com.example.momfood.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.momfood.R;
import com.example.momfood.activity.LoginRegisterActivity;
import com.example.momfood.activity.MainActivity;
import com.example.momfood.model.User;
import com.example.momfood.util.CustomToast;
import com.example.momfood.util.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUp_Fragment extends Fragment implements OnClickListener {
    private static View view;
    private static EditText fullName, emailId, mobileNumber, password;
    private static TextView login;
    private static Button signUpButton;
    private static CheckBox terms_conditions;
    ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    public SignUp_Fragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.signup_layout, container, false);
        initViews();
        setListeners();
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        return view;
    }

    // Initialize all views
    private void initViews() {
        fullName = view.findViewById(R.id.fullName);
        emailId = view.findViewById(R.id.userEmailId);
        mobileNumber = view.findViewById(R.id.mobileNumber);
        password = view.findViewById(R.id.password);
        signUpButton = view.findViewById(R.id.signUpBtn);
        login = view.findViewById(R.id.already_user);
        terms_conditions = view.findViewById(R.id.terms_conditions);
        progressDialog = new ProgressDialog(getContext());

        // Setting text selector over text views
        @SuppressLint("ResourceType") XmlResourceParser xrp = getResources().getXml(R.drawable.text_selector);
        try {
            ColorStateList csl = ColorStateList.createFromXml(getResources(), xrp);
            login.setTextColor(csl);
            terms_conditions.setTextColor(csl);
        } catch (Exception e) {
        }
    }

    // Set Listeners
    private void setListeners() {
        signUpButton.setOnClickListener(this);
        login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();

        if (viewId == R.id.signUpBtn) {
            // Call checkValidation method
            checkValidation();
        } else if (viewId == R.id.already_user) {
            // Replace login fragment
            new LoginRegisterActivity().replaceLoginFragment();
        }
    }

    // Check Validation Method
    private void checkValidation() {
        // Get all edittext texts
        final String getFullName = fullName.getText().toString();
        final String getEmailId = emailId.getText().toString();
        final String getMobileNumber = mobileNumber.getText().toString();
        final String getPassword = password.getText().toString();
        // Pattern match for email id
        Pattern p = Pattern.compile(Utils.regEx);
        Matcher m = p.matcher(getEmailId);

        if (getFullName.length() == 0) {
            fullName.setError("Enter Your Name");
            fullName.requestFocus();
        } else if (getEmailId.length() == 0) {
            emailId.setError("Enter Your Email");
            emailId.requestFocus();
        } else if (!m.find()) {
            emailId.setError("Enter Correct Email");
            emailId.requestFocus();
        } else if (getMobileNumber.length() == 0) {
            mobileNumber.setError("Enter Your Mobile Number");
            mobileNumber.requestFocus();
        } else if (getPassword.length() == 0) {
            password.setError("Enter Password");
            password.requestFocus();
        } else if (getPassword.length() < 6) {
            password.setError("Enter 6 digit Password");
            password.requestFocus();
        } else if (!terms_conditions.isChecked()) {
            new CustomToast().Show_Toast(getActivity(), view, "Accept Terms & Conditions");
        } else {
            progressDialog.setMessage("Registering Data....");
            progressDialog.show();

            mAuth.createUserWithEmailAndPassword(getEmailId, getPassword)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(getFullName).build();
                                firebaseUser.updateProfile(profileUpdates)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    saveUserToDatabase(firebaseUser, getFullName, getEmailId, getMobileNumber, getPassword);
                                                    startActivity(new Intent(getActivity(), MainActivity.class));
                                                    getActivity().finish();
                                                    getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                                                }
                                            }
                                        });
                            } else {
                                try {
                                    throw task.getException();
                                } catch (FirebaseAuthWeakPasswordException e) {
                                    password.setError("Weak password.");
                                    password.requestFocus();
                                } catch (FirebaseAuthUserCollisionException e) {
                                    emailId.setError("This email is already in use.");
                                    emailId.requestFocus();
                                } catch (Exception e) {
                                    new CustomToast().Show_Toast(getActivity(), view, "Registration failed.");
                                }
                            }
                        }
                    });
        }
    }

    private void saveUserToDatabase(FirebaseUser firebaseUser, String fullName, String email, String mobileNumber, String password ) {
        User user = new User(firebaseUser.getUid(), fullName, email, mobileNumber, password);
        mDatabase.child("users").child(firebaseUser.getUid()).setValue(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            new CustomToast().Show_Toast(getActivity(), view, "Failed to save user data.");
                        }
                    }
                });
    }
}

package com.example.momfood.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.momfood.R;
import com.example.momfood.activity.MainActivity;
import com.example.momfood.model.User;
import com.example.momfood.util.CustomToast;
import com.example.momfood.util.Utils;
import com.example.momfood.util.localstorage.LocalStorage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import androidx.annotation.NonNull;

public class Login_Fragment extends Fragment implements OnClickListener {
    private static View view;
    private static TextInputEditText emailid, password;
    private static Button loginButton;
    private static TextView forgotPassword, signUp;
    private static CheckBox show_hide_password;
    private static LinearLayout loginLayout;
    private static Animation shakeAnimation;
    private static FragmentManager fragmentManager;

    ProgressDialog progressDialog;
    private FirebaseAuth mAuth;

    public Login_Fragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.login_layout, container, false);
        initViews();
        setListeners();
        mAuth = FirebaseAuth.getInstance();
        return view;
    }

    // Initiate Views
    private void initViews() {
        fragmentManager = getActivity().getSupportFragmentManager();

        emailid = view.findViewById(R.id.login_emailid);
        password = view.findViewById(R.id.login_password);
        loginButton = view.findViewById(R.id.loginBtn);
        forgotPassword = view.findViewById(R.id.forgot_password);
        signUp = view.findViewById(R.id.createAccount);
        show_hide_password = view.findViewById(R.id.show_hide_password);
        loginLayout = view.findViewById(R.id.login_layout);
        progressDialog = new ProgressDialog(getContext());

        // Load ShakeAnimation
        shakeAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);

        // Setting text selector over textviews
        @SuppressLint("ResourceType") XmlResourceParser xrp = getResources().getXml(R.drawable.text_selector);
        try {
            ColorStateList csl = ColorStateList.createFromXml(getResources(), xrp);
            forgotPassword.setTextColor(csl);
            show_hide_password.setTextColor(csl);
            signUp.setTextColor(csl);
        } catch (Exception e) {
        }
    }

    // Set Listeners
    private void setListeners() {
        loginButton.setOnClickListener(this);
        forgotPassword.setOnClickListener(this);
        signUp.setOnClickListener(this);

        // Set check listener over checkbox for showing and hiding password
        show_hide_password.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton button, boolean isChecked) {
                if (isChecked) {
                    show_hide_password.setText(R.string.hide_pwd); // change checkbox text
                    password.setInputType(InputType.TYPE_CLASS_TEXT);
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance()); // show password
                } else {
                    show_hide_password.setText(R.string.show_pwd); // change checkbox text
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance()); // hide password
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();

        if (viewId == R.id.loginBtn) {
            checkValidation();
        } else if (viewId == R.id.createAccount) {
            // Replace signup fragment with animation
            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.right_enter, R.anim.left_out)
                    .replace(R.id.frameContainer, new SignUp_Fragment(), Utils.SignUp_Fragment)
                    .commit();
        }
    }

    // Check Validation before login
    private void checkValidation() {
        final String getEmailId = emailid.getText().toString();
        final String getPassword = password.getText().toString();

        Pattern p = Pattern.compile(Utils.regEx);
        Matcher m = p.matcher(getEmailId);

        if (getEmailId.equals("") || getEmailId.length() == 0 || getPassword.equals("") || getPassword.length() == 0) {
            loginLayout.startAnimation(shakeAnimation);
            new CustomToast().Show_Toast(getActivity(), view, "Enter both credentials.");
        } else if (!m.find()) {
            new CustomToast().Show_Toast(getActivity(), view, "Your Email Id is Invalid.");
        } else {
            progressDialog.setMessage("Please Wait....");
            progressDialog.show();

            mAuth.signInWithEmailAndPassword(getEmailId, getPassword)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                startActivity(new Intent(getActivity(), MainActivity.class));
                                getActivity().finish();
                                getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                            } else {
                                try {
                                    throw task.getException();
                                } catch (FirebaseAuthInvalidUserException e) {
                                    new CustomToast().Show_Toast(getActivity(), view, "This user does not exist.");
                                } catch (FirebaseAuthInvalidCredentialsException e) {
                                    new CustomToast().Show_Toast(getActivity(), view, "Invalid details.");
                                } catch (Exception e) {
                                    new CustomToast().Show_Toast(getActivity(), view, "Authentication failed.");
                                }
                            }
                        }
                    });
        }
    }
}

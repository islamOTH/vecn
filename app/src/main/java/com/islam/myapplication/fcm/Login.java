package com.islam.myapplication.fcm;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.islam.myapplication.R;
import com.islam.myapplication.fcm.db.MSP;
import com.islam.myapplication.fcm.model.Center;
import com.islam.myapplication.fcm.model.JsonPlaceHolderApi;
import com.islam.myapplication.fcm.model.User;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Login extends AppCompatActivity {
    private EditText mEmail;
    private EditText mPassword;
    private Button mLoginBtn;
    private TextView mCreateBtn;
    private ProgressBar progressBar;
    private User user;
    private Center center;
    private Retrofit mRetrofit;
    private JsonPlaceHolderApi mJsonPlaceHolderApi;
    private MSP msp;
    private CheckBox userCheck;
    private CheckBox centerCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        msp=new MSP(this) ;
        user=msp.getUserPref();
     msp.deleteUser();
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        mRetrofit=new Retrofit.Builder().baseUrl(Register.BASE_URL).addConverterFactory(GsonConverterFactory.create(gson)).build();
        mJsonPlaceHolderApi=mRetrofit.create(JsonPlaceHolderApi.class);
        mEmail = findViewById(R.id.Email);
        mPassword = findViewById(R.id.password);
        progressBar = findViewById(R.id.progressBar);
        mLoginBtn = findViewById(R.id.loginBtn);
        mCreateBtn = findViewById(R.id.createText);
        userCheck=findViewById(R.id.usercheck);
        centerCheck=findViewById(R.id.centerCheck);
        userCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)centerCheck.setChecked(false);
            }
        });
        centerCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)userCheck.setChecked(false);
            }
        });
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email is Required.");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    mPassword.setError("Password is Required.");
                    return;
                }

                if (password.length() < 6) {
                    mPassword.setError("Password Must be >= 6 Characters");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                // authenticate the user
                if (userCheck.isChecked()) {
                    mJsonPlaceHolderApi.mGetUser(email).enqueue(new Callback<List<User>>() {
                        @Override
                        public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                            progressBar.setVisibility(View.INVISIBLE);
                            if (!response.isSuccessful()) {
                                message(response.message());
                                return;
                            }
                            ArrayList<User> users = (ArrayList<User>) response.body();
                            if (users != null && users.size() > 0) {

                                user = users.get(0);
                                if (user.getPassword().equals(password)) {
                                    msp.setUser(user);
                                    startActivity(new Intent(Login.this, MainActivity.class));
                                    finish();
                                } else
                                    message("your password is not correct");
                            }

                        }

                        @Override
                        public void onFailure(Call<List<User>> call, Throwable t) {
                            progressBar.setVisibility(View.INVISIBLE);
                            message("error" + t.getMessage());

                        }
                    });
                }
                else if (centerCheck.isChecked()) {
                    mJsonPlaceHolderApi.mGetCenter(Integer.parseInt(email)).enqueue(new Callback<List<Center>>() {
                        @Override
                        public void onResponse(Call<List<Center>> call, Response<List<Center>> response) {
                            progressBar.setVisibility(View.INVISIBLE);
                            if (!response.isSuccessful()) {
                                message(response.message());
                                return;
                            }
                            ArrayList<Center> centers = (ArrayList<Center>) response.body();
                            if (centers != null && centers.size() > 0) {

                                center = centers.get(0);
                                if (center.getPassword().equals(password)) {
                                    Intent intent = new Intent(Login.this, MainActivity2.class);
                                    intent.putExtra(Center.ID, center.getId());
                                    intent.putExtra(Center.PASSWORD, center.getPassword());
                                    intent.putExtra(Center.ADDRESS, center.getAddress());
                                    intent.putExtra(Center.NAME, center.getName());
                                    startActivity(intent);
                                    finish();
                                } else
                                    message("your password is not correct");
                            }

                        }

                        @Override
                        public void onFailure(Call<List<Center>> call, Throwable t) {
                            progressBar.setVisibility(View.INVISIBLE);
                            message("error" + t.getMessage());

                        }
                    });
                }
            }



        });

        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Register.class));
                finish();
            }
        });




    }
    public void message(String s){
        Toast.makeText(this,s,Toast.LENGTH_SHORT).show();
    }

}

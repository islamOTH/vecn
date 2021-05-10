package com.islam.myapplication.fcm;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

public class Register extends AppCompatActivity {
   public static final String TAG = "TAG";
   private EditText mETXName;
   private EditText mETXEmail;
   private EditText mETXPassword;
   private EditText mETXPhone;
   private EditText mETXOld;
   private EditText mETXNAID;
   private Button mBTNRegister;
   private TextView mTXTlogin;
   private ProgressBar progressBar;
   private User user;
   private Retrofit mRetrofit;
   private MSP msp;
   private JsonPlaceHolderApi mJsonPlaceHolderApi;
   public final static String BASE_URL="http://192.168.1.107:8000/api/";//192.168.1.110   http://1ee70f12964f.ngrok.io    10.0.2.2:8000
   private String email;
   private String password;
   private String fullName;
   private String phone;
   private String naid;
   private String txtold;
   private ProgressDialog progressDialog;
   private Spinner dropdown;
    //create a list of items for the spinner.
    private ArrayList<String> items ;
    private String defualt="default";
    private  ArrayList <Center> centers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("loading");
        mETXName =findViewById(R.id.fullName);
        mETXEmail = findViewById(R.id.Email);
        mETXPassword = findViewById(R.id.password);
        mETXPhone = findViewById(R.id.phone);
        mETXOld=findViewById(R.id.old);
        mBTNRegister = findViewById(R.id.save);
        mETXNAID=findViewById(R.id.nationalnumber);
        mTXTlogin = findViewById(R.id.createText);
        //get the spinner from the xml.
        dropdown = findViewById(R.id.spinner);
        progressBar = findViewById(R.id.progressBar);
        items=new ArrayList<>();
        msp=new MSP(this) ;

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        mRetrofit=new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create(gson)).build();
        mJsonPlaceHolderApi=mRetrofit.create(JsonPlaceHolderApi.class);
        progressDialog.show();
        mJsonPlaceHolderApi.mGetCenters().enqueue(new Callback<List<Center>>() {
            @Override
            public void onResponse(Call<List<Center>> call, Response<List<Center>> response) {
                if (progressDialog.isShowing())progressDialog.dismiss();

                if (!response.isSuccessful()) {
                    message( response.message());
                    return;
                }
                centers=(ArrayList<Center>)response.body();
                items=new ArrayList<>();
                items.add(defualt);
//create an adapter to describe how the items are displayed, adapters are used in several places in android.
//There are multiple variations of this, but this is the basic variant.
              if (centers != null&&centers.size()>0) {
                  for (int i = 0; i < centers.size(); i++)
                      items.add(centers.get(i).getName());
                  //set the spinners adapter to the previously created one.
                  dropdown.setAdapter( new ArrayAdapter<>(Register.this, android.R.layout.simple_spinner_dropdown_item, items));

              }
            }

            @Override
            public void onFailure(Call<List<Center>> call, Throwable t) {
                if (progressDialog.isShowing())progressDialog.dismiss();

                message("error"+t.getMessage());
            }
        });


        mTXTlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register.this,Login.class));
                finish();
            }
        });

        mBTNRegister.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                email = mETXEmail.getText().toString();
                 password = mETXPassword.getText().toString();
                 fullName = mETXName.getText().toString();
                 phone    = mETXPhone.getText().toString();
                 txtold =mETXOld.getText().toString();
                naid=mETXNAID.getText().toString();
                if(TextUtils.isEmpty(email)){
                    mETXEmail.setError("Email is Required.");
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    mETXPassword.setError("Password is Required.");
                    return;
                }
                if(TextUtils.isEmpty(fullName)){
                    mETXName.setError("Name is Required.");
                    return;
                }
                if(TextUtils.isEmpty(txtold)){
                    mETXOld.setError("Name is Required.");
                    return;
                }
                if(TextUtils.isEmpty(naid)){
                    mETXNAID.setError("National Number is Required.");
                    return;
                }
                if(password.length() < 6){
                    mETXPassword.setError("Password Must be >= 6 Characters");
                    return;
                }
                if(dropdown.getSelectedItem().toString().equals(defualt)){
                   message("please select center");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                user=new User(fullName,password,email,phone
                        ,centers.get(dropdown.getSelectedItemPosition()-1).getId()
                        ,Integer.parseInt(txtold),naid);

                // register the user in api
                mJsonPlaceHolderApi.mPostUser(user).enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        progressBar.setVisibility(View.INVISIBLE);
                        if (!response.isSuccessful()) {
                            message( response.message());
                            return;
                        }
                        String s = (String) response.body();
                        if (s.equals("n") || s.equals("p") || s.equals("e"))
                            message(s);
                        else {
                            user.setId(Integer.parseInt(s));
                            msp.setUser(user);
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            message("successful");

                            finish();
                        } }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        progressBar.setVisibility(View.INVISIBLE);
                        message("connection Failure"+t.getMessage());
                    }
        });

    }});}
    public void message(String s){
        Toast.makeText(this,s,Toast.LENGTH_SHORT).show();
    }
}

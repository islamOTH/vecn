package com.islam.myapplication.fcm;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.islam.myapplication.R;
import com.islam.myapplication.fcm.db.MSP;
import com.islam.myapplication.fcm.model.Center;
import com.islam.myapplication.fcm.model.JsonPlaceHolderApi;
import com.islam.myapplication.fcm.model.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EditProfile extends AppCompatActivity {
private Button save;
private Button delete;
private EditText fullName;
private EditText password;
private Spinner dropdown;
private CheckBox[] checkBoxes;
private Retrofit mRetrofit;
private MSP msp;
private User user;
private JsonPlaceHolderApi mJsonPlaceHolderApi;
private ProgressDialog progressDialog;
private ArrayList<String>items;
private ArrayList<Center> centers;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        fullName=findViewById(R.id.fullName);
        password=findViewById(R.id.password);
        dropdown=findViewById(R.id.spinner);
        save=findViewById(R.id.save);
        delete=findViewById(R.id.delete);
        checkBoxes=new CheckBox[5];
        checkBoxes[0]=findViewById(R.id.diseases);
        checkBoxes[1]=findViewById(R.id.ch1);
        checkBoxes[2]=findViewById(R.id.ch10);
        checkBoxes[3]=findViewById(R.id.ch100);
        checkBoxes[4]=findViewById(R.id.ch1000);
        msp=new MSP(this);
        user=msp.getUserPref();
        if (user.getFirst()==0){
            for (int i=1;i<5;i++)checkBoxes[i].setVisibility(View.INVISIBLE);
            checkBoxes[0].setChecked(false);
        }
        else {
            int first=user.getFirst();
            message(""+first);
            checkBoxes[0].setChecked(true);
            for (int i=1 ; i<5 ; i++){
                if (first % 10 == 1)checkBoxes[i].setChecked(true);
                else checkBoxes[i].setChecked(false);
                first  =first/10;
            }
        }
        fullName.setText(user.getName());
        password.setText(user.getPassword());
checkBoxes[0].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            for (int i = 1; i < 5; i++) {
                checkBoxes[i].setVisibility(View.VISIBLE);
            }
        } else {
            for (int i = 1; i < 5; i++) {
                checkBoxes[i].setVisibility(View.INVISIBLE);
            }
        }
    }
});
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading");
        progressDialog.show();
        mRetrofit = new Retrofit.Builder().baseUrl(Register.BASE_URL).addConverterFactory(GsonConverterFactory.create(gson)).build();
        mJsonPlaceHolderApi = mRetrofit.create(JsonPlaceHolderApi.class);
        user.setToken(FirebaseInstanceId.getInstance().getToken());
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

//create an adapter to describe how the items are displayed, adapters are used in several places in android.
//There are multiple variations of this, but this is the basic variant.
                if (centers != null&&centers.size()>0) {
                    for (int i=0;i<centers.size();i++)
                        if (centers.get(i).getId()==user.getId_center())
                        {Center center=centers.get(i);
                        centers.remove(i);
                        centers.add(0,center);
                        break;}
                    for (int i = 0; i < centers.size(); i++)
                        items.add(centers.get(i).getName());
                    //set the spinners adapter to the previously created one.
                    dropdown.setAdapter( new ArrayAdapter<>(EditProfile.this, android.R.layout.simple_spinner_dropdown_item, items));

                }
            }

            @Override
            public void onFailure(Call<List<Center>> call, Throwable t) {
                if (progressDialog.isShowing())progressDialog.dismiss();

                message("error"+t.getMessage());
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        save.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                String passwordtxt = password.getText().toString();
                String nametxt = fullName.getText().toString();
                int first=0;
                if(TextUtils.isEmpty(nametxt)){
                    fullName.setError("Email is Required.");
                    return;
                }

                if(TextUtils.isEmpty(passwordtxt)){
                    password.setError("Password is Required.");
                    return;
                }
                if(passwordtxt.length() < 6){
                    password.setError("Password Must be >= 6 Characters");
                    return;
                }
                if (checkBoxes[0].isChecked()) {
                    for (int i = 1; i < 5; i++) {
                        if (checkBoxes[i].isChecked()) first += Math.pow(10, i - 1);
                    }
                } else first=0;

                user.setName(nametxt);
                user.setPassword(passwordtxt);
                user.setFirst(first);
                user.setId_center(centers.get(dropdown.getSelectedItemPosition()).getId());
                // register the user in api
                mJsonPlaceHolderApi.mEditeUser(user).enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                      if (progressDialog.isShowing())progressDialog.dismiss();
                        if (!response.isSuccessful()) {
                            message( response.message());
                            return;
                        }
                        msp.setUser(user);
                            message("successful");
                            startActivity(new Intent(EditProfile.this,MainActivity.class));
                            finish();
                        }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                    if (progressDialog.isShowing())progressDialog.dismiss();
                    message("connection Failure"+t.getMessage());

                    }
                });

            }});
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                mJsonPlaceHolderApi.mDeleteUser(user.getId()).enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (progressDialog.isShowing())progressDialog.dismiss();
                        if (!response.isSuccessful())
                        {
                            message(response.message());
                            return;
                        }
                        if (((String)response.body()).equals("false"))
                            message("error in server");
                        else {
                            message("successful");
                            startActivity(new Intent(EditProfile.this,Login.class));
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
if (progressDialog.isShowing())progressDialog.dismiss();
message(t.getMessage());
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
this.finish();
startActivity(new Intent(this,MainActivity.class));
    }

    public void message(String s){
        Toast.makeText(this,s,Toast.LENGTH_SHORT).show();
    }

}

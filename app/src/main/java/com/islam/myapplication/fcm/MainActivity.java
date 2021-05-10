package com.islam.myapplication.fcm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.islam.myapplication.R;
import com.islam.myapplication.fcm.db.MSP;
import com.islam.myapplication.fcm.model.JsonPlaceHolderApi;
import com.islam.myapplication.fcm.model.User;
import com.squareup.picasso.Picasso;

import javax.annotation.Nullable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private MSP msp;
    private User user;
    private Button logout;
    private Retrofit mRetrofit;
    private JsonPlaceHolderApi mJsonPlaceHolderApi;
    private ProgressDialog progressDialog;
    private Button editeprofile;
    private TextView txtUser;
    private Button resend;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        logout=findViewById(R.id.logout);
        editeprofile=findViewById(R.id.editeprofile);
        txtUser=findViewById(R.id.txtUser);
        resend=findViewById(R.id.resend);
        resend.setVisibility(View.INVISIBLE);
        msp=new MSP(this);
        user=msp.getUserPref();
        if (user == null) { startActivity(new Intent(this,Login.class)); finish(); }
else
    {

        editeprofile.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(MainActivity.this,EditProfile.class));
            finish();
        }
    });
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Loading");
           // progressDialog.show();
            mRetrofit = new Retrofit.Builder().baseUrl(Register.BASE_URL).addConverterFactory(GsonConverterFactory.create(gson)).build();
            mJsonPlaceHolderApi = mRetrofit.create(JsonPlaceHolderApi.class);
            user.setToken(FirebaseInstanceId.getInstance().getToken());
mJsonPlaceHolderApi.editetoken(user).enqueue(new Callback<User>() {
    @Override
    public void onResponse(Call<User> call, Response<User> response) {
        if (!response.isSuccessful()){
            message("error");
            finish();
            return;
        }
        User user=(User) response.body();
        if (user.getStatus().equals("wait"))txtUser.setText("wait");
        else if (user.getStatus().equals("recieve"))txtUser.setText("please go to center ");
        else if (user.getStatus().equals("stop")){
            txtUser.setText("your account is stoped please re send");
            resend.setVisibility(View.VISIBLE);
            resend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    user.setStatus("wait");
                    mJsonPlaceHolderApi.mEditeUser(user).enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            if (!response.isSuccessful()){
                                message("error");
                                return;
                            }
                            message("successful");
                            msp.setUser(user);
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            message("error");
                        }
                    });
                }
            });
        }
        else if (user.getStatus().equals("Deliver")){
            txtUser.setText("Wellcome to "+R.string.app_name);
            editeprofile.setVisibility(View.INVISIBLE);
        }
        msp.setUser(user);
        message("successful");
    }

    @Override
    public void onFailure(Call<User> call, Throwable t) {

    }
});
            //logout btn
            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   user.setToken("f");
                    msp.deleteUser();
                    mJsonPlaceHolderApi.mEditeUser(user).enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            if (!response.isSuccessful()) {
                                message(response.message());
                                return;
                            }
                            message("successful");
                            startActivity (new Intent(MainActivity.this, Login.class));
                            finish();
                        }
                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            message("error" + t.getMessage());
                        }
                    });

                }
            });
        }

    }



    public void message(String s){
        Toast.makeText(this,s,Toast.LENGTH_SHORT).show();
    }



}

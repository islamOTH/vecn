package com.islam.myapplication.fcm;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.islam.myapplication.R;
import com.islam.myapplication.fcm.model.Center;
import com.islam.myapplication.fcm.model.JsonPlaceHolderApi;
import com.islam.myapplication.fcm.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity2 extends AppCompatActivity {
private static final String AUTH_KEY ="key=AAAA51uVwU8:APA91bGJgohQaKZv6-_ICHASoTQL4SdT-9z8y843LAtgHHzTUzhGK_L6yAq8Z2xYa5QuYZVPqrvLeVFYS48DPzO0W5zyU8nDZka4mgxue5ppkiMUdpVch2EBcvuys3gxnyzpcW-OsODl";
private String m_Text = "";
private FloatingActionButton addPatients;
private FloatingActionButton allFloatingActionButton;
private FloatingActionButton changeFloatingActionButton;
private Center center;
private Retrofit mRetrofit;
private JsonPlaceHolderApi mJsonPlaceHolderApi;
private ProgressDialog progressDialog;
private ArrayList<User> users;
private ArrayList<User> usersWait;
private ArrayList<User> usersReceive;
private ArrayList<User> usersStop;
private ArrayList<User> usersDeliver;
private ArrayList<String> items;
private ListView listView;
private  ArrayList<User>userS;
private int i=0;
private TextView txtStatus;
@Override
protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
         txtStatus=findViewById(R.id.txtStatus);
            listView=findViewById(R.id.listCenter);
        changeFloatingActionButton=findViewById(R.id.changeFloat);
        allFloatingActionButton=findViewById(R.id.allFloat);
        changeFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (i==0){
                    txtStatus.setText("Recieve "+usersReceive.size());
                    items=new ArrayList<>();
                    for (int i=0;i<usersReceive.size();i++)
                        items.add(usersReceive.get(i).getName()+"        "+usersReceive.get(i).getStatus());
                    listView.setAdapter(new ArrayAdapter<>(MainActivity2.this, android.R.layout.simple_list_item_1,items));
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            askOption("Del"+usersReceive.get(position).getName(),position).show();
                        }
                    });
                    i=1;
                }
               else if (i==1){
                    txtStatus.setText("Wait "+usersWait.size());

                    items=new ArrayList<>();
                    for (int i=0;i<usersWait.size();i++)
                        items.add(usersWait.get(i).getName()+"        "+usersWait.get(i).getStatus());
                    listView.setAdapter(new ArrayAdapter<>(MainActivity2.this, android.R.layout.simple_list_item_1,items));
                    listView.setOnItemClickListener(null);
                    i=2;
                }
              else   if (i==2){
                    txtStatus.setText("Stop "+usersStop.size());

                    items=new ArrayList<>();
                    for (int i=0;i<usersStop.size();i++)
                        items.add(usersStop.get(i).getName()+"        "+usersStop.get(i).getStatus());
                    listView.setAdapter(new ArrayAdapter<>(MainActivity2.this, android.R.layout.simple_list_item_1,items));
                    listView.setOnItemClickListener(null);
                    i=3;
                }
               else if (i==3){
                    txtStatus.setText("Deliver "+ usersDeliver.size());

                    items=new ArrayList<>();
                    for (int i = 0; i< usersDeliver.size(); i++)
                        items.add(usersDeliver.get(i).getName()+"        "+ usersDeliver.get(i).getStatus());
                    listView.setAdapter(new ArrayAdapter<>(MainActivity2.this, android.R.layout.simple_list_item_1,items));
                    listView.setOnItemClickListener(null);
                    i=0;
                }

            }
        });
        allFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                items=new ArrayList<>();
                for (User user:usersReceive){ items.add(user.getId()+" "+user.getName()+"  "+user.getStatus());}
                for (User user:usersWait){ items.add(user.getId()+" "+user.getName()+"  "+user.getStatus());}
                for (User user:usersStop){ items.add(user.getId()+" "+user.getName()+"  "+user.getStatus());}
                for (User user: usersDeliver){ items.add(user.getId()+" "+user.getName()+"  "+user.getStatus());}

                listView.setAdapter(new ArrayAdapter<>(MainActivity2.this, android.R.layout.simple_list_item_1,items));
                listView.setOnItemClickListener(null);
                txtStatus.setText("All Users :"+users.size());
                i=0;
            }
        });
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        Intent intent=getIntent();
        int id=intent.getIntExtra(Center.ID,-1);
        String name=intent.getStringExtra(Center.NAME);
        String address=intent.getStringExtra(Center.ADDRESS);
        String password=intent.getStringExtra(Center.PASSWORD);
        String token= FirebaseInstanceId.getInstance().getToken();
        center=new Center(id,name, password,address,token);
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading");
        progressDialog.show();
        mRetrofit = new Retrofit.Builder().baseUrl(Register.BASE_URL).addConverterFactory(GsonConverterFactory.create(gson)).build();
        mJsonPlaceHolderApi = mRetrofit.create(JsonPlaceHolderApi.class);
editeCenter();
        addPatients =findViewById(R.id.floatingActionButton);
        addPatients.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity2.this);
                builder.setTitle("Add Patients......");

// Set up the input
                final EditText input = new EditText(MainActivity2.this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setView(input);
                input.setText(""+usersWait.size());
                builder.setCancelable(false);

// Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        m_Text = input.getText().toString();
                        int x = Integer.parseInt(m_Text);
                        if (x > usersWait.size() || x < 0) {
                            message("please enter number in 0 to " + usersWait.size());

                        } else {
                            userS = new ArrayList<>();
                            for (int i = 0; i < usersWait.size(); i++)
                            {if (!usersWait.get(i).getToken().equals("f")){userS.add(usersWait.get(i));
                            x--;}
                            if (x==0)break;
                            }

                          //------------------------------------------send notifacition

                            mJsonPlaceHolderApi.convert(userS).enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    if (response.isSuccessful()){sendWithOtherThread("tokens");message("successful");}
                                    else message(response.message());
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    message(t.getMessage());
                                }
                            });

                        }
                    }  });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });
    }
public void editeCenter(){
        users=new ArrayList<>();
        usersReceive=new ArrayList<>();
        usersStop=new ArrayList<>();
        usersWait=new ArrayList<>();
        items=new ArrayList<>();
        usersDeliver =new ArrayList<>();
        mJsonPlaceHolderApi.mEditeCenter(center).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (!response.isSuccessful()){
                    message(response.message());
                    if (progressDialog.isShowing())progressDialog.dismiss();

                    return;
                }
                users=(ArrayList<User>)response.body();
                for (  User userm:users){
                    if (userm.getStatus().equals("wait")&& ! userm.getToken().equals("f"))usersWait.add(userm);
                    if (userm.getStatus().equals("recieve"))usersReceive.add(userm);
                    if (userm.getStatus().equals("stop"))usersStop.add(userm);
                    if (userm.getStatus().equals("Deliver")) usersDeliver.add(userm);


                }
                Collections.sort(usersWait,new UserCompar());
                Collections.sort(usersReceive,new UserCompar());
                Collections.sort(usersStop,new UserCompar());
                Collections.sort(usersDeliver,new UserCompar());

                for (User user:usersReceive){ items.add(user.getId()+" "+user.getName()+"  "+user.getStatus());}
                for (User user:usersWait){ items.add(user.getId()+" "+user.getName()+"  "+user.getStatus());}
                for (User user:usersStop){ items.add(user.getId()+" "+user.getName()+"  "+user.getStatus());}
                for (User user: usersDeliver){ items.add(user.getId()+" "+user.getName()+"  "+user.getStatus());}

                listView.setAdapter(new ArrayAdapter<>(MainActivity2.this, android.R.layout.simple_list_item_1,items));
                listView.setOnItemClickListener(null);
                txtStatus.setText("All Users :"+users.size());
                i=0;
                if (progressDialog.isShowing())progressDialog.dismiss();


            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                message("error"+t.getMessage());
                finish();
            }
        });
    }
public void message(String s){
    Toast.makeText(this,s,Toast.LENGTH_SHORT).show();
    }
private void sendWithOtherThread(final String type ) {
    new Thread(new Runnable() {
        @Override
        public void run() {
            pushNotification(type);
        }
    }).start();
}
private void pushNotification(String type) {
    JSONObject jPayload = new JSONObject();
    JSONObject jNotification = new JSONObject();
    JSONObject jData = new JSONObject();
    try {
        jNotification.put("title", "It is your turn");
        jNotification.put("body", "please go to center "+center.getName()+"\n address:"+center.getAddress());
        jNotification.put("sound", "default");
        jNotification.put("badge", "1");
        jNotification.put("click_action", "OPEN_ACTIVITY_1");
        jNotification.put("icon", "ic_notification");
        jData.put("picture", "https://miro.medium.com/max/1400/1*QyVPcBbT_jENl8TGblk52w.png");
        switch(type) {
            case "tokens":
                JSONArray ja = new JSONArray();
                ja.put("c5pBXXsuCN0:APA91bH8nLMt084KpzMrmSWRS2SnKZudyNjtFVxLRG7VFEFk_RgOm-Q5EQr_oOcLbVcCjFH6vIXIyWhST1jdhR8WMatujccY5uy1TE0hkppW_TSnSBiUsH_tRReutEgsmIMmq8fexTmL");
                for ( User user:userS){
                    if (!user.getToken().equals("f"))
                    ja.put(user.getToken());
                }
                jPayload.put("registration_ids", ja);
                break;
            case "topic":
                jPayload.put("to", "/topics/news");
                break;
            case "condition":
                jPayload.put("condition", "'sport' in topics || 'news' in topics");
                break;
            default:
                jPayload.put("to", FirebaseInstanceId.getInstance().getToken());
        }

        jPayload.put("priority", "high");
        jPayload.put("notification", jNotification);
        jPayload.put("data", jData);

        URL url = new URL("https://fcm.googleapis.com/fcm/send");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", AUTH_KEY);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        // Send FCM message content.
        OutputStream outputStream = conn.getOutputStream();
        outputStream.write(jPayload.toString().getBytes());

        // Read FCM response.
        InputStream inputStream = conn.getInputStream();
        final String resp = convertStreamToString(inputStream);

        Handler h = new Handler(Looper.getMainLooper());

    } catch (JSONException | IOException e) {
        e.printStackTrace();
    }
    editeCenter();
}
private String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next().replace(",", ",\n") : "";
    }
private androidx.appcompat.app.AlertDialog askOption(String s,int position) {
        androidx.appcompat.app.AlertDialog myQuittingDialogBox = new androidx.appcompat.app.AlertDialog.Builder(this)
                // set message, title, and icon
                .setTitle("change to Delivered")
                .setMessage("Do you want to deliver the patient "+s)
                .setIcon(R.drawable.ic_baseline_change_circle_24)

                .setPositiveButton("Deliver", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        User user=usersReceive.get(position);
                        user.setStatus("Deliver");
                    mJsonPlaceHolderApi.mEditeUser(user).enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            if (response.isSuccessful())
                            {
                            for (int i=0;i<users.size();i++)
                                if (users.get(i).getId()==usersReceive.get(position).getId())users.get(i).setStatus("Deliver");
                                usersDeliver.add(usersReceive.get(position));
                                userS=new ArrayList<>();
                                userS.add(usersReceive.get(position));
                                sendWithOtherThread("tokens");
                                usersReceive.remove(position);
                                items=new ArrayList<>();
                                for (int i=0;i<usersReceive.size();i++)
                                    items.add(usersReceive.get(i).getName()+"        "+usersReceive.get(i).getStatus());
                                listView.setAdapter(new ArrayAdapter<>(MainActivity2.this, android.R.layout.simple_list_item_1,items));
                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        askOption(usersReceive.get(position).getName(),position).show();

                                    }
                                });
                            }
                            else message("error in server");

                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            message("error in server");
                        }
                    });

                        dialog.dismiss();
                    }

                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                })
                .create();

        return myQuittingDialogBox;
    }
}
package com.islam.myapplication.fcm.db;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.islam.myapplication.fcm.model.User;

public class MSP {
    private SharedPreferences.Editor editor;
    private SharedPreferences mSharedPreferences;
    private Context mContext;
    public static final String USERPREF="USERPREF";
    //--------------------User-----------------------
    public static final String KEYIDUSER ="KEYIDUSER";
    public static final String KEYNAMEUSER ="KEYNAMEUSER";
    public static final String KEYPASSWORDUSER="KEYPASSWORD";
    public static final String KEYEMAILUSER ="KEYEMAILUSER";
    public static final String KEYPHONEUSER="KEYPHONEUSER";
    public static final String KEYOLDUSER ="KEYOLDUSER";
    public static final String KEYNAIDUSER ="KEYNAIDUSER";
    public static final String KEYIDCENTERUSER="KEYIDCENTERUSER";
    public static final String KEYFIRSTUSER ="KEYFIRSTUSER";
    public static final String KEYSTATUSUSER="KEYSTATUSUSER";
    public static final String KEYTOKENUSER="KEYTOKENUSER";

public MSP( Context context)
{

    mSharedPreferences= PreferenceManager.getDefaultSharedPreferences(context);
   editor=mSharedPreferences.edit();
    this.mContext=context;
}

    public MSP(SharedPreferences preferences) {

        mSharedPreferences= preferences;
        editor = mSharedPreferences.edit();

    }

    public User getUserPref(){
    int id = mSharedPreferences.getInt(KEYIDUSER,-1);
if (id !=-1){
    String name=mSharedPreferences.getString(KEYNAMEUSER, null);          // getting string
    String password=mSharedPreferences.getString(KEYPASSWORDUSER, null);// getting String
    String email=mSharedPreferences.getString(KEYEMAILUSER,null);
    String phone=mSharedPreferences.getString(KEYPHONEUSER,null);
    int old=mSharedPreferences.getInt(KEYOLDUSER,-1);
    String naid=mSharedPreferences.getString(KEYNAIDUSER,null);
    int id_center=mSharedPreferences.getInt(KEYIDCENTERUSER,-1);
    int first=mSharedPreferences.getInt(KEYFIRSTUSER,-1);
    String status=mSharedPreferences.getString(KEYSTATUSUSER,null);
    String token=mSharedPreferences.getString(KEYTOKENUSER,null);
return new User(id,name,password,email,old,naid,phone,id_center,first,status,token);
}
return null;
}

    public void setUser(User user){
        editor.putString(KEYNAMEUSER, user.getName());
        editor.putString(KEYEMAILUSER, user.getEmail());
        editor.putInt(KEYIDUSER, user.getId());
        editor.putString(KEYPASSWORDUSER, user.getPassword());
        editor.putInt(KEYOLDUSER, user.getOld());
        editor.putString(KEYNAIDUSER, user.getNaid());
        editor.putInt(KEYIDCENTERUSER, user.getId_center());
        editor.putInt(KEYFIRSTUSER, user.getFirst());
        editor.putString(KEYSTATUSUSER, user.getStatus());
        editor.putString(KEYTOKENUSER, user.getToken());
        editor.putString(KEYPHONEUSER, user.getPhone());

        editor.apply(); // commit changes
}


public void deleteUser(){
    editor.putInt(KEYIDUSER, -1);
   editor.apply(); // commit changes

}

}

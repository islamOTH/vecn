package com.islam.myapplication.fcm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.islam.myapplication.R;
import com.islam.myapplication.fcm.db.MSP;
import com.islam.myapplication.fcm.model.JsonPlaceHolderApi;
import com.islam.myapplication.fcm.model.User;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
	public static final String FCM_PARAM = "picture";
	private static final String CHANNEL_NAME = "FCM";
	private static final String CHANNEL_DESC = "Firebase Cloud Messaging";
	private int numMessages = 0;
    private MSP msp;
	private Retrofit mRetrofit;
	private JsonPlaceHolderApi mJsonPlaceHolderApi;
	@Override
	public void onNewToken(@NonNull String s) {
		msp=new MSP(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
		User user=msp.getUserPref();
		Gson gson = new GsonBuilder()
				.setLenient()
				.create();

		mRetrofit = new Retrofit.Builder().baseUrl(Register.BASE_URL).addConverterFactory(GsonConverterFactory.create(gson)).build();
		mJsonPlaceHolderApi = mRetrofit.create(JsonPlaceHolderApi.class);
		if (s.equals(null))
		user.setToken(FirebaseInstanceId.getInstance().getToken());
		else user.setToken(s);
		mJsonPlaceHolderApi.mEditeUser(user).enqueue(new Callback<User>() {
			@Override
			public void onResponse(Call<User> call, Response<User> response) {

				if (!response.isSuccessful()) {

					return;

				}
				msp.setUser((User) response.body());
			}

			@Override
			public void onFailure(Call<User> call, Throwable t) {

			}
		});

	}


	@Override
	public void onMessageReceived(RemoteMessage remoteMessage) {
		super.onMessageReceived(remoteMessage);

		msp=new MSP(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
		User user=msp.getUserPref();
		if (user != null){
			if (user.getStatus().equals("wait")){
			user.setStatus("recieve");
		    msp.setUser(user);
		RemoteMessage.Notification notification = remoteMessage.getNotification();
		Map<String, String> data = remoteMessage.getData();
		sendNotification(notification, data,0,user);}
			else if (user.getStatus().equals("recieve")){
				user.setStatus("Deliver");
				msp.setUser(user);
				RemoteMessage.Notification notification = remoteMessage.getNotification();
				Map<String, String> data = remoteMessage.getData();
				sendNotification(notification, data,1,user);}

		}
	}

	private void sendNotification(RemoteMessage.Notification notification, Map<String, String> data,int y,User  user) {
		Bundle bundle = new Bundle();
		bundle.putString(FCM_PARAM, data.get(FCM_PARAM));

		Intent intent = new Intent(this, MainActivity2.class);
		intent.putExtras(bundle);
		NotificationCompat.Builder notificationBuilder;
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
if (y==0) {
 notificationBuilder = new NotificationCompat.Builder(this, getString(R.string.notification_channel_id))
			.setContentTitle(notification.getTitle())
			.setContentText(notification.getBody())
			.setAutoCancel(false)
			.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
			//.setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.win))
			.setContentIntent(pendingIntent)
			.setContentInfo("Hello")
			.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
			.setColor(getColor(R.color.black))
			.setLights(Color.RED, 1000, 300)
			.setDefaults(Notification.DEFAULT_VIBRATE)
			.setNumber(++numMessages)
			.setSmallIcon(R.drawable.ic_launcher_background);

}
else
{	notificationBuilder = new NotificationCompat.Builder(this, getString(R.string.notification_channel_id))
		.setContentTitle("sent delivered handed")
		.setContentText("The situation is now "+user.getStatus())
		.setAutoCancel(true)
		.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
		//.setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.win))
		.setContentIntent(pendingIntent)
		.setContentInfo("Hello")
		.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
		.setColor(getColor(R.color.black))
		.setLights(Color.RED, 1000, 300)
		.setDefaults(Notification.DEFAULT_VIBRATE)
		.setNumber(++numMessages)
		.setSmallIcon(R.drawable.ic_launcher_background);

}
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			NotificationChannel channel = new NotificationChannel(
					getString(R.string.notification_channel_id), CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT
			);
			channel.setDescription(CHANNEL_DESC);
			channel.setShowBadge(true);
			channel.canShowBadge();
			channel.enableLights(true);
			channel.setLightColor(Color.RED);
			channel.enableVibration(true);
			channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500});

			assert notificationManager != null;
			notificationManager.createNotificationChannel(channel);
		}

		assert notificationManager != null;
		notificationManager.notify(0, notificationBuilder.build());
	}


}
package org.techtown.locationgps;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;


public class RealService extends Service  {
    private Thread mainThread;
    public static Intent serviceIntent = null;

    public static String dburl = "http://sasasak1.cafe24.com/realtimelocation/addlocation.php",uniqueId;
    private static ArrayAdapter arrayAdapter;
    private ArrayList update = new ArrayList<String>();
    private void showLog(String message) {
        Log.e(TAG, "" + message);
    }
    private static final String TAG = "RealtimeActivity";
    //private Intent serviceIntent;

    ServiceThread serviceThread = null;


    @Override
    public void onStart(Intent intent, int startId) {
        Log.i(TAG, "startRealService");





        super.onStart(intent, startId);
    }



//    providerClient = LocationServices.getFusedLocationProviderClient(this);
//    apiClient = new  GoogleApiClient.Builder(this)
//            .addApi(LocationServices.API)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .build();

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            uniqueId = UUID.randomUUID().toString();
            super.onLocationResult(locationResult);
            if (locationResult != null && locationResult.getLastLocation() != null) {
                double Latitude = locationResult.getLastLocation().getLatitude();
                double Longitude = locationResult.getLastLocation().getLongitude();

                Log.v(TAG,"LOCATION_UPDATE:"+ Latitude + ", " + Longitude + "," + "test");
                // Check internet permission and
                // Send Location to Database
                if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
                    saveLocation(uniqueId, Double.toString(Latitude), Double.toString(Longitude));
                }
            }
        }
    };


    @SuppressLint("MissingPermission")
    private void startLocationService() {

        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if( locationGPS != null ){
            double Latitude = locationGPS.getLatitude();
            double Longitude = locationGPS.getLongitude();
            uniqueId = UUID.randomUUID().toString();
            saveLocation(uniqueId, Double.toString(Latitude), Double.toString(Longitude));
            Log.i(TAG, "Service Runninng------------>"+Latitude);

        }
        else if( locationNet != null ){
            double Latitude = locationNet.getLatitude();
            double Longitude = locationNet.getLongitude();
            uniqueId = UUID.randomUUID().toString();
            saveLocation(uniqueId, Double.toString(Latitude), Double.toString(Longitude));
        }
      /*  LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(3 * 1000);//80000
        locationRequest.setFastestInterval(500);//50000
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.getFusedLocationProviderClient(RealService.this)
                .requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        System.out.println("Complete");
                        Log.i(TAG, "Complete");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        Log.i(TAG, "onFailure:"+e.toString());
                    }
        });*/
        //startForeground(Constants.LOCATION_SERVICE_ID, locationRequest.build());
    }

    public void saveLocation(String uniqueId, String latitude, String longitude){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, dburl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Post response from server in JSON

                Log.v(TAG,"response:" +  response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if(error) {
                        update.add(Calendar.getInstance().getTime() + " - Location Updated");
                        if (arrayAdapter != null)   //----------새로 추가했습니다.--------------
                             arrayAdapter.notifyDataSetChanged();
                    }else{
                        update.add(Calendar.getInstance().getTime() + " - Update Failed");
                        if (arrayAdapter != null)  //----------새로 추가했습니다.--------------
                            arrayAdapter.notifyDataSetChanged();
                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                    showLog("Volley error: "+e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showLog("Volley error: "+error.toString());
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                // Adding parameters to post request
                params.put("uniqueId",uniqueId);
                params.put("latitude",latitude);
                params.put("longitude",longitude);
                return params;
            }
        };
        // Adding request to request queue
        requestQueue.add(stringRequest);
    }
    public class ServiceThread extends Thread {
        Handler handler;
        boolean isRun = true;

        public ServiceThread(Handler handler) {
            this.handler = handler;
        }

        public void run() {
            //반복적으로 수행할 작업을 한다.
            while (isRun) {
                handler.sendEmptyMessage( 0 );//쓰레드에 있는 핸들러에게 메세지를 보냄
                try {
                    Thread.sleep( 1000 * 80 ); //80초씩 쉰다.
                } catch (Exception e) {
                }
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        serviceIntent = intent;
        showToast(getApplication(), "Start Service");
        //new RealService();
        Log.v("LOCATION_UPDATE",  "test");




        if( serviceThread == null )
        {

            Log.v(TAG+"1","serviceThread created" );

            myServiceHandler handler = new myServiceHandler();
            serviceThread = new ServiceThread( handler );
            serviceThread.start();

        }


        mainThread = new Thread(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat sdf = new SimpleDateFormat("aa hh:mm");
                boolean run = true;
                while (run) {
                    try {
                        Thread.sleep(1000 * 60 * 1); // 1 minute
                        Date date = new Date();
                        //showToast(getApplication(), sdf.format(date));
                        sendNotification(sdf.format(date));
                        Log.v("LOCATION_UPDATE",  "test1");
                    } catch (InterruptedException e) {
                        run = false;
                        e.printStackTrace();
                    }
                }
            }
        });
        mainThread.start();

        return START_STICKY;
    }



    @Override
    public void onDestroy() {
        serviceIntent = null;
        setAlarmTimer();
        Thread.currentThread().interrupt();

        if (mainThread != null) {
            mainThread.interrupt();
            mainThread = null;
        }

        myServiceHandler handler = new myServiceHandler();
        serviceThread = new ServiceThread( handler );
        serviceThread.start();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }



    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public void showToast(final Application application, final String msg) {
        Handler h = new Handler(application.getMainLooper());
        h.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(application, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    protected void setAlarmTimer() {
        final Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        c.add(Calendar.SECOND, 1);
        Intent intent = new Intent(this, AlarmRecever.class);
        PendingIntent sender = PendingIntent.getBroadcast(this, 0,intent,0);

        AlarmManager mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mAlarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), sender);
    }

    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);

        String channelId = "fcm_default_channel";//getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher)//drawable.splash)
                        .setContentTitle("Service test")
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,"Channel human readable title", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }


    public class myServiceHandler extends Handler {
        @Override
        public void handleMessage(android.os.Message msg) {

            startLocationService();
        }
    }


}

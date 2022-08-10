package org.techtown.locationgps;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.ArrayAdapter;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LocationService extends Service {
    public static String dburl = "http://sasasak1.cafe24.com/realtimelocation/addlocation.php",uniqueId;
   private ArrayAdapter arrayAdapter;
   private ArrayList update = new ArrayList<String>();
   private void showLog(String message) {
           Log.e(TAG, "" + message);
      }
    private static final String TAG = "RealtimeActivity";
    private Intent serviceIntent;








    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            uniqueId = UUID.randomUUID().toString();

            super.onLocationResult(locationResult);
            if (locationResult != null && locationResult.getLastLocation() != null) {
                double Latitude = locationResult.getLastLocation().getLatitude();
                double Longitude = locationResult.getLastLocation().getLongitude();
                Log.v("LOCATION_UPDATE", Latitude + ", " + Longitude + "," + "test");

                    // Check internet permission and
                    // Send Location to Database
                   if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
                saveLocation(uniqueId, Double.toString(Latitude), Double.toString(Longitude));
                   }
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void startLocationService() {


        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(80000);
        locationRequest.setFastestInterval(50000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, mLocationCallback, Looper.getMainLooper());



    }

    private void stopLocationService() {
        LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(mLocationCallback);
        stopForeground(true);
        stopSelf();


    }






    public void saveLocation(String uniqueId, String latitude, String longitude){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, dburl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Post response from server in JSON
//                try {
//                    JSONObject jObj = new JSONObject(response);
//                    boolean error = jObj.getBoolean("error");
//                    if(error) {
//                        update.add(Calendar.getInstance().getTime() + " - Location Updated");
//                        arrayAdapter.notifyDataSetChanged();
//                    }else{
//                        update.add(Calendar.getInstance().getTime() + " - Update Failed");
//                        arrayAdapter.notifyDataSetChanged();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
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
}


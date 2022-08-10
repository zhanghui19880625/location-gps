package org.techtown.locationgps;

import android.Manifest;
import android.app.Instrumentation;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
//import android.support.v7.app.AppCompatActivity;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.snackbar.Snackbar;
//import org.techtown.locationgps.ProfileActivity

public class MainActivity extends AppCompatActivity implements PermissionLauncher{
    private Intent serviceIntent;

    ActivityResultLauncher<String> permissionLauncher;
    TextView userName,userEmail,userId;
    private GoogleApiClient googleApiClient;
    private GoogleSignInOptions gso;

    @Override
    protected void onResume() {
        super.onResume();
        setLaunchPermission();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userEmail=(TextView)findViewById(R.id.email);
        permissionRegisterLauncher();

        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(POWER_SERVICE);
        boolean isWhiteListing = false;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            isWhiteListing = pm.isIgnoringBatteryOptimizations(getApplicationContext().getPackageName());
        }
        if (!isWhiteListing) {
            Intent intent = new Intent();
            intent.setAction(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
            startActivity(intent);
        }
        setLaunchPermission();
        if (ActivityCompat.checkSelfPermission(this.getApplication().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Snackbar.make(this.findViewById(R.id.layout), "Permission needed for progress!", Snackbar.LENGTH_INDEFINITE).setAction("ALLOW", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Request permission
                        setLaunchPermission();

                    }
                }).show();
            } else {
                // Request permission
                setLaunchPermission();
                //PermissionLauncher.launchPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            }
        }
        else {
            if (RealService.serviceIntent==null) {
                serviceIntent = new Intent(this, RealService.class);
                ContextCompat.startForegroundService(this, serviceIntent);
               // startService(serviceIntent);
            } else {
                serviceIntent = RealService.serviceIntent;//getInstance().getApplication();
                Toast.makeText(getApplicationContext(), "Already", Toast.LENGTH_LONG).show();
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serviceIntent!=null) {
            stopService(serviceIntent);
            serviceIntent = null;
        }
    }
    @Override
    public void permissionRegisterLauncher() {
        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if(result) {
                    if  (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        //Permission granted
                        if (RealService.serviceIntent==null) {
                            serviceIntent = new Intent(MainActivity.this, RealService.class);
                            startService(serviceIntent);
                        } else {
                            serviceIntent = RealService.serviceIntent;//getInstance().getApplication();
                            Toast.makeText(getApplicationContext(), "Already", Toast.LENGTH_LONG).show();
                        }
                    }
                }
                else {
                    //Permission denied
                    Toast.makeText(MainActivity.this, "PLEASE GIVE PERMISSION ON SETTINGS", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void setLaunchPermission() {
        System.out.println("Permission requested.");
        this.permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        this.permissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION);
        this.permissionLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        this.permissionLauncher.launch(Manifest.permission.INTERNET);
       // this.permissionLauncher.launch(Manifest.permission.MO);

    }
}

package com.example.hikerswatch;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    LocationListener locationListener;
    LocationManager locationManager;
    TextView lat, lon, acc, adrr, alti;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length>0 && PackageManager.PERMISSION_GRANTED == grantResults[0]) {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Location lastLoc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if(lastLoc!=null) {
                    locUpdate(lastLoc);
                }
            }

        }
    }

    public void locUpdate(Location loc) {
        final DecimalFormat df = new DecimalFormat("0.00");
        lat.setText("Latitude : "+ df.format(loc.getLatitude()));
        lon.setText("Longitude : "+ df.format(loc.getLongitude()));
        acc.setText("Accuracy : "+ df.format(loc.getAccuracy()));
        alti.setText("Altitude : "+ df.format(loc.getAltitude()));

        String addr ="Could not find the address";

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocation(loc.getLatitude(),loc.getLongitude(),1);
            if(addressList!=null && addressList.size()>0 ) {
                addr = "Address : \n";
                if(addressList.get(0).getThoroughfare()!=null)
                    addr+=addressList.get(0).getThoroughfare() + "\n";
                if(addressList.get(0).getLocality()!=null)
                    addr+=addressList.get(0).getLocality() + ", ";
                if(addressList.get(0).getPostalCode()!=null)
                    addr+=addressList.get(0).getPostalCode() + ", ";
                if(addressList.get(0).getAdminArea()!=null)
                    addr+=addressList.get(0).getAdminArea();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        adrr.setText(addr);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lat = findViewById(R.id.tvLat);
        lon = findViewById(R.id.tvLong);
        acc = findViewById(R.id.tvAcc);
        alti = findViewById(R.id.tvAlt);
        adrr = findViewById(R.id.tvAddr);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                locUpdate(location);
            }
        };
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            Location lastLoc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(lastLoc!=null) {
                locUpdate(lastLoc);
            }
        }
    }
}
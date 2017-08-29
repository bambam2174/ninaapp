package de.sedatkilinc.ninaapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager mLocationManager;
    private static long LOCATION_REFRESH_TIME = 5000;
    private static float LOCATION_REFRESH_DISTANCE = 10;
    private Location mLocation;

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.d("Location", location.toString());
            mLocation = location;
            LatLng currLatLong = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.addMarker(new MarkerOptions().position(currLatLong).title("You are here alt:" + location.getAltitude()));
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(currLatLong));
//            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currLatLong, 13));
            MapsActivity.this.animateMaptoLatLng(currLatLong);
            //mMap.zoo
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            Log.d("onStatusChanged "+i, s);
            Log.d("Location ", mLocation.toString());
        }

        @Override
        public void onProviderEnabled(String s) {
            Log.d("onProviderEnabled ", s);
            Log.d("Location ", mLocation.toString());
        }

        @Override
        public void onProviderDisabled(String s) {
            Log.d("onProviderDisabled ", s);
            Log.d("Location ", mLocation.toString());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
    }

    private void getLocation() {
        if (this.checkLocationPermission()) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME, LOCATION_REFRESH_DISTANCE, mLocationListener);
        }
        else {
            String[] perms = { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS, Manifest.permission.LOCATION_HARDWARE };
            requestPermissions( perms, 1340);
        }
    }

    private boolean checkLocationPermission() {
        Log.d("ACCESS_FINE_LOCATION", String.valueOf(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)));
        Log.d("ACCESS_COARSE_LOCATION", String.valueOf(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)));
        Log.d("ACCESS_LOCATION_EXTRA_COMMANDS", String.valueOf(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS)));
        Log.d("LOCATION_HARDWARE", String.valueOf(ContextCompat.checkSelfPermission(this, Manifest.permission.LOCATION_HARDWARE)));
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        Location lastLocation = new Location("");
        lastLocation.setLatitude(sydney.latitude);
        lastLocation.setLongitude(sydney.longitude);
        if (checkLocationPermission()) {
            lastLocation = mLocationManager.getLastKnownLocation(mLocationManager.getBestProvider(new Criteria(), false));
        }
        LatLng currLatLong = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
        mMap.addMarker(new MarkerOptions().position(currLatLong).title("Marker last location"));

        this.animateMaptoLatLng(currLatLong);

    }

    private void animateMaptoLatLng(LatLng latLng) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
        CameraPosition.Builder cameraPositionBuilder = new CameraPosition.Builder();
        CameraPosition cameraPosition = cameraPositionBuilder
                .target(latLng)
                .zoom(17)
                .bearing(90)
                .tilt(40)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    public void geoLocate(View view) {
        Log.d("geoLocate", view.toString());
        this.getLocation();
    }

    public void getLocation(View view) {
        Log.d("getLocation", view.toString());
        RequestService requestService = new RequestService(this);
        String szResponse = requestService.getResponse("http://istanbulchicks.hol.es?controller=gps&action=getgpsbyid&aa=1&json=1");
        Log.d("getLocation", szResponse);
        JSONObject objJSON = null;

        JSONArray arrJSON = null;
        try {
            //objJSON = new JSONObject(szResponse);
            arrJSON = new JSONArray(szResponse);

        } catch (Exception e) {
            e.printStackTrace();
            e.getMessage();
        }
//        if (objJSON != null)
//            arrJSON = objJSON.getJSONArray();
        Log.d("array json", arrJSON.toString());
        try {
            for (int i=0; i < arrJSON.length(); i++) {

                JSONObject currObj = arrJSON.getJSONObject(i);

                LatLng currLatLong = new LatLng(currObj.getDouble("lat"), currObj.getDouble("long"));
                mMap.addMarker(new MarkerOptions().position(currLatLong).title("Bullen"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
